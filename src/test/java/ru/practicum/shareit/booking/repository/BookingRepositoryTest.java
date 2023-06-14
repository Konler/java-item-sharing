package ru.practicum.shareit.booking.repository;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import ru.practicum.shareit.PageSetup;
import ru.practicum.shareit.booking.Status;
import ru.practicum.shareit.booking.model.Booking;
import ru.practicum.shareit.exceptions.NotFoundException;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.item.repository.ItemRepository;
import ru.practicum.shareit.user.model.User;
import ru.practicum.shareit.user.repository.UserRepository;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

@DataJpaTest
class BookingRepositoryTest {
    private static final Sort SORT_BY_START_ASC = Sort.by("start").ascending();
    private static final Sort SORT_BY_START_DESC = Sort.by("start").descending();
    @Autowired
    private BookingRepository bookingRepository;
    @Autowired
    private ItemRepository itemRepository;
    @Autowired
    private UserRepository userRepository;
    private Item item;
    private Item savedItem;
    private Item item2;
    private Item savedItem2;
    private User user;
    private User savedUser;
    private User user2;
    private User savedUser2;
    private Booking booking;
    private Booking savedBooking;
    private Booking booking2;
    private Booking savedBooking2;
    private Pageable page;

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
        item2 = Item.builder()
                .name("Item2 Name")
                .description("Item2 description")
                .owner(user2)
                .available(true)
                .build();
        savedItem2 = itemRepository.save(item2);
        booking = Booking.builder()
                .start(LocalDateTime.now().plusDays(2))
                .end(LocalDateTime.now().plusDays(3))
                .booker(user)
                .item(item2)
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
        int from = 0;
        int size = 10;
        page = new PageSetup(from, size, Sort.unsorted());
    }

    @AfterEach
    public void deleteItem() {
        itemRepository.deleteAll();
        userRepository.deleteAll();
        bookingRepository.deleteAll();
    }

    @Test
    public void validateBooking() {
        assertEquals(booking, bookingRepository.validateBooking(savedBooking.getId()));
        assertEquals(booking2, bookingRepository.validateBooking(savedBooking2.getId()));
    }


    @Test
    public void validateNotSavedBooking() {
        assertThrows(NotFoundException.class, () -> bookingRepository.validateBooking(99L));
    }

    @Test
    public void findAllByBookerId() {
        assertEquals(List.of(booking), bookingRepository.findAllByBookerId(booking.getBooker().getId(), page).toList());
        assertThat(booking).hasFieldOrPropertyWithValue("id", booking.getId());
        assertThat(booking.getItem()).hasFieldOrPropertyWithValue("id", booking.getItem().getId());
        assertThat(booking.getBooker()).hasFieldOrPropertyWithValue("id", booking.getBooker().getId());
        assertThat(booking).hasFieldOrPropertyWithValue("start", booking.getStart());
        assertThat(booking).hasFieldOrPropertyWithValue("end", booking.getEnd());
        assertThat(booking).hasFieldOrPropertyWithValue("status", booking.getStatus());

        assertEquals(List.of(), bookingRepository.findAllByBookerId(99L, page).toList());

        assertEquals(List.of(booking2), bookingRepository.findAllByBookerId(booking2.getBooker().getId(), page).toList());
        assertThat(booking2).hasFieldOrPropertyWithValue("id", booking2.getId());
        assertThat(booking2.getItem()).hasFieldOrPropertyWithValue("id", booking2.getItem().getId());
        assertThat(booking2.getBooker()).hasFieldOrPropertyWithValue("id", booking2.getBooker().getId());
        assertThat(booking2).hasFieldOrPropertyWithValue("start", booking2.getStart());
        assertThat(booking2).hasFieldOrPropertyWithValue("end", booking2.getEnd());
        assertThat(booking2).hasFieldOrPropertyWithValue("status", booking2.getStatus());
    }

    @Test
    public void findAllByItemOwnerId() {
        assertEquals(List.of(booking2), bookingRepository.findAllByItemOwnerId(item.getOwner().getId(), page).toList());
        assertThat(booking2).hasFieldOrPropertyWithValue("id", booking2.getId());
        assertThat(booking2.getItem()).hasFieldOrPropertyWithValue("id", booking2.getItem().getId());
        assertThat(booking2.getBooker()).hasFieldOrPropertyWithValue("id", booking2.getBooker().getId());
        assertThat(booking2).hasFieldOrPropertyWithValue("start", booking2.getStart());
        assertThat(booking2).hasFieldOrPropertyWithValue("end", booking2.getEnd());
        assertThat(booking2).hasFieldOrPropertyWithValue("status", booking2.getStatus());
        assertEquals(List.of(), bookingRepository.findAllByItemOwnerId(99L, page).toList());
    }

    @Test
    public void findAllByOwnerAndStatus() {
        assertEquals(List.of(booking2), bookingRepository.findAllByOwnerAndStatus(item.getOwner().getId(),
                Status.WAITING, SORT_BY_START_ASC));
        assertThat(booking2).hasFieldOrPropertyWithValue("id", booking2.getId());
        assertThat(booking2.getItem()).hasFieldOrPropertyWithValue("id", booking2.getItem().getId());
        assertThat(booking2.getBooker()).hasFieldOrPropertyWithValue("id", booking2.getBooker().getId());
        assertThat(booking2).hasFieldOrPropertyWithValue("start", booking2.getStart());
        assertThat(booking2).hasFieldOrPropertyWithValue("end", booking2.getEnd());
        assertThat(booking2).hasFieldOrPropertyWithValue("status", booking2.getStatus());
        assertEquals(List.of(), bookingRepository.findAllByOwnerAndStatus(item.getOwner().getId(),
                Status.APPROVED, SORT_BY_START_ASC));

        booking2.setStatus(Status.APPROVED);
        assertEquals(List.of(booking2), bookingRepository.findAllByOwnerAndStatus(item.getOwner().getId(),
                Status.APPROVED, SORT_BY_START_ASC));
        assertThat(booking2).hasFieldOrPropertyWithValue("id", booking2.getId());
        assertThat(booking2.getItem()).hasFieldOrPropertyWithValue("id", booking2.getItem().getId());
        assertThat(booking2.getBooker()).hasFieldOrPropertyWithValue("id", booking2.getBooker().getId());
        assertThat(booking2).hasFieldOrPropertyWithValue("start", booking2.getStart());
        assertThat(booking2).hasFieldOrPropertyWithValue("end", booking2.getEnd());
        assertThat(booking2).hasFieldOrPropertyWithValue("status", booking2.getStatus());
    }

    @Test
    public void findAllCurrentOwnerBookings() {
        booking2.setStart(LocalDateTime.now().minusHours(2));
        assertEquals(List.of(booking2), bookingRepository.findAllCurrentOwnerBookings(item.getOwner().getId(),
                LocalDateTime.now(), page).toList());
        assertThat(booking2).hasFieldOrPropertyWithValue("id", booking2.getId());
        assertThat(booking2.getItem()).hasFieldOrPropertyWithValue("id", booking2.getItem().getId());
        assertThat(booking2.getBooker()).hasFieldOrPropertyWithValue("id", booking2.getBooker().getId());
        assertThat(booking2).hasFieldOrPropertyWithValue("start", booking2.getStart());
        assertThat(booking2).hasFieldOrPropertyWithValue("end", booking2.getEnd());
        assertThat(booking2).hasFieldOrPropertyWithValue("status", booking2.getStatus());

        assertEquals(List.of(), bookingRepository.findAllCurrentOwnerBookings(99L, LocalDateTime.now(), page).toList());

        booking2.setStatus(Status.APPROVED);
        assertEquals(List.of(booking2), bookingRepository.findAllCurrentOwnerBookings(item.getOwner().getId(),
                LocalDateTime.now(), page).toList());
        assertThat(booking2).hasFieldOrPropertyWithValue("id", booking2.getId());
        assertThat(booking2.getItem()).hasFieldOrPropertyWithValue("id", booking2.getItem().getId());
        assertThat(booking2.getBooker()).hasFieldOrPropertyWithValue("id", booking2.getBooker().getId());
        assertThat(booking2).hasFieldOrPropertyWithValue("start", booking2.getStart());
        assertThat(booking2).hasFieldOrPropertyWithValue("end", booking2.getEnd());
        assertThat(booking2).hasFieldOrPropertyWithValue("status", booking2.getStatus());
    }

    @Test
    public void findByBookerIdAndStatusIs() {
        assertEquals(List.of(booking), bookingRepository.findByBookerIdAndStatusIs(booking.getBooker().getId(),
                Status.WAITING, page).toList());
        assertThat(booking).hasFieldOrPropertyWithValue("id", booking.getId());
        assertThat(booking.getItem()).hasFieldOrPropertyWithValue("id", booking.getItem().getId());
        assertThat(booking.getBooker()).hasFieldOrPropertyWithValue("id", booking.getBooker().getId());
        assertThat(booking).hasFieldOrPropertyWithValue("start", booking.getStart());
        assertThat(booking).hasFieldOrPropertyWithValue("end", booking.getEnd());
        assertThat(booking).hasFieldOrPropertyWithValue("status", booking.getStatus());

        assertEquals(List.of(), bookingRepository.findByBookerIdAndStatusIs(99L, Status.WAITING, page).toList());

        booking.setStatus(Status.APPROVED);
        assertEquals(List.of(booking), bookingRepository.findByBookerIdAndStatusIs(booking.getBooker().getId(),
                Status.APPROVED, page).toList());
        assertThat(booking).hasFieldOrPropertyWithValue("id", booking.getId());
        assertThat(booking.getItem()).hasFieldOrPropertyWithValue("id", booking.getItem().getId());
        assertThat(booking.getBooker()).hasFieldOrPropertyWithValue("id", booking.getBooker().getId());
        assertThat(booking).hasFieldOrPropertyWithValue("start", booking.getStart());
        assertThat(booking).hasFieldOrPropertyWithValue("end", booking.getEnd());
        assertThat(booking).hasFieldOrPropertyWithValue("status", booking.getStatus());
    }

    @Test
    public void findByBookerIdAndNowBetweenStartAndEnd() {
        booking.setStart(LocalDateTime.now().minusDays(2));
        assertEquals(List.of(booking), bookingRepository.findByBookerIdAndNowBetweenStartAndEnd(booking.getBooker().getId(),
                LocalDateTime.now(), page).toList());
        assertThat(booking).hasFieldOrPropertyWithValue("id", booking.getId());
        assertThat(booking.getItem()).hasFieldOrPropertyWithValue("id", booking.getItem().getId());
        assertThat(booking.getBooker()).hasFieldOrPropertyWithValue("id", booking.getBooker().getId());
        assertThat(booking).hasFieldOrPropertyWithValue("start", booking.getStart());
        assertThat(booking).hasFieldOrPropertyWithValue("end", booking.getEnd());
        assertThat(booking).hasFieldOrPropertyWithValue("status", booking.getStatus());
    }

    @Test
    public void findByBookerIdAndStartIsAfter() {
        assertEquals(List.of(booking), bookingRepository.findByBookerIdAndStartIsAfter(booking.getBooker().getId(),
                LocalDateTime.now(), page).toList());
        assertThat(booking).hasFieldOrPropertyWithValue("id", booking.getId());
        assertThat(booking.getItem()).hasFieldOrPropertyWithValue("id", booking.getItem().getId());
        assertThat(booking.getBooker()).hasFieldOrPropertyWithValue("id", booking.getBooker().getId());
        assertThat(booking).hasFieldOrPropertyWithValue("start", booking.getStart());
        assertThat(booking).hasFieldOrPropertyWithValue("end", booking.getEnd());
        assertThat(booking).hasFieldOrPropertyWithValue("status", booking.getStatus());
    }

    @Test
    public void findByBookerIdAndEndIsBefore() {
        booking.setEnd(LocalDateTime.now().minusHours(2));
        assertEquals(List.of(booking), bookingRepository.findByBookerIdAndEndIsBefore(booking.getBooker().getId(),
                LocalDateTime.now(), page).toList());
        assertThat(booking).hasFieldOrPropertyWithValue("id", booking.getId());
        assertThat(booking.getItem()).hasFieldOrPropertyWithValue("id", booking.getItem().getId());
        assertThat(booking.getBooker()).hasFieldOrPropertyWithValue("id", booking.getBooker().getId());
        assertThat(booking).hasFieldOrPropertyWithValue("start", booking.getStart());
        assertThat(booking).hasFieldOrPropertyWithValue("end", booking.getEnd());
        assertThat(booking).hasFieldOrPropertyWithValue("status", booking.getStatus());
    }

    @Test
    public void findAllByItemOwnerIdAndStatusIs() {
        assertEquals(List.of(booking2), bookingRepository.findAllByItemOwnerIdAndStatusIs(item.getOwner().getId(), Status.WAITING, page).toList());
        assertThat(booking2).hasFieldOrPropertyWithValue("id", booking2.getId());
        assertThat(booking2.getItem()).hasFieldOrPropertyWithValue("id", booking2.getItem().getId());
        assertThat(booking2.getBooker()).hasFieldOrPropertyWithValue("id", booking2.getBooker().getId());
        assertThat(booking2).hasFieldOrPropertyWithValue("start", booking2.getStart());
        assertThat(booking2).hasFieldOrPropertyWithValue("end", booking2.getEnd());
        assertThat(booking2).hasFieldOrPropertyWithValue("status", booking2.getStatus());

        assertEquals(List.of(), bookingRepository.findAllByItemOwnerIdAndStatusIs(item.getOwner().getId(),
                Status.APPROVED, page).toList());

        booking2.setStatus(Status.APPROVED);
        assertEquals(List.of(booking2), bookingRepository.findAllByItemOwnerIdAndStatusIs(item.getOwner().getId(),
                Status.APPROVED, page).toList());
        assertThat(booking2).hasFieldOrPropertyWithValue("id", booking2.getId());
        assertThat(booking2.getItem()).hasFieldOrPropertyWithValue("id", booking2.getItem().getId());
        assertThat(booking2.getBooker()).hasFieldOrPropertyWithValue("id", booking2.getBooker().getId());
        assertThat(booking2).hasFieldOrPropertyWithValue("start", booking2.getStart());
        assertThat(booking2).hasFieldOrPropertyWithValue("end", booking2.getEnd());
        assertThat(booking2).hasFieldOrPropertyWithValue("status", booking2.getStatus());
    }

    @Test
    public void findAllByItemIdAndBookerIdAndStatusIsAndEndBefore() {
        booking.setEnd(LocalDateTime.now().minusHours(2));
        assertEquals(List.of(booking), bookingRepository.findAllByItemIdAndBookerIdAndStatusIsAndEndBefore(booking.getItem().getId(),
                booking.getBooker().getId(), Status.WAITING, LocalDateTime.now()));
        assertThat(booking).hasFieldOrPropertyWithValue("id", booking.getId());
        assertThat(booking.getItem()).hasFieldOrPropertyWithValue("id", booking.getItem().getId());
        assertThat(booking.getBooker()).hasFieldOrPropertyWithValue("id", booking.getBooker().getId());
        assertThat(booking).hasFieldOrPropertyWithValue("start", booking.getStart());
        assertThat(booking).hasFieldOrPropertyWithValue("end", booking.getEnd());
        assertThat(booking).hasFieldOrPropertyWithValue("status", booking.getStatus());

        assertEquals(List.of(), bookingRepository.findAllByItemIdAndBookerIdAndStatusIsAndEndBefore(99L,
                booking.getBooker().getId(), Status.WAITING, LocalDateTime.now()));

        booking.setStatus(Status.APPROVED);
        assertEquals(List.of(booking), bookingRepository.findAllByItemIdAndBookerIdAndStatusIsAndEndBefore(booking.getItem().getId(),
                booking.getBooker().getId(), Status.APPROVED, LocalDateTime.now()));
        assertThat(booking).hasFieldOrPropertyWithValue("id", booking.getId());
        assertThat(booking.getItem()).hasFieldOrPropertyWithValue("id", booking.getItem().getId());
        assertThat(booking.getBooker()).hasFieldOrPropertyWithValue("id", booking.getBooker().getId());
        assertThat(booking).hasFieldOrPropertyWithValue("start", booking.getStart());
        assertThat(booking).hasFieldOrPropertyWithValue("end", booking.getEnd());
        assertThat(booking).hasFieldOrPropertyWithValue("status", booking.getStatus());
    }

    @Test
    public void findFirstByItemIdAndStartBeforeAndStatus() {
        booking.setStart(LocalDateTime.now().minusDays(2));
        Optional<Booking> booking = bookingRepository.findFirstByItemIdAndStartBeforeAndStatus(item2.getId(),
                LocalDateTime.now(), Status.WAITING, SORT_BY_START_DESC);
        assertEquals(booking, bookingRepository.findFirstByItemIdAndStartBeforeAndStatus(item2.getId(),
                LocalDateTime.now(), Status.WAITING, SORT_BY_START_DESC));
        assertThat(booking).isPresent();
    }

    @Test
    public void findFirstByItemIdAndStartAfterAndStatus() {
        Optional<Booking> booking = bookingRepository.findFirstByItemIdAndStartAfterAndStatus(item2.getId(),
                LocalDateTime.now(), Status.WAITING, SORT_BY_START_DESC);
        assertEquals(booking, booking);
        assertThat(booking).isPresent();
    }

    @Test
    public void findByItemIdAndStatus() {
        List<Booking> booking = bookingRepository.findByItemIdAndStatus(item2.getId(), Status.WAITING);
        assertEquals(booking, booking);
    }
}