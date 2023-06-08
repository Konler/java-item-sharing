package ru.practicum.shareit.user.model;

import lombok.Data;

/**
 * TODO Sprint add-controllers.
 */
@Data
public class User {
    Long id;
    String name;
    String email;

    public User(Long id, String name, String email) {
        this.id = id;
        this.name = name;
        this.email = email;
    }

    public User() {
    }
}
