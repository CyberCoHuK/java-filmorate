package ru.yandex.practicum.filmorate.storage.user;

import ru.yandex.practicum.filmorate.model.User;

import java.util.Collection;

public interface UserStorage {
    Collection<User> findAllUsers();

    User createUser(User user);

    User updateUser(User user);

    User getById(Long userId);

    User addFriend(Long userId, Long friendId);

    void deleteFriend(Long userId, Long friendId);

    Collection<User> getFriends(Long userId);

    Collection<User> getMutualFriends(Long userId, Long secondUserId);

    String deleteUserById(Long userId);

    void isExist(Long userId);
}
