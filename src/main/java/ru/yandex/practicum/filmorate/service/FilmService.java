package ru.yandex.practicum.filmorate.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import ru.yandex.practicum.filmorate.enums.EventTypes;
import ru.yandex.practicum.filmorate.enums.Operations;
import ru.yandex.practicum.filmorate.model.Film;
import ru.yandex.practicum.filmorate.storage.director.DirectorStorage;
import ru.yandex.practicum.filmorate.storage.feed.FeedStorage;
import ru.yandex.practicum.filmorate.storage.film.FilmStorage;
import ru.yandex.practicum.filmorate.storage.user.UserStorage;

import java.util.Collection;
import java.util.List;

@Service
@RequiredArgsConstructor
public class FilmService {
    private final FilmStorage filmStorage;
    private final DirectorStorage directorStorage;
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
        filmStorage.isExist(filmId);
        userStorage.isExist(userId);
        feedStorage.addEvent(userId, EventTypes.LIKE, Operations.ADD, filmId);
        return filmStorage.addLike(filmId, userId);
    }

    public Film deleteLike(int filmId, int userId) {
        filmStorage.isExist(filmId);
        userStorage.isExist(userId);
        feedStorage.addEvent(userId, EventTypes.LIKE, Operations.REMOVE, filmId);
        return filmStorage.deleteLike(filmId, userId);
    }

    public Collection<Film> getPopular(int count, Integer genreId, Integer year) {
        return filmStorage.getPopular(count, genreId, year);
    }

    public String deleteFilmById(int filmId) {
        return filmStorage.deleteFilmById(filmId);
    }

    public List<Film> getFriendsCommonFilms(int userId, int friendId) {
        return filmStorage.getFriendsCommonFilms(userId, friendId);
    }

    public List<Film> getSortedFilmsByDirectorId(int directorId, String sortBy) {
        directorStorage.isExist(directorId);
        switch (sortBy) {
            case "year":
                return filmStorage.loadFilmsOfDirectorSortedByYears(directorId);
            case "likes":
                return filmStorage.loadFilmsOfDirectorSortedByLikes(directorId);
            default:
                throw new NullPointerException("Задан не корректный параметр сортировки");
        }
    }

    public List<Film> searchFilmByParameter(String query, String filmSearchParameter) {
        if (!(filmSearchParameter.equals("director,title")) ||
                !(filmSearchParameter.equals("title,director"))) {
            throw new IllegalArgumentException("Задан не корректный параметр сортировки");
        }
        return filmStorage.searchFilmByParameter(query, filmSearchParameter);
    }

}