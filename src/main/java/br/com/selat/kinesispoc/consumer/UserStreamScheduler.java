package br.com.selat.kinesispoc.consumer;

import br.com.selat.kinesispoc.contract.ServiceValidationException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import software.amazon.awssdk.services.cloudwatch.CloudWatchAsyncClient;
import software.amazon.awssdk.services.dynamodb.DynamoDbAsyncClient;
import software.amazon.awssdk.services.kinesis.KinesisAsyncClient;
import software.amazon.kinesis.common.ConfigsBuilder;
import software.amazon.kinesis.common.InitialPositionInStream;
import software.amazon.kinesis.common.InitialPositionInStreamExtended;
import software.amazon.kinesis.coordinator.Scheduler;
import software.amazon.kinesis.retrieval.RetrievalConfig;
import software.amazon.kinesis.retrieval.polling.PollingConfig;

import java.util.Optional;
import java.util.UUID;

@Service
public class UserStreamScheduler {

    private static final String KINESIS_STREAM_NAME = Optional.ofNullable(System.getenv("KINESIS_STREAM_NAME")).orElseThrow(() -> new ServiceValidationException("KINESIS_STREAM_NAME environment variable not defined"));
    private static final String KINESIS_APPLICATION_NAME = Optional.ofNullable(System.getenv("KINESIS_APPLICATION_NAME")).orElse("kinesispoc");

    @Autowired
    public UserStreamScheduler(
            KinesisAsyncClient kinesisAsyncClient,
            DynamoDbAsyncClient dynamoDbAsyncClient,
            CloudWatchAsyncClient cloudWatchAsyncClient,
            UserStreamProcessorFactory userStreamProcessorFactory){

        ConfigsBuilder configsBuilder = new ConfigsBuilder(
                KINESIS_STREAM_NAME,
                KINESIS_APPLICATION_NAME,
                kinesisAsyncClient,
                dynamoDbAsyncClient,
                cloudWatchAsyncClient,
                UUID.randomUUID().toString(),
                userStreamProcessorFactory);

        RetrievalConfig retrievalConfig = configsBuilder.retrievalConfig().retrievalSpecificConfig(new PollingConfig(KINESIS_STREAM_NAME, kinesisAsyncClient));
        retrievalConfig.initialPositionInStreamExtended(InitialPositionInStreamExtended.newInitialPosition(InitialPositionInStream.TRIM_HORIZON));

        Scheduler scheduler = new Scheduler(
                configsBuilder.checkpointConfig(),
                configsBuilder.coordinatorConfig(),
                configsBuilder.leaseManagementConfig(),
                configsBuilder.lifecycleConfig(),
                configsBuilder.metricsConfig(),
                configsBuilder.processorConfig(),
                retrievalConfig);

        Thread schedulerThread = new Thread(scheduler);
        schedulerThread.setDaemon(true);
        schedulerThread.start();
    }

}
