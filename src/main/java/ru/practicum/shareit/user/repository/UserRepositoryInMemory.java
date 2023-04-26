package ru.practicum.shareit.user.repository;

import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import ru.practicum.shareit.exceptions.DublException;
import ru.practicum.shareit.exceptions.KeyNotFoundException;
import ru.practicum.shareit.exceptions.UserNotFoundException;
import ru.practicum.shareit.user.model.User;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;


@Slf4j
@Component
public class UserRepositoryInMemory implements UserRepository {
    private final Map<Long, User> usersMap = new TreeMap<>();
    private final Map<Long, String> emailsMap = new TreeMap<>();
    private Long id = 1L;

    private Long generateUserId() {
        return id++;
    }

    public User getByEmail(String email) {
        User newUser = new User();
        for (User user : usersMap.values()) {
            if (user.getEmail().equals(email)) {
                newUser = user;
            }
        }
        return newUser;
    }

    @Override
    public User getUser(Long userId) {
        User result = usersMap.get(userId);
        log.info("{}.getUser({}})", this.getClass().getName(), userId);
        return result;
    }

    @Override
    public List<User> getAllUsers() {
        List<User> result = new ArrayList<>(usersMap.values());
        log.info("{}.getAll()", this.getClass().getName());
        return result;
    }

    @Override
    public User createUser(User user) {
        for (User userMap : usersMap.values()) {
            if (userMap.getEmail().equals(user.getEmail())) {
                throw new DublException("Адрес электронной почты пользователя уже существует!");
            }
        }
        user.setId(generateUserId());
        usersMap.put(user.getId(), user);
        emailsMap.put(user.getId(), user.getEmail());
        log.info("{}.created({})", this.getClass().getName(), user);
        return user;
    }

    @Override
    public User updateUser(Long userId, User user) {
        if (usersMap.containsKey(userId)) {
            User userInMap = usersMap.get(userId);
            if (user.getEmail() != null) {
                if (emailsMap.containsValue(user.getEmail())) {
                    User userByEmail = getByEmail(user.getEmail());
                    if (user.getId().equals(userByEmail.getId())) {
                        emailsMap.remove(userId);
                        emailsMap.put(userId, userInMap.getEmail());
                        userInMap.setEmail(user.getEmail());

                    } else {
                        throw new DublException("Почта уже используется");
                    }
                } else {
                    // emailsMap.remove(userInMap.getEmail());
                    //emailsMap.put(userId,user.getEmail());
                    emailsMap.remove(userId);
                    emailsMap.put(userId, userInMap.getEmail());
                    userInMap.setEmail(user.getEmail());

                }
            }
            if (user.getName() != null) {
                userInMap.setName(user.getName());
            }

            usersMap.put(userId, userInMap);
            return userInMap;
        } else {
            throw new UserNotFoundException("Нечегоо обновлять");
        }
    }

    @Override
    public void deleteUser(Long userId) {
        User user = usersMap.get(userId);
        if (user == null) {
            throw new KeyNotFoundException("Пользователь не найден");
        }
        usersMap.remove(userId);
        emailsMap.remove(userId);
        log.info("{}.delete({})", this.getClass().getName(), userId);
    }
}
