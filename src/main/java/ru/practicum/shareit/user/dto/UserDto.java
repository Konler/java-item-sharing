package ru.practicum.shareit.user.dto;

import lombok.Builder;
import lombok.Data;
import ru.practicum.shareit.messages.ValidationMessages;

import javax.validation.constraints.Email;
import javax.validation.constraints.NotBlank;

@Data
@Builder
public class UserDto {
    private Long id;
    @NotBlank(message = ValidationMessages.EMPTY_NAME)
    private String name;
    @Email(message = ValidationMessages.INCORRECT_EMAIL)
    @NotBlank(message = ValidationMessages.EMPTY_EMAIL)
    private String email;
}