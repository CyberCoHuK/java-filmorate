package ru.yandex.practicum.filmorate.controllers;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;
import ru.yandex.practicum.filmorate.exceptions.ObjectNotFoundException;
import ru.yandex.practicum.filmorate.model.Film;
import ru.yandex.practicum.filmorate.model.User;
import ru.yandex.practicum.filmorate.service.FilmService;
import ru.yandex.practicum.filmorate.service.UserService;
import ru.yandex.practicum.filmorate.storage.film.FilmStorage;
import ru.yandex.practicum.filmorate.storage.film.InMemoryFilmStorage;
import ru.yandex.practicum.filmorate.storage.user.InMemoryUserStorage;
import ru.yandex.practicum.filmorate.storage.user.UserStorage;

import javax.validation.ConstraintViolation;
import javax.validation.Validation;
import javax.validation.ValidationException;
import javax.validation.Validator;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

@SpringBootTest
public class FilmControllerTest {
    private FilmController filmController;
    private UserController userController;

    private Film film;
    private Film film2;
    private User user;
    private User user2;
    private final Validator validator = Validation.buildDefaultValidatorFactory().getValidator();

    @BeforeEach
    void beforeEach() {
        UserStorage userStorage = new InMemoryUserStorage();
        FilmStorage filmStorage = new InMemoryFilmStorage(userStorage);
        filmController = new FilmController(new FilmService(filmStorage));
        userController = new UserController(new UserService(userStorage));
        film = Film.builder()
                .name("name")
                .description("description")
                .releaseDate(LocalDate.of(1956, 12, 1))
                .duration(255)
                .build();
        film2 = Film.builder()
                .name("name2")
                .description("description2")
                .releaseDate(LocalDate.of(1967, 12, 1))
                .duration(2115)
                .build();
        user = User.builder()
                .name("nametest")
                .email("test@mail.ru")
                .login("logintest")
                .birthday(LocalDate.of(1956, 12, 1))
                .build();
        user2 = User.builder()
                .name("nametest2")
                .email("asdf@mail.ru")
                .login("logintest2")
                .birthday(LocalDate.of(1956, 12, 1))
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

    @Test
    public void getAllFilmsCheck() {
        filmController.createFilm(film);
        filmController.createFilm(film2);
        assertEquals(2, filmController.getAllFilms().size());
    }

    @Test
    public void createFilmCheck() {
        filmController.createFilm(film);
        assertEquals(film, filmController.getFilmById(film.getId()));
    }

    @Test
    public void getByIdCheck() {
        ObjectNotFoundException ex = assertThrows(ObjectNotFoundException.class, () -> filmController.getFilmById(888));
        assertEquals("Фильма с таким 888 не существует", ex.getMessage());
    }

    @Test
    public void addLikeCheck() {
        userController.createUser(user);
        userController.createUser(user2);
        filmController.createFilm(film);
        filmController.addLike(film.getId(), user.getId());
        assertEquals(1, filmController.getFilmById(film.getId()).getLikesList().size());
        filmController.addLike(film.getId(), user2.getId());
        assertEquals(2, filmController.getFilmById(film.getId()).getLikesList().size());
        ObjectNotFoundException ex = assertThrows(ObjectNotFoundException.class,
                () -> filmController.addLike(film.getId(), 888));
        assertEquals("Пользователя с таким 888 не существует", ex.getMessage());
    }

    @Test
    public void deleteLikeCheck() {
        userController.createUser(user);
        userController.createUser(user2);
        filmController.createFilm(film);
        filmController.addLike(film.getId(), user.getId());
        filmController.addLike(film.getId(), user2.getId());
        assertEquals(2, filmController.getFilmById(film.getId()).getLikesList().size());
        filmController.deleteLike(film.getId(), user.getId());
        assertEquals(1, filmController.getFilmById(film.getId()).getLikesList().size());
    }

    @Test
    public void getTopListCheck() {
        userController.createUser(user);
        userController.createUser(user2);
        filmController.createFilm(film);
        filmController.createFilm(film2);
        filmController.addLike(film.getId(), user.getId());
        filmController.addLike(film.getId(), user2.getId());
        filmController.addLike(film2.getId(), user.getId());
        List<Film> list = new ArrayList<>();
        list.add(film2);
        list.add(film);
        assertEquals(list.toString(),filmController.getTopList(5).toString());
    }
}