package by.bsu.dependency.usecase.service;


import by.bsu.dependency.annotation.Bean;
import by.bsu.dependency.annotation.Inject;
import by.bsu.dependency.annotation.PostConstruct;
import by.bsu.dependency.annotation.BeanScope;
import by.bsu.dependency.usecase.model.Order;

@Bean(scope = BeanScope.PROTOTYPE)
public class OrderProcessor {
    @Inject
    private OrderRepository orderRepository;
    
    @Inject
    private NotificationService notificationService;

    private final String processorId;
    private int processedOrders;

    public OrderProcessor() {
        this.processorId = "Processor-" + System.nanoTime();
        this.processedOrders = 0;
    }

    @PostConstruct
    private void init() {
        System.out.println("OrderProcessor " + processorId + " initialized");
    }

    public void processOrder(String customerName, double amount) {
        Order order = orderRepository.createOrder(customerName, amount);
        System.out.println(processorId + " processing order: " + order);
        
        order.setProcessed(true);
        processedOrders++;
        
        notificationService.notifyCustomer(customerName, 
            "Your order #" + order.getId() + " for $" + amount + " has been processed");
    }

    public int getProcessedOrders() {
        return processedOrders;
    }

    public String getProcessorId() {
        return processorId;
    }
}
