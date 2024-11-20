package by.bsu.dependency.usecase.service;

import by.bsu.dependency.annotation.Bean;
import by.bsu.dependency.annotation.PostConstruct;
import by.bsu.dependency.annotation.BeanScope;
import by.bsu.dependency.usecase.model.Order;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.atomic.AtomicInteger;

@Bean(name = "orderRepository", scope = BeanScope.SINGLETON)
public class OrderRepository {
    private final List<Order> orders;
    private final AtomicInteger orderIdGenerator;

    public OrderRepository() {
        this.orders = new ArrayList<>();
        this.orderIdGenerator = new AtomicInteger(1);
    }

    @PostConstruct
    private void init() {
        System.out.println("OrderRepository initialized");
    }

    public Order createOrder(String customerName, double amount) {
        Order order = new Order(orderIdGenerator.getAndIncrement(), customerName, amount);
        orders.add(order);
        return order;
    }

    public List<Order> getAllOrders() {
        return new ArrayList<>(orders);
    }
}