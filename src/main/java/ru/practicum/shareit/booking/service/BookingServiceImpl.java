package ru.practicum.shareit.booking.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import ru.practicum.shareit.PageSetup;
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
import ru.practicum.shareit.user.repository.UserRepository;
import ru.practicum.shareit.user.service.UserService;
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

    private final UserService userService;
    private static final Sort SORT = Sort.sort(Booking.class).by(Booking::getStart).descending();

    @Override
    @Transactional
    public BookingDto addBooking(BookingCreatDto bookingCreationDto, Long userId) {
        Item item = itemRepository.validateItem(bookingCreationDto.getItemId());
        User user = userService.validateUser(userId);
        if (!item.getAvailable()) {
            log.warn(LogMessages.BOOKING_NOT_AVAILABLE.toString(), bookingCreationDto.getItemId());
            throw new ValidationException(LogMessages.BOOKING_NOT_AVAILABLE.toString());
        }
        if (Objects.equals(item.getOwner().getId(), userId)) {
            log.warn(LogMessages.BOOKING_BY_OWNER.toString());
            throw new NotFoundException(LogMessages.BOOKING_BY_OWNER.toString());
        }
        bookingDateCheck(bookingCreationDto);
        Booking booking = bookingRepository.save(BookingMapper.toBooking(bookingCreationDto, item, user));
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
    public List<BookingDto> getAllUserBookings(Long bookerId, String state, Integer from, Integer size) {
        if (from < 0) {
            throw new BookingException("Введен отрицательный параметр");
        }
        userService.validateUser(bookerId);
        LocalDateTime now = LocalDateTime.now();
        BookingState bookingState = toBookingState(state);
        List<Booking> bookingDtoList = Collections.emptyList();
        PageRequest pageable = new PageSetup(from, size, SORT);
        switch (bookingState) {
            case ALL:
                bookingDtoList = bookingRepository.findAllByBookerId(bookerId, pageable).getContent();
                break;
            case CURRENT:
                bookingDtoList = bookingRepository.findByBookerIdAndNowBetweenStartAndEnd(bookerId, now, pageable).getContent();
                break;
            case FUTURE:
                bookingDtoList = bookingRepository.findByBookerIdAndStartIsAfter(bookerId, now, pageable).getContent();
                break;
            case PAST:
                bookingDtoList = bookingRepository.findByBookerIdAndEndIsBefore(bookerId, now, pageable).getContent();
                break;
            case WAITING:
                bookingDtoList = bookingRepository.findByBookerIdAndStatusIs(bookerId, Status.WAITING, pageable).getContent();
                break;
            case REJECTED:
                bookingDtoList = bookingRepository.findByBookerIdAndStatusIs(bookerId, Status.REJECTED, pageable).getContent();
                break;
        }
        return bookingDtoList.stream()
                .map(BookingMapper::toBookingDto)
                .collect(Collectors.toList());
    }


    @Override
    public List<BookingDto> getOwnerAllItemBookings(Long userId, String state, Integer from, Integer size) {
        if (from < 0) {
            throw new BookingException("Введен отрицательный параметр");
        }
        userService.validateUser(userId);
        LocalDateTime now = LocalDateTime.now();
        BookingState bookingState = toBookingState(state);
        List<Booking> bookingDtoList = Collections.emptyList();
        PageRequest pageable = new PageSetup(from, size, SORT);
        switch (bookingState) {
            case ALL:
                bookingDtoList = bookingRepository.findAllByItemOwnerId(userId, pageable).getContent();
                break;
            case CURRENT:
                bookingDtoList = bookingRepository.findAllCurrentOwnerBookings(userId, now, pageable).getContent();
                break;
            case FUTURE:
                bookingDtoList = bookingRepository.findAllByItemOwnerIdAndStartIsAfter(userId, now, pageable).getContent();
                break;
            case PAST:
                bookingDtoList = bookingRepository.findAllByItemOwnerIdAndEndIsBefore(userId, now, pageable).getContent();
                break;
            case WAITING:
                bookingDtoList = bookingRepository.findAllByItemOwnerIdAndStatusIs(userId,
                        Status.WAITING, pageable).getContent();
                break;
            case REJECTED:
                bookingDtoList = bookingRepository.findAllByItemOwnerIdAndStatusIs(userId,
                        Status.REJECTED, pageable).getContent();
                break;
        }
        return bookingDtoList.stream()
                .map(BookingMapper::toBookingDto)
                .collect(Collectors.toList());
    }


    private void bookingDateCheck(BookingCreatDto bookingCreationDto) {
        if (bookingCreationDto.getStart().isAfter(bookingCreationDto.getEnd())) {
            log.warn(LogMessages.BOOKING_START_DATE.toString(), bookingCreationDto.getStart());
            throw new BookingException(LogMessages.BOOKING_START_DATE.toString());
        }
        if (bookingCreationDto.getStart().isEqual(bookingCreationDto.getEnd())) {
            log.warn(LogMessages.BOOKING_START_DATE_EQUAL.toString(), bookingCreationDto.getStart());
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