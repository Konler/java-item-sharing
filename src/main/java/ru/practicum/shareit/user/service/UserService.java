package ru.practicum.shareit.user.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import ru.practicum.shareit.exceptions.ValidationException;
import ru.practicum.shareit.user.dto.UserDTO;
import ru.practicum.shareit.user.mapper.UserMapper;
import ru.practicum.shareit.user.model.User;
import ru.practicum.shareit.user.repository.UserRepository;

import javax.validation.constraints.NotNull;
import java.util.List;

@Slf4j
@Component
@RequiredArgsConstructor
public class UserService {
    private final UserRepository repository;

    public User getOne(Long userId) {
        return repository.getUser(userId);
    }

    public List<User> getAll() {
        return repository.getAllUsers();
    }

    public User create(UserDTO userDTO) {
        validateUserEmail(userDTO);
        return repository.createUser(UserMapper.toUser(userDTO));
    }

    public User update(Long userId, User user) {
        return repository.updateUser(userId, user);
    }

    public void delete(Long userId) {
        repository.deleteUser(userId);
    }

    private void validateUserEmail(@NotNull UserDTO userDTO) {
        if (userDTO.getEmail() == null || userDTO.getEmail().isBlank()) {
            log.warn("Пустой email");
            throw new ValidationException("Пустой email");
        }
    }

}
