package com.brilliant.academe.domain.payment;

import java.io.Serializable;

public class PaymentGatewayRequest implements Serializable {

    private String token;
    private PaymentGatewayRequestInfo info;

    public String getToken() {
        return token;
    }

    public void setToken(String token) {
        this.token = token;
    }

    public PaymentGatewayRequestInfo getInfo() {
        return info;
    }

    public void setInfo(PaymentGatewayRequestInfo info) {
        this.info = info;
    }
}
