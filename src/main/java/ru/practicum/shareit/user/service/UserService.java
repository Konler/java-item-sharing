package ru.practicum.shareit.user.service;

import ru.practicum.shareit.user.dto.UserDTO;
import ru.practicum.shareit.user.model.User;

import java.util.List;

public interface UserService {
    User getOne(Long userId);

    List<User> getAll();

    User create(UserDTO userDTO);

    User update(Long userId, User user);

    void delete(Long userId);
}
