package br.com.selat.kinesispoc.consumer;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import software.amazon.kinesis.processor.ShardRecordProcessor;
import software.amazon.kinesis.processor.ShardRecordProcessorFactory;

@Service
public class UserStreamProcessorFactory implements ShardRecordProcessorFactory {

    private final UserStreamProcessor userStreamProcessor;

    @Autowired
    public UserStreamProcessorFactory(UserStreamProcessor userStreamProcessor) {
        this.userStreamProcessor = userStreamProcessor;
    }

    @Override
    public ShardRecordProcessor shardRecordProcessor() {
        return userStreamProcessor;
    }
}
