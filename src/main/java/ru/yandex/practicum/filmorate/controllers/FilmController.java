package ru.yandex.practicum.filmorate.controllers;

import javax.validation.Valid;
import javax.validation.ValidationException;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;
import ru.yandex.practicum.filmorate.model.Film;

import java.time.LocalDate;
import java.util.*;

@RestController
@Slf4j
@RequestMapping("/films")
public class FilmController {
    private static final LocalDate DATE = LocalDate.of(1895, 12, 28);
    private int filmId = 1;
    private final Map<Integer, Film> films = new HashMap<>();

    @GetMapping
    public Collection<Film> getAllFilms() {
        return films.values();
    }

    @PutMapping
    public Film updateFilm(@Valid @RequestBody Film film) {
        validate(film);
        if (films.containsValue(film)) {
            throw new ValidationException();
        } else if (films.containsKey(film.getId())) {
            log.info("Информация о фильме {} обновлена", film.getName());
            films.replace(film.getId(), film);
            return film;
        } else {
            film.setId(filmId++);
            log.info("Фильм {} добавлен в коллекцию", film.getName());
            films.put(film.getId(), film);
            return film;
        }
    }

    @PostMapping
    public Film createFilm(@Valid @RequestBody Film film) {
        validate(film);
        film.setId(filmId++);
        log.info("Фильм {} добавлен в коллекцию", film.getName());
        films.put(film.getId(), film);
        return film;
    }

    private void validate(@Valid @RequestBody Film film) {
        if (film.getReleaseDate().isBefore(DATE)) {
            throw new ValidationException("В то время кино еще не было изобретено");
        }
    }
}
