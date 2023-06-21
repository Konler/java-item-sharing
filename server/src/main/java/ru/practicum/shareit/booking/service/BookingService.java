package ru.practicum.shareit.booking.service;

import ru.practicum.shareit.booking.dto.BookingCreatDto;
import ru.practicum.shareit.booking.dto.BookingDto;

import java.util.List;

public interface BookingService {
    BookingDto addBooking(BookingCreatDto bookingCreationDto, Long userId);

    BookingDto updateBooking(Long bookingId, Long userId, Boolean approved);

    BookingDto getBookingById(Long bookingId, Long userId);

    List<BookingDto> getAllUserBookings(Long bookerId, String state, Integer from, Integer size);

    List<BookingDto> getOwnerAllItemBookings(Long userId, String state, Integer from, Integer size);
}