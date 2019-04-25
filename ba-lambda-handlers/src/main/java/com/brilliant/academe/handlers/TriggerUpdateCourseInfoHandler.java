package com.brilliant.academe.handlers;

import com.amazonaws.services.dynamodbv2.AmazonDynamoDB;
import com.amazonaws.services.dynamodbv2.AmazonDynamoDBClientBuilder;
import com.amazonaws.services.dynamodbv2.document.DynamoDB;
import com.amazonaws.services.dynamodbv2.document.Item;
import com.amazonaws.services.dynamodbv2.document.ItemCollection;
import com.amazonaws.services.dynamodbv2.document.QueryOutcome;
import com.amazonaws.services.dynamodbv2.document.spec.UpdateItemSpec;
import com.amazonaws.services.dynamodbv2.document.utils.NameMap;
import com.amazonaws.services.dynamodbv2.document.utils.ValueMap;
import com.amazonaws.services.lambda.runtime.Context;
import com.amazonaws.services.lambda.runtime.RequestHandler;
import com.amazonaws.services.lambda.runtime.events.DynamodbEvent;
import com.brilliant.academe.util.CommonUtils;

import java.math.BigDecimal;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.Objects;

import static com.brilliant.academe.constant.Constant.*;

public class TriggerUpdateCourseInfoHandler implements RequestHandler<DynamodbEvent, Void> {

    private DynamoDB dynamoDB;

    @Override
    public Void handleRequest(DynamodbEvent dynamodbEvent, Context context) {
        this.initDynamoDbClient();
        return execute(dynamodbEvent);
    }

    private void initDynamoDbClient() {
        AmazonDynamoDB client = AmazonDynamoDBClientBuilder.standard()
                .withRegion(REGION)
                .build();
        dynamoDB = new DynamoDB(client);
    }

    public Void execute(DynamodbEvent dynamodbEvent) {
        for (DynamodbEvent.DynamodbStreamRecord record : dynamodbEvent.getRecords()) {
            if (record == null) {
                continue;
            }
            System.out.println("Record:"+ record+ ", Event Type:"+ record.getEventName());
            if(Objects.nonNull(record) && Objects.nonNull(record.getEventName())){

                if (record.getEventName().equals(EVENT_INSERT)) {
                    String courseId = null;
                    if(Objects.nonNull(record.getDynamodb().getNewImage().get("courseId"))){
                        courseId = record.getDynamodb().getNewImage().get("courseId").getS();
                    }
                    if(Objects.nonNull(courseId)){
                        Integer totalEnrolled = getUserEnrollmentCount(courseId);
                        updateEnrolledUsersCountInCourseTable(courseId, totalEnrolled);
                    }
                }

                if(record.getEventName().equals(EVENT_MODIFY)){
                    String oldRating = "";
                    String newRating = "";
                    if(Objects.nonNull(record.getDynamodb().getOldImage().get("courseRating"))){
                        oldRating = record.getDynamodb().getOldImage().get("courseRating").getN();
                    }

                    if(Objects.nonNull(record.getDynamodb().getNewImage().get("courseRating"))){
                        newRating = record.getDynamodb().getNewImage().get("courseRating").getN();
                    }

                    System.out.println("Old Rating:"+ oldRating + ", New Rating:"+ newRating);

                    if(!oldRating.equals(newRating) && !newRating.equals("")){
                        System.out.println("Update Required");
                        String courseId = record.getDynamodb().getKeys().get("courseId").getS();
                        System.out.println("Course Id"+ courseId);
                        Map<String, Object> result = calculateAverageRating(courseId);
                        Float averageRating = (Float) result.get("averageRating");
                        Integer totalRating = (Integer) result.get("totalRating");
                        updateRatingsInCourseTable(courseId, averageRating, totalRating);
                    }else{
                        System.out.println("Update Not Required");
                    }
                }
           }
        }
        return null;
    }

    private Integer getUserEnrollmentCount(String courseId){
        ItemCollection<QueryOutcome> items = CommonUtils.getUsersByCourseId(courseId, dynamoDB);
        Iterator<Item> iter = items.iterator();
        int enrolledCount = 0;
        while (iter.hasNext()) {
            iter.next();
            enrolledCount++;
        }
        return enrolledCount;
    }

