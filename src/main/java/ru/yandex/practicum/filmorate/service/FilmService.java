package ru.yandex.practicum.filmorate.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import ru.yandex.practicum.filmorate.model.Film;
import ru.yandex.practicum.filmorate.storage.director.DirectorStorage;
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
        return filmStorage.addLike(filmId, userId);
    }

    public Film deleteLike(int filmId, int userId) {
        userStorage.isExist(userId);
        return filmStorage.deleteLike(filmId, userId);
    }

    public Collection<Film> getListOfTopFilms(int count) {
        return filmStorage.getListOfTopFilms(count);
    }

    public String deleteFilmById(int filmId) {
        return filmStorage.deleteFilmById(filmId);
    }

    public List<Film> getFriendsCommonFilms(int userId, int friendId) {
        List<Film> usersCommonFilms = filmStorage.getFriendsCommonFilms(userId, friendId);
        return usersCommonFilms;
    }

    public List<Film> getSortedFilmsByDirectorId(int directorId, String sortBy) {
        directorStorage.isExist(directorId);
        switch (sortBy) {
            case "year":
                List<Film> films = filmStorage.loadFilmsOfDirectorSortedByYears(directorId);
                return films;
            case "likes":
                films = filmStorage.loadFilmsOfDirectorSortedByLikes(directorId);
                return films;
            default:
                throw new NullPointerException("Задан не корректный параметр сортировки");
        }
    }
}