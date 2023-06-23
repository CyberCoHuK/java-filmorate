package ru.yandex.practicum.filmorate.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import ru.yandex.practicum.filmorate.enums.EventTypes;
import ru.yandex.practicum.filmorate.enums.Operations;
import ru.yandex.practicum.filmorate.enums.SortTypesForDirectors;
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

    public Film getFilmById(Long filmId) {
        return filmStorage.getById(filmId);
    }

    public Film addLike(Long filmId, Long userId) {
        filmStorage.isExist(filmId);
        userStorage.isExist(userId);
        feedStorage.addEvent(userId, EventTypes.LIKE, Operations.ADD, filmId);
        return filmStorage.addLike(filmId, userId);
    }

    public Film deleteLike(Long filmId, Long userId) {
        filmStorage.isExist(filmId);
        userStorage.isExist(userId);
        feedStorage.addEvent(userId, EventTypes.LIKE, Operations.REMOVE, filmId);
        return filmStorage.deleteLike(filmId, userId);
    }

    public Collection<Film> getPopular(Integer count, Long genreId, Integer year) {
        return filmStorage.getPopular(count, genreId, year);
    }

    public String deleteFilmById(Long filmId) {
        return filmStorage.deleteFilmById(filmId);
    }

    public List<Film> getFriendsCommonFilms(Long userId, Long friendId) {
        return filmStorage.getFriendsCommonFilms(userId, friendId);
    }

    public List<Film> getSortedFilmsByDirectorId(Long directorId, String sortBy) {
        directorStorage.isExist(directorId);
        SortTypesForDirectors sortTypes = SortTypesForDirectors.valueOfSortBy(sortBy);

        switch (sortTypes) {
            case YEAR:
                return filmStorage.loadFilmsOfDirectorSortedByYears(directorId);
            case LIKES:
                return filmStorage.loadFilmsOfDirectorSortedByLikes(directorId);
            default:
                throw new IllegalArgumentException(SortTypesForDirectors.UNKNOW + sortBy);
        }
    }

    public List<Film> searchFilmByParameter(String query, String filmSearchParameter) {
        return filmStorage.searchFilmByParameter(query, filmSearchParameter);
    }
}