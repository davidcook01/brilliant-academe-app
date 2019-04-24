package com.brilliant.academe.domain.order;

import java.io.Serializable;

public class GetOrderRequest implements Serializable {

    private String orderId;
    private String token;

    public String getOrderId() {
        return orderId;
    }

    public void setOrderId(String orderId) {
        this.orderId = orderId;
    }

    public String getToken() {
        return token;
    }

    public void setToken(String token) {
        this.token = token;
    }
}
