package ru.practicum.shareit.item.service;

import ru.practicum.shareit.item.model.Item;

import java.util.List;

public interface ItemService {
    Item getOne(Long itemId);

    List<Item> getAll(Long ownerId);

    List<Item> searchText(String text);

    Item create(Long ownerId, Item item);

    Item update(Long ownerId, Long itemId, Item item);

    void delete(Long ownerId, Long itemId);


}


