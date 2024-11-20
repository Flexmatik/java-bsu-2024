package by.bsu.dependency.context;

import by.bsu.dependency.annotation.Bean;

public class SimpleApplicationContext extends AbstractApplicationContext {

    public SimpleApplicationContext(Class<?>... beanClasses) {
        for (Class<?> beanClass : beanClasses) {
            String beanName = getBeanName(beanClass);
            boolean isSingleton = beanClass.isAnnotationPresent(Bean.class) ? 
                isSingletonClass(beanClass) : true;

            beanDefinitions.put(beanName, beanClass);
            isSingletonMap.put(beanName, isSingleton);
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
                singletonBeans.put(name, bean);
            }
        });

        singletonBeans.forEach((name, bean) -> {
            injectDependencies(bean);
            invokePostConstruct(bean);
        });
    }

    @Override
    protected String getBeanName(Class<?> beanClass) {
        if (beanClass.isAnnotationPresent(Bean.class)) {
            return super.getBeanName(beanClass);
        }
        String className = beanClass.getSimpleName();
        return Character.toLowerCase(className.charAt(0)) + className.substring(1);
    }

}