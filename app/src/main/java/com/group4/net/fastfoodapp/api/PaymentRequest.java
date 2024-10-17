package com.group4.net.fastfoodapp.api;

import java.time.OffsetDateTime;
import java.util.Date;

public class PaymentRequest {
    private int orderId;
    private String orderInfo;
    private double amount;

    public PaymentRequest() {

    }

    public PaymentRequest(int orderId, String orderInfo, double amount) {
        this.orderId = orderId;
        this.orderInfo = orderInfo;
        this.amount = amount;
    }

    public int getOrderId() {
        return orderId;
    }

    public void setOrderId(int orderId) {
        this.orderId = orderId;
    }

    public String getOrderInfo() {
        return orderInfo;
    }

    public void setOrderInfo(String orderInfo) {
        this.orderInfo = orderInfo;
    }
    public double getAmount() {
        return amount;
    }

    public void setAmount(double amount) {
        this.amount = amount;
    }
}
