package ru.practicum.shareit.item.service;

import lombok.RequiredArgsConstructor;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.transaction.annotation.Transactional;
import ru.practicum.shareit.booking.Status;
import ru.practicum.shareit.booking.dto.BookingCreatDto;
import ru.practicum.shareit.booking.model.Booking;
import ru.practicum.shareit.booking.repository.BookingRepository;
import ru.practicum.shareit.item.dto.ItemDto;
import ru.practicum.shareit.item.model.Comment;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.item.repository.CommentRepository;
import ru.practicum.shareit.item.repository.ItemRepository;
import ru.practicum.shareit.user.model.User;
import ru.practicum.shareit.user.repository.UserRepository;

import java.time.LocalDateTime;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;

@Transactional
@SpringBootTest(properties = {"db.name=test"})
@RequiredArgsConstructor(onConstructor_ = @Autowired)
class ItemServiceImplIntegrationTest {
    private LocalDateTime now = LocalDateTime.now();
    private final UserRepository userRepository;
    private final ItemService itemService;
    private final ItemRepository itemRepository;
    private final BookingRepository bookingRepository;
    private final CommentRepository commentRepository;
    private User user;
    private User savedUser;
    private User user2;
    private User savedUser2;
    private Item item;
    private Item savedItem;
    private BookingCreatDto bookingCreationDto;
    private Booking booking;
    private Booking savedBooking;
    private Comment comment;
    private Comment savedComment;

    @BeforeEach
    void setUp() {
        user = User.builder()
                .id(1L)
                .name("Name")
                .email("username@gmail.com")
                .build();
        savedUser = userRepository.save(user);
        user2 = User.builder()
                .id(2L)
                .name("Name2")
                .email("user2name@gmail.com")
                .build();
        savedUser2 = userRepository.save(user2);
        item = Item.builder()
                .id(1L)
                .name("Item name")
                .description("Item description")
                .available(true)
                .owner(savedUser)
                .build();
        savedItem = itemRepository.save(item);
        booking = Booking.builder()
                .id(1L)
                .start(now.plusDays(1))
                .end(now.plusDays(2))
                .item(savedItem)
                .booker(savedUser2)
                .status(Status.APPROVED)
                .build();
        bookingCreationDto = BookingCreatDto.builder()
                .id(1L)
                .start(now.plusDays(1))
                .end(now.plusDays(2))
                .itemId(savedItem.getId())
                .build();
        savedBooking = bookingRepository.save(booking);
        comment = Comment.builder()
                .id(1L)
                .author(savedUser2)
                .item(savedItem)
                .text("Comment to item")
                .created(LocalDateTime.now())
                .build();
        savedComment = commentRepository.save(comment);
    }

    @Test
    public void getPersonal() {
        long userId = savedUser.getId();
        List<ItemDto> itemDtoList = itemService.getItemsByUserId(userId, 0, 5);

        assertEquals(1, itemDtoList.size());
        assertEquals(savedItem.getName(), itemDtoList.get(0).getName());
        assertEquals(1, itemDtoList.get(0).getComments().size());
        assertEquals(booking.getStart(), itemDtoList.get(0).getNextBooking().getStart());
    }
}