package com.brilliant.academe.domain.checkout;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonInclude;

import java.io.Serializable;

@JsonIgnoreProperties(ignoreUnknown = true)
@JsonInclude(JsonInclude.Include.NON_NULL)
public class DisplayItem implements Serializable {

    private SkuInfo sku;

    public SkuInfo getSku() {
        return sku;
    }

    public void setSku(SkuInfo sku) {
        this.sku = sku;
    }
}
