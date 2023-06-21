package ru.practicum.shareit.request;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import ru.practicum.shareit.messages.LogMessages;
import ru.practicum.shareit.request.dto.AddItemRequestDto;

import javax.validation.Valid;
import javax.validation.constraints.Min;
import javax.validation.constraints.PositiveOrZero;

@Slf4j
@Validated
@Controller
@RequestMapping(path = "/requests")
@RequiredArgsConstructor
public class ItemRequestController {
    private final ItemRequestClient itemRequestClient;

    @PostMapping
    public ResponseEntity<Object> addRequest(@Valid @RequestBody AddItemRequestDto addItemRequestDto,
                                             @RequestHeader("X-Sharer-User-Id") Long requestorId) {
        log.info(LogMessages.ADD_ITEM_REQUEST_REQUEST.toString(), addItemRequestDto, requestorId);
        return itemRequestClient.addRequest(requestorId, addItemRequestDto);
    }

    @GetMapping
    public ResponseEntity<Object> getUserRequests(@RequestHeader("X-Sharer-User-Id") Long requestorId) {
        log.info(LogMessages.GET_REQUESTS.toString(), requestorId);
        return itemRequestClient.getUserRequests(requestorId);
    }

    @GetMapping("all")
    public ResponseEntity<Object> getOtherUsersRequests(@RequestHeader("X-Sharer-User-Id") Long userId,
                                                        @RequestParam(name = "from", defaultValue = "0") @PositiveOrZero Integer from,
                                                        @RequestParam(name = "size", defaultValue = "10") @Min(1) Integer size) {
        log.info(LogMessages.GET_OTHER_REQUESTS.toString(), userId, from, size);
        return itemRequestClient.getOtherUsersRequests(userId, from, size);
    }

    @GetMapping("{requestId}")
    public ResponseEntity<Object> getItemRequestById(@RequestHeader("X-Sharer-User-Id") Long userId,
                                                     @PathVariable Long requestId) {
        log.info(LogMessages.GET_REQUEST.toString(), requestId, userId);
        return itemRequestClient.getItemRequestById(userId, requestId);
    }
}