package com.brilliant.academe.domain.payment;

import com.stripe.model.Charge;

import java.io.Serializable;

public class PaymentGatewayResponse implements Serializable {

    private Charge result;
    private boolean isPaymentSuccess;
    private boolean isEnrollmentSuccess;

    public Charge getResult() {
        return result;
    }

    public void setResult(Charge result) {
        this.result = result;
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
}
