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
import ru.yandex.practicum.filmorate.storage.film.FilmDbStorage;
import ru.yandex.practicum.filmorate.storage.mpa.MpaDbStorage;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
@AutoConfigureTestDatabase
@RequiredArgsConstructor(onConstructor_ = @Autowired)
@DirtiesContext(classMode = DirtiesContext.ClassMode.BEFORE_EACH_TEST_METHOD)

public class MpaDbStorageTest {

    private final MpaDbStorage mpaDbStorage;

    private final FilmDbStorage filmDbStorage;
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
    }

    @Test
    void findAllMpaTest() {
        List<Mpa> mpaListTest = (List<Mpa>) mpaDbStorage.findAll();
        assertEquals(5, mpaListTest.size());
    }

    @Test
    void getMpaForIdTest() {
        Mpa mpaTest = mpaDbStorage.getById(5);
        assertEquals("NC-17", mpaTest.getName());
    }

    @Test
    void addMpaInFilmTest() {
        assertNull(film.getMpa());
        film.setMpa(Mpa.builder()
                .id(5)
                .name("NC-17")
                .build());
        filmDbStorage.createFilm(film);
        assertNotNull(film.getMpa());
    }
}