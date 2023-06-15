package ru.practicum.shareit.user.service;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import ru.practicum.shareit.exceptions.NotFoundException;
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
    private User savedUser;

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
    public void renewalUserWithCorrectData() {
        UserDto userNewDto = UserDto.builder()
                .name("NewName")
                .email("userNEWname@gmail.com")
                .build();
        User updatedUser = new User("NewName","userNEWname@gmail.com");
        UserDto expectedUser = UserMapper.toUserDto(updatedUser);

        when(userRepository.validateUser(anyLong())).thenReturn(user);
        when(userRepository.save(any())).thenReturn(updatedUser);
        UserDto actualNewUser = userService.updateUser(userNewDto, user.getId());
        assertEquals(expectedUser, actualNewUser);
        verify(userRepository, times(1)).save(any());
    }

    @Test
    public void getUserById() {
        UserDto expectedUser = UserMapper.toUserDto(user);

        when(userRepository.validateUser(anyLong())).thenReturn(user);
        UserDto actualUser = userService.getUserById(1L);
        assertNotNull(actualUser);
        assertEquals(expectedUser.getId(), actualUser.getId());
        assertEquals(expectedUser.getEmail(), actualUser.getEmail());
        assertEquals(expectedUser, actualUser);
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

    @Test
    public void deleteUserExists() {
        doNothing().when(userRepository).deleteById(anyLong());

        userService.deleteUserById(1L);
        verify(userRepository, times(1)).deleteById(1L);
    }
    @Test
    public void validateNotSavedUser() {
        assertThrows(NotFoundException.class, () -> userService.validateUser(99L));
    }


}