package ru.practicum.shareit.request.dto;

import lombok.experimental.UtilityClass;
import ru.practicum.shareit.item.dto.ItemMapper;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.request.model.ItemRequest;
import ru.practicum.shareit.user.model.User;

import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

@UtilityClass
public class ItemRequestMapper {

    public static ItemRequest toItemRequest(AddItemRequestDto addItemRequestDto, User user) {
        return ItemRequest.builder()
                .id(addItemRequestDto.getId())
                .requestor(user)
                .description(addItemRequestDto.getDescription())
                .created(addItemRequestDto.getCreated())
                .build();
    }

    public static AddItemRequestDto toAddItemRequest(ItemRequest itemRequest) {
        List<Item> items = itemRequest.getItems();
        return AddItemRequestDto.builder()
                .id(itemRequest.getId())
                .requestor(itemRequest.getRequestor().getId())
                .description(itemRequest.getDescription())
                .created(itemRequest.getCreated())
                .items((items == null || items.isEmpty()) ? Collections.emptyList() : items.stream()
                        .map(ItemMapper::toItemDto)
                        .collect(Collectors.toList()))
                .build();
    }
}
