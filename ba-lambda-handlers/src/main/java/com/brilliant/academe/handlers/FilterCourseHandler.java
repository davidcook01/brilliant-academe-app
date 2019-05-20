package com.brilliant.academe.handlers;

import com.amazonaws.services.dynamodbv2.AmazonDynamoDB;
import com.amazonaws.services.dynamodbv2.AmazonDynamoDBClientBuilder;
import com.amazonaws.services.dynamodbv2.document.DynamoDB;
import com.amazonaws.services.dynamodbv2.document.Index;
import com.amazonaws.services.dynamodbv2.document.Item;
import com.amazonaws.services.dynamodbv2.document.ItemCollection;
import com.amazonaws.services.dynamodbv2.document.spec.QuerySpec;
import com.amazonaws.services.dynamodbv2.document.spec.ScanSpec;
import com.amazonaws.services.dynamodbv2.document.utils.ValueMap;
import com.amazonaws.services.lambda.runtime.Context;
import com.amazonaws.services.lambda.runtime.RequestHandler;
import com.brilliant.academe.domain.filter.FilterCourse;
import com.brilliant.academe.domain.filter.FilterCourseInfo;
import com.brilliant.academe.domain.filter.FilterCourseRequest;
import com.brilliant.academe.domain.filter.FilterCourseResponse;
import com.fasterxml.jackson.databind.ObjectMapper;

import java.io.IOException;
import java.util.*;
import java.util.stream.Collectors;

import static com.brilliant.academe.constant.Constant.*;

public class FilterCourseHandler implements RequestHandler<FilterCourseRequest, FilterCourseResponse> {

    private DynamoDB dynamoDB;

    @Override
    public FilterCourseResponse handleRequest(FilterCourseRequest filterCourseRequest, Context context) {
        this.initDynamoDbClient();
        return execute(filterCourseRequest);
    }

    private void initDynamoDbClient() {
        AmazonDynamoDB client = AmazonDynamoDBClientBuilder.standard()
                .withRegion(REGION)
                .build();
        dynamoDB = new DynamoDB(client);
    }

    public FilterCourseResponse execute(FilterCourseRequest filterCourseRequest){
        FilterCourseResponse response = new FilterCourseResponse();
        List<FilterCourseInfo> filterCourseInfos = new ArrayList<>();

        if(Objects.nonNull(filterCourseRequest) && Objects.nonNull(filterCourseRequest.getFilter())){
            String filterName = filterCourseRequest.getFilter().getFilterName();
            String filterValue = filterCourseRequest.getFilter().getFilterValue();
            String type = filterCourseRequest.getType();

            ItemCollection items = null;
            ObjectMapper objectMapper = new ObjectMapper();

            if(type.equals("query")){
                Index index = dynamoDB.getTable(DYNAMODB_TABLE_NAME_COURSE).getIndex(filterName+"-index");

                ValueMap valueMap = new ValueMap();
                valueMap.withString(":v_"+filterName, filterValue);
                StringBuilder filterExpression = new StringBuilder();
                int count = 0;
                int filterCount = 0;
                boolean isFilterPresent = false;
                if(Objects.nonNull(filterCourseRequest.getFilters()) && filterCourseRequest.getFilters().size() > 0){
                    isFilterPresent = true;
                }

                if(isFilterPresent){
                    filterExpression.append("(");
                    filterCount = filterCourseRequest.getFilters().size();
                    for(FilterCourse filterCourse: filterCourseRequest.getFilters()){
                        count++;
                        valueMap.withString(":v_"+filterCourse.getFilterName()+"", filterCourse.getFilterValue());
                        filterExpression.append(filterCourse.getFilterName() + " = :v_"+ filterCourse.getFilterName());
                        if(count != filterCount)
                            filterExpression.append(" AND ");
                    }
                    filterExpression.append(")");
                }
                System.out.println("FilterExpression:" + filterExpression);
                QuerySpec querySpec = new QuerySpec();
                String expression = ""+ filterName+" = :v_"+filterName;
                if(isFilterPresent){
                    querySpec.withKeyConditionExpression(expression)
                            .withFilterExpression(filterExpression.toString())
                            .withValueMap(valueMap);
                }else{
                    querySpec.withKeyConditionExpression(expression)
                            .withValueMap(valueMap);
                }

                items = index.query(querySpec);
            }else{
                ScanSpec scanSpec = null;
                if(filterName.equals("courseName")){
                    scanSpec = new ScanSpec()
                            .withFilterExpression("contains( tags , :v_"+filterName+")")
                            .withValueMap(new ValueMap()
                                    .withString(":v_"+filterName+"",filterValue));
                }else if(filterName.equals("courseRating")){
                    Float rating = new Float(filterValue);
                    scanSpec = new ScanSpec()
                            .withFilterExpression(""+filterName+" > :rating")
                            .withValueMap(new ValueMap()
                                    .withNumber(":rating", rating));
                }
                System.out.println(scanSpec.getFilterExpression());
                items = dynamoDB.getTable(DYNAMODB_TABLE_NAME_COURSE_RESOURCE).scan(scanSpec);
            }

            Iterator<Item> iter = items.iterator();
            while (iter.hasNext()) {
                try {
                    filterCourseInfos.add(objectMapper.readValue(iter.next().toJSON(), FilterCourseInfo.class));
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
            response.setCourses(filterUniqueAndReviewedCourses(filterCourseInfos));
        }
        return response;
    }

    private List<FilterCourseInfo> filterUniqueAndReviewedCourses(List<FilterCourseInfo> filterCourseInfos){
        Set<FilterCourseInfo> uniqueCourses = new HashSet<>(filterCourseInfos);
        List<FilterCourseInfo> courses = new ArrayList<>(uniqueCourses);
        return courses.stream().filter(c->c.getReviewed().equals(STATUS_YES)).collect(Collectors.toList());
    }
}
