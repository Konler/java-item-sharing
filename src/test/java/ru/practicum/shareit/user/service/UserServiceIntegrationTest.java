package ru.practicum.shareit.user.service;

import lombok.RequiredArgsConstructor;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.transaction.annotation.Transactional;
import ru.practicum.shareit.user.dto.UserDto;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

@Transactional
@SpringBootTest(properties = {"db.name=test"})
@RequiredArgsConstructor(onConstructor_ = @Autowired)
class UserServiceIntegrationTest {
    private final UserService userService;
    private UserDto user;
    private UserDto user2;

    @BeforeEach
    void setUp() {
        user = UserDto.builder()
                .name("Name")
                .email("username@gmail.com")
                .build();
        user2 = UserDto.builder()
                .name("Name2")
                .email("user2name@gmail.com")
                .build();
    }

    @Test
    public void getAllUsers() {
        userService.addUser(user);
        userService.addUser(user2);
        List<UserDto> expectedUsers = List.of(user, user2);

        List<UserDto> actualUsers = userService.getAllUsers();
        assertEquals(expectedUsers.size(), actualUsers.size());
        assertEquals(expectedUsers.get(0).getName(), actualUsers.get(0).getName());
        assertEquals(expectedUsers.get(0).getEmail(), actualUsers.get(0).getEmail());
    }
}