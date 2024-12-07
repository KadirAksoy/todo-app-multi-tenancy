package com.kadiraksoy.todoapp.exception;

public class UserNotFoundException extends BaseException{
    public UserNotFoundException(String message) {
        super(message);
    }
}
