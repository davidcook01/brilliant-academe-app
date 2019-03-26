package com.brilliant.academe.domain.payment;

import java.io.Serializable;

public class PaymentTokenRequest implements Serializable {

    private String stripeToken;
    private String stripeTokenType;
    private String stripeEmail;

    public String getStripeToken() {
        return stripeToken;
    }

    public void setStripeToken(String stripeToken) {
        this.stripeToken = stripeToken;
    }

    public String getStripeTokenType() {
        return stripeTokenType;
    }

    public void setStripeTokenType(String stripeTokenType) {
        this.stripeTokenType = stripeTokenType;
    }

    public String getStripeEmail() {
        return stripeEmail;
    }

    public void setStripeEmail(String stripeEmail) {
        this.stripeEmail = stripeEmail;
    }
}
