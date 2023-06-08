package ru.practicum.shareit.item.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import ru.practicum.shareit.exceptions.KeyNotFoundException;
import ru.practicum.shareit.exceptions.ValidationException;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.item.repository.ItemRepository;
import ru.practicum.shareit.user.model.User;
import ru.practicum.shareit.user.repository.UserRepository;

import java.util.List;

@Component
@RequiredArgsConstructor
@Slf4j
public class ItemServiceImpl implements ItemService {
    private final ItemRepository itemRepository;
    private final UserRepository userRepository;

    public Item getOne(Long itemId) {
        return itemRepository.getItem(itemId);
    }

    public List<Item> getAll(Long ownerId) {
        checkOwner(ownerId);
        return itemRepository.getAllItems(ownerId);
    }

    public List<Item> searchText(String text) {
        return itemRepository.searchText(text);
    }

    public Item create(Long ownerId, Item item) {
        checkOwner(ownerId);
        validate(item);
        return itemRepository.create(ownerId, item);
    }

    public Item update(Long ownerId, Long itemId, Item item) {
        checkOwner(ownerId);
        return itemRepository.update(ownerId, itemId, item);
    }

    public void delete(Long ownerId, Long itemId) {
        checkOwner(ownerId);
        itemRepository.delete(ownerId, itemId);
    }

    private void validate(Item item) {
        if (item.getAvailable() == null) {
            log.warn("Не указан доступ вещи");
            throw new ValidationException("Не указан доступ вещи");
        }
        if (item.getDescription() == null) {
            log.warn("Описание отсутствует");
            throw new ValidationException("Описание отсутствует");
        }
        if (item.getName() == null || item.getName().isEmpty()) {
            log.warn("Пустое имя");
            throw new ValidationException("Пустое имя");
        }
    }

    private void checkOwner(Long ownerId) {
        User owner = userRepository.getUser(ownerId);
        if (owner == null) {
            throw new KeyNotFoundException("Пользователь не найден");
        }
    }

}
