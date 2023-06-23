package ru.yandex.practicum.filmorate.storage.film;

import ru.yandex.practicum.filmorate.model.Film;

import java.util.Collection;
import java.util.List;

public interface FilmStorage {
    Collection<Film> getAllFilms();

    Film updateFilm(Film film);

    Film createFilm(Film film);

    Film getById(Long filmId);

    Film addLike(Long filmId, Long userId);

    Film deleteLike(Long filmId, Long userId);

    List<Film> getPopular(Integer count, Long genreId, Integer year);

    List<Film> getFriendsCommonFilms(Long userId, Long friendId);

    String deleteFilmById(Long filmId);

    List<Film> loadFilmsOfDirectorSortedByYears(Long directorId);

    List<Film> loadFilmsOfDirectorSortedByLikes(Long directorId);

    Collection<Film> getUserRecommendations(Long userId);

    List<Film> searchFilmByParameter(String query, String filmSearchParameter);

    void isExist(Long filmId);

}