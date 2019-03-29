package com.brilliant.academe.handlers;

import com.amazonaws.services.dynamodbv2.AmazonDynamoDB;
import com.amazonaws.services.dynamodbv2.AmazonDynamoDBClientBuilder;
import com.amazonaws.services.dynamodbv2.document.*;
import com.amazonaws.services.dynamodbv2.document.spec.BatchGetItemSpec;
import com.amazonaws.services.dynamodbv2.document.spec.QuerySpec;
import com.amazonaws.services.dynamodbv2.document.utils.ValueMap;
import com.amazonaws.services.lambda.runtime.Context;
import com.amazonaws.services.lambda.runtime.RequestHandler;
import com.brilliant.academe.domain.cart.CartInfo;
import com.brilliant.academe.domain.cart.CourseCartRequest;
import com.brilliant.academe.domain.cart.CourseCartResponse;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Objects;

import static com.brilliant.academe.constant.Constant.*;

public class GetCartHandler implements RequestHandler<CourseCartRequest, CourseCartResponse> {

    private DynamoDB dynamoDB;

    @Override
    public CourseCartResponse handleRequest(CourseCartRequest request, Context context) {
        this.initDynamoDbClient();
        return execute(request);
    }

    private void initDynamoDbClient() {
        AmazonDynamoDB client = AmazonDynamoDBClientBuilder.standard()
                .withRegion(REGION)
                .build();
        dynamoDB = new DynamoDB(client);
    }

    public CourseCartResponse execute(CourseCartRequest request){
        Index index = dynamoDB.getTable(DYNAMODB_TABLE_NAME_CART).getIndex("userId-index");
        QuerySpec querySpec = new QuerySpec()
                .withKeyConditionExpression("userId = :v_user_id")
                .withFilterExpression("cartStatus = :v_cart_status")
                .withValueMap(new ValueMap()
                        .withString(":v_user_id", request.getUserId())
                        .withString(":v_cart_status", STATUS_SAVE));

        ItemCollection<QueryOutcome> userCourseCartItems = index.query(querySpec);
        List<String> addedCoursesInCart = new ArrayList();
        for(Item item: userCourseCartItems){
            addedCoursesInCart.add((String) item.get("courseId"));
        }

        List<CartInfo> cartInfos = new ArrayList<>();

        String[] attributes = {"id", "courseName", "coverImage",
                "discountedPrice", "instructorId", "instructorName", "price"};

        List<String> attributedToGet = Arrays.asList(attributes);

        if(Objects.nonNull(addedCoursesInCart) && addedCoursesInCart.size() > 0){
            BatchGetItemOutcome batchGetItemOutcome = dynamoDB.batchGetItem(new BatchGetItemSpec()
                    .withTableKeyAndAttributes(new TableKeysAndAttributes(DYNAMODB_TABLE_NAME_COURSE_RESOURCE)
                            .withHashOnlyKeys("id", addedCoursesInCart.toArray())
                            .withAttributeNames(attributedToGet)
                            .withConsistentRead(true)));
            List<Item> courseItemsList = batchGetItemOutcome.getTableItems().get(DYNAMODB_TABLE_NAME_COURSE_RESOURCE);
            ObjectMapper objectMapper = new ObjectMapper();
            for(Item courseItem: courseItemsList){
                try {
                    cartInfos.add(objectMapper.readValue(courseItem.toJSON(), CartInfo.class));
                }catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }

        CourseCartResponse response = new CourseCartResponse();
        response.setCartDetails(cartInfos);
        try {
            System.out.println(new ObjectMapper().writeValueAsString(response));
        } catch (JsonProcessingException e) {
            e.printStackTrace();
        }
        return response;
    }
}
