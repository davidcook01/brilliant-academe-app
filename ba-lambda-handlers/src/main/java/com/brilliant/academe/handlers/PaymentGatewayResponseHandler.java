package com.brilliant.academe.handlers;

import com.amazonaws.services.lambda.runtime.Context;
import com.amazonaws.services.lambda.runtime.RequestHandler;
import com.google.gson.Gson;
import com.stripe.model.Event;

public class PaymentGatewayResponseHandler implements RequestHandler<Event, String> {
    @Override
    public String handleRequest(Event request, Context context) {

        if(request.getType().equals("charge.succeeded")){

        }
        return new Gson().toJson(request);
    }
}
