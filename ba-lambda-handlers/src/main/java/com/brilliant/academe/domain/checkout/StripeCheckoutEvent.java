package com.brilliant.academe.domain.checkout;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonInclude;

import java.io.Serializable;
import java.util.List;

@JsonIgnoreProperties(ignoreUnknown = true)
@JsonInclude(JsonInclude.Include.NON_NULL)
public class StripeCheckoutEvent implements Serializable {

    private String id;
    private String object;
    private String cancel_url;
    private String client_reference_id;
    private String customer;
    private String customer_email;
    private List<DisplayItem> display_items;
    private String payment_intent;

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getObject() {
        return object;
    }

    public void setObject(String object) {
        this.object = object;
    }

    public String getCancel_url() {
        return cancel_url;
    }

    public void setCancel_url(String cancel_url) {
        this.cancel_url = cancel_url;
    }

    public String getClient_reference_id() {
        return client_reference_id;
    }

    public void setClient_reference_id(String client_reference_id) {
        this.client_reference_id = client_reference_id;
    }

    public String getCustomer() {
        return customer;
    }

    public void setCustomer(String customer) {
        this.customer = customer;
    }

    public String getCustomer_email() {
        return customer_email;
    }

    public void setCustomer_email(String customer_email) {
        this.customer_email = customer_email;
    }

    public List<DisplayItem> getDisplay_items() {
        return display_items;
    }

    public void setDisplay_items(List<DisplayItem> display_items) {
        this.display_items = display_items;
    }

    public String getPayment_intent() {
        return payment_intent;
    }

    public void setPayment_intent(String payment_intent) {
        this.payment_intent = payment_intent;
    }
}
