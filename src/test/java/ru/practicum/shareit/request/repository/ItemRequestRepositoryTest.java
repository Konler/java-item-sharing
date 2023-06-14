package ru.practicum.shareit.request.repository;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import ru.practicum.shareit.PageSetup;
import ru.practicum.shareit.exceptions.NotFoundException;
import ru.practicum.shareit.request.model.ItemRequest;
import ru.practicum.shareit.user.model.User;
import ru.practicum.shareit.user.repository.UserRepository;


import java.time.LocalDateTime;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

@DataJpaTest
class ItemRequestRepositoryTest {
    @Autowired
    private ItemRequestRepository itemRequestRepository;
    @Autowired
    private UserRepository userRepository;
    private User user;
    private User savedUser;
    private User user2;
    private User savedUser2;
    private ItemRequest itemRequest;
    private ItemRequest savedItemRequest;
    private ItemRequest itemRequest2;
    private ItemRequest savedItemRequest2;
    private Pageable page;

    @BeforeEach
    void setUp() {
        user = User.builder()
                .name("Name")
                .email("username@gmail.com")
                .build();
        savedUser = userRepository.save(user);
        user2 = User.builder()
                .name("Name2")
                .email("user2name@gmail.com")
                .build();
        savedUser2 = userRepository.save(user2);
        itemRequest = ItemRequest.builder()
                .description("Item request description")
                .requestor(user)
                .created(LocalDateTime.now())
                .build();
        savedItemRequest = itemRequestRepository.save(itemRequest);
        itemRequest2 = ItemRequest.builder()
                .description("Item request2 description")
                .requestor(user2)
                .created(LocalDateTime.now())
                .build();
        savedItemRequest2 = itemRequestRepository.save(itemRequest2);
        int from = 0;
        int size = 10;
        page = new PageSetup(from, size, Sort.unsorted());
    }

    @AfterEach
    public void deleteItem() {
        userRepository.deleteAll();
        itemRequestRepository.deleteAll();
    }

    @Test
    public void validateSavedItemRequest() {
        assertEquals(itemRequest, itemRequestRepository.validateItemRequest(savedItemRequest.getId()));
        assertEquals(itemRequest2, itemRequestRepository.validateItemRequest(savedItemRequest2.getId()));
    }


    @Test
    public void validateNotSavedItemRequest() {
        assertThrows(NotFoundException.class, () -> itemRequestRepository.validateItemRequest(99L));
    }

    @Test
    public void findAllByRequestorId() {
        assertEquals(List.of(itemRequest), itemRequestRepository.findAllByRequestorId(user.getId()));
        assertThat(List.of(itemRequest).get(0)).hasFieldOrPropertyWithValue("id", itemRequest.getId());
        assertThat(List.of(itemRequest).get(0)).hasFieldOrPropertyWithValue("requestor", itemRequest.getRequestor());

        assertEquals(List.of(), itemRequestRepository.findAllByRequestorId(99L));

        assertEquals(List.of(itemRequest2), itemRequestRepository.findAllByRequestorId(user2.getId()));
        assertThat(List.of(itemRequest2).get(0)).hasFieldOrPropertyWithValue("id", itemRequest2.getId());
        assertThat(List.of(itemRequest2).get(0)).hasFieldOrPropertyWithValue("requestor", itemRequest2.getRequestor());
    }

    @Test
    public void findAllByRequestorIdNot() {
        assertEquals(List.of(itemRequest2), itemRequestRepository.findAllByRequestorIdNot(user.getId(), page).toList());
        assertThat(List.of(itemRequest2).get(0)).hasFieldOrPropertyWithValue("id", itemRequest2.getId());
        assertThat(List.of(itemRequest2).get(0)).hasFieldOrPropertyWithValue("requestor", itemRequest2.getRequestor());

        assertEquals(List.of(), itemRequestRepository.findAllByRequestorId(99L));

        assertEquals(List.of(itemRequest), itemRequestRepository.findAllByRequestorIdNot(user2.getId(), page).toList());
        assertThat(List.of(itemRequest).get(0)).hasFieldOrPropertyWithValue("id", itemRequest.getId());
        assertThat(List.of(itemRequest).get(0)).hasFieldOrPropertyWithValue("requestor", itemRequest.getRequestor());
    }
}