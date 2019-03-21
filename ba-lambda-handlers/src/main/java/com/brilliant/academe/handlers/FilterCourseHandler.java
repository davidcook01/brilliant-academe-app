package com.brilliant.academe.handlers;

import com.amazonaws.regions.Regions;
import com.amazonaws.services.dynamodbv2.AmazonDynamoDB;
import com.amazonaws.services.dynamodbv2.AmazonDynamoDBClientBuilder;
import com.amazonaws.services.dynamodbv2.document.*;
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
import java.util.function.Function;
import java.util.stream.Collectors;

public class FilterCourseHandler implements RequestHandler<FilterCourseRequest, FilterCourseResponse> {

    private DynamoDB dynamoDb;
    private String DYNAMODB_TABLE_NAME_COURSE = "ba_course";
    private Regions REGION = Regions.US_EAST_1;

    public FilterCourseResponse handleRequest(FilterCourseRequest filterCourseRequest, Context context) {
        this.initDynamoDbClient();
        return getData(filterCourseRequest);
    }

    private void initDynamoDbClient() {
        AmazonDynamoDB client = AmazonDynamoDBClientBuilder.standard()
                .withRegion(REGION)
                .build();
        this.dynamoDb = new DynamoDB(client);
    }

    private FilterCourseResponse getData(FilterCourseRequest filterCourseRequest){
        FilterCourseResponse response = new FilterCourseResponse();
        List<FilterCourseInfo> filterCourseInfos = new ArrayList<>();

        if(Objects.nonNull(filterCourseRequest) && Objects.nonNull(filterCourseRequest.getFilter())){
            String filterName = filterCourseRequest.getFilter().getFilterName();
            String filterValue = filterCourseRequest.getFilter().getFilterValue();
            String type = filterCourseRequest.getType();

            ItemCollection items = null;
            ObjectMapper objectMapper = new ObjectMapper();

            if(type.equals("query")){
                Table table = dynamoDb.getTable(DYNAMODB_TABLE_NAME_COURSE);
                Index index = table.getIndex(filterName+"-index");

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
                Table table = dynamoDb.getTable(DYNAMODB_TABLE_NAME_COURSE);

                ScanSpec scanSpec = new ScanSpec()
                        .withFilterExpression("contains( "+filterName+" , :v_"+filterName+")")
                        .withValueMap(new ValueMap()
                                .withString(":v_"+filterName+"",filterValue));

                items = table.scan(scanSpec);
            }

            Iterator<Item> iter = items.iterator();
            while (iter.hasNext()) {
                try {
                    filterCourseInfos.add(objectMapper.readValue(iter.next().toJSON(), FilterCourseInfo.class));
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }

            List<FilterCourseInfo> distinctCourses = filterCourseInfos.stream()
                    .collect(Collectors.collectingAndThen(
                            Collectors.toMap(c -> Arrays.asList(c.getCourseId()),
                                    Function.identity(), (a, b) -> a, LinkedHashMap::new),
                            m -> new ArrayList<>(m.values())));

            response.setCourses(distinctCourses);
        }
        return response;
    }
}
