package com.brilliant.academe.handlers;

import com.amazonaws.services.dynamodbv2.AmazonDynamoDB;
import com.amazonaws.services.dynamodbv2.AmazonDynamoDBClientBuilder;
import com.amazonaws.services.dynamodbv2.document.*;
import com.amazonaws.services.dynamodbv2.document.spec.PutItemSpec;
import com.amazonaws.services.dynamodbv2.document.spec.QuerySpec;
import com.amazonaws.services.dynamodbv2.document.spec.UpdateItemSpec;
import com.amazonaws.services.dynamodbv2.document.utils.NameMap;
import com.amazonaws.services.dynamodbv2.document.utils.ValueMap;
import com.amazonaws.services.lambda.runtime.Context;
import com.amazonaws.services.lambda.runtime.RequestHandler;
import com.brilliant.academe.domain.checkout.StripeCheckoutEvent;
import com.brilliant.academe.domain.checkout.StripeCheckoutResponse;
import com.brilliant.academe.domain.payment.PaymentGatewayWebhookRequest;
import com.brilliant.academe.util.CommonUtils;
import com.google.gson.Gson;
import com.stripe.Stripe;
import com.stripe.exception.StripeException;
import com.stripe.model.Event;
import com.stripe.model.PaymentIntent;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

import static com.brilliant.academe.constant.Constant.*;

public class PaymentGatewayWebhookHandler implements RequestHandler<PaymentGatewayWebhookRequest, Void> {

    private DynamoDB dynamoDB;

    @Override
    public Void handleRequest(PaymentGatewayWebhookRequest request, Context context)  {
        initDynamoDbClient();
        String stripeJson = new Gson().toJson(request.getEventJson());
        return execute(stripeJson);
    }

    private void initDynamoDbClient() {
        AmazonDynamoDB client = AmazonDynamoDBClientBuilder.standard()
                .withRegion(REGION)
                .build();
        dynamoDB = new DynamoDB(client);
    }

    public Void execute(String stripeJson){
        Event event = Event.GSON.fromJson(stripeJson, Event.class);
        if(event.getType().equals("checkout.session.completed")){
            StripeCheckoutResponse response = new Gson().fromJson(event.getData().toJson(), StripeCheckoutResponse.class);
            StripeCheckoutEvent checkoutEvent = response.getObject();
            System.out.println("***"+new Gson().toJson(checkoutEvent));
            String orderId = checkoutEvent.getClient_reference_id();
            String userId = getUserId(orderId);
            String paymentIntentId = checkoutEvent.getPayment_intent();

            Stripe.apiKey = STRIPE_SECRET_KEY;
            String paymentIntentJson = "NA";
            boolean isPaymentSucceeded = false;
            Long amount = 0L;
            try {
                PaymentIntent paymentIntent = PaymentIntent.retrieve(paymentIntentId);
                paymentIntentJson = new Gson().toJson(paymentIntent);
                amount =  paymentIntent.getAmount();
                System.out.println("**"+ paymentIntentJson);
                if(Objects.nonNull(paymentIntent) && Objects.nonNull(paymentIntent.getCharges())
                    && Objects.nonNull(paymentIntent.getCharges().getData())
                        && Objects.nonNull(paymentIntent.getCharges().getData().size() > 0)
                        && paymentIntent.getCharges().getData().get(0).getStatus().equals("succeeded")
                        && paymentIntent.getStatus().equals("succeeded")){
                    isPaymentSucceeded = true;
                }
                System.out.println("Payment Succeeded:"+ isPaymentSucceeded);

            } catch (StripeException e) {
                e.printStackTrace();
            }

            if(Objects.nonNull(userId) && isPaymentSucceeded){
                List<String> skuIds = new ArrayList<>();
                checkoutEvent.getDisplay_items().forEach(s->skuIds.add(s.getSku().getId()));
                List<String> courses = getCourses(skuIds);
                String transactionId = event.getId();
                updateOrderDetails(checkoutEvent, orderId, userId, transactionId, paymentIntentJson, amount);
                updateCart(userId, courses, orderId, transactionId);
                enrollCourses(courses, userId);
            }
        }
        return null;
    }

