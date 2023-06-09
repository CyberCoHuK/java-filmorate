package ru.yandex.practicum.filmorate.storage.user;

import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;
import ru.yandex.practicum.filmorate.exceptions.ObjectAlreadyExistException;
import ru.yandex.practicum.filmorate.exceptions.ObjectNotFoundException;
import ru.yandex.practicum.filmorate.model.User;

import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;

@Slf4j
@Component
public class InMemoryUserStorage implements UserStorage {
    private final HashMap<Integer, User> users = new HashMap<>();
    private static int userId = 1;

    @Override
    public Collection<User> findAllUsers() {
        log.info("Текущее количество пользователей: {}", users.size());
        return users.values();
    }

    @Override
    public User createUser(User user) {
        if (users.containsKey(user.getId())) {
            throw new ObjectAlreadyExistException("Пользователь с такой почтой уже существует");
        } else {
            validate(user);
            user.setId(userId++);
            user.setFriendsList(new HashSet<>());
            log.info("Добавлен пользователь:{}", user);
            users.put(user.getId(), user);
            return user;
        }
    }

    @Override
    public User updateUser(User user) {
        isExist(user.getId());
        if (users.get(user.getId()).equals(user)) {
            throw new ObjectAlreadyExistException(String
                    .format("Такой пользователь уже существует c ID = %s", user.getId()));
        }
        log.info("Обновлен пользователь:{}", user);
        users.replace(user.getId(), user);
        return user;
    }

    @Override
    public User getById(int userId) {
        isExist(userId);
        log.info("Пользователь {} возвращен", users.get(userId));
        return users.get(userId);
    }

    @Override
    public User addFriend(int userId, int friendId) {
        isExist(userId);
        isExist(friendId);
        if (users.get(userId).getFriendsList().contains(friendId)) {
            throw new ObjectAlreadyExistException("Пользователь уже добавлен в друзья");
        }
        users.get(userId).getFriendsList().add(friendId);
        users.get(friendId).getFriendsList().add(userId);
        log.info("Пользователь {} добавлен в друзья пользователю {}", users.get(userId), users.get(friendId));
        return users.get(friendId);
    }

    @Override
    public void deleteFriend(int userId, int friendId) {
        isExist(userId);
        isExist(friendId);
        log.info("Пользователь {} удалил из друзей пользователя {}", users.get(userId), users.get(friendId));
        users.get(userId).getFriendsList().remove(friendId);
        users.get(friendId).getFriendsList().remove(userId);
    }

    @Override
    public Collection<User> getFriends(int userId) {
        isExist(userId);
        Collection<User> friendsList = new HashSet<>();
        if (users.get(userId).getFriendsList() != null && users.get(userId).getFriendsList().size() > 0) {
            for (int id : users.get(userId).getFriendsList()) {
                friendsList.add(users.get(id));
            }
            log.info("Запрос получения списка друзей пользователя {} выполнен", userId);
            return friendsList;
        }
        return Collections.emptyList();
    }

    @Override
    public Collection<User> getMutualFriends(int userId, int secondUserId) {
        log.info("Список общих друзей {} и {} отправлен", userId, secondUserId);
        isExist(userId);
        isExist(secondUserId);
        Collection<User> friendsList = new HashSet<>();
        for (int id : users.get(userId).getFriendsList()) {
            if (users.get(secondUserId).getFriendsList().contains(id)) {
                friendsList.add(users.get(id));
            }
        }
        log.info("Список общих друзей {} и {} отправлен", userId, secondUserId);
        return friendsList;
    }

    private void validate(User user) {
        if (StringUtils.isEmpty(user.getName())) {
            user.setName(user.getLogin());
        }
    }

    public void isExist(int userId) {
        if (!users.containsKey(userId)) {
            throw new ObjectNotFoundException("Пользователя с таким " + userId + " не существует");
        }
    }

    public String deleteUserById(int userId) {
        return "Пользователь user_id=" + userId + " успешно удален.";
    }
}