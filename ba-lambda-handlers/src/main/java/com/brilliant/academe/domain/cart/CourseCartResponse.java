package com.brilliant.academe.domain.cart;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonInclude;

import java.io.Serializable;
import java.util.List;

@JsonInclude(JsonInclude.Include.NON_NULL)
public class CourseCartResponse implements Serializable {

    private List<CartInfo> cartDetails;
    private String message;

    public List<CartInfo> getCartDetails() {
        return cartDetails;
    }

    public void setCartDetails(List<CartInfo> cartDetails) {
        this.cartDetails = cartDetails;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }
}
