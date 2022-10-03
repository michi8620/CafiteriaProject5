package com.example.cafiteriaproject5;

public class User {
    private String firstName;
    private String lastName;
    private String grade;
    private String password;

    public User(String firstName, String lastName, String grade, String password) {
        this.firstName = firstName;
        this.lastName = lastName;
        this.grade = grade;
        this.password = password;
    }

    public User() {
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
}
