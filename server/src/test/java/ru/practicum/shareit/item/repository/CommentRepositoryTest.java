package ru.practicum.shareit.item.repository;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import ru.practicum.shareit.booking.Status;
import ru.practicum.shareit.booking.mapper.BookingMapper;
import ru.practicum.shareit.booking.model.Booking;
import ru.practicum.shareit.booking.repository.BookingRepository;
import ru.practicum.shareit.item.dto.ItemDto;
import ru.practicum.shareit.item.model.Comment;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.user.model.User;
import ru.practicum.shareit.user.repository.UserRepository;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertEquals;

@DataJpaTest
class CommentRepositoryTest {
    @Autowired
    private CommentRepository commentRepository;
    @Autowired
    private UserRepository userRepository;
    @Autowired
    private ItemRepository itemRepository;
    @Autowired
    private BookingRepository bookingRepository;
    private Item item;
    private Item savedItem;
    private ItemDto itemDto;
    private User user;
    private User savedUser;
    private User user2;
    private User savedUser2;
    private Comment comment;
    private Comment savedComment;
    private Booking booking;
    private Booking savedBooking;
    private Booking booking2;
    private Booking savedBooking2;

    @BeforeEach
    void setUp() {
        user = User.builder()
                .name("Name")
                .email("username@gmail.com")
                .build();
        savedUser = userRepository.save(user);
        user2 = User.builder()
                .name("Name2")
                .email("user2name@gmail.com")
                .build();
        savedUser2 = userRepository.save(user2);
        item = Item.builder()
                .name("Item Name")
                .description("Item description")
                .owner(user)
                .available(true)
                .build();
        savedItem = itemRepository.save(item);
        itemDto = ItemDto.builder()
                .id(item.getId())
                .lastBooking(BookingMapper.toBookingShortDto(booking))
                .nextBooking(BookingMapper.toBookingShortDto(booking2))
                .build();
        booking = Booking.builder()
                .start(LocalDateTime.now().plusDays(2))
                .end(LocalDateTime.now().plusDays(3))
                .booker(user2)
                .item(item)
                .status(Status.WAITING)
                .build();
        savedBooking = bookingRepository.save(booking);
        booking2 = Booking.builder()
                .start(LocalDateTime.now().plusDays(5))
                .end(LocalDateTime.now().plusDays(7))
                .booker(user2)
                .item(item)
                .status(Status.WAITING)
                .build();
        savedBooking2 = bookingRepository.save(booking2);
        comment = Comment.builder()
                .author(user2)
                .item(item)
                .text("Comment to item")
                .created(LocalDateTime.now())
                .build();
        savedComment = commentRepository.save(comment);
    }

    @AfterEach
    public void deleteItem() {
        userRepository.deleteAll();
        itemRepository.deleteAll();
        commentRepository.deleteAll();
    }

    @Test
    public void findAllCommentsByItemId() {
        assertEquals(List.of(comment), commentRepository.findAllByItemId(item.getId()));
        assertThat(List.of(comment).get(0)).hasFieldOrPropertyWithValue("id", comment.getId());
        assertThat(List.of(comment).get(0)).hasFieldOrPropertyWithValue("author", comment.getAuthor());
        assertThat(List.of(comment).get(0)).hasFieldOrPropertyWithValue("text", comment.getText());

        assertEquals(List.of(), commentRepository.findAllByItemId(99L));
    }

    @Test
    public void findAllCommentsByItemIds() {
        List<Item> items = new ArrayList<>();
        items.add(item);
        assertEquals(List.of(comment), commentRepository.findAllByItemIn(items));
        assertThat(List.of(comment).get(0)).hasFieldOrPropertyWithValue("id", comment.getId());
        assertThat(List.of(comment).get(0)).hasFieldOrPropertyWithValue("author", comment.getAuthor());
        assertThat(List.of(comment).get(0)).hasFieldOrPropertyWithValue("text", comment.getText());
    }
}