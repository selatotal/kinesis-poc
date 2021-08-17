package br.com.selat.kinesispoc.service;

import br.com.selat.kinesispoc.contract.ServiceValidationException;
import br.com.selat.kinesispoc.contract.User;
import br.com.selat.kinesispoc.controller.UserRestController;
import com.google.gson.Gson;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Optional;
import java.util.UUID;

import static org.springframework.util.StringUtils.hasText;

@Service
public class UserService {

    private static final Logger logger = LoggerFactory.getLogger(UserService.class);
    private static final String ALLOWED_GENDER = "MFO";
    private static final String KINESIS_STREAM_NAME = Optional.ofNullable(System.getenv("KINESIS_STREAM_NAME")).orElseThrow(() -> new ServiceValidationException("KINESIS_STREAM_NAME environment variable not defined"));

    private final Gson gson = new Gson();
    private final KinesisPublisherService kinesisPublisherService;

    @Autowired
    public UserService(KinesisPublisherService kinesisPublisherService) {
        this.kinesisPublisherService = kinesisPublisherService;
    }

    public User save(User input) {
        if (!hasText(input.getName())){
            throw new ServiceValidationException("invalid name");
        }
        if (!ALLOWED_GENDER.contains(""+input.getGender())){
            throw new ServiceValidationException("invalid gender");
        }
        input.setId(UUID.randomUUID());
        logger.info("Saved User: {}", input);
        String message = gson.toJson(input);
        kinesisPublisherService.publishMessage(KINESIS_STREAM_NAME, input.getId().toString(), message);
        return input;
    }
}
