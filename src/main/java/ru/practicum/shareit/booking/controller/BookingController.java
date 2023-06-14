package ru.practicum.shareit.booking.controller;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;
import ru.practicum.shareit.booking.dto.BookingCreatDto;
import ru.practicum.shareit.booking.dto.BookingDto;
import ru.practicum.shareit.booking.service.BookingService;
import ru.practicum.shareit.messages.LogMessages;

import javax.validation.Valid;
import javax.validation.constraints.Min;
import javax.validation.constraints.PositiveOrZero;
import java.util.List;

@Slf4j
@RestController
@RequiredArgsConstructor
@RequestMapping(path = "/bookings")
public class BookingController {
    private final BookingService bookingService;
    private static final String X_SHARER_USER_ID = "X-Sharer-User-Id";

    @PostMapping
    public BookingDto addBooking(@Valid @RequestBody BookingCreatDto bookingCreatDto,
                                 @RequestHeader(X_SHARER_USER_ID) Long userId) {
        log.info(LogMessages.BOOKING_REQUEST.toString(), userId, bookingCreatDto);
        return bookingService.addBooking(bookingCreatDto, userId);
    }

    @PatchMapping("/{bookingId}")
    public BookingDto updateBooking(@RequestHeader(X_SHARER_USER_ID) Long userId,
                                    @PathVariable Long bookingId,
                                    @RequestParam Boolean approved) {
        log.info(LogMessages.BOOKING_RENEWAL_REQUEST.toString(), bookingId);
        return bookingService.updateBooking(bookingId, userId, approved);
    }

    @GetMapping("/{bookingId}")
    public BookingDto getBookingById(@RequestHeader(X_SHARER_USER_ID) Long userId,
                                     @PathVariable Long bookingId) {
        log.info(LogMessages.GET_BOOKING_REQUEST.toString(), userId, bookingId);
        return bookingService.getBookingById(bookingId, userId);
    }

    @GetMapping
    public List<BookingDto> getAllUserBookings(@RequestHeader("X-Sharer-User-Id") Long bookerId,
                                               @RequestParam(defaultValue = "ALL") String state,
                                               @RequestParam(name = "from", defaultValue = "0") @PositiveOrZero Integer from,
                                               @RequestParam(name = "size", defaultValue = "10") @Min(1) Integer size) {
        log.info(LogMessages.GET_BOOKING_REQUEST_STATUS.toString(), bookerId, state);
        return bookingService.getAllUserBookings(bookerId, state, from, size);
    }

    @GetMapping("/owner")
    public List<BookingDto> getOwnerAllItemBookings(@RequestHeader("X-Sharer-User-Id") Long ownerId,
                                                    @RequestParam(defaultValue = "ALL") String state,
                                                    @RequestParam(name = "from", defaultValue = "0") @PositiveOrZero Integer from,
                                                    @RequestParam(name = "size", defaultValue = "10") @Min(1) Integer size) {
        log.info(LogMessages.GET_ALL_BOOKING_REQUEST_STATUS.toString(), ownerId, state);
        return bookingService.getOwnerAllItemBookings(ownerId, state, from, size);
    }
}