package by.bsu.dependency.context;

import org.junit.jupiter.api.Test;
import by.bsu.dependency.example.*;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertNotSame;
import static org.junit.jupiter.api.Assertions.assertSame;
import static org.junit.jupiter.api.Assertions.assertThrows;

import org.junit.jupiter.api.BeforeEach;

import by.bsu.dependency.exception.ApplicationContextNotStartedException;
import by.bsu.dependency.exception.NoSuchBeanDefinitionException;


class SimpleApplicationContextTest {
    
    private ApplicationContext applicationContext;

    @BeforeEach
    void init() {
        applicationContext = new SimpleApplicationContext(FirstBean.class, OtherBean.class, PrototypeBean.class, NotBean.class);
    }


    @Test
    void testEmptyContext() {
        assertThat(applicationContext.isRunning()).isFalse();
        applicationContext.start();
        assertThat(applicationContext.isRunning()).isTrue();
    }

    @Test
    void testContextNotStarted() {
        assertThrows(ApplicationContextNotStartedException.class, () -> applicationContext.getBean("anyBean"));
    }

    @Test
    void testNoSuchBean() {
        applicationContext.start();
        assertThrows(NoSuchBeanDefinitionException.class, () -> applicationContext.getBean("nonExistentBean"));
    }

    @Test
    void testContextGetBeanNotStarted() {
        assertThrows(
                ApplicationContextNotStartedException.class,
                () -> applicationContext.getBean("firstBean")
        );
    }

    @Test
    void testBeanWithoutAnnotation() {
        applicationContext.start();
        
        assertThat(applicationContext.containsBean("notBean")).isTrue();
        assertNotNull(applicationContext.getBean("notBean"));
        assertThat(applicationContext.isSingleton("notBean")).isTrue();
        assertThat(applicationContext.isPrototype("notBean")).isFalse();
    }

    @Test
    void testSingletonBean() {
        applicationContext.start();

        Object bean1 = applicationContext.getBean("firstBean");
        Object bean2 = applicationContext.getBean("firstBean");
        
        assertSame(bean1, bean2);
        assertThat(applicationContext.isSingleton("firstBean")).isTrue();
    }

    @Test
    void testContextContainsBeans() {
        applicationContext.start();

        assertThat(applicationContext.containsBean("firstBean")).isTrue();
        assertThat(applicationContext.containsBean("otherBean")).isTrue();
        assertThat(applicationContext.containsBean("notBean")).isTrue();
        assertThat(applicationContext.containsBean("randomName")).isFalse();
    }

    @Test
    void testPrototypeBean() {
        applicationContext.start();

        Object bean1 = applicationContext.getBean("prototypeBean");
        Object bean2 = applicationContext.getBean("prototypeBean");
        
        assertNotSame(bean1, bean2);
        assertThat(applicationContext.isPrototype("prototypeBean")).isTrue();
    }

    @Test
    void testGetBeanByClass() {
        applicationContext.start();

        NotBean bean = applicationContext.getBean(NotBean.class);
        assertNotNull(bean);
    }

    @Test
    void testDependencyInjection() {
        applicationContext.start();

        PrototypeBean prototypeBean = (PrototypeBean) applicationContext.getBean(PrototypeBean.class);
        prototypeBean.doSomethingWithNotBean();
    }
}