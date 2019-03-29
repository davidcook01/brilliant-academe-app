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
import com.brilliant.academe.domain.payment.PaymentGatewayRequest;
import com.brilliant.academe.domain.payment.PaymentGatewayResponse;
import com.stripe.Stripe;
import com.stripe.exception.StripeException;
import com.stripe.model.Charge;

import java.math.BigDecimal;
import java.util.*;

import static com.brilliant.academe.constant.Constant.*;

public class PaymentGatewayHandler implements RequestHandler<PaymentGatewayRequest, PaymentGatewayResponse> {

    private DynamoDB dynamoDB;
    private String orderId;

    @Override
    public PaymentGatewayResponse handleRequest(PaymentGatewayRequest request, Context context) {
        initDynamoDbClient();
        return execute(request);
    }

    private void initDynamoDbClient() {
        AmazonDynamoDB client = AmazonDynamoDBClientBuilder.standard()
                .withRegion(REGION)
                .build();
        dynamoDB = new DynamoDB(client);
    }

    public PaymentGatewayResponse execute(PaymentGatewayRequest request){
        System.out.println("Token: "+request.getToken() + ", User Id:"+ request.getUserId() + ", Courses:"+ request.getCourses()
                + "Payment Type:"+ request.getPaymentType() + "Amount:"+ request.getAmount() + "Currency:"+ request.getCurrency());

        orderId = UUID.randomUUID().toString();
        Stripe.apiKey = STRIPE_SECRET_KEY;

        Map<String, Object> params = new HashMap<>();
        params.put("source", request.getToken());
        params.put("amount", request.getAmount());
        params.put("currency", request.getCurrency());
        params.put("description", "Charge");

        PaymentGatewayResponse paymentGatewayResponse = new PaymentGatewayResponse();
        paymentGatewayResponse.setOrderId(orderId);
        Charge charge = initiateCharge(paymentGatewayResponse, params);
        updateCart(paymentGatewayResponse, request.getUserId(),
                request.getCourses(), orderId);
        updateOrderDetails(paymentGatewayResponse, charge, orderId);
        if(paymentGatewayResponse.isPaymentSuccess()){
            enrollCourses(paymentGatewayResponse, request);
        }
        return paymentGatewayResponse;
    }

    private Charge initiateCharge(PaymentGatewayResponse paymentGatewayResponse, Map params){
        try {
            Charge charge = Charge.create(params);
            System.out.println("Charge Completed");
            paymentGatewayResponse.setTransactionId(charge.getId());
            paymentGatewayResponse.setPaymentSuccess(true);
            return charge;
        } catch (StripeException e) {
            e.printStackTrace();
            paymentGatewayResponse.setTransactionId("NA");
            paymentGatewayResponse.setPaymentSuccess(false);
        }
        return null;
    }

    private void updateCart(PaymentGatewayResponse paymentGatewayResponse, String userId, List<String> courses, String orderId){
        Table table = dynamoDB.getTable(DYNAMODB_TABLE_NAME_CART);
        ItemCollection<QueryOutcome> items = table.getIndex("userId-index").query(new QuerySpec()
                                    .withKeyConditionExpression("userId = :v_user_id")
                                    .withFilterExpression("cartStatus = :v_cart_status")
                                    .withValueMap(new ValueMap().withString(":v_user_id", userId)
                                                                .withString(":v_cart_status", STATUS_SAVE)));

        String cartStatus = STATUS_FAILED;
        if(paymentGatewayResponse.isPaymentSuccess()){
            cartStatus = STATUS_SUCCESS;
        }

        int count = 0;
        for(String courseId: courses){
            for(Item item: items){
                String cartCourseId = (String) item.get("courseId");
                if(courseId.equals(cartCourseId)){
                    UpdateItemSpec updateItemSpec = new UpdateItemSpec()
                            .withPrimaryKey("userId", userId,
                                    "courseId", courseId)
                            .withUpdateExpression("set #s = :cartStatus, " +
                                    "#o = :orderId, " +
                                    "#t = :transactionId")
                            .withNameMap(new NameMap()
                                    .with("#s", "cartStatus")
                                    .with("#o", "orderId")
                                    .with("#t", "transactionId"))
                            .withValueMap(new ValueMap()
                                    .withString(":cartStatus", cartStatus)
                                    .withString(":orderId", orderId)
                                    .withString(":transactionId", paymentGatewayResponse.getTransactionId()));
                    table.updateItem(updateItemSpec);
                    count++;
                }
            }
        }

        if(count == courses.size()){
            paymentGatewayResponse.setCartUpdated(true);
        }
    }

    private void updateOrderDetails(PaymentGatewayResponse paymentGatewayResponse, Charge charge, String orderId){
        String orderDetailsJson = "NA";
        if(Objects.nonNull(charge))
            orderDetailsJson = charge.toJson();

        String orderStatus = STATUS_FAILED;
        if(paymentGatewayResponse.isPaymentSuccess())
            orderStatus = STATUS_SUCCESS;

        dynamoDB.getTable(DYNAMODB_TABLE_NAME_ORDER).putItem(new PutItemSpec().withItem(new Item()
                .withString("id", orderId)
                .withString("transactionId", paymentGatewayResponse.getTransactionId())
                .withString("orderStatus", orderStatus)
                .withString("orderDetails", orderDetailsJson)));
        paymentGatewayResponse.setOrderUpdated(true);
    }

    private void enrollCourses(PaymentGatewayResponse paymentGatewayResponse, PaymentGatewayRequest request){
        List<PutItemSpec> putItemSpecs = new ArrayList<>();
        BigDecimal totalAmount = new BigDecimal(0);

        for(String courseId: request.getCourses()){
            Item item = dynamoDB.getTable(DYNAMODB_TABLE_NAME_COURSE_RESOURCE).getItem("id", courseId);
            totalAmount = totalAmount.add((BigDecimal) item.get("discountedPrice"));

            PutItemSpec putItemSpec = new PutItemSpec();
            putItemSpec.withItem(new Item()
                    .withString("userId", request.getUserId())
                    .withString("courseId", courseId)
                    .withNumber("percentageCompleted", 0));
            putItemSpecs.add(putItemSpec);
        }

        BigDecimal requestAmount = request.getAmount().divide(new BigDecimal(100));

        if(totalAmount.equals(requestAmount)){
            for(PutItemSpec spec: putItemSpecs){
                dynamoDB.getTable(DYNAMODB_TABLE_NAME_USER_COURSE).putItem(spec);
            }
            paymentGatewayResponse.setEnrollmentSuccess(true);
        }else{
            paymentGatewayResponse.setEnrollmentSuccess(false);
        }
    }
}
