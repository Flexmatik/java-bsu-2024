package by.bsu.dependency.cyclic;

import by.bsu.dependency.annotation.Bean;
import by.bsu.dependency.annotation.Inject;
import by.bsu.dependency.annotation.BeanScope;

@Bean(name = "CyclicBean2", scope = BeanScope.PROTOTYPE)
public class CyclicBean2 {

    @Inject
    private CyclicBean1 cyclicBean1;

    public void doSomething() {
        System.out.println("CyclicBean1 doing something...");
    }
}

