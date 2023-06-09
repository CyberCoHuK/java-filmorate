package ru.yandex.practicum.filmorate.storage;

import lombok.AccessLevel;
import lombok.experimental.FieldDefaults;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.test.context.jdbc.Sql;
import org.springframework.test.context.jdbc.SqlGroup;
import ru.yandex.practicum.filmorate.model.Film;
import ru.yandex.practicum.filmorate.model.Mpa;
import ru.yandex.practicum.filmorate.model.User;
import ru.yandex.practicum.filmorate.service.FilmService;
import ru.yandex.practicum.filmorate.storage.feed.FeedStorage;
import ru.yandex.practicum.filmorate.storage.film.FilmDbStorage;
import ru.yandex.practicum.filmorate.storage.genre.GenreDbStorage;
import ru.yandex.practicum.filmorate.storage.user.UserDbStorage;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;

import static org.assertj.core.api.AssertionsForClassTypes.assertThat;
import static org.junit.jupiter.api.Assertions.assertEquals;

@SpringBootTest
@AutoConfigureTestDatabase
@DirtiesContext(classMode = DirtiesContext.ClassMode.BEFORE_EACH_TEST_METHOD)
@FieldDefaults(level = AccessLevel.PRIVATE)
class FilmDbStorageTest {
    @Autowired
    FilmDbStorage filmDbStorage;
    @Autowired
    FilmService filmService;
    @Autowired
    UserDbStorage userDbStorage;
    @Autowired
    FeedStorage feedStorage;
    @Autowired
    GenreDbStorage genreDbStorage;

    Film film;
    Film secondFilm;
    Film thirdFilm;
    User user;
    User secondUser;

    @BeforeEach
    void setUp() {
        film = createFilm();
        secondFilm = createSecondFilm();
        thirdFilm = createThirdFilm();
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
        filmDbStorage.addLike(1L, 1L);
        filmDbStorage.addLike(1L, 2L);
        film.setLikesList(filmDbStorage.getLikesForCurrentFilm(film.getId()));
        assertEquals(2, film.getLikesList().size());
        filmDbStorage.deleteLike(1L, 1L);
        film.setLikesList(filmDbStorage.getLikesForCurrentFilm(film.getId()));
        assertEquals(1, film.getLikesList().size());
    }

    @Test
    void getRatingTest() {
        filmDbStorage.createFilm(film);
        userDbStorage.createUser(user);
        userDbStorage.createUser(secondUser);
        filmDbStorage.addLike(1L, 1L);
        filmDbStorage.addLike(1L, 2L);
        filmService.getPopular(10,null,null);
        assertEquals(1, filmService.getPopular(10,null,null).size());
    }

    @Test
    @SqlGroup({
            @Sql(value = "/test/recommendation-test.sql", executionPhase = Sql.ExecutionPhase.BEFORE_TEST_METHOD),
            @Sql(value = "/test/clear.sql", executionPhase = Sql.ExecutionPhase.AFTER_TEST_METHOD),
    })
    void getUserRecommendations() {
        List<Film> check = new ArrayList<>();
        thirdFilm.setId(3L);
        check.add(thirdFilm);
        assertEquals(check, filmDbStorage.getUserRecommendations(1L));
    }

    @Test
    void deleteFilmByIdInStorageCheck() {
        filmDbStorage.createFilm(film);
        filmDbStorage.deleteFilmById(film.getId());
        assertThat(filmDbStorage.getAllFilms().isEmpty());
        filmService.getPopular(10, 0L, 0);
    }

    @Test
    void searchFilmByParameter() {
        filmDbStorage.createFilm(film);
        filmDbStorage.createFilm(secondFilm);
        userDbStorage.createUser(user);
        userDbStorage.createUser(secondUser);
        filmDbStorage.addLike(1L, 1L);
        filmDbStorage.addLike(1L, 2L);
        assertEquals(2, filmService.searchFilmByParameter("ame", "title").size());
    }

    @Test
    void getFriendsCommonFilmsTest() {
        filmDbStorage.createFilm(film);
        userDbStorage.createUser(user);
        userDbStorage.createUser(secondUser);
        Long userId = user.getId();
        Long secondUserId = secondUser.getId();
        filmDbStorage.addLike(1L, userId);
        filmDbStorage.addLike(1L, secondUserId);
        film.setLikesList(filmDbStorage.getLikesForCurrentFilm(film.getId()));
        assertEquals(1, filmDbStorage.getFriendsCommonFilms(userId, secondUserId).size());
    }

    protected static Film createFilm() {
        return Film.builder()
                .name("name")
                .description("desc")
                .releaseDate(LocalDate.of(1999, 8, 17))
                .duration(136L)
                .genres(new ArrayList<>())
                .likesList(new HashSet<>())
                .mpa(Mpa.builder()
                        .id(1L)
                        .name("G")
                        .build())
                .directors(new ArrayList<>())
                .build();
    }

    protected static Film createSecondFilm() {
        return Film.builder()
                .name("SecondName")
                .description("SecondDesc")
                .releaseDate(LocalDate.of(1997, 3, 13))
                .duration(46L)
                .genres(new ArrayList<>())
                .likesList(new HashSet<>())
                .mpa(Mpa.builder()
                        .id(1L)
                        .name("G")
                        .build())
                .directors(new ArrayList<>())
                .build();
    }

    protected static Film createThirdFilm() {
        return Film.builder()
                .name("ThirdName")
                .description("ThirdDesc")
                .releaseDate(LocalDate.of(1977, 2, 23))
                .duration(13L)
                .genres(new ArrayList<>())
                .likesList(new HashSet<>())
                .mpa(Mpa.builder()
                        .id(1L)
                        .name("G")
                        .build())
                .directors(new ArrayList<>())
                .build();
    }
}
