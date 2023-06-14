package ru.practicum.shareit.booking.repository;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
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

    Page<Booking> findAllByBookerId(Long bookerId, Pageable pageable);

    @Query("SELECT b FROM Booking AS b " +
            "WHERE b.item.owner.id = :ownerId " +
            "AND b.status = :status")
    List<Booking> findAllByOwnerAndStatus(Long ownerId, Status status, Sort sort);

    @Query("SELECT b FROM Booking AS b " +
            "WHERE b.booker.id = :bookerId " +
            "AND :now BETWEEN b.start AND b.end")
    Page<Booking> findByBookerIdAndNowBetweenStartAndEnd(Long bookerId, LocalDateTime now, Pageable pageable);

    Page<Booking> findByBookerIdAndStartIsAfter(Long bookerId, LocalDateTime now, Pageable pageable);

    Page<Booking> findByBookerIdAndEndIsBefore(Long bookerId, LocalDateTime now, Pageable pageable);

    Page<Booking> findByBookerIdAndStatusIs(Long bookerId, Status status, Pageable pageable);

    Page<Booking> findAllByItemOwnerId(Long userId, Pageable pageable);

    @Query("SELECT b FROM Booking AS b " +
            "WHERE b.item.owner.id = :userId " +
            "AND :now BETWEEN b.start AND b.end")
    Page<Booking> findAllCurrentOwnerBookings(Long userId, LocalDateTime now, Pageable pageable);

    Page<Booking> findAllByItemOwnerIdAndStartIsAfter(Long userId, LocalDateTime now, Pageable pageable);

    Page<Booking> findAllByItemOwnerIdAndEndIsBefore(Long userId, LocalDateTime now, Pageable pageable);

    Page<Booking> findAllByItemOwnerIdAndStatusIs(Long userId, Status status, Pageable pageable);

    List<Booking> findAllByItemIdAndBookerIdAndStatusIsAndEndBefore(Long itemId, Long bookerId,
                                                                    Status bookingStatus, LocalDateTime now);

    Optional<Booking> findFirstByItemIdAndStartBeforeAndStatus(Long itemId, LocalDateTime now, Status status, Sort sort);

    Optional<Booking> findFirstByItemIdAndStartAfterAndStatus(Long itemId, LocalDateTime now, Status status, Sort sort);

    List<Booking> findByItemIdAndStatus(Long itemId, Status status);
}