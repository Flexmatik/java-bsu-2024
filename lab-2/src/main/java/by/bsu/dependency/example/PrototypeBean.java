package by.bsu.dependency.example;

import by.bsu.dependency.annotation.Bean;
import by.bsu.dependency.annotation.BeanScope;
import by.bsu.dependency.annotation.Inject;
import by.bsu.dependency.annotation.PostConstruct;

@Bean(name = "prototypeBean", scope = BeanScope.PROTOTYPE)
public class PrototypeBean {

    @Inject
    private FirstBean firstBean;

    @Inject
    private NotBean notBean;

    public void doSomething() {
        System.out.println("Hi, I'm prototype bean");
    }

    public void doSomethingWithNotBean() {
        System.out.println("Is it even a bean?...");
        notBean.doSomething();
    }

    @PostConstruct
    public void init() {
        System.out.println("Prototype bean is initialized");
    }
}

