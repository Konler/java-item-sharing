package ru.practicum.shareit.exceptions.handlerExceptions;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import ru.practicum.shareit.exceptions.ValidationException;
import ru.practicum.shareit.user.util.ItemErrorResponce;

@RestControllerAdvice
public class ControllerAdvice {

    @ExceptionHandler
    public static ResponseEntity<ItemErrorResponce> handleExceptionItemErrorResponce(ValidationException e) {
        ItemErrorResponce response = new ItemErrorResponce("Ошибка валидации", System.currentTimeMillis());
        return new ResponseEntity<>(response, HttpStatus.BAD_REQUEST);
    }
}
