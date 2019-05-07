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
import com.google.gson.Gson;

import java.util.ArrayList;
import java.util.List;

import static com.brilliant.academe.constant.Constant.REGION;

public class TranscodeVideoHandler implements RequestHandler<S3Event, String> {

    private AmazonElasticTranscoder amazonElasticTranscoder;
    private DynamoDB dynamoDB;
    private Item item;
    private String[] attributes = {"transcoderPipelineId"};

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
        System.out.println("Json: "+new Gson().toJson(event));
        context.getLogger().log("Received event: " + event);
        // Get the object from the event and show its content type
        String bucket = event.getRecords().get(0).getS3().getBucket().getName();
        String key = event.getRecords().get(0).getS3().getObject().getKey();
        System.out.println("Bucket:"+ bucket);
        System.out.println("Key:"+key);
        try {
            key = key.replace("+", " ");
            JobInput input = new JobInput().withKey(key);
            /*String[] fileName = key.split("\\.");
            String outputKey = fileName[fileName.length];
            //String outputKey = key.substring(0, key.lastIndexOf('/'));*/

            String[] output = key.split("/");
            String outputKey = output[output.length-1].split("\\.")[0];
            System.out.println(outputKey);
            System.out.println("Output Key:"+ outputKey);
            String folderPrefix = "videos/"+output[1];

            // Setup the job output using the provided input key to generate an output key.
            List<CreateJobOutput> outputs = new ArrayList<CreateJobOutput>();

            CreateJobOutput output1 = new CreateJobOutput().withKey(outputKey + "_1080p")
                    .withPresetId("1550770439115-c9e06e")
                    .withSegmentDuration("60");
            outputs.add(output1);

            /*CreateJobOutput output2 = new CreateJobOutput().withKey(outputKey + "_720p")
                    .withPresetId("1550770524254-cgtysk")
                    .withSegmentDuration("60");
            outputs.add(output2);

            CreateJobOutput output3 = new CreateJobOutput().withKey(outputKey + "_360p")
                    .withPresetId("1550770612124-5h1nnk").
                            withSegmentDuration("60");
            outputs.add(output3);*/

            // Create a job on the specified pipeline and return the job ID.
            String transcoderPipelineId = (String) item.get("transcoderPipelineId");
            CreateJobRequest createJobRequest = new CreateJobRequest().withPipelineId(transcoderPipelineId)
                    .withOutputKeyPrefix(folderPrefix + "/").withInput(input).withOutputs(outputs);
            System.out.println("Transcoder Job Completed");

            return amazonElasticTranscoder.createJob(createJobRequest).getJob().getId();

            // return contentType;
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