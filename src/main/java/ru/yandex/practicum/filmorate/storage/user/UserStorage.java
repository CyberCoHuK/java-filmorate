package ru.yandex.practicum.filmorate.storage.user;

import ru.yandex.practicum.filmorate.model.User;

import java.util.Collection;

public interface UserStorage {
    Collection<User> findAllUsers();

    User createUser(User user);

    User updateUser(User user);

    User getById(int userId);

    User addFriend(int userId, int friendId);

    void deleteFriend(int userId, int friendId);

    Collection<User> getFriends(int userId);

    Collection<User> getMutualFriends(int userId, int secondUserId);

    void isExist(int userId);
}
