package ru.practicum.shareit.user.controller;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;
import ru.practicum.shareit.user.dto.UserDTO;
import ru.practicum.shareit.user.mapper.UserMapper;
import ru.practicum.shareit.user.model.User;
import ru.practicum.shareit.user.service.UserService;

import javax.validation.Valid;
import java.util.List;
import java.util.stream.Collectors;

@RestController
@RequestMapping(path = "/users")
@RequiredArgsConstructor
@Slf4j
class UserController {
    private final UserService userService;

    @GetMapping(path = "/{userId}")
    UserDTO getUser(@PathVariable Long userId) {
        log.debug("{}.getOne({})", this.getClass().getName(), userId);
        User user = userService.getOne(userId);
        return UserMapper.toUserDTO(user);
    }

    @GetMapping
    List<UserDTO> getAll() {
        log.debug("{}.getAll()", this.getClass().getName());
        return userService.getAll().stream().map(UserMapper::toUserDTO).collect(Collectors.toList());
    }

    @PostMapping
    UserDTO create(@Valid @RequestBody UserDTO userDTO) {
        log.debug("{}.create({})", this.getClass().getName(), userDTO);
        User user = userService.create(userDTO);
        return UserMapper.toUserDTO(user);
    }

    @PatchMapping(path = "/{userId}")
    UserDTO update(@PathVariable Long userId, @RequestBody @Valid UserDTO userDTO) {
        log.debug("{}.update({}, {})", userId, this.getClass().getName(), userDTO);
        User user = userService.update(userId, UserMapper.toUser(userDTO));
        return UserMapper.toUserDTO(user);
    }

    @DeleteMapping(path = "/{userId}")
    void delete(@PathVariable Long userId) {
        log.debug("{}.delete({})", userId, this.getClass().getName());
        userService.delete(userId);
    }
}
