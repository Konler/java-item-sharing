package ru.practicum.shareit.item.service;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.Sort;
import ru.practicum.shareit.booking.Status;
import ru.practicum.shareit.booking.dto.BookingDto;
import ru.practicum.shareit.booking.dto.BookingDtoLittle;
import ru.practicum.shareit.booking.mapper.BookingMapper;
import ru.practicum.shareit.booking.model.Booking;
import ru.practicum.shareit.booking.repository.BookingRepository;
import ru.practicum.shareit.exceptions.IllegalAccessException;
import ru.practicum.shareit.exceptions.RequestException;
import ru.practicum.shareit.item.dto.CommentDto;
import ru.practicum.shareit.item.dto.CommentMapper;
import ru.practicum.shareit.item.dto.ItemDto;
import ru.practicum.shareit.item.dto.ItemMapper;
import ru.practicum.shareit.item.model.Comment;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.item.repository.CommentRepository;
import ru.practicum.shareit.item.repository.ItemRepository;
import ru.practicum.shareit.request.model.ItemRequest;
import ru.practicum.shareit.request.repository.ItemRequestRepository;
import ru.practicum.shareit.user.model.User;
import ru.practicum.shareit.user.service.UserServiceImpl;

import java.time.LocalDateTime;
import java.util.Collections;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class ItemServiceImplTest {
    @Mock
    private ItemRepository itemRepository;
    @Mock
    private UserServiceImpl userService;
    @Mock
    private BookingRepository bookingRepository;
    @Mock
    private CommentRepository commentRepository;
    @Mock
    private ItemRequestRepository itemRequestRepository;
    @InjectMocks
    private ItemServiceImpl itemService;
    private User user;
    private User user2;
    private Item item;
    private ItemDto itemDto;
    private ItemRequest itemRequest;
    private Comment comment;
    private Booking booking;

    @BeforeEach
    void setUp() {
        user = User.builder()
                .id(1L)
                .name("Name")
                .email("username@gmail.com")
                .build();
        user2 = User.builder()
                .id(2L)
                .name("Name2")
                .email("user2name@gmail.com")
                .build();
        item = Item.builder()
                .id(1L)
                .name("Item name")
                .description("Item description")
                .available(true)
                .owner(user)
                .build();
        itemDto = ItemDto.builder()
                .id(1L)
                .name("Item name")
                .description("Item description")
                .available(true)
                .ownerId(user.getId())
                .build();
        itemRequest = ItemRequest.builder()
                .id(1L)
                .description("Item request description")
                .requestor(user2)
                .created(LocalDateTime.now())
                .build();
        booking = Booking.builder()
                .id(1L)
                .start(LocalDateTime.now().plusDays(1))
                .end(LocalDateTime.now().plusDays(2))
                .item(item)
                .booker(user2)
                .status(Status.WAITING)
                .build();
        comment = Comment.builder()
                .id(1L)
                .author(user2)
                .item(item)
                .text("Comment to item")
                .created(LocalDateTime.now())
                .build();
    }

    @Test
    public void addItemAndReturnSavedItemWithoutRequest() {
        long userId = user.getId();
        ItemDto expectedItem = ItemMapper.toItemDto(item);
        ItemDto expectedItemDto = ItemDto.builder()
                .id(1L)
                .name("Item name")
                .description("Item description")
                .available(true)
                .ownerId(userId)
                .comments(Collections.emptyList())
                .build();
        when(userService.validateUser(anyLong())).thenReturn(user);
        when(itemRepository.save(any())).thenReturn(item);
        ItemDto actualItemDto = itemService.addItem(userId, expectedItemDto);

        assertEquals(expectedItem, actualItemDto);
        assertEquals(expectedItem.getName(), actualItemDto.getName());
        assertEquals(expectedItem.getDescription(), actualItemDto.getDescription());
        assertEquals(expectedItem.getRequestId(), actualItemDto.getRequestId());
        assertNull(actualItemDto.getRequestId());
        verify(userService, times(1)).validateUser(userId);
        verify(itemRepository, times(1)).save(any());
        verify(itemRequestRepository, never()).validateItemRequest(1L);
    }

    @Test
    public void addItemAndReturnSavedItemWithRequest() {
        long requestId = itemRequest.getId();
        long userId = user.getId();
        item.setRequest(itemRequest);
        ItemDto expectedItem = ItemMapper.toItemDto(item);

        ItemDto expectedItemDto = ItemDto.builder()
                .id(1L)
                .name("Item name")
                .description("Item description")
                .available(true)
                .requestId(requestId)
                .ownerId(user.getId())
                .comments(Collections.emptyList())
                .build();
        when(userService.validateUser(anyLong())).thenReturn(user);
        when(itemRepository.save(any())).thenReturn(item);
        when(itemRequestRepository.validateItemRequest(anyLong())).thenReturn(itemRequest);
        ItemDto actualItemDto = itemService.addItem(userId, expectedItemDto);

        assertEquals(expectedItem, actualItemDto);
        assertEquals(expectedItem.getName(), actualItemDto.getName());
        assertEquals(expectedItem.getDescription(), actualItemDto.getDescription());
        assertEquals(expectedItem.getRequestId(), actualItemDto.getRequestId());
        verify(userService, times(1)).validateUser(userId);
        verify(itemRepository, times(1)).save(any());
    }

    @Test
    public void renewalItem() {
        itemDto.setDescription("New item description");
        itemDto.setName("New item name");
        Item itemNew = ItemMapper.toItem(itemDto, user);
        ItemDto expectedItem = ItemMapper.toItemDto(itemNew);
        when(userService.validateUser(anyLong())).thenReturn(user);
        when(itemRepository.validateItem(anyLong())).thenReturn(item);
        when(itemRepository.save(any())).thenReturn(ItemMapper.toItem(itemDto, user));
        ItemDto actualItemDto = itemService.updateItem(item.getId(), itemDto, user.getId());

        assertEquals(expectedItem, actualItemDto);
        assertEquals(expectedItem.getName(), actualItemDto.getName());
        assertEquals(expectedItem.getDescription(), actualItemDto.getDescription());
        assertEquals(expectedItem.getRequestId(), actualItemDto.getRequestId());
        verify(itemRepository, times(1)).save(any());
        verify(itemRequestRepository, never()).findById(1L);
    }

    @Test
    public void renewalItemWithNull() {
        itemDto.setDescription(null);
        itemDto.setName(null);
        itemDto.setAvailable(null);
        Item itemNew = ItemMapper.toItem(itemDto, user);
        ItemDto expectedItem = ItemMapper.toItemDto(itemNew);
        when(userService.validateUser(anyLong())).thenReturn(user);
        when(itemRepository.validateItem(anyLong())).thenReturn(item);
        when(itemRepository.save(any())).thenReturn(ItemMapper.toItem(itemDto, user));

        ItemDto actualItemDto = itemService.updateItem(item.getId(), itemDto, user.getId());

        assertEquals(expectedItem, actualItemDto);
        assertEquals(expectedItem.getName(), actualItemDto.getName());
        assertEquals(expectedItem.getDescription(), actualItemDto.getDescription());
        assertEquals(expectedItem.getRequestId(), actualItemDto.getRequestId());
        verify(itemRepository, times(1)).save(any());
        verify(itemRequestRepository, never()).findById(1L);
    }

    @Test
    public void renewalItemByNotOwner() {
        itemDto.setDescription("New item description");
        itemDto.setName("New item name");
        when(itemRepository.validateItem(anyLong())).thenReturn(item);

        assertThrows(IllegalAccessException.class, () -> itemService.updateItem(item.getId(), itemDto, user2.getId()));
        verify(itemRepository, times(1)).validateItem(any());
    }

    @Test
    public void getItemByIdWithoutBookingAndComments() {
        ItemDto expectedItemDto = ItemMapper.toItemDto(item);
        expectedItemDto.setComments(Collections.emptyList());
        long expectedItemId = item.getId();
        long expectedUserId = user.getId();

        when(itemRepository.validateItem(anyLong())).thenReturn(item);
        ItemDto actualItemDto = itemService.getItemById(expectedItemId, expectedUserId);

        assertEquals(expectedItemDto, actualItemDto);
        assertEquals(item.getName(), actualItemDto.getName());
        assertEquals(item.getDescription(), actualItemDto.getDescription());
        verify(itemRepository, times(1)).validateItem(expectedItemId);
        verify(commentRepository, times(1)).findAllByItemId(expectedItemId);
    }

    @Test
    public void getItemById() {
        booking.setStatus(Status.APPROVED);
        long expectedItemId = item.getId();
        long expectedUserId = user.getId();
        BookingDto bookingDto1 = BookingMapper.toBookingDto(booking);
        BookingDtoLittle bookingShortDto = BookingMapper.toBookingShortDto(bookingDto1);
        ItemDto expectedItemDto = ItemMapper.toItemDto(item);
        CommentDto commentDto = CommentMapper.toCommentDto(comment);
        expectedItemDto.setComments(List.of(commentDto));
        expectedItemDto.setLastBooking(bookingShortDto);
        when(itemRepository.validateItem(anyLong())).thenReturn(item);
        when(bookingRepository.findFirstByItemIdAndStartBeforeAndStatus(anyLong(), any(LocalDateTime.class),
                any(Status.class), any(Sort.class))).thenReturn(Optional.of(booking));
        when(commentRepository.findAllByItemId(anyLong())).thenReturn(List.of(comment));
        ItemDto actualItem = itemService.getItemById(expectedItemId, expectedUserId);
        assertEquals(expectedItemDto, actualItem);

        verify(itemRepository, times(1)).validateItem(expectedItemId);
        verify(bookingRepository, times(1)).findFirstByItemIdAndStartBeforeAndStatus(anyLong(),
                any(LocalDateTime.class), any(Status.class), any(Sort.class));
        verify(commentRepository, times(1)).findAllByItemId(expectedItemId);
    }

    @Test
    public void addComment() {
        long expectedUserId = user2.getId();
        long expectedItemId = item.getId();
        CommentDto commentDto = CommentDto.builder()
                .text("Comment for item1")
                .build();
        Comment expectedComment = Comment.builder()
                .item(item)
                .text("Comment for item1")
                .author(user2)
                .created(LocalDateTime.now())
                .build();

        when(userService.validateUser(anyLong())).thenReturn(user);
        when(bookingRepository.findAllByItemIdAndBookerIdAndStatusIsAndEndBefore(anyLong(), anyLong(), any(Status.class), any(LocalDateTime.class)))
                .thenReturn(List.of(booking));
        when(commentRepository.save(any())).thenReturn(expectedComment);
        CommentDto actualComment = itemService.addComment(expectedUserId, expectedItemId, commentDto);
        assertEquals(CommentMapper.toCommentDto(expectedComment), actualComment);
    }

    @Test
    public void addCommentWithNoItems() {
        long expectedUserId = user2.getId();
        long expectedItemId = item.getId();
        CommentDto commentDto = CommentDto.builder()
                .text("Comment for item1")
                .build();

        when(userService.validateUser(anyLong())).thenReturn(user);
        when(bookingRepository.findAllByItemIdAndBookerIdAndStatusIsAndEndBefore(anyLong(), anyLong(), any(Status.class), any(LocalDateTime.class)))
                .thenReturn(List.of());
        assertThrows(RequestException.class, () -> itemService.addComment(expectedUserId, expectedItemId, commentDto));
        verify(commentRepository, never()).save(comment);
    }
}