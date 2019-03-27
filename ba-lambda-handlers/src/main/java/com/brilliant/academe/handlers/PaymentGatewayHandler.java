package com.brilliant.academe.handlers;

import com.amazonaws.regions.Regions;
import com.amazonaws.services.dynamodbv2.AmazonDynamoDB;
import com.amazonaws.services.dynamodbv2.AmazonDynamoDBClientBuilder;
import com.amazonaws.services.dynamodbv2.document.DynamoDB;
import com.amazonaws.services.dynamodbv2.document.Item;
import com.amazonaws.services.dynamodbv2.document.Table;
import com.amazonaws.services.dynamodbv2.document.spec.PutItemSpec;
import com.amazonaws.services.lambda.runtime.Context;
import com.amazonaws.services.lambda.runtime.RequestHandler;
import com.brilliant.academe.domain.payment.PaymentGatewayRequest;
import com.brilliant.academe.domain.payment.PaymentGatewayResponse;
import com.stripe.Stripe;
import com.stripe.exception.StripeException;
import com.stripe.model.Charge;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class PaymentGatewayHandler implements RequestHandler<PaymentGatewayRequest, PaymentGatewayResponse> {

    private DynamoDB dynamoDb;
    private String DYNAMODB_TABLE_NAME_COURSE_RESOURCE = "ba_course_resource";
    private String DYNAMODB_TABLE_NAME_USER_COURSE = "ba_user_course";
    private Regions REGION = Regions.US_EAST_1;
    private String STRIPE_SECRET_KEY = "sk_test_UTifZqEZPT8SyQBTrWCSwfAn00xySX7uI7";

    private void initDynamoDbClient() {
        AmazonDynamoDB client = AmazonDynamoDBClientBuilder.standard()
                .withRegion(REGION)
                .build();
        this.dynamoDb = new DynamoDB(client);
    }

    @Override
    public PaymentGatewayResponse handleRequest(PaymentGatewayRequest request, Context context) {
        System.out.println("Token: "+request.getToken() + ", User Id:"+ request.getUserId() + ", Courses:"+ request.getCourses()
                + "Payment Type:"+ request.getPaymentType() + "Amount:"+ request.getAmount() + "Currency:"+ request.getCurrency());
        initDynamoDbClient();
        return execute(request);
    }

    private PaymentGatewayResponse execute(PaymentGatewayRequest request){
        Stripe.apiKey = STRIPE_SECRET_KEY;

        Map<String, Object> params = new HashMap<>();
        params.put("source", request.getToken());
        params.put("amount", request.getAmount());
        params.put("currency", request.getCurrency());
        params.put("description", "Charge");

        PaymentGatewayResponse paymentGatewayResponse = new PaymentGatewayResponse();
        initiateCharge(paymentGatewayResponse, params);
        if(paymentGatewayResponse.isPaymentSuccess())
            enrollCourses(paymentGatewayResponse, request);
        return paymentGatewayResponse;
    }

    private void initiateCharge(PaymentGatewayResponse paymentGatewayResponse, Map params){
        try {
            Charge charge = Charge.create(params);
            System.out.println("Charge Completed");
            paymentGatewayResponse.setResult(charge);
            paymentGatewayResponse.setPaymentSuccess(true);
        } catch (StripeException e) {
            e.printStackTrace();
            paymentGatewayResponse.setPaymentSuccess(false);
        }
    }

    private void enrollCourses(PaymentGatewayResponse paymentGatewayResponse, PaymentGatewayRequest request){
        Table table = dynamoDb.getTable(DYNAMODB_TABLE_NAME_COURSE_RESOURCE);
        List<PutItemSpec> putItemSpecs = new ArrayList<>();
        BigDecimal totalAmount = new BigDecimal(0);

        for(String courseId: request.getCourses()){
            Item item = table.getItem("id", courseId);
            totalAmount = totalAmount.add((BigDecimal) item.get("discountedPrice"));

            PutItemSpec putItemSpec = new PutItemSpec();
            BigDecimal courseDuration = (BigDecimal) item.get("courseDuration");
            putItemSpec.withItem(new Item()
                    .withString("userId", request.getUserId())
                    .withString("courseId", (String) item.get("id"))
                    .withString("courseName", (String) item.get("courseName"))
                    .withString("description", (String) item.get("description"))
                    .withString("coverImage", (String) item.get("coverImage"))
                    .withString("instructorId", (String) item.get("instructorId"))
                    .withString("instructorName", (String) item.get("instructorName"))
                    .withNumber("percentageCompleted", 0)
                    .withNumber("courseDuration", courseDuration.floatValue()));
            putItemSpecs.add(putItemSpec);
        }

        BigDecimal requestAmount = request.getAmount().divide(new BigDecimal(100));

        if(totalAmount.equals(requestAmount)){
            Table courseTable = this.dynamoDb.getTable(DYNAMODB_TABLE_NAME_USER_COURSE);
            for(PutItemSpec spec: putItemSpecs){
                courseTable.putItem(spec);
            }
            paymentGatewayResponse.setEnrollmentSuccess(true);
        }else{
            paymentGatewayResponse.setEnrollmentSuccess(false);
        }
    }
}
