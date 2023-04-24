package ru.yandex.practicum.filmorate.storage.film;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import ru.yandex.practicum.filmorate.exceptions.ObjectAlreadyExistException;
import ru.yandex.practicum.filmorate.exceptions.ObjectNotFoundException;
import ru.yandex.practicum.filmorate.model.Film;
import ru.yandex.practicum.filmorate.storage.user.UserStorage;

import javax.validation.ValidationException;
import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.stream.Collectors;

import static ru.yandex.practicum.filmorate.Constants.FIRST_FILM_DATE;

@Slf4j
@Component
public class InMemoryFilmStorage implements FilmStorage {
    private final Map<Integer, Film> films = new HashMap<>();
    private static int filmId = 1;

    UserStorage userStorage;

    @Autowired
    public InMemoryFilmStorage(UserStorage userStorage) {
        this.userStorage = userStorage;
    }

    @Override
    public Collection<Film> getAllFilms() {
        return films.values();
    }

    @Override
    public Film updateFilm(Film film) {
        validate(film);
        if (films.containsValue(film)) {
            log.info("Фильм {} уже добавлен", film.getName());
            throw new ObjectAlreadyExistException("Такой фильм добавлен существует");
        } else if (films.containsKey(film.getId())) {
            log.info("Информация о фильме {} обновлена", film.getName());
            films.replace(film.getId(), film);
            return film;
        } else {
            return createFilm(film);
        }
    }

    @Override
    public Film createFilm(Film film) {
        validate(film);
        film.setId(filmId++);
        film.setLikesList(new HashSet<>());
        log.info("Фильм {} добавлен в коллекцию c ID = {}", film.getName(), film.getId());
        films.put(film.getId(), film);
        return film;
    }

    @Override
    public Film getById(int filmId) {
        isExist(filmId);
        log.info("Фильм {} возвращен", films.get(filmId));
        return films.get(filmId);
    }

    @Override
    public Film addLike(int filmId, int userId) {
        isExist(filmId);
        userStorage.isExist(userId);
        films.get(filmId).getLikesList().add(userId);
        log.info("Фильму {} поставил лайк пользователь {}", films.get(filmId), userId);
        return films.get(filmId);
    }

    @Override
    public Film deleteLike(int filmId, int userId) {
        isExist(filmId);
        userStorage.isExist(userId);
        films.get(filmId).getLikesList().remove(userId);
        log.info("Фильму {} удалил лайк пользователь {}", films.get(filmId), userId);
        return films.get(filmId);
    }

    @Override
    public Collection<Film> getListOfTopFilms(int count) {
        log.info("Возвращено топ {} фильмов", count);
        return getAllFilms().stream()
                .sorted((f1, f2) -> f2.getLikesList().size() - f1.getLikesList().size())
                .limit(count)
                .collect(Collectors.toCollection(HashSet::new));
    }

    private void validate(Film film) {
        if (film.getReleaseDate().isBefore(FIRST_FILM_DATE)) {
            throw new ValidationException("Фильм не может выйти раньше первого фильма в истории");
        }
    }

    private void isExist(int filmId) {
        if (!films.containsKey(filmId)) {
            throw new ObjectNotFoundException("Фильма с таким " + filmId + " не существует");
        }
    }
}
