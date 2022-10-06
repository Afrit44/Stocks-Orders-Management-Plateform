package model;

import java.util.ArrayList;

public class Order {

    private int orderId;
    private String name;
    private String client;
    private Status status;
    private String products;

    public Order(int orderId, String name, String client, Status status, String products) {
        this.orderId = orderId;
        this.products = products;
        this.name = name;
        this.client = client;
        this.status = status;
    }

    public int getOrderId() {
        return orderId;
    }

    public void setOrderId(int orderId) {
        this.orderId = orderId;
    }

    public String getProducts() {
        return products;
    }

    public void setProducts(String products) {
        this.products = products;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getClient() {
        return client;
    }

    public void setClient(String client) {
        this.client = client;
    }

    public Status getStatus() {
        return status;
    }

    public void setStatus(Status status) {
        this.status = status;
    }
}
