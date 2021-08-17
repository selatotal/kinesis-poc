package br.com.selat.kinesispoc.consumer;

import br.com.selat.kinesispoc.contract.User;
import com.google.gson.Gson;
import com.google.gson.JsonSyntaxException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import software.amazon.awssdk.core.SdkBytes;
import software.amazon.kinesis.exceptions.InvalidStateException;
import software.amazon.kinesis.exceptions.ShutdownException;
import software.amazon.kinesis.exceptions.ThrottlingException;
import software.amazon.kinesis.lifecycle.events.*;
import software.amazon.kinesis.processor.RecordProcessorCheckpointer;
import software.amazon.kinesis.processor.ShardRecordProcessor;
import software.amazon.kinesis.retrieval.KinesisClientRecord;

@Service
public class UserStreamProcessor implements ShardRecordProcessor {

    private static final Logger logger = LoggerFactory.getLogger(UserStreamProcessor.class);
    private final Gson gson = new Gson();
    private String kinesisShardId;

    @Override
    public void initialize(InitializationInput initializationInput) {
        kinesisShardId = initializationInput.shardId();
        logger.info("Initializing record processor for shard: {}", kinesisShardId);
        logger.info("Initializing @ Sequence: {}", initializationInput.extendedSequenceNumber());
    }

    @Override
    public void processRecords(ProcessRecordsInput processRecordsInput) {
        logger.debug("Processing {} records", processRecordsInput.records().size());
        processRecordsInput.records().forEach(this::processRecord);
        checkpoint(processRecordsInput.checkpointer());
    }

    private void processRecord(KinesisClientRecord kinesisClientRecord) {
        String partitionKey = kinesisClientRecord.partitionKey();
        String message = SdkBytes.fromByteBuffer(kinesisClientRecord.data()).asUtf8String();
        logger.debug("Processing key {} value {}", partitionKey, message);
        process(message);
    }


    @Override
    public void leaseLost(LeaseLostInput leaseLostInput) {
        logger.info("Lost lease, so terminating");
    }

    @Override
    public void shardEnded(ShardEndedInput shardEndedInput) {
        logger.debug("Reach shard end checkpointing");
        try {
            shardEndedInput.checkpointer().checkpoint();
        } catch (InvalidStateException | ShutdownException e) {
            logger.error("Exception while checkpointing at shard end. Giving up", e);
        }
    }

    @Override
    public void shutdownRequested(ShutdownRequestedInput shutdownRequestedInput) {
        logger.info("Scheduler is shutting down, checkpointing");
        checkpoint(shutdownRequestedInput.checkpointer());
    }

    private void checkpoint(RecordProcessorCheckpointer checkpointer) {
        logger.debug("Checkpoint shard {}", kinesisShardId);
        try {
            checkpointer.checkpoint();
        } catch (InvalidStateException e) {
            logger.error("Cannot save checkpoint to DynamoDB table used by the Amazon Kinesis Client Library.", e);
        } catch (ThrottlingException e) {
            logger.error("Caught throttling exception, skipping checkpoint.", e);
        } catch (ShutdownException e) {
            logger.error("Caught shutdown exception, skipping checkpoint.", e);
        }
    }

    public void process(String message) {
        logger.info("Received: {}", message);

        try {
            User userMessage = gson.fromJson(message, User.class);
            if (userMessage == null) {
                logger.error("The payload received could not be converted to User");
                return;
            }
            logger.info("Received User: {}", userMessage);
        } catch (JsonSyntaxException e){
            logger.error("Incorrect payload: {}", message);
        } catch (Exception e) {
            logger.error("Generic Error: {} - {}", e.getMessage(), message);
        }
    }

}
