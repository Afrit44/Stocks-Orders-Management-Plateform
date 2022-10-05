package model;

import java.util.ArrayList;

public class Order {

    private int orderId;
    private ArrayList<Integer> products;
    private String name;
    private String client;
    private STATUS status;

    public Order(int orderId, ArrayList<Integer> products, String name, String client, STATUS status) {
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

    public ArrayList<Integer> getProducts() {
        return products;
    }

    public void setProducts(ArrayList<Integer> products) {
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

    public STATUS getStatus() {
        return status;
    }

    public void setStatus(STATUS status) {
        this.status = status;
    }
}
