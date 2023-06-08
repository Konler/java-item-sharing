package ru.practicum.shareit.item.service;

import ru.practicum.shareit.item.dto.CommentDto;
import ru.practicum.shareit.item.dto.ItemDto;

import java.util.List;

public interface ItemService {
    ItemDto addItem(Long userId, ItemDto itemDto);

    ItemDto updateItem(Long itemId, ItemDto itemDto, Long userId);

    ItemDto getItemById(Long itemId, Long ownerId);

    List<ItemDto> getItemsByUserId(Long userId);

    List<ItemDto> searchItem(String text);

    CommentDto addComment(Long userId, Long itemId, CommentDto commentDto);
}