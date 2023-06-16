package ru.yandex.practicum.filmorate.storage.film;

import ru.yandex.practicum.filmorate.model.Film;

import java.util.Collection;
import java.util.List;

public interface FilmStorage {
    Collection<Film> getAllFilms();

    Film updateFilm(Film film);

    Film createFilm(Film film);

    Film getById(int filmId);

    Film addLike(int filmId, int userId);

    Film deleteLike(int filmId, int userId);

    List<Film> getPopular(Integer count, Integer genreId, Integer year);

    List<Film> getFriendsCommonFilms(int userId, int friendId);

    String deleteFilmById(int filmId);

    List<Film> loadFilmsOfDirectorSortedByYears(int directorId);

    List<Film> loadFilmsOfDirectorSortedByLikes(int directorId);

    Collection<Film> getUserRecommendations(int userId);

    List<Film> searchFilmByParameter(String query, String filmSearchParameter);

    void isExist(int filmId);

}