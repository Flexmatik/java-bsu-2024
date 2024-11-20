package by.bsu.dependency.context;

import by.bsu.dependency.annotation.Bean;
import by.bsu.dependency.annotation.BeanScope;
import by.bsu.dependency.annotation.Inject;
import by.bsu.dependency.annotation.PostConstruct;
import by.bsu.dependency.exception.ApplicationContextNotStartedException;
import by.bsu.dependency.exception.CyclicDependencyException;
import by.bsu.dependency.exception.NoSuchBeanDefinitionException;

import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;


public abstract class AbstractApplicationContext implements ApplicationContext {
    protected final Map<String, Class<?>> beanDefinitions = new HashMap<>();
    protected final Map<String, Object> singletonBeans = new HashMap<>();
    protected final Map<String, Boolean> isSingletonMap = new HashMap<>();
    protected ContextStatus status = ContextStatus.NOT_STARTED;
    private Set<String> beansInCreation = new HashSet<>();
    
    protected enum ContextStatus {
        NOT_STARTED,
        STARTED
    }

    protected void checkIfRunning() {
        if (status != ContextStatus.STARTED) {
            throw new ApplicationContextNotStartedException("Application context is not started");
        }
    }

    protected void checkBeanExists(String name) {
        if (!beanDefinitions.containsKey(name)) {
            throw new NoSuchBeanDefinitionException("Bean with name '" + name + "' is not defined");
        }
    }

    protected <T> T instantiateBean(Class<T> beanClass) {
        try {
            return beanClass.getConstructor().newInstance();
        } catch (InstantiationException | IllegalAccessException | InvocationTargetException | NoSuchMethodException e) {
            throw new RuntimeException("Failed to instantiate bean: " + beanClass.getName(), e);
        }
    }

    protected void injectDependencies(Object bean) {
        String beanName = getBeanName(bean.getClass());
        if (!beansInCreation.add(beanName)) {
            throw new RuntimeException("Circular dependency detected for bean: " + beanName);
        }
        
        try {
            for (Field field : bean.getClass().getDeclaredFields()) {
                if (field.isAnnotationPresent(Inject.class)) {
                    field.setAccessible(true);
                    Object dependency = getBean(field.getType());
                    field.set(bean, dependency);
                }
            }
        } catch (IllegalAccessException e) {
            throw new RuntimeException("Failed to inject dependencies for bean: " + beanName, e);
        } finally {
            beansInCreation.remove(beanName);
        }
    }

    protected void invokePostConstruct(Object bean) {
        for (Method method : bean.getClass().getDeclaredMethods()) {
            if (method.isAnnotationPresent(PostConstruct.class)) {
                try {
                    method.setAccessible(true);
                    method.invoke(bean);
                } catch (IllegalAccessException | InvocationTargetException e) {
                    throw new RuntimeException("Failed to invoke @PostConstruct method: " + method.getName(), e);
                }
            }
        }
    }

    @Override
    public boolean isRunning() {
        return status == ContextStatus.STARTED;
    }

    @Override
    public boolean containsBean(String name) {
        checkIfRunning();
        return beanDefinitions.containsKey(name);
    }

    @Override
    public boolean isPrototype(String name) {
        checkBeanExists(name);
        return !isSingletonMap.get(name);
    }

    @Override
    public boolean isSingleton(String name) {
        checkBeanExists(name);
        return isSingletonMap.get(name);
    }

    protected String getBeanName(Class<?> beanClass) {

        if (!beanClass.isAnnotationPresent(Bean.class)) {
            String className = beanClass.getSimpleName();
            return Character.toLowerCase(className.charAt(0)) + className.substring(1);
        }

        Bean beanAnnotation = beanClass.getAnnotation(Bean.class);
        String name = beanAnnotation.name();
        if (name.isEmpty()) {
            String className = beanClass.getSimpleName();
            return Character.toLowerCase(className.charAt(0)) + className.substring(1);
        }
        return name;
    }

    @Override
    @SuppressWarnings("unchecked")
    public <T> T getBean(Class<T> clazz) {
        checkIfRunning();
        
        String beanName = getBeanName(clazz);
        if (!containsBean(beanName)) {
            throw new NoSuchBeanDefinitionException("No bean found for class: " + clazz.getName());
        }
        
        return (T) getBean(beanName);
    }

    @Override
    public Object getBean(String name) {
        checkIfRunning();
        checkBeanExists(name);

        if (isSingleton(name)) {
            return singletonBeans.get(name);
        } else {
            Object bean = instantiateBean(beanDefinitions.get(name));
            injectDependencies(bean);
            invokePostConstruct(bean);
            return bean;
        }
    }

    protected boolean isSingletonClass(Class<?> beanClass) {
        if (!beanClass.isAnnotationPresent(Bean.class)) {
            return true;
        }
        Bean beanAnnotation = beanClass.getAnnotation(Bean.class);
        return beanAnnotation.scope() == BeanScope.SINGLETON;
    }

    protected void checkForCyclicDependencies() {
        if (beanDefinitions.isEmpty()) {
            return;
        }
        
        Map<String, Set<String>> dependencyGraph = buildDependencyGraph();
        Set<String> visited = new HashSet<>();
        Set<String> recursionStack = new HashSet<>();
    
        for (String beanName : beanDefinitions.keySet()) {
            if (dependencyGraph.containsKey(beanName) && hasCycle(beanName, dependencyGraph, visited, recursionStack)) {
                throw new CyclicDependencyException("Cyclic dependency detected for bean: " + beanName);
            }
        }
    }
    
    private Map<String, Set<String>> buildDependencyGraph() {
        Map<String, Set<String>> graph = new HashMap<>();
        
        for (Map.Entry<String, Class<?>> entry : beanDefinitions.entrySet()) {
            String beanName = entry.getKey();
            Class<?> beanClass = entry.getValue();
            Set<String> dependencies = new HashSet<>();
    
            for (Field field : beanClass.getDeclaredFields()) {
                if (field.isAnnotationPresent(Inject.class)) {
                    Class<?> dependencyClass = field.getType();
                    String dependencyName = getBeanName(dependencyClass);
                    dependencies.add(dependencyName);
                }
            }
    
            graph.put(beanName, dependencies);
        }
    
        return graph;
    }
    
    private boolean hasCycle(String beanName, Map<String, Set<String>> graph, 
                            Set<String> visited, Set<String> recursionStack) {
        if (recursionStack.contains(beanName)) {
            return true;
        }
    
        if (visited.contains(beanName)) {
            return false;
        }
    
        visited.add(beanName);
        recursionStack.add(beanName);
    
        Set<String> dependencies = graph.getOrDefault(beanName, new HashSet<>());
        for (String dependency : dependencies) {
            if (hasCycle(dependency, graph, visited, recursionStack)) {
                return true;
            }
        }
    
        recursionStack.remove(beanName);
        return false;
    }
}