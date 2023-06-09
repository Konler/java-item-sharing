package ru.practicum.shareit.item.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import ru.practicum.shareit.booking.Status;
import ru.practicum.shareit.booking.dto.BookingDto;
import ru.practicum.shareit.booking.dto.BookingDtoLittle;
import ru.practicum.shareit.booking.mapper.BookingMapper;
import ru.practicum.shareit.booking.model.Booking;
import ru.practicum.shareit.booking.repository.BookingRepository;
import ru.practicum.shareit.exceptions.IllegalAccessException;
import ru.practicum.shareit.exceptions.NotFoundException;
import ru.practicum.shareit.exceptions.RequestException;
import ru.practicum.shareit.item.dto.CommentDto;
import ru.practicum.shareit.item.dto.CommentMapper;
import ru.practicum.shareit.item.dto.ItemDto;
import ru.practicum.shareit.item.dto.ItemMapper;
import ru.practicum.shareit.item.model.Comment;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.item.repository.CommentRepository;
import ru.practicum.shareit.item.repository.ItemRepository;
import ru.practicum.shareit.messages.LogMessages;
import ru.practicum.shareit.user.model.User;
import ru.practicum.shareit.user.service.UserServiceImpl;

import javax.transaction.Transactional;
import java.time.LocalDateTime;
import java.util.Collection;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Slf4j
@Service
@RequiredArgsConstructor
public class ItemServiceImpl implements ItemService {
    private final ItemRepository itemRepository;
    private final UserServiceImpl userService;
    private final CommentRepository commentRepository;
    private final BookingRepository bookingRepository;
    public static final Sort SORT_BY_START_ASC = Sort.by("start").ascending();
    public static final Sort SORT_BY_START_DESC = Sort.by("start").descending();

    @Override
    public ItemDto addItem(Long userId, ItemDto itemDto) {
        Item newItem = ItemMapper.toItem(itemDto);
        User user = userService.validateUser(userId);
        newItem.setOwner(user);
        return ItemMapper.toItemDto(itemRepository.save(newItem));
    }

    @Override
    public ItemDto updateItem(Long itemId, ItemDto itemDto, Long userId) {
        Item item = itemRepository.validateItem(itemId);
        userService.validateUser(userId);
        if (!item.getOwner().getId().equals(userId)) {
            log.warn(LogMessages.ILLEGAL_ACCESS.toString());
            throw new IllegalAccessException(LogMessages.ILLEGAL_ACCESS.toString());
        }
        String updatedName = itemDto.getName();
        if (updatedName != null) {
            item.setName(updatedName);
        }
        String updatedDescription = itemDto.getDescription();
        if (updatedDescription != null) {
            item.setDescription(updatedDescription);
        }
        Boolean updatedAvailable = itemDto.getAvailable();
        if (updatedAvailable != null) {
            item.setAvailable(updatedAvailable);
        }
        ItemMapper.toItemDto(item);
        Item updatedItem = itemRepository.save(item);
        return ItemMapper.toItemDto(updatedItem);
    }

    @Override
    @Transactional
    public ItemDto getItemById(Long itemId, Long ownerId) {
        Item item = itemRepository.validateItem(itemId);
        ItemDto itemDto = ItemMapper.toItemDto(item);
        List<Comment> comments = commentRepository.findAllByItemId(itemId);
        setComments(itemDto, comments);
        if (item.getOwner().getId().equals(ownerId)) {
            setBookings(itemDto);
        }
        return itemDto;
    }

    @Override
    public List<ItemDto> getItemsByUserId(Long userId) {
        userService.validateUser(userId);
        List<Item> items = itemRepository.findAllByOwnerId(userId);
        if (items.isEmpty()) {
            log.warn(LogMessages.NOT_FOUND.toString());
            throw new NotFoundException(LogMessages.NOT_FOUND.toString());
        }
        Map<Long, List<BookingDto>> bookings =
                bookingRepository.findAllByOwnerAndStatus(userId, Status.APPROVED, SORT_BY_START_ASC)
                        .stream()
                        .map(BookingMapper::toBookingDto)
                        .collect(Collectors.groupingBy(BookingDto::getItemId));

        Map<Long, List<CommentDto>> comments = commentRepository.findAllByItemIn(items)
                .stream()
                .map(CommentMapper::toCommentDto)
                .collect(Collectors.groupingBy(CommentDto::getItemId));

        return items.stream()
                .map(item -> ItemMapper.toItemDto(
                        item,
                        getLastBookingForPersonal(bookings.get(item.getId())),
                        getNextBookingForPersonal(bookings.get(item.getId())),
                        comments.getOrDefault(item.getId(), Collections.emptyList())))
                .collect(Collectors.toList());
    }

    @Override
    public List<ItemDto> searchItem(String text) {
        if (text.isBlank()) {
            log.warn(LogMessages.BLANK_TEXT.toString());
            return Collections.emptyList();
        }
        String lowerText = text.toLowerCase();
        Collection<Item> items = itemRepository.searchItemByText(lowerText);
        return items.stream()
                .map(ItemMapper::toItemDto)
                .collect(Collectors.toList());
    }

    @Override
    @Transactional
    public CommentDto addComment(Long userId, Long itemId, CommentDto commentDto) {
        User user = userService.validateUser(userId);
        Item item = itemRepository.validateItem(itemId);
        LocalDateTime now = LocalDateTime.now();
        List<Booking> items = bookingRepository
                .findAllByItemIdAndBookerIdAndStatusIsAndEndBefore(itemId, userId, Status.APPROVED, now);
        if (items.isEmpty()) {
            log.warn(LogMessages.REQUEST_EXCEPTION.toString(), userId, itemId);
            throw new RequestException(LogMessages.REQUEST_EXCEPTION.toString() + userId + itemId);
        }
        commentDto.setCreated(LocalDateTime.now());
        Comment comment = commentRepository.save(CommentMapper.toComment(commentDto, user, item));
        return CommentMapper.toCommentDto(comment);
    }

    private void setComments(ItemDto itemDto, List<Comment> comments) {
        itemDto.setComments(comments.stream()
                .map(CommentMapper::toCommentDto)
                .collect(Collectors.toList()));
    }

    public void setBookings(ItemDto itemDto) {
        Long itemId = itemDto.getId();
        Booking lastBooking = bookingRepository.findFirstByItemIdAndStartBeforeAndStatus(
                itemId, LocalDateTime.now(), Status.APPROVED, SORT_BY_START_DESC).orElse(null);
        itemDto.setLastBooking(BookingMapper.toBookingShortDto(lastBooking));
        Booking nextBooking = bookingRepository.findFirstByItemIdAndStartAfterAndStatus(
                itemId, LocalDateTime.now(), Status.APPROVED, SORT_BY_START_ASC).orElse(null);
        itemDto.setNextBooking(BookingMapper.toBookingShortDto(nextBooking));
    }

    private BookingDtoLittle getNextBookingForPersonal(List<BookingDto> booking) {
        if (booking == null || booking.isEmpty()) {
            return null;
        }
        return booking.stream()
                .filter(b -> b.getStart().isAfter(LocalDateTime.now()))
                .findFirst()
                .map(BookingMapper::toBookingShortDto)
                .orElse(null);
    }

    private BookingDtoLittle getLastBookingForPersonal(List<BookingDto> booking) {
        if (booking == null || booking.isEmpty()) {
            return null;
        }
        return booking.stream()
                .filter(b -> b.getStart().isBefore(LocalDateTime.now()))
                .reduce((booking1, booking2) -> booking2)
                .map(BookingMapper::toBookingShortDto)
                .orElse(null);
    }
}