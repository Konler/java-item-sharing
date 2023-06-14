package ru.practicum.shareit.item.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import ru.practicum.shareit.exceptions.NotFoundException;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.messages.LogMessages;

import java.util.List;

public interface ItemRepository extends JpaRepository<Item, Long> {

    default Item validateItem(Long itemId) {
        return findById(itemId).orElseThrow(() -> new NotFoundException(
                LogMessages.NOT_FOUND.toString() + itemId));
    }

    @Query("SELECT i FROM Item AS i " +
            "WHERE UPPER(i.name) LIKE UPPER(CONCAT('%', ?1, '%')) " +
            "OR UPPER(i.description) LIKE UPPER(CONCAT('%', ?1, '%')) " +
            "AND i.available IS TRUE")
    List<Item> searchItemByText(String text);

    List<Item> findAllByOwnerId(Long userId);
}