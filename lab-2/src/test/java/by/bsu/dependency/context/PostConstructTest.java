package by.bsu.dependency.context;

import by.bsu.dependency.annotation.Bean;
import by.bsu.dependency.annotation.Inject;
import by.bsu.dependency.annotation.PostConstruct;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;

public class PostConstructTest {
    @Bean(name = "postConstructBean")
    public static class PostConstructBean {
        
        @Inject
        private DependencyBean dependency;

        private String message;

        @PostConstruct
        public void init() {
            message = "Invoking PostConstruct can mean only one thing: " + dependency.getMessage();
        }

        public String getMessage() {
            return message;
        }
    }

    @Bean(name = "dependencyBean")
    public static class DependencyBean {
        public String getMessage() {
            return "Lab-2 is finally done";
        }
    }

    @Test
    public void testPostConstruct() {
        ApplicationContext context = new SimpleApplicationContext(
            PostConstructBean.class,
            DependencyBean.class
        );
        context.start();

        PostConstructBean postConstructBean = context.getBean(PostConstructBean.class);
        assertEquals("Invoking PostConstruct can mean only one thing: Lab-2 is finally done", postConstructBean.getMessage());
    }
}