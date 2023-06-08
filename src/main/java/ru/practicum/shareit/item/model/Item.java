package ru.practicum.shareit.item.model;

import lombok.Data;

import javax.validation.constraints.NotBlank;

/**
 * TODO Sprint add-controllers.
 */
@Data
public class Item {
    Long id;
    @NotBlank
    String name;
    @NotBlank
    String description;
    @NotBlank
    Boolean available;
    Long owner;
    String request;

    public Item(Long id, String name, String description, Boolean available, Long owner, String request) {
        this.id = id;
        this.name = name;
        this.description = description;
        this.available = available;
        this.owner = owner;
        this.request = request;
    }
}
