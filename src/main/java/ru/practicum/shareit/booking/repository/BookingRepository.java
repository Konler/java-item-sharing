package ru.practicum.shareit.booking.repository;

import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import ru.practicum.shareit.booking.Status;
import ru.practicum.shareit.booking.model.Booking;
import ru.practicum.shareit.exceptions.NotFoundException;
import ru.practicum.shareit.messages.LogMessages;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

public interface BookingRepository extends JpaRepository<Booking, Long> {

    default Booking validateBooking(Long bookingId) {
        return findById(bookingId).orElseThrow(() -> new NotFoundException(
                LogMessages.NOT_FOUND.toString() + bookingId));
    }

    List<Booking> findAllByBookerId(Long bookerId, Sort sort);

    @Query("SELECT b FROM Booking AS b " +
            "WHERE b.item.owner.id = :ownerId " +
            "AND b.status = :status")
    List<Booking> findAllByOwnerAndStatus(Long ownerId, Status status, Sort order);

    @Query("SELECT b FROM Booking AS b " +
            "JOIN FETCH b.item AS i " +
            "JOIN FETCH i.owner " +
            "JOIN FETCH b.booker " +
            "WHERE b.booker.id = :bookerId " +
            "AND :now BETWEEN b.start AND b.end")
    List<Booking> findByBookerIdAndNowBetweenStartAndEnd(Long bookerId, LocalDateTime now, Sort sort);

    List<Booking> findByBookerIdAndStartIsAfter(Long bookerId, LocalDateTime now, Sort sort);

    List<Booking> findByBookerIdAndEndIsBefore(Long bookerId, LocalDateTime now, Sort sort);

    List<Booking> findByBookerIdAndStatusIs(Long bookerId, Status status, Sort sort);

    List<Booking> findAllByItemOwnerId(Long userId, Sort sort);

    @Query(value = "SELECT b FROM Booking AS b " +
            "JOIN FETCH b.item AS i " +
            "JOIN FETCH i.owner " +
            "JOIN FETCH b.booker " +
            "WHERE b.item.owner.id = :userId " +
            "AND :now BETWEEN b.start AND b.end")
    List<Booking> findAllCurrentOwnerBookings(Long userId, LocalDateTime now, Sort sort);

    List<Booking> findAllByItemOwnerIdAndStartIsAfter(Long userId, LocalDateTime now, Sort sort);

    List<Booking> findAllByItemOwnerIdAndEndIsBefore(Long userId, LocalDateTime now, Sort sort);

    List<Booking> findAllByItemOwnerIdAndStatusIs(Long userId, Status status, Sort sort);

    List<Booking> findAllByItemIdAndBookerIdAndStatusIsAndEndBefore(Long itemId, Long bookerId,
                                                                    Status bookingStatus, LocalDateTime now);

    Optional<Booking> findFirstByItemIdAndStartBeforeAndStatus(Long itemId, LocalDateTime now, Status status, Sort sort);

    Optional<Booking> findFirstByItemIdAndStartAfterAndStatus(Long itemId, LocalDateTime now, Status status, Sort sort);
}