    private void updateEnrolledUsersCountInCourseTable(String courseId, Integer totalEnrolled){
        ItemCollection<QueryOutcome> items = CommonUtils.getCoursesByCourseIdInMaster(courseId, dynamoDB);
        for(Item item: items){
            String updateCourseId = (String) item.get("id");
            String updateCategoryId = (String) item.get("categoryId");
            System.out.println("Update Course Id:" +updateCourseId + ", Update Category Id:"+ updateCategoryId);

            UpdateItemSpec updateItemSpec = new UpdateItemSpec()
                    .withPrimaryKey("id", updateCourseId, "categoryId", updateCategoryId)
                    .withUpdateExpression("set #p = :totalEnrolled")
                    .withNameMap(new NameMap().with("#p", "totalEnrolled"))
                    .withValueMap(new ValueMap().withNumber(":totalEnrolled", totalEnrolled));
            dynamoDB.getTable(DYNAMODB_TABLE_NAME_COURSE).updateItem(updateItemSpec);
        }

        UpdateItemSpec updateItemSpec = new UpdateItemSpec()
                .withPrimaryKey("id", courseId)
                .withUpdateExpression("set #p = :totalEnrolled")
                .withNameMap(new NameMap().with("#p", "totalEnrolled"))
                .withValueMap(new ValueMap().withNumber(":totalEnrolled", totalEnrolled));
        dynamoDB.getTable(DYNAMODB_TABLE_NAME_COURSE_RESOURCE).updateItem(updateItemSpec);
    }

    private Map<String, Object> calculateAverageRating(String courseId){
        ItemCollection<QueryOutcome> items = CommonUtils.getUsersByCourseId(courseId, dynamoDB);
        Iterator<Item> iter = items.iterator();
        Float courseRating = 0F;
        int count = 0;
        while (iter.hasNext()) {
            Item obj = iter.next();
            if(Objects.nonNull(obj.get("courseRating"))){
                BigDecimal rating = (BigDecimal) obj.get("courseRating");
                if(Objects.nonNull(rating)) {
                    courseRating = courseRating + rating.floatValue();
                    count++;
                }
            }
        }
        Float averageRating = courseRating/(new Float(count));
        System.out.println("Total Course Count:"+ count + ", " +
                "Total Rating:" + courseRating + ", " +
                "Average Rating:"+ averageRating);

        Map<String, Object> result = new HashMap<>();
        result.put("averageRating", averageRating);
        result.put("totalRating", Integer.valueOf(count));
        return result;
    }

    private void updateRatingsInCourseTable(String courseId, Float averageRating, Integer totalRating){
        ItemCollection<QueryOutcome> items = CommonUtils.getCoursesByCourseIdInMaster(courseId, dynamoDB);
        for(Item item: items){
            String updateCourseId = (String) item.get("id");
            String updateCategoryId = (String) item.get("categoryId");
            System.out.println("Update Course Id:" +updateCourseId + ", Update Category Id:"+ updateCategoryId);

            UpdateItemSpec updateItemSpec = new UpdateItemSpec()
                    .withPrimaryKey("id", updateCourseId, "categoryId", updateCategoryId)
                    .withUpdateExpression("set courseRating = :r, totalRating=:t")
                    .withValueMap(new ValueMap()
                            .withNumber(":r", averageRating)
                            .withNumber(":t", totalRating));

             dynamoDB.getTable(DYNAMODB_TABLE_NAME_COURSE).updateItem(updateItemSpec);
        }

        UpdateItemSpec updateItemSpec = new UpdateItemSpec()
                .withPrimaryKey("id", courseId)
                .withUpdateExpression("set courseRating = :r, totalRating=:t")
                .withValueMap(new ValueMap()
                        .withNumber(":r", averageRating)
                        .withNumber(":t", totalRating));
        dynamoDB.getTable(DYNAMODB_TABLE_NAME_COURSE_RESOURCE).updateItem(updateItemSpec);
    }
}
