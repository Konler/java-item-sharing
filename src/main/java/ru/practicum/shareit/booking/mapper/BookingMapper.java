package ru.practicum.shareit.booking.dto;

import lombok.experimental.UtilityClass;
import ru.practicum.shareit.booking.model.Booking;
import ru.practicum.shareit.item.dto.ItemDtoShort;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.user.dto.UserDtoShort;
import ru.practicum.shareit.user.model.User;

@UtilityClass
public class BookingMapper {

    public static Booking toBooking(BookingCreatDto bookingCreatDto, Item item, User user) {
        return Booking.builder()
                .item(item)
                .booker(user)
                .start(bookingCreatDto.getStart())
                .end(bookingCreatDto.getEnd())
                .status(bookingCreatDto.getStatus())
                .build();
    }

    public static BookingDto toBookingDto(Booking booking) {
        return BookingDto.builder()
                .id(booking.getId())
                .itemId((booking.getItem().getId()))
                .item(new ItemDtoShort(booking.getItem().getId(), booking.getItem().getName()))
                .booker(new UserDtoShort(booking.getBooker().getId(), booking.getBooker().getName()))
                .start(booking.getStart())
                .end(booking.getEnd())
                .status(booking.getStatus())
                .build();
    }

    public static BookingDtoLittle toBookingShortDto(Booking booking) {
        if (booking == null) return null;
        return BookingDtoLittle.builder()
                .id(booking.getId())
                .bookerId(booking.getBooker().getId())
                .start(booking.getStart())
                .build();
    }

    public static BookingDtoLittle toBookingShortDto(BookingDto booking) {
        if (booking == null) return null;
        return BookingDtoLittle.builder()
                .id(booking.getId())
                .bookerId(booking.getBooker().getId())
                .start(booking.getStart())
                .build();
    }
}