package ru.yandex.practicum.filmorate.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import ru.yandex.practicum.filmorate.enums.EventTypes;
import ru.yandex.practicum.filmorate.enums.Operations;
import ru.yandex.practicum.filmorate.model.Film;
import ru.yandex.practicum.filmorate.storage.feed.FeedStorage;
import ru.yandex.practicum.filmorate.storage.film.FilmStorage;
import ru.yandex.practicum.filmorate.storage.user.UserStorage;

import java.util.Collection;

@Service
@RequiredArgsConstructor
public class FilmService {
    private final FilmStorage filmStorage;
    private final UserStorage userStorage;
    private final FeedStorage feedStorage;

    public Collection<Film> getAllFilms() {
        return filmStorage.getAllFilms();
    }

    public Film updateFilm(Film film) {
        return filmStorage.updateFilm(film);
    }

    public Film createFilm(Film film) {
        return filmStorage.createFilm(film);
    }

    public Film getFilmById(int filmId) {
        return filmStorage.getById(filmId);
    }

    public Film addLike(int filmId, int userId) {
        userStorage.isExist(userId);
        feedStorage.addEvent(userId, EventTypes.LIKE, Operations.ADD, filmId);
        return filmStorage.addLike(filmId, userId);
    }

    public Film deleteLike(int filmId, int userId) {
        userStorage.isExist(userId);
        feedStorage.addEvent(userId, EventTypes.LIKE, Operations.REMOVE, filmId);
        return filmStorage.deleteLike(filmId, userId);
    }

    public Collection<Film> getListOfTopFilms(int count) {
        return filmStorage.getListOfTopFilms(count);
    }
}
