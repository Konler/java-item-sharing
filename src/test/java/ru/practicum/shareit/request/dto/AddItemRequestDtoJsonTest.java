package ru.practicum.shareit.request.dto;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.json.JsonTest;
import org.springframework.boot.test.json.JacksonTester;
import org.springframework.boot.test.json.JsonContent;

import java.io.IOException;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

import static org.assertj.core.api.Assertions.assertThat;

@JsonTest
class AddItemRequestDtoJsonTest {
    @Autowired
    private JacksonTester<AddItemRequestDto> json;
    private static final DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd'T'HH:mm:ss.SSS");

    @Test
    void testItemRequestDto() throws IOException {
        LocalDateTime now = LocalDateTime.now();
        AddItemRequestDto addItemRequestDto = AddItemRequestDto.builder()
                .id(1L)
                .requestor(1L)
                .description("item description")
                .created(now)
                .build();
        JsonContent<AddItemRequestDto> result = json.write(addItemRequestDto);

        assertThat(result).extractingJsonPathNumberValue("$.id").isEqualTo(1);
        assertThat(result).extractingJsonPathNumberValue("$.requestor").isEqualTo(1);
        assertThat(result).extractingJsonPathStringValue("$.description").isEqualTo(addItemRequestDto.getDescription());
        assertThat(result).extractingJsonPathStringValue("$.created").isEqualTo(now.format(formatter));
        assertThat(result).hasEmptyJsonPathValue("$.items");
    }
}