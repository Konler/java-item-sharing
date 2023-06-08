package ru.practicum.shareit.request.dto;

import ru.practicum.shareit.messages.ValidationMessages;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.PastOrPresent;
import java.time.LocalDateTime;

/**
 * TODO Sprint add-item-requests.
 */
public class ItemRequestDto {
    private Long id;
    @NotBlank(message = ValidationMessages.EMPTY_DESCRIPTION)
    private String description;
    @NotNull
    private Long requestor;
    @PastOrPresent(message = ValidationMessages.REQUEST_CREATED_DATA)
    private LocalDateTime created;
}