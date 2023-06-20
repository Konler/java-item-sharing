package ru.practicum.shareit.request.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import ru.practicum.shareit.PageSetup;
import ru.practicum.shareit.request.dto.AddItemRequestDto;
import ru.practicum.shareit.request.dto.ItemRequestMapper;
import ru.practicum.shareit.request.model.ItemRequest;
import ru.practicum.shareit.request.repository.ItemRequestRepository;
import ru.practicum.shareit.user.model.User;
import ru.practicum.shareit.user.service.UserServiceImpl;

import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

@Slf4j
@Service
@RequiredArgsConstructor
public class ItemRequestServiceImpl implements ItemRequestService {
    private static final Sort SORT_BY_CREATED_DESC = Sort.by("created").descending();
    private final UserServiceImpl userService;
    private final ItemRequestRepository itemRequestRepository;

    @Override
    public AddItemRequestDto addRequest(AddItemRequestDto addItemRequestDto, Long requestorId) {
        User user = userService.validateUser(requestorId);
        ItemRequest itemRequest = ItemRequestMapper.toItemRequest(addItemRequestDto, user);
        itemRequest.setRequestor(user);
        itemRequest.setCreated(LocalDateTime.now());
        return ItemRequestMapper.toAddItemRequest(itemRequestRepository.save(itemRequest));
    }

    @Override
    public List<AddItemRequestDto> getUserRequests(Long requestorId) {
        User user = userService.validateUser(requestorId);
        return itemRequestRepository.findAllByRequestorId(user.getId()).stream()
                .map(ItemRequestMapper::toAddItemRequest)
                .collect(Collectors.toList());
    }

    @Override
    public List<AddItemRequestDto> getOtherUsersRequests(Long userId, Integer from, Integer size) {
        userService.validateUser(userId);
        PageRequest pageable = new PageSetup(from, size, SORT_BY_CREATED_DESC);
        return itemRequestRepository.findAllByRequestorIdNot(userId, pageable)
                .map(ItemRequestMapper::toAddItemRequest)
                .getContent();
    }

    @Override
    public AddItemRequestDto getItemRequestById(Long userId, Long requestId) {
        userService.validateUser(userId);
        ItemRequest itemRequest = itemRequestRepository.validateItemRequest(requestId);
        return ItemRequestMapper.toAddItemRequest(itemRequest);
    }
}
