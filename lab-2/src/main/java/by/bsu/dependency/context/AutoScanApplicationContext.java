package by.bsu.dependency.context;

import by.bsu.dependency.annotation.Bean;

import org.reflections.Reflections;

import java.util.Set;


public class AutoScanApplicationContext extends AbstractApplicationContext {

    public AutoScanApplicationContext(String packageName) {
        Reflections reflections = new Reflections(packageName);
        Set<Class<?>> allClasses = reflections.getTypesAnnotatedWith(Bean.class);

        for (Class<?> beanClass : allClasses) {
            String beanName = getBeanName(beanClass);
            beanDefinitions.put(beanName, beanClass);
            isSingletonMap.put(beanName, isSingletonClass(beanClass));
        }

        checkForCyclicDependencies();
        
    }

    @Override
    public void start() {
        if (status == ContextStatus.STARTED) {
            return;
        }

        status = ContextStatus.STARTED;

        beanDefinitions.forEach((name, clazz) -> {
            if (isSingletonMap.get(name)) {
                Object bean = instantiateBean(clazz);
                injectDependencies(bean);
                invokePostConstruct(bean);
                singletonBeans.put(name, bean);
            }
        });
    }
}