package com.brilliant.academe.domain.payment;

import java.io.Serializable;

public class PaymentGatewayWebhookRequest implements Serializable {

    private Object eventJson;

    public Object getEventJson() {
        return eventJson;
    }

    public void setEventJson(Object eventJson) {
        this.eventJson = eventJson;
    }
}
