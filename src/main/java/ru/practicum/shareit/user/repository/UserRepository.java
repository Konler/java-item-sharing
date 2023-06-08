package ru.practicum.shareit.user.repository;

import ru.practicum.shareit.user.model.User;

import java.util.List;

public interface UserRepository {

    User getUser(Long userId);

    List<User> getAllUsers();

    User createUser(User user);

    User updateUser(Long userId, User user);

    void deleteUser(Long userId);
}
