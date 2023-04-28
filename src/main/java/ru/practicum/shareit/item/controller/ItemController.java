package ru.practicum.shareit.item.controller;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import ru.practicum.shareit.item.dto.ItemDto;
import ru.practicum.shareit.item.mapper.ItemMapper;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.item.service.ItemServiceImpl;

import java.util.List;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/items")
@Slf4j
@RequiredArgsConstructor
public class ItemController {
    private static final String X_SHARER_USER_ID = "X-Sharer-User-Id";
    private final ItemServiceImpl itemServiceImpl;

    @GetMapping(path = "/search")
    List<ItemDto> searchText(@RequestHeader(X_SHARER_USER_ID) Long ownerId,
                             @RequestParam String text) {
        log.debug("{}.searchText()", this.getClass().getName());
        return itemServiceImpl.searchText(text).stream().map(ItemMapper::toItemDTO).collect(Collectors.toList());
    }

    @GetMapping(path = "/{itemId}")
    ItemDto getOne(@RequestHeader(X_SHARER_USER_ID) Long ownerId,
                   @PathVariable Long itemId) {
        log.debug("{}.getOne({})", this.getClass().getName(), itemId);
        return ItemMapper.toItemDTO(itemServiceImpl.getOne(itemId));
    }

    @GetMapping
    List<ItemDto> getAll(@RequestHeader(X_SHARER_USER_ID) Long ownerId) {
        log.debug("{}.getAll()", this.getClass().getName());
        return itemServiceImpl.getAll(ownerId).stream().map(ItemMapper::toItemDTO).collect(Collectors.toList());
    }

    @PostMapping
    ItemDto add(@RequestHeader(X_SHARER_USER_ID) Long ownerId,
                @Validated @RequestBody ItemDto itemDTO) {
        log.debug("{}.create({})", this.getClass().getName(), itemDTO);
        Item item = itemServiceImpl.create(ownerId, ItemMapper.toItem(itemDTO));
        return ItemMapper.toItemDTO(item);
    }

    @PatchMapping(path = "/{itemId}")
    ItemDto update(@RequestHeader(X_SHARER_USER_ID) Long ownerId,
                   @PathVariable Long itemId,
                   @Validated @RequestBody ItemDto itemDTO) {
        log.debug("{}.update({}, {})", itemId, this.getClass().getName(), itemDTO);
        return ItemMapper.toItemDTO(itemServiceImpl.update(ownerId, itemId, ItemMapper.toItem(itemDTO)));
    }

    @DeleteMapping(path = "/{itemId}")
    void delete(@RequestHeader(X_SHARER_USER_ID) Long ownerId,
                @PathVariable Long itemId) {
        log.debug("{}.delete({})", itemId, this.getClass().getName());

        itemServiceImpl.delete(ownerId, itemId);
    }
}
