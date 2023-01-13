package com.example.cafiteriaproject5;

public class User {
    private String firstName;
    private String lastName;
    private String gmail;
    private String grade;
    private String password;
    private String type;
    private double money;


    public User(String firstName, String lastName, String gmail, String grade, String password, String type) {
        this.firstName = firstName;
        this.lastName = lastName;
        this.gmail = gmail;
        this.grade = grade;
        this.password = password;
        this.type = type;
        this.money = 0;
    }

    public User(String firstName, String lastName, String gmail, String grade, Double money) {
        this.firstName = firstName;
        this.lastName = lastName;
        this.gmail = gmail;
        this.grade = grade;
        this.money = money;
    }

    public User() {
    }

    public String getType(){return type;}

    public String getGmail() {
        return gmail;
    }

    public String getFirstName() {
        return firstName;
    }

    public String getLastName() {
        return lastName;
    }

    public String getGrade() {
        return grade;
    }

    public String getPassword() {
        return password;
    }

    public void setType(String type){ this.type = type; }

    public void setGmail(String gmail) {
        this.gmail = gmail;
    }

    public void setFirstName(String firstName) {
        this.firstName = firstName;
    }

    public void setLastName(String lastName) {
        this.lastName = lastName;
    }

    public void setGrade(String grade) {
        this.grade.equals(grade);
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public double getMoney() {return money;}

    public void setMoney(double money) {this.money = money;}
}
