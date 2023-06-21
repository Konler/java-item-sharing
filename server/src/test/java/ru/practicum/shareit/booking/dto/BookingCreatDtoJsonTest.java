package ru.practicum.shareit.booking.dto;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.json.JsonTest;
import org.springframework.boot.test.json.JacksonTester;
import org.springframework.boot.test.json.JsonContent;
import ru.practicum.shareit.booking.Status;

import java.io.IOException;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

import static org.assertj.core.api.Assertions.assertThat;

@JsonTest
class BookingCreatDtoJsonTest {
    @Autowired
    private JacksonTester<BookingCreatDto> json;
    private static final DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd'T'HH:mm:ss");

    @Test
    void testBookingCreationDto() throws IOException {
        LocalDateTime now = LocalDateTime.now();
        BookingCreatDto bookingCreationDto = BookingCreatDto.builder()
                .id(1L)
                .status(Status.WAITING)
                .start(now.plusDays(2))
                .end(now.plusDays(3))
                .itemId(1L)
                .build();
        JsonContent<BookingCreatDto> result = json.write(bookingCreationDto);

        assertThat(result).extractingJsonPathNumberValue("$.id").isEqualTo(1);
        assertThat(result).extractingJsonPathStringValue("$.status").isEqualTo("WAITING");
        assertThat(result).extractingJsonPathStringValue("$.start").isEqualTo((now.plusDays(2)).format(formatter));
        assertThat(result).extractingJsonPathStringValue("$.end").isEqualTo((now.plusDays(3)).format(formatter));
        assertThat(result).extractingJsonPathNumberValue("$.itemId").isEqualTo(1);
    }
}