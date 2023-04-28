package ru.practicum.shareit.exceptions;

public class DublicateException extends RuntimeException {
    public DublicateException(String message) {
        super(message);
    }
}
