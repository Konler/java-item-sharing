package ru.practicum.shareit.item;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import ru.practicum.shareit.item.dto.CommentDto;
import ru.practicum.shareit.item.dto.ItemDto;
import ru.practicum.shareit.messages.LogMessages;

import javax.validation.Valid;
import javax.validation.constraints.Min;
import javax.validation.constraints.PositiveOrZero;

@Slf4j
@Validated
@Controller
@RequestMapping(path = "/items")
@RequiredArgsConstructor
public class ItemController {
    private final ItemClient itemClient;

    @PostMapping
    public ResponseEntity<Object> addItem(@RequestHeader("X-Sharer-User-Id") long userId,
                                          @Valid @RequestBody ItemDto itemDto) {
        log.info(LogMessages.ADD_ITEM.toString(), itemDto, userId);
        return itemClient.addItem(userId, itemDto);
    }

    @PatchMapping("/{itemId}")
    public ResponseEntity<Object> renewalBooking(@PathVariable long itemId,
                                                 @RequestBody ItemDto itemDto,
                                                 @RequestHeader("X-Sharer-User-Id") Long userId) {
        log.info(LogMessages.RENEWAL_ITEM.toString(), itemId, userId);
        return itemClient.renewalItem(userId, itemDto, itemId);
    }

    @GetMapping("/{itemId}")
    public ResponseEntity<Object> getItemById(@RequestHeader("X-Sharer-User-Id") long ownerId,
                                              @PathVariable long itemId) {
        log.info(LogMessages.GET_ITEM_BY_ID.toString(), itemId, ownerId);
        return itemClient.getItemById(ownerId, itemId);
    }

    @GetMapping
    public ResponseEntity<Object> getPersonal(@RequestHeader("X-Sharer-User-Id") long userId,
                                              @RequestParam(name = "from", defaultValue = "0") @PositiveOrZero Integer from,
                                              @RequestParam(name = "size", defaultValue = "10") @Min(1) Integer size) {
        log.info(LogMessages.GET_ALL_REQUEST.toString(), userId, from, size);
        return itemClient.getPersonal(userId, from, size);
    }

    @GetMapping("/search")
    public ResponseEntity<Object> searchItem(@RequestHeader(value = "X-Sharer-User-Id") long userId,
                                             @RequestParam String text,
                                             @RequestParam(name = "from", defaultValue = "0") @PositiveOrZero Integer from,
                                             @RequestParam(name = "size", defaultValue = "10") @Min(1) Integer size) {
        log.info(LogMessages.SEARCH_REQUEST.toString(), text, userId, from, size);
        return itemClient.searchItem(userId, text, from, size);
    }

    @PostMapping("/{itemId}/comment")
    public ResponseEntity<Object> addComment(@RequestHeader(value = "X-Sharer-User-Id") long userId,
                                 @PathVariable long itemId,
                                 @Valid @RequestBody CommentDto commentDto) {
        log.info(LogMessages.COMMENT_REQUEST.toString(), itemId, userId);
        return itemClient.addComment(userId, itemId, commentDto);
    }
}