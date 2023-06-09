package ru.yandex.practicum.filmorate.storage.film;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import ru.yandex.practicum.filmorate.exceptions.ObjectAlreadyExistException;
import ru.yandex.practicum.filmorate.exceptions.ObjectNotFoundException;
import ru.yandex.practicum.filmorate.model.Film;
import ru.yandex.practicum.filmorate.storage.user.UserStorage;

import javax.validation.ValidationException;
import java.util.*;
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
        isExist(film.getId());
        validate(film);
        if (films.get(film.getId()).equals(film)) {
            log.info("Фильм {} уже добавлен c ID = {}", film.getName(), film.getId());
            throw new ObjectAlreadyExistException(String
                    .format("Такой фильм уже добавлен с ID = %s", film.getId()));
        }
        log.info("Информация о фильме {} c ID = {} обновлена", film.getName(), film.getId());
        films.replace(film.getId(), film);
        return film;
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

    @Override
    public Collection<Film> getUserRecommendations(int userId) {
        return null;
    }


    private void validate(Film film) {
        if (film.getReleaseDate().isBefore(FIRST_FILM_DATE)) {
            throw new ValidationException("Фильм не может выйти раньше первого фильма в истории");
        }
    }

    @Override
    public void isExist(int filmId) {
        if (!films.containsKey(filmId)) {
            throw new ObjectNotFoundException("Фильма с таким " + filmId + " не существует");
        }
    }

    public String deleteFilmById(int filmId) {
        return "Фильм film_id=" + filmId + " успешно удален.";
    }

    @Override
    public List<Film> loadFilmsOfDirectorSortedByYears(int directorId) {
        return null;
    }

    @Override
    public List<Film> loadFilmsOfDirectorSortedByLikes(int directorId) {
        return null;
    }

    @Override
    public List<Film> searchFilmByParameter(String query, String filmSearchParameter) {
        return null;
    }
}