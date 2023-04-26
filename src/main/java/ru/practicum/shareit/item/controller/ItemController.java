package ru.practicum.shareit.item.controller;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import ru.practicum.shareit.exceptions.ValidationException;
import ru.practicum.shareit.item.dto.ItemDto;
import ru.practicum.shareit.item.mapper.ItemMapper;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.item.service.ItemService;
import ru.practicum.shareit.user.util.ItemErrorResponce;

import java.util.List;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/items")
@Slf4j
@RequiredArgsConstructor
public class ItemController {
    private static final String X_SHARER_USER_ID = "X-Sharer-User-Id";
    private final ItemService itemService;

    @GetMapping(path = "/search")
    List<ItemDto> searchText(@RequestHeader(X_SHARER_USER_ID) Long ownerId,
                             @RequestParam String text) {
        log.debug("{}.searchText()", this.getClass().getName());
        return itemService.searchText(text).stream().map(ItemMapper::toItemDTO).collect(Collectors.toList());
    }

    @GetMapping(path = "/{itemId}")
    ItemDto getOne(@RequestHeader(X_SHARER_USER_ID) Long ownerId,
                   @PathVariable Long itemId) {
        log.debug("{}.getOne({})", this.getClass().getName(), itemId);
        return ItemMapper.toItemDTO(itemService.getOne(itemId));
    }

    @GetMapping
    List<ItemDto> getAll(@RequestHeader(X_SHARER_USER_ID) Long ownerId) {
        log.debug("{}.getAll()", this.getClass().getName());
        return itemService.getAll(ownerId).stream().map(ItemMapper::toItemDTO).collect(Collectors.toList());
    }

    @PostMapping
    ItemDto add(@RequestHeader(X_SHARER_USER_ID) Long ownerId,
                @Validated @RequestBody ItemDto itemDTO) {
        log.debug("{}.create({})", this.getClass().getName(), itemDTO);
        Item item = itemService.create(ownerId, ItemMapper.toItem(itemDTO));
        return ItemMapper.toItemDTO(item);
    }

    @PatchMapping(path = "/{itemId}")
    ItemDto update(@RequestHeader(X_SHARER_USER_ID) Long ownerId,
                   @PathVariable Long itemId,
                   @Validated @RequestBody ItemDto itemDTO) {
        log.debug("{}.update({}, {})", itemId, this.getClass().getName(), itemDTO);
        return ItemMapper.toItemDTO(itemService.update(ownerId, itemId, ItemMapper.toItem(itemDTO)));
    }

    @DeleteMapping(path = "/{itemId}")
    void delete(@RequestHeader(X_SHARER_USER_ID) Long ownerId,
                @PathVariable Long itemId) {
        log.debug("{}.delete({})", itemId, this.getClass().getName());

        itemService.delete(ownerId, itemId);
    }

    @ExceptionHandler
    private ResponseEntity<ItemErrorResponce> handleException(ValidationException e) {
        ItemErrorResponce response = new ItemErrorResponce("Ошибка валидации", System.currentTimeMillis());
        return new ResponseEntity<>(response, HttpStatus.BAD_REQUEST);

    }

}
