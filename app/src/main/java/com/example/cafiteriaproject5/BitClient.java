package com.example.cafiteriaproject5;

public class BitClient {

    private String code;
    private String name;
    private String lastName;
    private String product;
    private String quantity;

    public BitClient(String code, String name, String lastName, String product, String quantity) {
        this.code = code;
        this.name = name;
        this.lastName = lastName;
        this.product = product;
        this.quantity = quantity;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getLastName() {
        return lastName;
    }

    public void setLastName(String lastName) {
        this.lastName = lastName;
    }

    public String getProduct() {
        return product;
    }

    public void setProduct(String product) {
        this.product = product;
    }

    public String getQuantity() {
        return quantity;
    }

    public void setQuantity(String quantity) {
        this.quantity = quantity;
    }

    public String getCode() {
        return code;
    }

    public void setCode(String code) {
        this.code = code;
    }
}
