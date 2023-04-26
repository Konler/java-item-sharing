package ru.practicum.shareit.item.repository;

import ru.practicum.shareit.item.model.Item;

import java.util.List;

public interface ItemRepository {

    Item getItem(Long itemId);

    List<Item> getAllItems(Long ownerId);

    List<Item> searchText(String text);

    Item create(Long ownerId, Item item);

    Item update(Long ownerId, Long itemId, Item item);

    void delete(Long ownerId, Long itemId);


}
