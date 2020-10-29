package com.example.bankservice.exception;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

@ResponseStatus(value = HttpStatus.BAD_REQUEST)
public class InvalidTransactionAmountException extends Exception{

    public InvalidTransactionAmountException(){
        super("Invalid transaction amount");
    }
}
