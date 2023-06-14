package ru.practicum.shareit.booking.servce;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import ru.practicum.shareit.booking.Status;
import ru.practicum.shareit.booking.dto.BookingCreatDto;
import ru.practicum.shareit.booking.dto.BookingDto;
import ru.practicum.shareit.booking.mapper.BookingMapper;
import ru.practicum.shareit.booking.model.Booking;
import ru.practicum.shareit.booking.repository.BookingRepository;
import ru.practicum.shareit.booking.service.BookingServiceImpl;
import ru.practicum.shareit.exceptions.BookingException;
import ru.practicum.shareit.exceptions.InvalidIdException;
import ru.practicum.shareit.exceptions.NotFoundException;
import ru.practicum.shareit.exceptions.ValidationException;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.item.repository.ItemRepository;
import ru.practicum.shareit.user.model.User;
import ru.practicum.shareit.user.repository.UserRepository;
import ru.practicum.shareit.user.service.UserServiceImpl;

import java.time.LocalDateTime;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class BookingServiceImplTest {
    @Mock
    private BookingRepository bookingRepository;
    @Mock
    private UserRepository userRepository;
    @Mock
    private ItemRepository itemRepository;
    @Mock
    private UserServiceImpl userService;
    @InjectMocks
    private BookingServiceImpl bookingService;
    private User user;
    private User user2;
    private Item item;
    private Booking booking;
    private BookingCreatDto bookingCreationDto;

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
        booking = Booking.builder()
                .id(1L)
                .start(LocalDateTime.now().plusDays(1))
                .end(LocalDateTime.now().plusDays(2))
                .item(item)
                .booker(user2)
                .status(Status.WAITING)
                .build();
        bookingCreationDto = BookingCreatDto.builder()
                .id(1L)
                .itemId(item.getId())
                .start(booking.getStart())
                .end(booking.getEnd())
                .status(booking.getStatus())
                .build();
    }

    @Test
    public void addBookingWithInvalidTime() {
        when(userService.validateUser(anyLong())).thenReturn(user2);
        when(itemRepository.validateItem(anyLong())).thenReturn(item);
        bookingCreationDto.setStart(LocalDateTime.now().plusHours(5));
        bookingCreationDto.setEnd(LocalDateTime.now().minusHours(5));
        assertThrows(BookingException.class, () -> bookingService.addBooking(bookingCreationDto, user2.getId()));
        verify(userService, times(1)).validateUser(user2.getId());
        verify(itemRepository, times(1)).validateItem(item.getId());
        verify(bookingRepository, never()).save(booking);
    }

    @Test
    public void addBookingWhenNotAvailable() {
        item.setAvailable(false);
        when(userService.validateUser(anyLong())).thenReturn(user2);
        when(itemRepository.validateItem(anyLong())).thenReturn(item);
        assertThrows(ValidationException.class, () -> bookingService.addBooking(bookingCreationDto, user2.getId()));
        verify(bookingRepository, never()).save(any());
        verify(userService, times(1)).validateUser(user2.getId());
        verify(itemRepository, times(1)).validateItem(item.getId());
    }

    @Test
    public void addBookingByOwner() {
        when(userService.validateUser(anyLong())).thenReturn(user);
        when(itemRepository.validateItem(anyLong())).thenReturn(item);
        assertThrows(NotFoundException.class, () -> bookingService.addBooking(bookingCreationDto, user.getId()));
        verify(bookingRepository, never()).save(any());
        verify(userService, times(1)).validateUser(user.getId());
        verify(itemRepository, times(1)).validateItem(item.getId());
    }

    @Test
    public void renewalBookingApproved() {
        long bookingId = booking.getId();
        when(bookingRepository.validateBooking(anyLong())).thenReturn(booking);
        when(userService.validateUser(anyLong())).thenReturn(user);
        Booking exitedBooking = Booking.builder()
                .id(1L)
                .start(LocalDateTime.now().plusDays(1))
                .end(LocalDateTime.now().plusDays(2))
                .item(item)
                .booker(user2)
                .status(Status.APPROVED)
                .build();

        when(bookingRepository.save(any())).thenReturn(exitedBooking);
        BookingDto actualBooking = bookingService.updateBooking(bookingId, user.getId(), true);
        assertEquals(BookingMapper.toBookingDto(exitedBooking), actualBooking);
        verify(bookingRepository, times(1)).save(booking);
    }

    @Test
    public void renewalBookingRejected() {
        long bookingId = booking.getId();
        when(bookingRepository.validateBooking(anyLong())).thenReturn(booking);
        when(userService.validateUser(anyLong())).thenReturn(user);
        Booking exitedBooking = Booking.builder()
                .id(1L)
                .start(LocalDateTime.now().plusDays(1))
                .end(LocalDateTime.now().plusDays(2))
                .item(item)
                .booker(user2)
                .status(Status.REJECTED)
                .build();

        when(bookingRepository.save(any())).thenReturn(exitedBooking);
        BookingDto actualBooking = bookingService.updateBooking(bookingId, user.getId(), false);
        assertEquals(BookingMapper.toBookingDto(exitedBooking), actualBooking);
        verify(bookingRepository, times(1)).save(booking);
    }

    @Test
    public void renewalBookingWithNoRights() {
        long bookingId = booking.getId();
        when(bookingRepository.validateBooking(anyLong())).thenReturn(booking);
        assertThrows(InvalidIdException.class, () -> bookingService.updateBooking(bookingId, 99L, true));
        verify(bookingRepository, never()).save(any());
    }

    @Test
    public void renewalBookingWhenAlreadyApproved() {
        long bookingId = booking.getId();
        booking.setStatus(Status.APPROVED);
        when(bookingRepository.validateBooking(anyLong())).thenReturn(booking);
        when(userService.validateUser(anyLong())).thenReturn(user);
        assertThrows(BookingException.class, () -> bookingService.updateBooking(bookingId, user.getId(), true));
        verify(bookingRepository, never()).save(any());
    }

    @Test
    public void getBookingByUnknown() {
        User user3 = User.builder()
                .id(3L)
                .name("Name3")
                .email("user3name@gmail.com")
                .build();
        long userId = user3.getId();
        long bookingId = booking.getId();
        when(userService.validateUser(anyLong())).thenReturn(user3);
        when(bookingRepository.validateBooking(anyLong())).thenReturn(booking);

        assertThrows(NotFoundException.class, () -> bookingService.getBookingById(bookingId, userId));
        verify(bookingRepository, times(1)).validateBooking(bookingId);
    }

    @Test
    public void getBookingById() {
        long userId = user.getId();
        long bookingId = booking.getId();
        BookingDto exitedBookingDto = BookingMapper.toBookingDto(booking);

        when(userService.validateUser(anyLong())).thenReturn(user);
        when(bookingRepository.validateBooking(anyLong())).thenReturn(booking);
        BookingDto actualBooking = bookingService.getBookingById(bookingId, userId);
        assertEquals(exitedBookingDto, actualBooking);
        verify(userService, times(1)).validateUser(userId);
        verify(bookingRepository, times(1)).validateBooking(bookingId);
    }

    @Test
    public void getAllUserBookings() {
        int from = 0;
        int size = 5;
        User booker = user2;
        List<Booking> expectedList = List.of(booking);
        List<BookingDto> bookingDtoList = List.of(BookingMapper.toBookingDto(booking));
        when(userService.validateUser(anyLong())).thenReturn(user2);
        when(bookingRepository.findAllByBookerId(anyLong(), any(PageRequest.class))).thenReturn(new PageImpl<>(expectedList));
        List<BookingDto> actualList = bookingService.getAllUserBookings(booker.getId(), "ALL", from, size);
        assertEquals(bookingDtoList, actualList);

        when(bookingRepository.findByBookerIdAndStatusIs(anyLong(), any(Status.class), any(PageRequest.class)))
                .thenReturn(new PageImpl<>(expectedList));
        actualList = bookingService.getAllUserBookings(booker.getId(), "WAITING", from, size);
        assertEquals(bookingDtoList, actualList);

        when(bookingRepository.findByBookerIdAndStatusIs(anyLong(), any(Status.class), any(PageRequest.class)))
                .thenReturn(new PageImpl<>(expectedList));
        actualList = bookingService.getAllUserBookings(booker.getId(), "REJECTED", from, size);
        assertEquals(bookingDtoList, actualList);

        booking.setStart(LocalDateTime.now().minusHours(1));
        booking.setEnd(LocalDateTime.now().plusHours(5));
        List<BookingDto> bookingDtoList2 = List.of(BookingMapper.toBookingDto(booking));
        when(bookingRepository.findByBookerIdAndNowBetweenStartAndEnd(anyLong(), any(LocalDateTime.class), any(PageRequest.class)))
                .thenReturn(new PageImpl<>(expectedList));
        actualList = bookingService.getAllUserBookings(booker.getId(), "CURRENT", from, size);
        assertEquals(bookingDtoList2, actualList);

        booking.setStart(LocalDateTime.now().plusHours(1));
        booking.setEnd(LocalDateTime.now().plusHours(5));
        List<BookingDto> bookingDtoList3 = List.of(BookingMapper.toBookingDto(booking));
        when(bookingRepository.findByBookerIdAndStartIsAfter(anyLong(), any(LocalDateTime.class), any(PageRequest.class)))
                .thenReturn(new PageImpl<>(expectedList));
        actualList = bookingService.getAllUserBookings(booker.getId(), "FUTURE", from, size);
        assertEquals(bookingDtoList3, actualList);

        booking.setStart(LocalDateTime.now().minusHours(5));
        booking.setEnd(LocalDateTime.now().plusHours(1));
        List<BookingDto> bookingDtoList4 = List.of(BookingMapper.toBookingDto(booking));
        when(bookingRepository.findByBookerIdAndEndIsBefore(anyLong(), any(LocalDateTime.class), any(PageRequest.class)))
                .thenReturn(new PageImpl<>(expectedList));
        actualList = bookingService.getAllUserBookings(booker.getId(), "PAST", from, size);
        assertEquals(bookingDtoList4, actualList);
    }

    @Test
    void getAllByBookerWithWrongStatus() {
        int from = 0;
        int size = 5;
        String bookingStatus = "UNSUPPORTED_STATUS";
        when(userService.validateUser(anyLong())).thenReturn(user2);
        BookingException ex = assertThrows(BookingException.class, () -> bookingService.getAllUserBookings(user2.getId(),
                String.valueOf("UNSUPPORTED_STATUS"), from, size));
        assertEquals("Unknown state: " + bookingStatus, ex.getMessage());
    }

    @Test
    public void getOwnerAllItemBookings() {
        int from = 0;
        int size = 5;
        User owner = user;
        List<Booking> expectedList = List.of(booking);
        List<BookingDto> bookingDtoList = List.of(BookingMapper.toBookingDto(booking));
        when(userService.validateUser(anyLong())).thenReturn(user);
        when(bookingRepository.findAllByItemOwnerId(anyLong(), any(PageRequest.class))).thenReturn(new PageImpl<>(expectedList));
        List<BookingDto> actualList = bookingService.getOwnerAllItemBookings(owner.getId(), "ALL", from, size);
        assertEquals(bookingDtoList, actualList);

        when(bookingRepository.findAllByItemOwnerIdAndStatusIs(anyLong(), any(Status.class),
                any(PageRequest.class))).thenReturn(new PageImpl<>(expectedList));
        actualList = bookingService.getOwnerAllItemBookings(owner.getId(), "WAITING", from, size);
        assertEquals(bookingDtoList, actualList);

        when(bookingRepository.findAllByItemOwnerIdAndStatusIs(anyLong(), any(Status.class),
                any(PageRequest.class))).thenReturn(new PageImpl<>(expectedList));
        actualList = bookingService.getOwnerAllItemBookings(owner.getId(), "REJECTED", from, size);
        assertEquals(bookingDtoList, actualList);

        booking.setStart(LocalDateTime.now().minusHours(1));
        booking.setEnd(LocalDateTime.now().plusHours(5));
        List<BookingDto> bookingDtoList2 = List.of(BookingMapper.toBookingDto(booking));
        when(bookingRepository.findAllCurrentOwnerBookings(anyLong(), any(LocalDateTime.class),
                any(PageRequest.class))).thenReturn(new PageImpl<>(expectedList));
        actualList = bookingService.getOwnerAllItemBookings(owner.getId(), "CURRENT", from, size);
        assertEquals(bookingDtoList2, actualList);

        booking.setStart(LocalDateTime.now().plusHours(1));
        booking.setEnd(LocalDateTime.now().plusHours(5));
        List<BookingDto> bookingDtoList3 = List.of(BookingMapper.toBookingDto(booking));
        when(bookingRepository.findAllByItemOwnerIdAndStartIsAfter(anyLong(), any(LocalDateTime.class),
                any(PageRequest.class))).thenReturn(new PageImpl<>(expectedList));
        actualList = bookingService.getOwnerAllItemBookings(owner.getId(), "FUTURE", from, size);
        assertEquals(bookingDtoList3, actualList);

        booking.setStart(LocalDateTime.now().minusHours(5));
        booking.setEnd(LocalDateTime.now().minusHours(1));
        List<BookingDto> bookingDtoList4 = List.of(BookingMapper.toBookingDto(booking));
        when(bookingRepository.findAllByItemOwnerIdAndEndIsBefore(anyLong(), any(LocalDateTime.class),
                any(PageRequest.class))).thenReturn(new PageImpl<>(expectedList));
        actualList = bookingService.getOwnerAllItemBookings(owner.getId(), "PAST", from, size);
        assertEquals(bookingDtoList4, actualList);
    }

    @Test
    void getOwnerAllItemBookingsWithWrongStatus() {
        int from = 0;
        int size = 5;
        String bookingStatus = "UNSUPPORTED_STATUS";
        when(userService.validateUser(anyLong())).thenReturn(user);
        BookingException ex = assertThrows(BookingException.class, () -> bookingService.getOwnerAllItemBookings(user.getId(),
                String.valueOf("UNSUPPORTED_STATUS"), from, size));
        assertEquals("Unknown state: " + bookingStatus, ex.getMessage());
    }
}