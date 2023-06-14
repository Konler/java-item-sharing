package ru.practicum.shareit.request.service;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import ru.practicum.shareit.PageSetup;
import ru.practicum.shareit.exceptions.NotFoundException;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.request.dto.AddItemRequestDto;
import ru.practicum.shareit.request.dto.ItemRequestMapper;
import ru.practicum.shareit.request.model.ItemRequest;
import ru.practicum.shareit.request.repository.ItemRequestRepository;
import ru.practicum.shareit.user.model.User;
import ru.practicum.shareit.user.repository.UserRepository;
import ru.practicum.shareit.user.service.UserService;
import ru.practicum.shareit.user.service.UserServiceImpl;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class ItemRequestServiceImplTest {
    private static final Sort SORT_BY_CREATED_DESC = Sort.by("created").descending();
    @Mock
    private ItemRequestRepository itemRequestRepository;
    @Mock
    private UserRepository userRepository;
    @Mock
    private UserService userService;
    @Mock
    private UserServiceImpl userServiceImpl;
    @InjectMocks
    private ItemRequestServiceImpl itemRequestService;
    private User user;
    private User user2;
    private AddItemRequestDto addItemRequestDto;
    private ItemRequest itemRequest;
    private Item item;

    @BeforeEach
    void setUp() {
        user = User.builder()
                .id(1L)
                .name("Name")
                .email("username@gmail.com")
                .build();
        user2 = User.builder()
                .id(2L)
                .name("Name2")
                .email("user2name@gmail.com")
                .build();
        item = Item.builder()
                .id(1L)
                .name("Item Name")
                .description("Item description")
                .owner(user)
                .available(true)
                .request(itemRequest)
                .build();
        itemRequest = ItemRequest.builder()
                .id(1L)
                .description("Description")
                .requestor(user)
                .created(LocalDateTime.now())
                .build();
        addItemRequestDto = AddItemRequestDto.builder()
                .id(itemRequest.getId())
                .requestor(itemRequest.getRequestor().getId())
                .description("Description")
                .created(itemRequest.getCreated())
                .items(List.of())
                .build();
    }

    @Test
    public void addItemRequestAndReturnSavedRequest() {
        when(itemRequestRepository.save(any())).thenReturn(itemRequest);

        AddItemRequestDto actualNewRequest = itemRequestService.addRequest(addItemRequestDto, user.getId());
        assertNotNull(actualNewRequest);
        assertEquals(addItemRequestDto, actualNewRequest);
        assertEquals(addItemRequestDto.getId(), actualNewRequest.getId());
        assertEquals(addItemRequestDto.getDescription(), actualNewRequest.getDescription());
        verify(itemRequestRepository, times(1)).save(any());
    }

    @Test
    public void getUserRequests() {
        Long userId = user.getId();
        List<ItemRequest> requestList = new ArrayList<>();
        requestList.add(itemRequest);
        List<AddItemRequestDto> addItemList = List.of(
                ItemRequestMapper.toAddItemRequest(requestList.get(0)));

        when(userServiceImpl.validateUser(anyLong())).thenReturn(user);
        when(itemRequestRepository.findAllByRequestorId(anyLong()))
                .thenReturn(requestList);
        List<AddItemRequestDto> actualRequestList = itemRequestService.getUserRequests(userId);
        assertEquals(addItemList, actualRequestList);
        verify(itemRequestRepository, times(1)).findAllByRequestorId(userId);
    }

    @Test
    public void getOtherUsersRequests() {
        int from = 0;
        int size = 5;
        Pageable page = new PageSetup(from, size, SORT_BY_CREATED_DESC);
        Long userId = user.getId();
        List<AddItemRequestDto> addItemList = Collections.emptyList();

        when(userServiceImpl.validateUser(anyLong())).thenReturn(user);
        when(itemRequestRepository.findAllByRequestorIdNot(userId, page)).thenReturn(Page.empty());
        List<AddItemRequestDto> actualRequestList = itemRequestService.getOtherUsersRequests(userId, from, size);
        assertEquals(addItemList, actualRequestList);
        verify(itemRequestRepository, times(1)).findAllByRequestorIdNot(userId, page);
    }

    @Test
    public void getItemRequestById() {
        AddItemRequestDto expectedRequest = ItemRequestMapper.toAddItemRequest(itemRequest);

        when(itemRequestRepository.validateItemRequest(anyLong())).thenReturn(itemRequest);
        AddItemRequestDto actualRequest = itemRequestService.getItemRequestById(user.getId(), itemRequest.getId());
        assertNotNull(actualRequest);
        assertEquals(expectedRequest.getId(), actualRequest.getId());
        assertEquals(expectedRequest.getDescription(), actualRequest.getDescription());
        assertEquals(expectedRequest, actualRequest);
    }

    @Test
    public void getItemRequestByNotFoundId() {
        when(itemRequestRepository.validateItemRequest(itemRequest.getId())).thenThrow(new NotFoundException(""));
        assertThrows(NotFoundException.class, () -> itemRequestService.getItemRequestById(user.getId(), itemRequest.getId()));
    }
}