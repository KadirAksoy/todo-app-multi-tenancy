package com.kadiraksoy.todoapp.exception;

public class TodoNotFoundException extends BaseException {
    public TodoNotFoundException(String message) {
        super(message);
    }
}
