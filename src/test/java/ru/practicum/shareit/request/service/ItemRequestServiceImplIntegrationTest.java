package ru.practicum.shareit.request.service;

import lombok.RequiredArgsConstructor;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.transaction.annotation.Transactional;
import ru.practicum.shareit.item.dto.ItemDto;
import ru.practicum.shareit.item.service.ItemService;
import ru.practicum.shareit.request.dto.AddItemRequestDto;
import ru.practicum.shareit.user.dto.UserDto;
import ru.practicum.shareit.user.model.User;
import ru.practicum.shareit.user.service.UserService;

import java.time.LocalDateTime;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;

@Transactional
@SpringBootTest(properties = {"db.name=test"})
@RequiredArgsConstructor(onConstructor_ = @Autowired)
class ItemRequestServiceImplIntegrationTest {
    private final ItemRequestService itemRequestService;
    private final UserService userService;
    private final ItemService itemService;
    private ItemDto itemDto;
    private ItemDto savedItem;
    private User itemOwner;
    private UserDto owner;
    private UserDto requestor;
    private UserDto savedRequestor;
    private AddItemRequestDto savedRequest;

    @BeforeEach
    void setUp() {
        itemOwner = User.builder()
                .name("Name")
                .email("username@gmail.com")
                .build();
        owner = UserDto.builder()
                .name("Name")
                .email("username@gmail.com")
                .build();
        requestor = UserDto.builder()
                .name("Name2")
                .email("user2name@gmail.com")
                .build();
        itemDto = ItemDto.builder()
                .name("Item")
                .description("Item description")
                .available(true)
                .ownerId(itemOwner.getId())
                .build();
    }

    @Test
    public void getAllItemRequests() {
        UserDto savedOwner = userService.addUser(owner);
        savedRequestor = userService.addUser(requestor);
        savedItem = itemService.addItem(savedOwner.getId(), itemDto);
        AddItemRequestDto addItemRequestDto = AddItemRequestDto.builder()
                .description("Description")
                .requestor(savedRequestor.getId())
                .items(List.of(savedItem))
                .created(LocalDateTime.now())
                .build();
        savedRequest = itemRequestService.addRequest(addItemRequestDto, savedRequestor.getId());
        List<AddItemRequestDto> requestsList = itemRequestService.getUserRequests(savedRequestor.getId());
        assertNotNull(requestsList);
        assertEquals(1, requestsList.size());
        assertEquals(savedRequest.getId(), requestsList.get(0).getId());
        assertEquals(savedRequest.getDescription(), requestsList.get(0).getDescription());
        assertEquals(savedRequest.getRequestor(), requestsList.get(0).getRequestor());
    }
}