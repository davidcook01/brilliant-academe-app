package com.brilliant.academe.handlers;

import com.amazonaws.services.dynamodbv2.AmazonDynamoDB;
import com.amazonaws.services.dynamodbv2.AmazonDynamoDBClientBuilder;
import com.amazonaws.services.dynamodbv2.document.DynamoDB;
import com.amazonaws.services.dynamodbv2.document.Item;
import com.amazonaws.services.dynamodbv2.document.ItemCollection;
import com.amazonaws.services.dynamodbv2.document.QueryOutcome;
import com.amazonaws.services.lambda.runtime.Context;
import com.amazonaws.services.lambda.runtime.RequestHandler;
import com.brilliant.academe.domain.cart.CartInfo;
import com.brilliant.academe.domain.cart.CourseCartRequest;
import com.brilliant.academe.domain.cart.CourseCartResponse;
import com.brilliant.academe.util.CommonUtils;
import com.fasterxml.jackson.databind.ObjectMapper;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Objects;

import static com.brilliant.academe.constant.Constant.REGION;

public class GetCartHandler implements RequestHandler<CourseCartRequest, CourseCartResponse> {

    private DynamoDB dynamoDB;

    @Override
    public CourseCartResponse handleRequest(CourseCartRequest request, Context context) {
        initDynamoDbClient();
        return execute(request);
    }

    private void initDynamoDbClient() {
        AmazonDynamoDB client = AmazonDynamoDBClientBuilder.standard()
                .withRegion(REGION)
                .build();
        dynamoDB = new DynamoDB(client);
    }

    public CourseCartResponse execute(CourseCartRequest request){
        String userId = CommonUtils.getUserFromToken(request.getToken());
        ItemCollection<QueryOutcome> userCourseCartItems = CommonUtils.getUserInprocessCart(userId, dynamoDB);

        List<String> addedCoursesInCart = new ArrayList();
        String orderId = null;
        for(Item item: userCourseCartItems){
            addedCoursesInCart.add((String) item.get("courseId"));
            orderId = (String) item.get("orderId");
        }

        List<CartInfo> cartInfos = new ArrayList<>();

        if(Objects.nonNull(addedCoursesInCart) && addedCoursesInCart.size() > 0){
            String[] attributes = {"id", "courseName", "coverImage",
                    "discountedPrice", "instructorId", "instructorName", "price", "skuId"};
            List<String> attributedToGet = Arrays.asList(attributes);
            List<Item> courseItemsList = CommonUtils.getCoursesList(dynamoDB, addedCoursesInCart, attributedToGet);
            ObjectMapper objectMapper = new ObjectMapper();
            for(Item courseItem: courseItemsList){
                try {
                    cartInfos.add(objectMapper.readValue(courseItem.toJSON(), CartInfo.class));
                }catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }
        final String fOrderId = orderId;
        cartInfos.forEach(c->c.setOrderId(fOrderId));

        CourseCartResponse response = new CourseCartResponse();
        response.setCartDetails(cartInfos);
        return response;
    }
}
