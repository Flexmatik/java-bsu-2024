package by.bsu.dependency.usecase.model;

public class Order {
    private final int id;
    private final String customerName;
    private final double amount;
    private boolean processed;

    public Order(int id, String customerName, double amount) {
        this.id = id;
        this.customerName = customerName;
        this.amount = amount;
        this.processed = false;
    }

    public int getId() {
        return id;
    }

    public String getCustomerName() {
        return customerName;
    }

    public double getAmount() {
        return amount;
    }

    public boolean isProcessed() {
        return processed;
    }

    public void setProcessed(boolean processed) {
        this.processed = processed;
    }

    @Override
    public String toString() {
        return "Order{" +
                "id = " + id +
                ", customerName = " + customerName + 
                ", amount = " + amount +
                ", processed = " + processed +
                '}';
    }
}