package by.bsu.dependency.cyclic;

import by.bsu.dependency.annotation.Bean;
import by.bsu.dependency.annotation.Inject;
import by.bsu.dependency.annotation.BeanScope;

@Bean(name = "CyclicBean1", scope = BeanScope.PROTOTYPE)
public class CyclicBean1 {

    @Inject
    private CyclicBean2 cyclicBean2;

    public void doSomething() {
        System.out.println("CyclicBean2 doing something...");
    }
}