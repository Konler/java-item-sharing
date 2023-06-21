package ru.practicum.shareit.booking.servce;

import lombok.RequiredArgsConstructor;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.transaction.annotation.Transactional;
import ru.practicum.shareit.booking.Status;
import ru.practicum.shareit.booking.dto.BookingCreatDto;
import ru.practicum.shareit.booking.dto.BookingDto;
import ru.practicum.shareit.booking.model.Booking;
import ru.practicum.shareit.booking.repository.BookingRepository;
import ru.practicum.shareit.booking.service.BookingService;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.item.repository.ItemRepository;
import ru.practicum.shareit.user.model.User;
import ru.practicum.shareit.user.repository.UserRepository;

import java.time.LocalDateTime;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;

@Transactional
@SpringBootTest(properties = {"db.name=test"})
@RequiredArgsConstructor(onConstructor_ = @Autowired)
class BookingServiceIntegrationTest {
    private LocalDateTime now = LocalDateTime.now();
    private final UserRepository userRepository;
    private final BookingService bookingService;
    private final ItemRepository itemRepository;
    private final BookingRepository bookingRepository;
    private User user;
    private User savedUser;
    private User user2;
    private User savedUser2;
    private Item item;
    private Item savedItem;
    private BookingCreatDto bookingCreationDto;
    private Booking booking;
    private Booking savedBooking;
    private BookingCreatDto bookingCreationDto2;
    private Booking booking2;
    private Booking savedBooking2;

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
        booking2 = Booking.builder()
                .id(5L)
                .start(now.minusDays(3))
                .end(now.minusDays(5))
                .item(savedItem)
                .booker(savedUser2)
                .status(Status.APPROVED)
                .build();
        bookingCreationDto2 = BookingCreatDto.builder()
                .id(5L)
                .start(now.minusDays(3))
                .end(now.minusDays(5))
                .itemId(savedItem.getId())
                .build();
        savedBooking2 = bookingRepository.save(booking2);
    }

    @Test
    public void getOwnerAllItemBookings() {
        long userId = savedUser.getId();
        int from = 0;
        int size = 10;
        List<BookingDto> bookingDtoList = bookingService.getOwnerAllItemBookings(userId, "ALL", from, size);

        assertEquals(2, bookingDtoList.size());
        assertEquals(savedBooking.getId(), bookingDtoList.get(0).getId());
        assertEquals(savedBooking2.getId(), bookingDtoList.get(1).getId());
        assertEquals(savedBooking.getStart(), bookingDtoList.get(0).getStart());
        assertEquals(savedBooking.getEnd(), bookingDtoList.get(0).getEnd());
        assertEquals(savedBooking2.getStart(), bookingDtoList.get(1).getStart());
        assertEquals(savedBooking2.getEnd(), bookingDtoList.get(1).getEnd());
    }
}