package ru.practicum.shareit.request.service;

import ru.practicum.shareit.request.dto.AddItemRequestDto;

import java.util.List;

public interface ItemRequestService {

    AddItemRequestDto addRequest(AddItemRequestDto addItemRequestDto, Long requestorId);

    List<AddItemRequestDto> getUserRequests(Long requestorId);

    List<AddItemRequestDto> getOtherUsersRequests(Long userId, Integer from, Integer size);

    AddItemRequestDto getItemRequestById(Long userId, Long requestId);
}
