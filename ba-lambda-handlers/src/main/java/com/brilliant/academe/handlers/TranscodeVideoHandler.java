package com.brilliant.academe.handlers;

import com.amazonaws.services.dynamodbv2.AmazonDynamoDB;
import com.amazonaws.services.dynamodbv2.AmazonDynamoDBClientBuilder;
import com.amazonaws.services.dynamodbv2.document.DynamoDB;
import com.amazonaws.services.dynamodbv2.document.Item;
import com.amazonaws.services.elastictranscoder.AmazonElasticTranscoder;
import com.amazonaws.services.elastictranscoder.AmazonElasticTranscoderClient;
import com.amazonaws.services.elastictranscoder.model.CreateJobOutput;
import com.amazonaws.services.elastictranscoder.model.CreateJobRequest;
import com.amazonaws.services.elastictranscoder.model.JobInput;
import com.amazonaws.services.lambda.runtime.Context;
import com.amazonaws.services.lambda.runtime.RequestHandler;
import com.amazonaws.services.lambda.runtime.events.S3Event;
import com.brilliant.academe.util.CommonUtils;

import java.util.ArrayList;
import java.util.List;

import static com.brilliant.academe.constant.Constant.REGION;

public class TranscodeVideoHandler implements RequestHandler<S3Event, String> {

    private AmazonElasticTranscoder amazonElasticTranscoder;
    private DynamoDB dynamoDB;
    private Item item;
    private String[] attributes = {"transcoderPipelineId", "transcoderPresetId" , "transcoderSegmentDuration"};

    @Override
    public String handleRequest(S3Event event, Context context) {
        initElasticTranscoder();
        initDynamoDbClient();
        initConfig();
        return execute(event, context);
    }

    private void initElasticTranscoder(){
        amazonElasticTranscoder = AmazonElasticTranscoderClient.builder().build();
    }

    private void initDynamoDbClient() {
        AmazonDynamoDB client = AmazonDynamoDBClientBuilder.standard()
                .withRegion(REGION)
                .build();
        dynamoDB = new DynamoDB(client);
    }

    private void initConfig(){
        item = CommonUtils.getConfigInfo(dynamoDB, attributes);
    }

    public String execute(S3Event event, Context context){
        System.out.println("Json: "+CommonUtils.convertObjectToJson(event));
        context.getLogger().log("Received event: " + event);
        String bucket = event.getRecords().get(0).getS3().getBucket().getName();
        String key = event.getRecords().get(0).getS3().getObject().getKey();
        System.out.println("Bucket:"+ bucket);
        System.out.println("Key:"+key);
        try {
            key = key.replace("+", " ");
            JobInput input = new JobInput().withKey(key);
            String[] output = key.split("/");
            String outputKey = output[output.length-1].split("\\.")[0];
            System.out.println(outputKey);
            System.out.println("Output Key:"+ outputKey);
            String folderPrefix = "content/"+output[1];

            List<CreateJobOutput> outputs = new ArrayList<CreateJobOutput>();

            String transcoderPresetId = (String) item.get("transcoderPresetId");
            String transcoderSegmentDuration = (String) item.get("transcoderSegmentDuration");

            String jobOutputKey = outputKey + "_hls";
            System.out.println("Job Output Key:" + jobOutputKey);
            CreateJobOutput output1 = new CreateJobOutput().withKey(jobOutputKey)
                    .withPresetId(transcoderPresetId)
                    .withSegmentDuration(transcoderSegmentDuration);
            outputs.add(output1);

            String transcoderPipelineId = (String) item.get("transcoderPipelineId");
            CreateJobRequest createJobRequest = new CreateJobRequest().withPipelineId(transcoderPipelineId)
                    .withOutputKeyPrefix(folderPrefix + "/").withInput(input).withOutputs(outputs);
            System.out.println("Transcoder Job Completed");

            return amazonElasticTranscoder.createJob(createJobRequest).getJob().getId();

        } catch (Exception e) {
            e.printStackTrace();
            context.getLogger().log(String.format("Error getting object %s from bucket %s. Make sure they exist and"
                    + " your bucket is in the same region as this function.", key, bucket));
            try {
                throw e;
            } catch (Exception e1) {
                e1.printStackTrace();
            }
        }
        return null;
    }
}