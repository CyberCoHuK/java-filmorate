package ru.yandex.practicum.filmorate.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import ru.yandex.practicum.filmorate.enums.EventTypes;
import ru.yandex.practicum.filmorate.enums.Operations;
import ru.yandex.practicum.filmorate.model.Event;
import ru.yandex.practicum.filmorate.model.Film;
import ru.yandex.practicum.filmorate.model.User;
import ru.yandex.practicum.filmorate.storage.feed.FeedStorage;
import ru.yandex.practicum.filmorate.storage.film.FilmStorage;
import ru.yandex.practicum.filmorate.storage.user.UserStorage;

import java.util.Collection;

@Service
@RequiredArgsConstructor
public class UserService {

    private final UserStorage userStorage;
    private final FilmStorage filmStorage;
    private final FeedStorage feedStorage;

    public Collection<User> findAllUsers() {
        return userStorage.findAllUsers();
    }

    public User createUser(User user) {
        return userStorage.createUser(user);
    }

    public User updateUser(User user) {
        return userStorage.updateUser(user);
    }

    public User getById(Long userId) {
        return userStorage.getById(userId);
    }

    public User addFriend(Long userId, Long friendId) {
        userStorage.isExist(userId);
        userStorage.isExist(friendId);
        feedStorage.addEvent(userId, EventTypes.FRIEND, Operations.ADD, friendId);
        return userStorage.addFriend(userId, friendId);
    }

    public void deleteFriend(Long userId, Long friendId) {
        userStorage.isExist(userId);
        userStorage.isExist(friendId);
        feedStorage.addEvent(userId, EventTypes.FRIEND, Operations.REMOVE, friendId);
        userStorage.deleteFriend(userId, friendId);
    }

    public Collection<User> getFriends(Long userId) {
        return userStorage.getFriends(userId);
    }

    public Collection<User> getMutualFriends(Long userId, Long secondUserId) {
        return userStorage.getMutualFriends(userId, secondUserId);
    }

    public String deleteUserById(Long userId) {
        return userStorage.deleteUserById(userId);
    }

    public Collection<Film> getUserRecommendations(Long userId) {
        userStorage.isExist(userId);
        return filmStorage.getUserRecommendations(userId);
    }

    public Collection<Event> getFeedById(Long userId) {
        userStorage.isExist(userId);
        return feedStorage.getFeedById(userId);
    }
}
