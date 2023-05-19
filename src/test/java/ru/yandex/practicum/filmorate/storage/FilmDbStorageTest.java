package ru.yandex.practicum.filmorate.storage;

import lombok.RequiredArgsConstructor;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.annotation.DirtiesContext;
import ru.yandex.practicum.filmorate.model.Film;
import ru.yandex.practicum.filmorate.model.Mpa;
import ru.yandex.practicum.filmorate.model.User;
import ru.yandex.practicum.filmorate.service.FilmService;
import ru.yandex.practicum.filmorate.storage.film.FilmDbStorage;
import ru.yandex.practicum.filmorate.storage.user.UserDbStorage;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.HashSet;

import static org.junit.jupiter.api.Assertions.assertEquals;

@SpringBootTest
@AutoConfigureTestDatabase
@RequiredArgsConstructor(onConstructor_ = @Autowired)
@DirtiesContext(classMode = DirtiesContext.ClassMode.BEFORE_EACH_TEST_METHOD)
class FilmDbStorageTest {
    private final FilmDbStorage filmDbStorage;
    private final FilmService filmService;
    private final UserDbStorage userDbStorage;

    Film film;
    Film film2;
    User user;
    User user2;


    @BeforeEach
    void setUp() {
        film = Film.builder()
                .name("name")
                .description("desc")
                .releaseDate(LocalDate.of(1999, 8, 17))
                .duration(136)
                .build();
        film.setGenres(new ArrayList<>());
        film.setLikesList(new HashSet<>());
        film.setMpa(Mpa.builder()
                .id(1)
                .name("G")
                .build());

        film2 = Film.builder()
                .name("name2")
                .description("desc")
                .releaseDate(LocalDate.of(1999, 8, 17))
                .duration(136)
                .build();
        film2.setGenres(new ArrayList<>());
        film2.setLikesList(new HashSet<>());
        film2.setMpa(Mpa.builder()
                .id(1)
                .name("G")
                .build());

        user = User.builder()
                .email("mail@mail.mail")
                .login("login")
                .name("login")
                .birthday(LocalDate.of(1999, 8, 17))
                .build();
        user.setFriendsList(new HashSet<>());

        user2 = User.builder()
                .email("gmail@gmail.gmail")
                .login("nelogin")
                .name("nelogin")
                .birthday(LocalDate.of(2001, 6, 19))
                .build();
        user2.setFriendsList(new HashSet<>());
    }

    @Test
    void addFilmTest() {
        filmDbStorage.createFilm(film);
        assertEquals(film, filmDbStorage.getById(film.getId()));
    }

    @Test
    void updateFilmTest() {
        filmDbStorage.createFilm(film);
        assertEquals(film, filmDbStorage.getById(film.getId()));

        film.setName("updateName");
        filmDbStorage.updateFilm(film);
        assertEquals("updateName", filmDbStorage.getById(film.getId()).getName());
    }

    @Test
    void likeAndDeleteLikeTest() {
        filmDbStorage.createFilm(film);
        userDbStorage.createUser(user);
        userDbStorage.createUser(user2);
        filmDbStorage.addLike(1, 1);
        filmDbStorage.addLike(1, 2);
        film.setLikesList(filmDbStorage.getLikesForCurrentFilm(film.getId()));
        assertEquals(2, film.getLikesList().size());

        filmDbStorage.deleteLike(1, 1);
        film.setLikesList(filmDbStorage.getLikesForCurrentFilm(film.getId()));
        assertEquals(1, film.getLikesList().size());
    }

    @Test
    void getRatingTest() {
        filmDbStorage.createFilm(film);
        userDbStorage.createUser(user);
        userDbStorage.createUser(user2);
        filmDbStorage.addLike(1, 1);
        filmDbStorage.addLike(1, 2);
        filmService.getListOfTopFilms(1);
    }
}