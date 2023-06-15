package ru.practicum.shareit.user;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import ru.practicum.shareit.exceptions.NotFoundException;
import ru.practicum.shareit.user.model.User;
import ru.practicum.shareit.user.repository.UserRepository;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

@DataJpaTest
class UserRepositoryTest {
    @Autowired
    private UserRepository userRepository;
    private User user;
    private User savedUser;

    @BeforeEach
    void setUp() {
        user = User.builder()
                .id(1L)
                .name("Name")
                .email("username@gmail.com")
                .build();
        savedUser = userRepository.save(user);
    }

    @Test
    public void validateSavedUser() {
        assertEquals(user.getName(), userRepository.validateUser(savedUser.getId()).getName());
    }

    @Test
    public void validateNotSavedUser() {
        assertThrows(NotFoundException.class, () -> userRepository.validateUser(99L));
    }

    @AfterEach
    public void deleteUser() {
        userRepository.deleteAll();
    }
}