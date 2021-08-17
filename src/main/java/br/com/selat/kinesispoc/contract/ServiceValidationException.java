package br.com.selat.kinesispoc.contract;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

@ResponseStatus(value=HttpStatus.BAD_REQUEST)
public class ServiceValidationException extends RuntimeException{

    public ServiceValidationException(String message){
        super(message);
    }

    public ServiceValidationException(Throwable e){
        super(e);
    }

    public ServiceValidationException(String message, Throwable e){
        super(message, e);
    }
}
