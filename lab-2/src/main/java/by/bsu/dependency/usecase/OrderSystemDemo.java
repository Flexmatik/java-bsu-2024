package by.bsu.dependency.usecase;

import by.bsu.dependency.context.AutoScanApplicationContext;
import by.bsu.dependency.usecase.service.OrderProcessor;
import by.bsu.dependency.usecase.service.OrderRepository;

/**
 * Demonstrates a dependency injection-based order processing system where:
 * - OrderRepository (singleton) stores all orders
 * - OrderProcessor (prototype) handles order creation and processing
 * - NotificationService sends notifications to customers
 * 
 * The demo creates two separate processor instances to show how prototype
 * beans maintain individual state while sharing the singleton repository.
 * Each processor tracks its own order count while all orders are stored
 * in the shared repository.
 */

public class OrderSystemDemo {
    public static void main(String[] args) {
        AutoScanApplicationContext context = new AutoScanApplicationContext("by.bsu.dependency.usecase");
        context.start();

        // Getting the order repository
        OrderRepository orderRepository = context.getBean(OrderRepository.class);
        
        // Creating two order processors as prototype beans
        OrderProcessor processor1 = context.getBean(OrderProcessor.class);
        OrderProcessor processor2 = context.getBean(OrderProcessor.class);

        // Processing orders through different processors
        processor1.processOrder("Georgiy Razmyslovich", 100.00);
        processor1.processOrder("Jasos Biba", 52.52);
        processor2.processOrder("Meow meeeow", 2.28);

        // Some debug info just for fun
        System.out.println("\nOrder Processing Statistics:");
        System.out.println(processor1.getProcessorId() + " processed " + 
            processor1.getProcessedOrders() + " orders");
        System.out.println(processor2.getProcessorId() + " processed " + 
            processor2.getProcessedOrders() + " orders");
        System.out.println("\nAll Orders in Repository:");
        orderRepository.getAllOrders().forEach(System.out::println);
    }
}