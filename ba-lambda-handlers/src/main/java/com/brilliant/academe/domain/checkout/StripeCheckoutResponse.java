package com.brilliant.academe.domain.checkout;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonInclude;

import java.io.Serializable;

@JsonIgnoreProperties(ignoreUnknown = true)
@JsonInclude(JsonInclude.Include.NON_NULL)
public class StripeCheckoutResponse implements Serializable {

    private StripeCheckoutEvent object;

    public StripeCheckoutEvent getObject() {
        return object;
    }

    public void setObject(StripeCheckoutEvent object) {
        this.object = object;
    }
}
