package ru.yandex.practicum.filmorate.storage;

import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
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
@FieldDefaults(level = AccessLevel.PRIVATE)
class FilmDbStorageTest {
    final FilmDbStorage filmDbStorage;
    final FilmService filmService;
    final UserDbStorage userDbStorage;

    Film film;
    User user;
    User secondUser;


    @BeforeEach
    void setUp() {
        film = createFilm();
        user = UserDbStorageTest.createUser(1);
        secondUser = UserDbStorageTest.createUser(2);
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
        userDbStorage.createUser(secondUser);
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
        userDbStorage.createUser(secondUser);
        filmDbStorage.addLike(1, 1);
        filmDbStorage.addLike(1, 2);
        filmService.getListOfTopFilms(1);
    }

    protected static Film createFilm() {
        return Film.builder()
                .name("name")
                .description("desc")
                .releaseDate(LocalDate.of(1999, 8, 17))
                .duration(136)
                .genres(new ArrayList<>())
                .likesList(new HashSet<>())
                .mpa(Mpa.builder()
                        .id(1)
                        .name("G")
                        .build())
                .directors(new ArrayList<>())
                .build();
    }
}


