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
import ru.yandex.practicum.filmorate.storage.film.FilmDbStorage;
import ru.yandex.practicum.filmorate.storage.mpa.MpaDbStorage;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
@AutoConfigureTestDatabase
@RequiredArgsConstructor(onConstructor_ = @Autowired)
@DirtiesContext(classMode = DirtiesContext.ClassMode.BEFORE_EACH_TEST_METHOD)
@FieldDefaults(level = AccessLevel.PRIVATE)
public class MpaDbStorageTest {

    final MpaDbStorage mpaDbStorage;

    final FilmDbStorage filmDbStorage;
    Film film;


    @BeforeEach
    void setUp() {
        film = FilmDbStorageTest.createFilm();
    }

    @Test
    void findAllMpaTest() {
        List<Mpa> mpaListTest = (List<Mpa>) mpaDbStorage.findAll();
        assertEquals(5, mpaListTest.size());
    }

    @Test
    void getMpaForIdTest() {
        Mpa mpaTest = mpaDbStorage.getById(5L);
        assertEquals("NC-17", mpaTest.getName());
    }

    @Test
    void addMpaInFilmTest() {
        film.setMpa(null);
        assertNull(film.getMpa());
        film.setMpa(Mpa.builder()
                .id(5L)
                .name("NC-17")
                .build());
        filmDbStorage.createFilm(film);
        assertNotNull(film.getMpa());
    }
}