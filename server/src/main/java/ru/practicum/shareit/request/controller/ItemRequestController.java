package ru.practicum.shareit.request.controller;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import ru.practicum.shareit.messages.LogMessages;
import ru.practicum.shareit.request.dto.AddItemRequestDto;
import ru.practicum.shareit.request.service.ItemRequestService;

import java.util.List;

@Slf4j
@Validated
@RestController
@RequiredArgsConstructor
@RequestMapping(path = "/requests")
public class ItemRequestController {
    private final ItemRequestService itemRequestService;
    private static final String X_SHARER_USER_ID = "X-Sharer-User-Id";

    @PostMapping
    public AddItemRequestDto addRequest(@RequestBody AddItemRequestDto addItemRequestDto,
                                        @RequestHeader(X_SHARER_USER_ID) Long requestorId) {
        log.info(LogMessages.ADD_ITEMREQUEST_REQUEST.toString());
        return itemRequestService.addRequest(addItemRequestDto, requestorId);
    }

    @GetMapping
    public List<AddItemRequestDto> getUserRequests(@RequestHeader(X_SHARER_USER_ID) Long requestorId) {
        return itemRequestService.getUserRequests(requestorId);
    }

    @GetMapping("all")
    public List<AddItemRequestDto> getOtherUsersRequests(@RequestHeader(X_SHARER_USER_ID) Long userId,
                                                         @RequestParam(name = "from", defaultValue = "0") Integer from,
                                                         @RequestParam(name = "size", defaultValue = "10") Integer size) {
        return itemRequestService.getOtherUsersRequests(userId, from, size);
    }

    @GetMapping("{requestId}")
    public AddItemRequestDto getItemRequestById(@RequestHeader(X_SHARER_USER_ID) Long userId,
                                                @PathVariable Long requestId) {
        return itemRequestService.getItemRequestById(userId, requestId);
    }
}