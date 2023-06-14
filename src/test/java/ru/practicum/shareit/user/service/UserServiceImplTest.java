package ru.practicum.shareit.user.service;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import ru.practicum.shareit.user.dto.UserDto;

import ru.practicum.shareit.user.mapper.UserMapper;
import ru.practicum.shareit.user.model.User;
import ru.practicum.shareit.user.repository.UserRepository;

import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class UserServiceImplTest {
    @Mock
    private UserRepository userRepository;
    @InjectMocks
    private UserServiceImpl userService;

    private UserDto userDto;
    private User user;

    @BeforeEach
    void setUp() {
        user = User.builder()
                .id(1L)
                .name("Name")
                .email("username@gmail.com")
                .build();
        userDto = UserDto.builder()
                .id(1L)
                .name("Name")
                .email("username@gmail.com")
                .build();
    }

    @Test
    public void addUserAndReturnSavedUser() {
        when(userRepository.save(any())).thenReturn(user);

        UserDto actualNewUser = userService.addUser(userDto);
        assertNotNull(actualNewUser);
        assertEquals(userDto, actualNewUser);
        assertEquals(userDto.getId(), actualNewUser.getId());
        assertEquals(userDto.getEmail(), actualNewUser.getEmail());
        verify(userRepository, times(1)).save(any());
    }

    @Test
    public void getAllUsers() {
        List<UserDto> expectedUserList = new ArrayList<>();
        expectedUserList.add(userDto);
        when(userRepository.findAll()).thenReturn(List.of(user));

        List<UserDto> actualUserList = userService.getAllUsers();

        assertFalse(actualUserList.isEmpty());
        assertEquals(1, actualUserList.size());
        assertEquals(expectedUserList, actualUserList);
    }
}