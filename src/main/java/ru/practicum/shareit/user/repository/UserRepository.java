package ru.practicum.shareit.user.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import ru.practicum.shareit.exceptions.NotFoundException;
import ru.practicum.shareit.messages.LogMessages;
import ru.practicum.shareit.user.model.User;

public interface UserRepository extends JpaRepository<User, Long> {

    default User validateUser(Long userId) {
        return findById(userId).orElseThrow(() -> new NotFoundException(
                LogMessages.NOT_FOUND.toString() + userId));
    }
}