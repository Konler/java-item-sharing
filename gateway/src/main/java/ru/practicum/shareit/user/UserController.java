package ru.practicum.shareit.user;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;
import ru.practicum.shareit.messages.LogMessages;
import ru.practicum.shareit.user.dto.UserDto;

import javax.validation.Valid;

@Slf4j
@Controller
@RequestMapping(path = "/users")
@RequiredArgsConstructor
public class UserController {
    private final UserClient userClient;

    @PostMapping
    public ResponseEntity<Object> addUser(@Valid @RequestBody UserDto userDto) {
        log.info(LogMessages.ADD_USER.toString(), userDto);
        return userClient.addUser(userDto);
    }

    @PatchMapping("/{userId}")
    public ResponseEntity<Object> renewalUser(@PathVariable long userId, @RequestBody UserDto userDto) {
        log.info(LogMessages.RENEWAL_USER.toString(), userId);
        return userClient.renewalUser(userId, userDto);
    }

    @GetMapping("/{userId}")
    public ResponseEntity<Object> getUserById(@PathVariable long userId) {
        log.info(LogMessages.GET_USER_BY_ID.toString(), userId);
        return userClient.getUserById(userId);
    }

    @GetMapping
    public ResponseEntity<Object> getListOfUsers() {
        log.info(LogMessages.GET_ALL_USERS.toString());
        return userClient.getListOfUsers();
    }

    @DeleteMapping("/{userId}")
    public ResponseEntity<Object> removeUserById(@PathVariable long userId) {
        log.info(LogMessages.REMOVE_REQUEST.toString(), userId);
        return userClient.removeUserById(userId);
    }
}