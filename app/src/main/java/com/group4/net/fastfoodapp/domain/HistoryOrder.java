package com.group4.net.fastfoodapp.domain;

public class HistoryOrder {

    public String email;
    public String title;
    public String description;
    public String price;
    public String status;
    public String time;

    public  HistoryOrder() {}

    public HistoryOrder(String email, String title, String description, String price, String status, String time) {
        this.email = email;
        this.title = title;
        this.description = description;
        this.price = price;
        this.status = status;
        this.time = time;
    }

    public HistoryOrder(String title, String description, String price, String status, String time) {
        this.title = title;
        this.description = description;
        this.price = price;
        this.status = status;
        this.time = time;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getPrice() {
        return price;
    }

    public void setPrice(String price) {
        this.price = price;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public String getTime() {
        return time;
    }

    public void setTime(String time) {
        this.time = time;
    }
}
