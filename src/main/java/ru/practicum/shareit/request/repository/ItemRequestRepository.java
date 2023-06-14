package ru.practicum.shareit.request.repository;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import ru.practicum.shareit.exceptions.NotFoundException;
import ru.practicum.shareit.messages.LogMessages;
import ru.practicum.shareit.request.model.ItemRequest;

import java.util.List;

public interface ItemRequestRepository extends JpaRepository<ItemRequest, Long> {

    default ItemRequest validateItemRequest(Long requestId) {
        return findById(requestId).orElseThrow(() -> new NotFoundException(
                LogMessages.NOT_FOUND.toString() + requestId));
    }

    List<ItemRequest> findAllByRequestorId(Long requestorId);

    Page<ItemRequest> findAllByRequestorIdNot(Long userId, Pageable pageable);
}