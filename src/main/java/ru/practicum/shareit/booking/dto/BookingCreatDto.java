package ru.practicum.shareit.booking.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import ru.practicum.shareit.booking.Status;
import ru.practicum.shareit.messages.ValidationMessages;

import javax.validation.constraints.FutureOrPresent;
import javax.validation.constraints.NotNull;
import java.time.LocalDateTime;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class BookingCreatDto {
    private Long id;
    @NotNull
    @FutureOrPresent(message = ValidationMessages.START_DATA)

    private LocalDateTime start;
    @NotNull
    @FutureOrPresent(message = ValidationMessages.END_DATA)

    private LocalDateTime end;
    @NotNull
    private Long itemId;
    private Status status = Status.WAITING;

    public void setStatus(Status approved) {
        this.status = approved;
    }
}
