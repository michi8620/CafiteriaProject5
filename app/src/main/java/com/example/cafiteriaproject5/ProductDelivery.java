package com.example.cafiteriaproject5;

public class ProductDelivery {

    private int index;
    private String name;
    private int quantity;
    private Double total;

    public ProductDelivery(int index, String name, int quantity, Double total) {
        this.index = index;
        this.name = name;
        this.quantity = quantity;
        this.total = total;
    }

    public int getIndex() {
        return index;
    }

    public void setIndex(int index) {
        this.index = index;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public int getQuantity() {
        return quantity;
    }

    public void setQuantity(int quantity) {
        this.quantity = quantity;
    }

    public Double getTotal() {
        return total;
    }

    public void setTotal(Double total) {
        this.total = total;
    }

    @Override
    public String toString() {
        return name + ", כמות: " + quantity;
    }
}
