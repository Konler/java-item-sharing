package ru.practicum.shareit.item.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import ru.practicum.shareit.booking.dto.BookingShortDto;
import ru.practicum.shareit.messages.ValidationMessages;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import java.util.List;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ItemDto {
    private Long id;
    @NotBlank(message = ValidationMessages.EMPTY_NAME)
    private String name;
    @NotBlank(message = ValidationMessages.EMPTY_DESCRIPTION)
    private String description;
    @NotNull(message = ValidationMessages.AVAILABLE_NULL)
    private Boolean available;
    private Long ownerId;
    private Long requestId;
    private List<CommentDto> comments;
    private BookingShortDto lastBooking;
    private BookingShortDto nextBooking;
}