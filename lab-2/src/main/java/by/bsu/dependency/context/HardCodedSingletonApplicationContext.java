package by.bsu.dependency.context;

import by.bsu.dependency.annotation.Bean;


public class HardCodedSingletonApplicationContext extends AbstractApplicationContext {

    public HardCodedSingletonApplicationContext(Class<?>... beanClasses) {
        for (Class<?> beanClass : beanClasses) {
            String beanName = beanClass.getAnnotation(Bean.class).name();
            this.beanDefinitions.put(beanName, beanClass);
            isSingletonMap.put(beanName, isSingletonClass(beanClass));
        }
    }

    @Override
    public void start() {
        if (status == ContextStatus.STARTED) {
            return;
        }

        status = ContextStatus.STARTED;
        
        beanDefinitions.forEach((beanName, beanClass) -> {
            Object bean = instantiateBean(beanClass);
            injectDependencies(bean);
            invokePostConstruct(bean);
            singletonBeans.put(beanName, bean);
        });
    }
}