package by.bsu.dependency.context;


import by.bsu.dependency.annotation.Bean;
import by.bsu.dependency.annotation.Inject;
import by.bsu.dependency.annotation.BeanScope;
import by.bsu.dependency.exception.CyclicDependencyException;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertThrows;

public class CyclicDependencyTest {

@Bean(name = "groupBean", scope = BeanScope.PROTOTYPE)
    public static class GroupBean {

        @Inject
        private RingBean dependency;

        public String doSomething() {
            return dependency.doSomething();
        }
    }

    @Bean(name = "ringBean", scope = BeanScope.PROTOTYPE)
    public static class RingBean {

        @Inject
        private FieldBean testB;

        public String doSomething() {
            return testB.doSomething();
        }
        
    }

    @Bean(name = "fieldBean", scope = BeanScope.PROTOTYPE)
    public static class FieldBean {

        @Inject
        private GroupBean groupBean;

        public String doSomething() {
            return groupBean.doSomething();
        }

        
    }
    @Test
    public void testCyclicSimple() {
        assertThrows(CyclicDependencyException.class, () -> {
            new SimpleApplicationContext(
                GroupBean.class,
                RingBean.class,
                FieldBean.class
            );
        });
    }

    @Test
    public void testCyclicAuto() {
        assertThrows(CyclicDependencyException.class, () -> {
            new AutoScanApplicationContext(
                "by.bsu.dependency.cyclic"  // Cyclic1 and Cyclic2
            );
        });
    }

}