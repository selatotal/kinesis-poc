package br.com.selat.kinesispoc.service;

import br.com.selat.kinesispoc.contract.ServiceValidationException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import software.amazon.awssdk.core.SdkBytes;
import software.amazon.awssdk.services.kinesis.KinesisAsyncClient;
import software.amazon.awssdk.services.kinesis.model.PutRecordRequest;
import software.amazon.awssdk.services.kinesis.model.PutRecordResponse;

import java.nio.charset.Charset;
import java.util.concurrent.ExecutionException;

@Service
public class KinesisPublisherService {

    private static final Logger logger = LoggerFactory.getLogger(KinesisPublisherService.class);
    private final KinesisAsyncClient kinesisAsyncClient;

    @Autowired
    public KinesisPublisherService(KinesisAsyncClient kinesisAsyncClient) {
        this.kinesisAsyncClient = kinesisAsyncClient;
    }

    public void publishMessage(String streamName, String key, String message){
        PutRecordRequest request = PutRecordRequest
                .builder()
                .partitionKey(key)
                .streamName(streamName)
                .data(SdkBytes.fromString(message, Charset.defaultCharset()))
                .build();
        try {
            PutRecordResponse response = kinesisAsyncClient.putRecord(request).get();
            logger.info("Message {} - {} published in shard {}", message, response.sequenceNumber(), response.shardId());
        } catch (InterruptedException | ExecutionException e) {
            logger.error("Error publishing to kinesis", e);
            throw new ServiceValidationException("Error publishing to kinesis", e);
        }
    }
}
