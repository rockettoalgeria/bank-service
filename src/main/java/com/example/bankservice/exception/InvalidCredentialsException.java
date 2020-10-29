package com.example.bankservice.exception;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

@ResponseStatus(value = HttpStatus.OK)
public class InvalidCredentialsException extends Exception{

    public InvalidCredentialsException(){
        super("Invalid credentials");
    }
}
