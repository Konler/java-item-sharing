package ru.practicum.shareit.booking;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import ru.practicum.shareit.booking.dto.BookItemRequestDto;
import ru.practicum.shareit.booking.dto.BookingState;
import ru.practicum.shareit.messages.LogMessages;

import javax.validation.Valid;
import javax.validation.constraints.Min;
import javax.validation.constraints.PositiveOrZero;

@Slf4j
@Validated
@Controller
@RequestMapping(path = "/bookings")
@RequiredArgsConstructor
public class BookingController {
	private final BookingClient bookingClient;

	@GetMapping
	public ResponseEntity<Object> getBookings(@RequestHeader("X-Sharer-User-Id") long userId,
											  @RequestParam(name = "state", defaultValue = "all") String stateParam,
											  @RequestParam(name = "from", defaultValue = "0") @PositiveOrZero Integer from,
											  @RequestParam(name = "size", defaultValue = "10") @Min(1) Integer size) {
		BookingState state = BookingState.from(stateParam)
				.orElseThrow(() -> new IllegalArgumentException("Unknown state: " + stateParam));
		log.info(LogMessages.GET_BOOKINGS.toString(), stateParam, userId, from, size);
		return bookingClient.getBookings(userId, state, from, size);
	}

	@PostMapping
	public ResponseEntity<Object> bookItem(@RequestHeader("X-Sharer-User-Id") long userId,
										   @Valid @RequestBody BookItemRequestDto requestDto) {
		log.info(LogMessages.BOOK_ITEM.toString(), requestDto, userId);
		return bookingClient.bookItem(userId, requestDto);
	}

	@GetMapping("/{bookingId}")
	public ResponseEntity<Object> getBooking(@RequestHeader("X-Sharer-User-Id") long userId,
											 @PathVariable Long bookingId) {
		log.info(LogMessages.GET_BOOKING.toString(), bookingId, userId);
		return bookingClient.getBooking(userId, bookingId);
	}

	@GetMapping("/owner")
	public ResponseEntity<Object> getAllByOwner(@RequestHeader(name = "X-Sharer-User-Id") long userId,
												@RequestParam(name = "state", defaultValue = "all") String stateParam,
												@RequestParam(name = "from", defaultValue = "0") @PositiveOrZero Integer from,
												@RequestParam(name = "size", defaultValue = "10") @Min(1) Integer size) {
		BookingState state = BookingState.from(stateParam)
				.orElseThrow(() -> new IllegalArgumentException("Unknown state: " + stateParam));
		log.info(LogMessages.GET_ALL_BY_OWNER.toString(), stateParam, userId, from, size);
		return bookingClient.getAllByOwner(userId, state, from, size);
	}

	@PatchMapping("/{bookingId}")
	public ResponseEntity<Object> renewalBooking(@RequestHeader(value = "X-Sharer-User-Id") long userId,
												 @PathVariable long bookingId,
												 @RequestParam Boolean approved) {
		log.info(LogMessages.RENEWAL_BOOKING.toString(), bookingId, userId);
		return bookingClient.renewalBooking(userId, bookingId, approved);
	}
}