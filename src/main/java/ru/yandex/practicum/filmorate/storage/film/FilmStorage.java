package ru.yandex.practicum.filmorate.storage.film;

import org.springframework.stereotype.Component;
import ru.yandex.practicum.filmorate.model.Film;

import java.util.Collection;

@Component
public interface FilmStorage {
    Collection<Film> getAllFilms();

    Film updateFilm(Film film);

    Film createFilm(Film film);

    Film getById(int filmId);

    Film addLike(int filmId, int userId);

    Film deleteLike(int filmId, int userId);

    Collection<Film> getListOfTopFilms(int count);
}
