package ru.practicum.shareit.item.repository;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import ru.practicum.shareit.PageSetup;
import ru.practicum.shareit.exceptions.NotFoundException;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.user.model.User;
import ru.practicum.shareit.user.repository.UserRepository;

import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

@DataJpaTest
class ItemRepositoryTest {
    @Autowired
    private ItemRepository itemRepository;
    @Autowired
    private UserRepository userRepository;
    private Item item;
    private Item savedItem;
    private Item item2;
    private Item savedItem2;
    private User user;
    private User savedUser;
    private User user2;
    private User savedUser2;
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
        item = Item.builder()
                .name("Item Name")
                .description("Item description")
                .owner(user)
                .available(true)
                .build();
        savedItem = itemRepository.save(item);
        item2 = Item.builder()
                .name("Item2 Name")
                .description("Item2 description")
                .owner(user2)
                .available(true)
                .build();
        savedItem2 = itemRepository.save(item2);
        int from = 0;
        int size = 10;
        page = new PageSetup(from, size, Sort.unsorted());
        ;
    }

    @AfterEach
    public void deleteItems() {
        itemRepository.deleteAll();
        userRepository.deleteAll();
    }

    @Test
    public void validateItem() {
        assertEquals(item.getName(), itemRepository.validateItem(savedItem.getId()).getName());
        assertEquals(item2.getName(), itemRepository.validateItem(savedItem2.getId()).getName());
    }

    @Test
    public void validateNotSavedItem() {
        assertThrows(NotFoundException.class, () -> itemRepository.validateItem(99L));
    }

    @Test
    public void searchItemByHisText() {
        assertEquals(List.of(), itemRepository.searchItemByText("kjfgkjdg", page).toList());
        assertEquals(List.of(), itemRepository.searchItemByText(null, page).toList());
        assertEquals(List.of(item, item2), itemRepository.searchItemByText("Item", page).toList());
        assertEquals(List.of(item2), itemRepository.searchItemByText("Item2", page).toList());
    }

    @Test
    public void findAllItemsByOwnerId() {
        assertEquals(List.of(item), itemRepository.findAllByOwnerId(user.getId(), page).toList());
        assertEquals(List.of(), itemRepository.findAllByOwnerId(99L, page).toList());
        item2.setOwner(user);
        assertEquals(List.of(item, item2), itemRepository.findAllByOwnerId(user.getId(), page).toList());
    }
}