    private String getUserId(String orderId){
        Index index = dynamoDB.getTable(DYNAMODB_TABLE_NAME_ORDER_CART).getIndex("orderId-index");
        QuerySpec querySpec = new QuerySpec()
                .withKeyConditionExpression("orderId = :v_order_id")
                .withFilterExpression("cartStatus = :v_cart_status")
                .withValueMap(new ValueMap()
                        .withString(":v_order_id", orderId)
                        .withString(":v_cart_status", STATUS_IN_PROCESS));

        ItemCollection<QueryOutcome> userCourseCartItems = index.query(querySpec);
        String userId = null;
        for(Item item: userCourseCartItems){
            userId = (String) item.get("userId");
            break;
        }
        return userId;
    }

    private List<String> getCourses(List<String> skuIds){
        List<String> courses = new ArrayList<>();
        for(String skuId: skuIds) {
            Index index = dynamoDB.getTable(DYNAMODB_TABLE_NAME_COURSE_RESOURCE).getIndex("skuId-index");
            QuerySpec querySpec = new QuerySpec()
                    .withKeyConditionExpression("skuId = :v_sku_id")
                    .withValueMap(new ValueMap()
                            .withString(":v_sku_id", skuId));

            ItemCollection<QueryOutcome> courseInfo = index.query(querySpec);
            for (Item item : courseInfo) {
                courses.add((String) item.get("id"));
            }
        }
        return courses;
    }


    private void updateCart(String userId, List<String> courses, String orderId, String transactionId){
        Table table = dynamoDB.getTable(DYNAMODB_TABLE_NAME_ORDER_CART);
        ItemCollection<QueryOutcome> items = table.getIndex("orderId-index").query(new QuerySpec()
                .withKeyConditionExpression("orderId = :v_order_id")
                .withFilterExpression("cartStatus = :v_cart_status")
                .withValueMap(new ValueMap().withString(":v_order_id", orderId)
                        .withString(":v_cart_status", STATUS_IN_PROCESS)));

        String cartStatus = STATUS_SUCCESS;

        for(String courseId: courses){
            for(Item item: items){
                String cartCourseId = (String) item.get("courseId");
                if(courseId.equals(cartCourseId)){
                    UpdateItemSpec updateItemSpec = new UpdateItemSpec()
                            .withPrimaryKey("orderId", orderId,
                                    "courseId", courseId)
                            .withUpdateExpression("set #s = :cartStatus, " +
                                    "#t = :transactionId")
                            .withNameMap(new NameMap()
                                    .with("#s", "cartStatus")
                                    .with("#t", "transactionId"))
                            .withValueMap(new ValueMap()
                                    .withString(":cartStatus", cartStatus)
                                    .withString(":transactionId", transactionId));
                    table.updateItem(updateItemSpec);
                }
            }
        }
    }


    private void updateOrderDetails(StripeCheckoutEvent checkoutEvent, String orderId, String userId, String transactionId, String paymentIntentDeatils, Long amount){
        String orderDetailsJson = "NA";
        if(Objects.nonNull(checkoutEvent))
            orderDetailsJson = new Gson().toJson(checkoutEvent);

        String orderStatus = STATUS_SUCCESS;
        BigDecimal amountInDollars = new BigDecimal(amount).divide(new BigDecimal(100));
        dynamoDB.getTable(DYNAMODB_TABLE_NAME_ORDER).putItem(new PutItemSpec().withItem(new Item()
                .withString("id", orderId)
                .withString("transactionId", transactionId)
                .withString("userId", userId)
                .withString("orderStatus", orderStatus)
                .withString("orderDetails", orderDetailsJson)
                .withNumber("amount", amountInDollars)
                .withString("createdDate", CommonUtils.getDateTime())
                .withString("paymentDetails", paymentIntentDeatils)));
    }


    private void enrollCourses(List<String> courses, String userId){
        for(String courseId: courses){
            CommonUtils.enrollUserCourse(userId, courseId, dynamoDB);
        }

    }

}
