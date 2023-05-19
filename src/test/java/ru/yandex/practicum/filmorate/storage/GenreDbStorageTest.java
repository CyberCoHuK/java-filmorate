package ru.yandex.practicum.filmorate.storage;

import lombok.RequiredArgsConstructor;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.annotation.DirtiesContext;
import ru.yandex.practicum.filmorate.model.Film;
import ru.yandex.practicum.filmorate.model.Genre;
import ru.yandex.practicum.filmorate.model.Mpa;
import ru.yandex.practicum.filmorate.storage.film.FilmDbStorage;
import ru.yandex.practicum.filmorate.storage.genre.GenreDbStorage;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

@SpringBootTest
@AutoConfigureTestDatabase
@RequiredArgsConstructor(onConstructor_ = @Autowired)
@DirtiesContext(classMode = DirtiesContext.ClassMode.BEFORE_EACH_TEST_METHOD)
class GenreDbStorageTest {

    private final FilmDbStorage filmDbStorage;

    private final GenreDbStorage genreDbStorage;
    Film film;


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
    }

    @Test
    void findAllGenreTest() {
        List<Genre> genreListTest = (List<Genre>) genreDbStorage.findAll();
        assertEquals(6, genreListTest.size());
    }

    @Test
    void setFilmGenreTest() {
        assertTrue(film.getGenres().isEmpty());
        film.getGenres().add(Genre.builder()
                .id(1)
                .name("Комедия")
                .build());
        assertEquals(1, film.getGenres().size());
    }

    @Test
    void getGenreForIdTest() {
        Genre genreTest = genreDbStorage.getById(1);
        assertEquals("Комедия", genreTest.getName());
    }

    @Test
    void addGenreTest() {
        assertTrue(film.getGenres().isEmpty());
        filmDbStorage.createFilm(film);
        film.getGenres().add(Genre.builder()
                .id(1)
                .name("Комедия")
                .build());
        filmDbStorage.updateFilm(film);
        assertEquals(1, film.getGenres().size());
    }

    @Test
    void updateGenreTest() {
        assertTrue(film.getGenres().isEmpty());
        filmDbStorage.createFilm(film);
        film.getGenres().add(Genre.builder()
                .id(1)
                .name("Комедия")
                .build());
        film.getGenres().add(Genre.builder()
                .id(2)
                .name("Боевик")
                .build());
        filmDbStorage.updateFilm(film);
        assertEquals(2, film.getGenres().size());
    }
}