package ru.practicum.shareit.user.mapper;

import ru.practicum.shareit.user.dto.UserDTO;
import ru.practicum.shareit.user.model.User;

public class UserMapper {

    private UserMapper() {
    }

    public static User toUser(UserDTO userDTO) {
        User user = new User(userDTO.getId(), userDTO.getName(), userDTO.getEmail());
        return user;
    }

    public static UserDTO toUserDTO(User user) {
        return new UserDTO(user.getId(), user.getName(), user.getEmail());
    }
}
