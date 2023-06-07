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
    Film secondFilm;
    Film thirdFilm;
    User user;
    User secondUser;


    @BeforeEach
    void setUp() {
        film = createFilm(0);
        secondFilm = createFilm(1);
        thirdFilm = createFilm(2);
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
        assertEquals(1, filmService.getListOfTopFilms(1).size());

    }

    @Test
    void getUserRecommendations() {
        filmDbStorage.createFilm(film);
        filmDbStorage.createFilm(secondFilm);
        filmDbStorage.createFilm(thirdFilm);
        userDbStorage.createUser(user);
        userDbStorage.createUser(secondUser);
        filmDbStorage.addLike(1, 1);
        filmDbStorage.addLike(1, 2);
        filmDbStorage.addLike(2, 1);
        filmDbStorage.addLike(3, 2);
        ArrayList<Film> check = new ArrayList<>();
        check.add(thirdFilm);
        assertEquals(check, filmDbStorage.getUserRecommendations(1));
    }

    protected static Film createFilm(int num) {
        if (num == 1) {
            return Film.builder()
                    .name("SecondName")
                    .description("SecondDesc")
                    .releaseDate(LocalDate.of(1997, 3, 13))
                    .duration(46)
                    .genres(new ArrayList<>())
                    .likesList(new HashSet<>())
                    .mpa(Mpa.builder()
                            .id(1)
                            .name("G")
                            .build())
                    .build();
        } else if (num == 2) {
            return Film.builder()
                    .name("ThirdName")
                    .description("ThirdDesc")
                    .releaseDate(LocalDate.of(1977, 2, 23))
                    .duration(13)
                    .genres(new ArrayList<>())
                    .likesList(new HashSet<>())
                    .mpa(Mpa.builder()
                            .id(1)
                            .name("G")
                            .build())
                    .build();
        } else {
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
                    .build();
        }
    }
}


