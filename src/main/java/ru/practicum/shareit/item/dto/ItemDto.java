package ru.practicum.shareit.item.dto;

import lombok.Builder;
import lombok.Data;
import ru.practicum.shareit.booking.dto.BookingDtoLittle;
import ru.practicum.shareit.messages.ValidationMessages;
import ru.practicum.shareit.request.model.ItemRequest;
import ru.practicum.shareit.user.model.User;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import java.util.List;

/**
 * TODO Sprint add-controllers.
 */
@Data
@Builder
public class ItemDto {
    private Long id;
    @NotBlank(message = ValidationMessages.EMPTY_NAME)
    private String name;
    @NotBlank(message = ValidationMessages.EMPTY_DESCRIPTION)
    private String description;
    @NotNull(message = ValidationMessages.AVAILABLE_NULL)
    private Boolean available;
    private User owner;
    private ItemRequest request;
    private List<CommentDto> comments;
    private BookingDtoLittle lastBooking;
    private BookingDtoLittle nextBooking;
}