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

    public User getById(int userId) {
        return userStorage.getById(userId);
    }

    public User addFriend(int userId, int friendId) {
        feedStorage.addEvent(userId, EventTypes.FRIEND, Operations.ADD, friendId);
        return userStorage.addFriend(userId, friendId);
    }

    public void deleteFriend(int userId, int friendId) {
        feedStorage.addEvent(userId, EventTypes.FRIEND, Operations.REMOVE, friendId);
        userStorage.deleteFriend(userId, friendId);
    }

    public Collection<User> getFriends(int userId) {
        return userStorage.getFriends(userId);
    }

    public Collection<User> getMutualFriends(int userId, int secondUserId) {
        return userStorage.getMutualFriends(userId, secondUserId);
    }

    public String deleteUserById(int userId) {
        return userStorage.deleteUserById(userId);
    }

    public Collection<Film> getUserRecommendations(int userId) {
        userStorage.isExist(userId);
        return filmStorage.getUserRecommendations(userId);
    }

    public Collection<Event> getFeedById(int userId) {
        userStorage.isExist(userId);
        return feedStorage.getFeedById(userId);
    }
}
