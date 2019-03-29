package com.brilliant.academe.domain.payment;

import java.io.Serializable;

public class PaymentGatewayResponse implements Serializable {

    private String orderId;
    private String transactionId;
    private boolean isPaymentSuccess;
    private boolean isEnrollmentSuccess;
    private boolean isCartUpdated;
    private boolean isOrderUpdated;

    public String getOrderId() {
        return orderId;
    }

    public void setOrderId(String orderId) {
        this.orderId = orderId;
    }

    public String getTransactionId() {
        return transactionId;
    }

    public void setTransactionId(String transactionId) {
        this.transactionId = transactionId;
    }

    public boolean isPaymentSuccess() {
        return isPaymentSuccess;
    }

    public void setPaymentSuccess(boolean paymentSuccess) {
        isPaymentSuccess = paymentSuccess;
    }

    public boolean isEnrollmentSuccess() {
        return isEnrollmentSuccess;
    }

    public void setEnrollmentSuccess(boolean enrollmentSuccess) {
        isEnrollmentSuccess = enrollmentSuccess;
    }

    public boolean isCartUpdated() {
        return isCartUpdated;
    }

    public void setCartUpdated(boolean cartUpdated) {
        isCartUpdated = cartUpdated;
    }

    public boolean isOrderUpdated() {
        return isOrderUpdated;
    }

    public void setOrderUpdated(boolean orderUpdated) {
        isOrderUpdated = orderUpdated;
    }
}
