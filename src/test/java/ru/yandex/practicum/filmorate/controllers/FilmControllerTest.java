package ru.yandex.practicum.filmorate.controllers;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;
import ru.yandex.practicum.filmorate.model.Film;

import javax.validation.ConstraintViolation;
import javax.validation.Validation;
import javax.validation.ValidationException;
import javax.validation.Validator;
import java.time.LocalDate;
import java.util.Set;

import static org.junit.jupiter.api.Assertions.assertEquals;

@SpringBootTest
public class FilmControllerTest {
    private FilmController filmController;

    private Film film;
    private final Validator validator = Validation.buildDefaultValidatorFactory().getValidator();

    @BeforeEach
    void beforeEach() {
        filmController = new FilmController();
        film = Film.builder()
                .id(1)
                .name("name")
                .description("description")
                .releaseDate(LocalDate.of(1956, 12, 1))
                .duration(255)
                .build();
    }

    @Test
    public void addUser() {
        Set<ConstraintViolation<Film>> violations = validator.validate(film);
        assertEquals(0, violations.size());
    }

    @Test
    public void emptyNameOfFilm() {
        film.setName("");
        Set<ConstraintViolation<Film>> violations = validator.validate(film);
        assertEquals(1, violations.size());
        ConstraintViolation<Film> violation = violations.iterator().next();
        assertEquals("Отсутствует название фильма", violation.getMessage());
    }

    @Test
    public void maxLengthOfFilmDescription() {
        String newString = "a".repeat(250);
        film.setDescription(newString);
        Set<ConstraintViolation<Film>> violations = validator.validate(film);
        assertEquals(1, violations.size());
        ConstraintViolation<Film> violation = violations.iterator().next();
        assertEquals("Слишком длинное описание. Максимальное количество символов - 200", violation.getMessage());
    }

    @Test
    public void wrongReleaseDate() {
        film.setReleaseDate(LocalDate.of(1885, 12, 1));
        Assertions.assertThrows(ValidationException.class, () -> filmController.createFilm(film));
    }

    @Test
    public void wrongDurationOfFilm() {
        film.setDuration(-13);
        Set<ConstraintViolation<Film>> violations = validator.validate(film);
        assertEquals(1, violations.size());
        ConstraintViolation<Film> violation = violations.iterator().next();
        assertEquals("Неправильная продолжительность фильма", violation.getMessage());
    }
}