package com.brilliant.academe.handlers;

import com.amazonaws.regions.Regions;
import com.amazonaws.services.dynamodbv2.AmazonDynamoDB;
import com.amazonaws.services.dynamodbv2.AmazonDynamoDBClientBuilder;
import com.amazonaws.services.dynamodbv2.document.*;
import com.amazonaws.services.dynamodbv2.document.spec.QuerySpec;
import com.amazonaws.services.dynamodbv2.document.spec.UpdateItemSpec;
import com.amazonaws.services.dynamodbv2.document.utils.NameMap;
import com.amazonaws.services.dynamodbv2.document.utils.ValueMap;
import com.amazonaws.services.lambda.runtime.Context;
import com.amazonaws.services.lambda.runtime.RequestHandler;
import com.amazonaws.services.lambda.runtime.events.DynamodbEvent;

import java.math.BigDecimal;
import java.util.Iterator;
import java.util.Objects;

public class TriggerCourseRatingHandler implements RequestHandler<DynamodbEvent, Void> {

    private DynamoDB dynamoDb;
    private String DYNAMODB_TABLE_NAME_USER_COURSE = "ba_user_course";
    private String DYNAMODB_TABLE_NAME_COURSE = "ba_course";
    private Regions REGION = Regions.US_EAST_1;

    @Override
    public Void handleRequest(DynamodbEvent dynamodbEvent, Context context) {
        this.initDynamoDbClient();
        return getData(dynamodbEvent);
    }

    private void initDynamoDbClient() {
        AmazonDynamoDB client = AmazonDynamoDBClientBuilder.standard()
                .withRegion(REGION)
                .build();
        this.dynamoDb = new DynamoDB(client);
    }

    private Void getData(DynamodbEvent dynamodbEvent) {
        for (DynamodbEvent.DynamodbStreamRecord record : dynamodbEvent.getRecords()) {
            if (record == null) {
                continue;
            }
            System.out.println(record);
            System.out.println("Event Type:"+ record.getEventName());
            if(Objects.nonNull(record) && Objects.nonNull(record.getEventName())){
                if(record.getEventName().equals("MODIFY")){
                    String oldRating = record.getDynamodb().getOldImage().get("courseRating").getN();
                    String newRating = record.getDynamodb().getNewImage().get("courseRating").getN();
                    System.out.println("Old Rating:"+ oldRating + ", New Rating:"+ newRating);

                    if(!oldRating.equals(newRating)){
                        System.out.println("Update Required");
                        String courseId = record.getDynamodb().getKeys().get("courseId").getS();
                        System.out.println("Course Id"+ courseId);
                        BigDecimal averageRating = calculateAverageRating(courseId);
                        updateRatingsInCourseTable(courseId, averageRating);
                    }else{
                        System.out.println("Update Not Required");
                    }
                }
            }
        }
        return null;
    }

    private BigDecimal calculateAverageRating(String courseId){
        Table table = dynamoDb.getTable(DYNAMODB_TABLE_NAME_USER_COURSE);
        Index index = table.getIndex("courseId-index");
        QuerySpec querySpec = new QuerySpec()
                .withKeyConditionExpression("courseId = :v_course_id")
                .withValueMap(new ValueMap()
                        .withString(":v_course_id", courseId));

        ItemCollection<QueryOutcome> items = index.query(querySpec);
        Iterator<Item> iter = items.iterator();
        BigDecimal courseRating = new BigDecimal(0);
        int count = 0;
        while (iter.hasNext()) {
            count++;
            courseRating = courseRating.add((BigDecimal) iter.next().get("courseRating"));
        }
        BigDecimal averageRating = courseRating.divide(new BigDecimal(count));
        System.out.println("Total Course Count:"+ count + ", Total Rating:" + courseRating + ", Average Rating:"+ averageRating);
        return averageRating;
    }

    private void updateRatingsInCourseTable(String courseId, BigDecimal averageRating){
        Table table = dynamoDb.getTable(DYNAMODB_TABLE_NAME_COURSE);
        Index index = table.getIndex("id-index");
        QuerySpec querySpec = new QuerySpec()
                .withKeyConditionExpression("id = :v_course_id")
                .withValueMap(new ValueMap()
                        .withString(":v_course_id", courseId));

        ItemCollection<QueryOutcome> items = index.query(querySpec);

        for(Item item: items){
            String updateCourseId = (String) item.get("id");
            String updateCategoryId = (String) item.get("categoryId");
            System.out.println("Update Course Id:" +updateCourseId + ", Update Category Id:"+ updateCategoryId);

            UpdateItemSpec updateItemSpec = new UpdateItemSpec()
                    .withPrimaryKey("id", updateCourseId, "categoryId", updateCategoryId)
                    .withUpdateExpression("set #p = :courseRating")
                    .withNameMap(new NameMap().with("#p", "courseRating"))
                    .withValueMap(new ValueMap().withNumber(":courseRating", averageRating));
            table.updateItem(updateItemSpec);
        }
    }
}
