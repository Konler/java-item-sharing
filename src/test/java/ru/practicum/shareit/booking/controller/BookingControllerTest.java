package ru.practicum.shareit.booking.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import ru.practicum.shareit.booking.BookingController;
import ru.practicum.shareit.booking.Status;
import ru.practicum.shareit.booking.dto.BookingCreatDto;
import ru.practicum.shareit.booking.dto.BookingDto;
import ru.practicum.shareit.booking.service.BookingService;
import ru.practicum.shareit.exceptions.InvalidIdException;
import ru.practicum.shareit.exceptions.NotFoundException;
import ru.practicum.shareit.exceptions.ValidationException;
import ru.practicum.shareit.item.dto.ItemDtoShort;
import ru.practicum.shareit.user.dto.UserDtoShort;

import java.nio.charset.StandardCharsets;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@AutoConfigureMockMvc
@WebMvcTest(BookingController.class)
class BookingControllerTest {
    private static final DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd'T'HH:mm:ss");
    @Autowired
    private MockMvc mockMvc;
    @Autowired
    private ObjectMapper objectMapper;
    @MockBean
    private BookingService bookingService;
    private BookingCreatDto bookingCreationDto;
    private BookingDto bookingDto;

    @BeforeEach
    void setUp() {
        bookingCreationDto = BookingCreatDto.builder()
                .start(LocalDateTime.now().plusDays(1))
                .end(LocalDateTime.now().plusDays(2))
                .itemId(1L)
                .build();
        bookingDto = BookingDto.builder()
                .itemId(bookingCreationDto.getItemId())
                .start(bookingCreationDto.getStart())
                .end(bookingCreationDto.getEnd())
                .item(new ItemDtoShort(1L, "Item"))
                .booker(new UserDtoShort(1L, "User"))
                .status(Status.WAITING)
                .build();
    }

    @Test
    public void shouldNotCreateBookingWithEmptyStart() throws Exception {
        bookingCreationDto.setStart(null);
        when(bookingService.addBooking(any(), anyLong())).thenThrow(new ValidationException(""));
        mockMvc.perform(post("/bookings")
                        .header("X-Sharer-User-Id", 1)
                        .content(objectMapper.writeValueAsString(bookingDto))
                        .characterEncoding(StandardCharsets.UTF_8)
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isBadRequest());
    }

    @Test
    public void shouldNotCreateBookingWithInvalidStart() throws Exception {
        bookingCreationDto.setStart(LocalDateTime.of(2020, 12, 12, 13, 15));
        when(bookingService.addBooking(any(), anyLong()))
                .thenThrow(new ValidationException("Дата начала бронирования должна быть в настоящем или будущем"));
        mockMvc.perform(post("/bookings")
                        .header("X-Sharer-User-Id", 1)
                        .content(objectMapper.writeValueAsString(bookingDto))
                        .characterEncoding(StandardCharsets.UTF_8)
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isBadRequest());
    }

    @Test
    public void shouldNotCreateBookingWithInvalidEnd() throws Exception {
        bookingCreationDto.setStart(LocalDateTime.of(2020, 12, 12, 13, 15));
        when(bookingService.addBooking(any(), anyLong()))
                .thenThrow(new ValidationException("Дата конца бронирования должна быть в настоящем или будущем"));
        mockMvc.perform(post("/bookings")
                        .header("X-Sharer-User-Id", 1)
                        .content(objectMapper.writeValueAsString(bookingDto))
                        .characterEncoding(StandardCharsets.UTF_8)
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isBadRequest());
    }

    @Test
    public void shouldNotCreateBookingWithEmptyEnd() throws Exception {
        bookingCreationDto.setEnd(null);
        when(bookingService.addBooking(any(), anyLong())).thenThrow(new ValidationException(""));
        mockMvc.perform(post("/bookings")
                        .header("X-Sharer-User-Id", 1)
                        .content(objectMapper.writeValueAsString(bookingDto))
                        .characterEncoding(StandardCharsets.UTF_8)
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isBadRequest());
    }

    @Test
    public void shouldNotCreateBookingWithEmptyItemId() throws Exception {
        bookingCreationDto.setItemId(null);
        when(bookingService.addBooking(any(), anyLong())).thenThrow(new ValidationException(""));
        mockMvc.perform(post("/bookings")
                        .header("X-Sharer-User-Id", 1)
                        .content(objectMapper.writeValueAsString(bookingDto))
                        .characterEncoding(StandardCharsets.UTF_8)
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isBadRequest());
    }

    @Test
    public void shouldNotRenewalBookingByNotOwner() throws Exception {
        bookingCreationDto.setStatus(Status.APPROVED);
        when(bookingService.updateBooking(anyLong(), anyLong(), anyBoolean()))
                .thenThrow(new InvalidIdException("Нет прав на подтверждение бронирования. " +
                        "Пользователь {} не является собственником предмета"));
        mockMvc.perform(patch("/bookings/{bookingId}", 1L)
                        .header("X-Sharer-User-Id", 3)
                        .param("approved", "true")
                        .content(objectMapper.writeValueAsString(bookingDto))
                        .characterEncoding(StandardCharsets.UTF_8)
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isNotFound());
    }

    @Test
    void shouldNotGetBookingByNotFoundId() throws Exception {
        when(bookingService.getBookingById(anyLong(), anyLong())).thenThrow(new NotFoundException("Объект не найден {}"));
        mockMvc.perform(get("/bookings/{bookingId}", 99L)
                        .header("X-Sharer-User-Id", 1))
                .andExpect(status().isNotFound());
    }

    @Test
    void shouldNotGetBookingByIdNotFoundUser() throws Exception {
        when(bookingService.getBookingById(anyLong(), anyLong())).thenThrow(new NotFoundException("Объект не найден {}"));
        mockMvc.perform(get("/bookings/{bookingId}", 1L)
                        .header("X-Sharer-User-Id", 99))
                .andExpect(status().isNotFound());
    }
}