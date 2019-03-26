package com.brilliant.academe.handlers;

import com.amazonaws.services.lambda.runtime.Context;
import com.amazonaws.services.lambda.runtime.RequestHandler;
import com.brilliant.academe.domain.payment.PaymentTokenRequest;

import java.util.HashMap;
import java.util.Map;

public class PaymentGatewayHandler implements RequestHandler<PaymentTokenRequest, Map> {

    @Override
    public Map<String, String> handleRequest(PaymentTokenRequest request, Context context) {
        Map<String, String> map = new HashMap<>();
        String result = "";
        map.put("result", result);
        map.put("message", "SUCCESS");
        return map;
    }

}
