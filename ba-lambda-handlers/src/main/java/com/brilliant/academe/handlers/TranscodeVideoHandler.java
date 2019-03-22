package com.brilliant.academe.handlers;

import com.amazonaws.services.elastictranscoder.AmazonElasticTranscoder;
import com.amazonaws.services.elastictranscoder.AmazonElasticTranscoderClient;
import com.amazonaws.services.elastictranscoder.model.CreateJobOutput;
import com.amazonaws.services.elastictranscoder.model.CreateJobRequest;
import com.amazonaws.services.elastictranscoder.model.JobInput;
import com.amazonaws.services.lambda.runtime.Context;
import com.amazonaws.services.lambda.runtime.RequestHandler;
import com.amazonaws.services.lambda.runtime.events.S3Event;
import com.amazonaws.services.s3.AmazonS3;
import com.amazonaws.services.s3.AmazonS3ClientBuilder;

import java.util.ArrayList;
import java.util.List;

public class TranscodeVideoHandler implements RequestHandler<S3Event, String> {

    private AmazonS3 s3 = AmazonS3ClientBuilder.standard().build();
    private AmazonElasticTranscoder amazonElasticTranscoder = AmazonElasticTranscoderClient.builder().build();
    private static final String PIPELINE_ID = "1551221588927-5zpuyx";

    @Override
    public String handleRequest(S3Event event, Context context) {
        context.getLogger().log("Received event: " + event);

        // Get the object from the event and show its content type
        String bucket = event.getRecords().get(0).getS3().getBucket().getName();
        String key = event.getRecords().get(0).getS3().getObject().getKey();
        try {
            key = key.replace("+", " ");
            JobInput input = new JobInput().withKey(key);
            String[] fileName = key.split("\\.");
            String outputKey = fileName[0];

            // Setup the job output using the provided input key to generate an output key.
            List<CreateJobOutput> outputs = new ArrayList<CreateJobOutput>();

            CreateJobOutput output1 = new CreateJobOutput().withKey(outputKey + "_1080p")
                    .withPresetId("1550770439115-c9e06e")
                    .withSegmentDuration("60");
            outputs.add(output1);

            CreateJobOutput output2 = new CreateJobOutput().withKey(outputKey + "_720p")
                    .withPresetId("1550770524254-cgtysk")
                            .withSegmentDuration("60");
            outputs.add(output2);

            CreateJobOutput output3 = new CreateJobOutput().withKey(outputKey + "_360p")
                    .withPresetId("1550770612124-5h1nnk").
                            withSegmentDuration("60");
            outputs.add(output3);

            // Create a job on the specified pipeline and return the job ID.
            CreateJobRequest createJobRequest = new CreateJobRequest().withPipelineId(PIPELINE_ID)
                    .withOutputKeyPrefix(outputKey + "/").withInput(input).withOutputs(outputs);

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