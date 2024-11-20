package by.bsu.dependency.usecase.service;

import by.bsu.dependency.annotation.Bean;
import by.bsu.dependency.annotation.PostConstruct;

@Bean
public class NotificationService {
    @PostConstruct
    private void init() {
        System.out.println("NotificationService initialized");
    }

    public void notifyCustomer(String customerName, String message) {
        System.out.println("Sending notification to " + customerName + ": " + message);
    }
}