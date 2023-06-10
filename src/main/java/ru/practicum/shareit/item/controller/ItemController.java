package ru.practicum.shareit.item.controller;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;
import ru.practicum.shareit.item.dto.CommentDto;
import ru.practicum.shareit.item.dto.ItemDto;
import ru.practicum.shareit.item.service.ItemService;
import ru.practicum.shareit.messages.LogMessages;

import javax.validation.Valid;
import java.util.List;

@Slf4j
@RestController
@RequiredArgsConstructor
@RequestMapping("/items")
public class ItemController {
    private final ItemService itemService;
    private static final String X_SHARER_USER_ID = "X-Sharer-User-Id";

    @PostMapping
    public ItemDto addItem(@RequestHeader(X_SHARER_USER_ID) Long userId,
                           @Valid @RequestBody ItemDto itemDto) {
        log.info(LogMessages.ADD_REQUEST.toString(), itemDto);
        return itemService.addItem(userId, itemDto);
    }

    @PatchMapping("/{itemId}")
    public ItemDto updateItem(@PathVariable Long itemId,
                              @RequestBody ItemDto itemDto,
                              @RequestHeader(X_SHARER_USER_ID) Long userId) {
        log.info(LogMessages.RENEWAL_REQUEST.toString(), itemId, itemDto);
        return itemService.updateItem(itemId, itemDto, userId);
    }

    @GetMapping("/{itemId}")
    public ItemDto getItemById(@RequestHeader(X_SHARER_USER_ID) Long ownerId,
                               @PathVariable Long itemId) {
        log.info(LogMessages.GET_BY_ID_REQUEST.toString(), itemId);
        return itemService.getItemById(itemId, ownerId);
    }

    @GetMapping
    public List<ItemDto> getItemsByUserId(@RequestHeader(X_SHARER_USER_ID) Long userId) {
        log.info(LogMessages.GET_ALL_REQUEST.toString());
        return itemService.getItemsByUserId(userId);
    }

    @GetMapping("/search")
    public List<ItemDto> searchItem(@RequestParam String text) {
        log.info(LogMessages.SEARCH_REQUEST.toString());
        return itemService.searchItem(text);
    }

    @PostMapping("/{itemId}/comment")
    public CommentDto addComment(@RequestHeader(value = X_SHARER_USER_ID) Long userId,
                                 @PathVariable Long itemId,
                                 @Valid @RequestBody CommentDto commentDto) {
        log.info(LogMessages.COMMENT_REQUEST.toString(), itemId, userId);
        return itemService.addComment(userId, itemId, commentDto);
    }
}