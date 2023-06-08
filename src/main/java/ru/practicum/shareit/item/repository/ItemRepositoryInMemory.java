package ru.practicum.shareit.item.repository;

import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import ru.practicum.shareit.exceptions.KeyNotFoundException;
import ru.practicum.shareit.item.model.Item;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;
import java.util.stream.Collectors;

@Component
@Slf4j
public class ItemRepositoryInMemory implements ItemRepository {
    private Long lastItemId = 1L;
    private final Map<Long, Item> itemsMap = new TreeMap<>();

    private synchronized Long generateNewId() {
        return lastItemId++;
    }

    @Override
    public Item getItem(Long itemId) {
        Item result = itemsMap.get(itemId);
        if (result == null) {
            throw new KeyNotFoundException("Вещь не найдена");
        }
        log.info("{}.getOne({}})", this.getClass().getName(), itemId);
        return result;
    }

    @Override
    public List<Item> getAllItems(Long ownerId) {
        List<Item> result = itemsMap.values().stream().filter(item -> item.getOwner().equals(ownerId))
                .collect(Collectors.toList());
        log.info("{}.getAll()", this.getClass().getName());
        return result;
    }

    @Override
    public List<Item> searchText(String text) {
        if (text.isEmpty()) {
            return new ArrayList<>();
        }
        final String lowerCaseText = text.toLowerCase();
        List<Item> result = itemsMap.values().stream().filter(item -> item.getAvailable()
                        && (item.getName().toLowerCase().contains(lowerCaseText)
                        || item.getDescription().toLowerCase().contains(lowerCaseText)))
                .collect(Collectors.toList());
        log.info("{}.searchText({})", this.getClass().getName(), text);
        return result;
    }

    @Override
    public Item create(Long ownerId, Item item) {
        item.setId(generateNewId());
        item.setOwner(ownerId);
        itemsMap.put(item.getId(), item);
        log.info("{}.create({})", this.getClass().getName(), item);
        return item;
    }

    @Override
    public Item update(Long ownerId, Long itemId, final Item item) {
        Item storedItem = itemsMap.get(itemId);
        if (storedItem == null || !storedItem.getOwner().equals(ownerId)) {
            throw new KeyNotFoundException("Вещь не найдена");
        }

        if (item.getName() != null) {
            storedItem.setName(item.getName());
        }
        if (item.getDescription() != null) {
            storedItem.setDescription(item.getDescription());
        }
        if (item.getAvailable() != null) {
            storedItem.setAvailable(item.getAvailable());
        }

        log.info("{}.update({}, {})", this.getClass().getName(), itemId, item);
        return storedItem;
    }

    @Override
    public void delete(Long ownerId, Long itemId) {
        Item item = itemsMap.get(itemId);
        if (item == null || !item.getOwner().equals(ownerId)) {
            throw new KeyNotFoundException("Вещь не найдена");
        }
        itemsMap.remove(itemId);
        log.info("{}.delete({})", this.getClass().getName(), itemId);
    }

}
