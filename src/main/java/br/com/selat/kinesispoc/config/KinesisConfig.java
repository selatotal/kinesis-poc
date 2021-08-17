package br.com.selat.kinesispoc.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import software.amazon.awssdk.regions.Region;
import software.amazon.awssdk.services.cloudwatch.CloudWatchAsyncClient;
import software.amazon.awssdk.services.dynamodb.DynamoDbAsyncClient;
import software.amazon.awssdk.services.kinesis.KinesisAsyncClient;

import java.util.Optional;

@Configuration
public class KinesisConfig {

    @Bean
    public DynamoDbAsyncClient dynamoDbAsyncClient(){
        return DynamoDbAsyncClient.builder().region(getRegion()).build();
    }

    @Bean
    public CloudWatchAsyncClient cloudWatchAsyncClient(){
        return CloudWatchAsyncClient.builder().region(getRegion()).build();
    }

    @Bean
    public KinesisAsyncClient kinesisAsyncClient(){
        return KinesisAsyncClient.builder().region(getRegion()).build();
    }

    private Region getRegion(){
        String awsRegion = Optional.ofNullable(System.getenv("AWS_REGION")).orElse("us-east-1");
        return Region.of(awsRegion);
    }
}
