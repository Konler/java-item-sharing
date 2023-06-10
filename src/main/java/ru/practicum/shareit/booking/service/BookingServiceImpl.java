package ru.practicum.shareit.booking.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import ru.practicum.shareit.booking.BookingState;
import ru.practicum.shareit.booking.Status;
import ru.practicum.shareit.booking.dto.BookingCreatDto;
import ru.practicum.shareit.booking.dto.BookingDto;
import ru.practicum.shareit.booking.mapper.BookingMapper;
import ru.practicum.shareit.booking.model.Booking;
import ru.practicum.shareit.booking.repository.BookingRepository;
import ru.practicum.shareit.exceptions.BookingException;
import ru.practicum.shareit.exceptions.InvalidIdException;
import ru.practicum.shareit.exceptions.NotFoundException;
import ru.practicum.shareit.exceptions.ValidationException;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.item.repository.ItemRepository;
import ru.practicum.shareit.messages.LogMessages;
import ru.practicum.shareit.user.model.User;
import ru.practicum.shareit.user.service.UserServiceImpl;

import javax.transaction.Transactional;
import java.time.LocalDateTime;
import java.util.Collections;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

@Slf4j
@Service
@RequiredArgsConstructor
public class BookingServiceImpl implements BookingService {
    private final BookingRepository bookingRepository;
    private final ItemRepository itemRepository;
    private final UserServiceImpl userService;
    private static final Sort SORT = Sort.sort(Booking.class).by(Booking::getStart).descending();

    @Override
    @Transactional
    public BookingDto addBooking(BookingCreatDto bookingCreatDto, Long userId) {
        Item item = itemRepository.validateItem(bookingCreatDto.getItemId());
        User user = userService.validateUser(userId);
        if (!item.getAvailable()) {
            log.warn(LogMessages.BOOKING_NOT_AVAILABLE.toString(), bookingCreatDto.getItemId());
            throw new ValidationException(LogMessages.BOOKING_NOT_AVAILABLE.toString());
        }
        if (Objects.equals(item.getOwner().getId(), userId)) {
            log.warn(LogMessages.BOOKING_BY_OWNER.toString());
            throw new NotFoundException(LogMessages.BOOKING_BY_OWNER.toString());
        }
        bookingDateCheck(bookingCreatDto);
        Booking booking = bookingRepository.save(BookingMapper.toBooking(bookingCreatDto, item, user));
        return BookingMapper.toBookingDto(booking);
    }

    @Override
    @Transactional
    public BookingDto updateBooking(Long bookingId, Long userId, Boolean approved) {
        Booking booking = bookingRepository.validateBooking(bookingId);
        User user = userService.validateUser(userId);
        Item item = booking.getItem();
        if (!item.getOwner().equals(user)) {
            log.warn(LogMessages.BOOKING_INVALID_ID.toString(), userId);
            throw new InvalidIdException(LogMessages.BOOKING_INVALID_ID.toString());
        }
        if (booking.getStatus().equals(Status.APPROVED)) {
            log.warn(LogMessages.BOOKING_APPROVED.toString());
            throw new BookingException(LogMessages.BOOKING_APPROVED.toString());
        }
        if (approved && booking.getStatus().equals(Status.WAITING)) {
            booking.setStatus(Status.APPROVED);
        } else {
            booking.setStatus(Status.REJECTED);
        }
        return BookingMapper.toBookingDto(bookingRepository.save(booking));
    }

    @Override
    public BookingDto getBookingById(Long bookingId, Long userId) {
        Booking booking = bookingRepository.validateBooking(bookingId);
        User user = userService.validateUser(userId);
        Long bookerId = booking.getBooker().getId();
        Long itemOwnerId = booking.getItem().getOwner().getId();
        if (!(bookerId.equals(user.getId()) || itemOwnerId.equals(user.getId()))) {
            log.warn(LogMessages.BOOKING_GET_BY_ID.toString(), userId);
            throw new NotFoundException(LogMessages.BOOKING_GET_BY_ID.toString());
        }
        return BookingMapper.toBookingDto(booking);
    }

    @Override
    public List<BookingDto> getAllUserBookings(Long bookerId, String state) {
        userService.validateUser(bookerId);
        LocalDateTime now = LocalDateTime.now();
        BookingState bookingState = toBookingState(state);
        List<Booking> bookingDtoList = Collections.emptyList();
        ;
        switch (bookingState) {
            case ALL:
                bookingDtoList = bookingRepository.findAllByBookerId(bookerId, SORT);
                break;
            case CURRENT:
                bookingDtoList = bookingRepository.findByBookerIdAndNowBetweenStartAndEnd(bookerId, now, SORT);
                break;
            case FUTURE:
                bookingDtoList = bookingRepository.findByBookerIdAndStartIsAfter(bookerId, now, SORT);
                break;
            case PAST:
                bookingDtoList = bookingRepository.findByBookerIdAndEndIsBefore(bookerId, now, SORT);
                break;
            case WAITING:
                bookingDtoList = bookingRepository.findByBookerIdAndStatusIs(bookerId, Status.WAITING, SORT);
                break;
            case REJECTED:
                bookingDtoList = bookingRepository.findByBookerIdAndStatusIs(bookerId, Status.REJECTED, SORT);
                break;
        }
        return bookingDtoList.stream()
                .map(BookingMapper::toBookingDto)
                .collect(Collectors.toList());
    }

    @Override
    public List<BookingDto> getOwnerAllItemBookings(Long userId, String state) {
        userService.validateUser(userId);
        LocalDateTime now = LocalDateTime.now();
        BookingState bookingState = toBookingState(state);
        List<Booking> bookingDtoList = Collections.emptyList();
        switch (bookingState) {
            case ALL:
                bookingDtoList = bookingRepository.findAllByItemOwnerId(userId, SORT);
                break;
            case CURRENT:
                bookingDtoList = bookingRepository.findAllCurrentOwnerBookings(userId, now, SORT);
                break;
            case FUTURE:
                bookingDtoList = bookingRepository.findAllByItemOwnerIdAndStartIsAfter(userId, now, SORT);
                break;
            case PAST:
                bookingDtoList = bookingRepository.findAllByItemOwnerIdAndEndIsBefore(userId, now, SORT);
                break;
            case WAITING:
                bookingDtoList = bookingRepository.findAllByItemOwnerIdAndStatusIs(userId, Status.WAITING, SORT);
                break;
            case REJECTED:
                bookingDtoList = bookingRepository.findAllByItemOwnerIdAndStatusIs(userId, Status.REJECTED, SORT);
                break;
        }
        return bookingDtoList.stream()
                .map(BookingMapper::toBookingDto)
                .collect(Collectors.toList());
    }

    private void bookingDateCheck(BookingCreatDto bookingCreatDto) {
        if (bookingCreatDto.getStart().isAfter(bookingCreatDto.getEnd())) {
            log.warn(LogMessages.BOOKING_START_DATE.toString(), bookingCreatDto.getStart());
            throw new BookingException(LogMessages.BOOKING_START_DATE.toString());
        }
        if (bookingCreatDto.getStart().isEqual(bookingCreatDto.getEnd())) {
            log.warn(LogMessages.BOOKING_START_DATE_EQUAL.toString(), bookingCreatDto.getStart());
            throw new BookingException(LogMessages.BOOKING_START_DATE_EQUAL.toString());
        }
    }

    private BookingState toBookingState(String state) {
        try {
            return BookingState.valueOf(state.toUpperCase());
        } catch (IllegalArgumentException ex) {
            throw new BookingException("Unknown state: " + state);
        }
    }
}