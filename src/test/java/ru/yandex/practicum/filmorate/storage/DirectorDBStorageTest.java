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
import ru.yandex.practicum.filmorate.model.Director;
import ru.yandex.practicum.filmorate.storage.director.DirectorDBStorage;

import java.util.Collection;

import static org.junit.jupiter.api.Assertions.assertEquals;


@SpringBootTest
@AutoConfigureTestDatabase
@RequiredArgsConstructor(onConstructor_ = @Autowired)
@DirtiesContext(classMode = DirtiesContext.ClassMode.BEFORE_EACH_TEST_METHOD)
@FieldDefaults(level = AccessLevel.PRIVATE)
class DirectorDBStorageTest {

    Director director;
    final DirectorDBStorage directorDBStorage;


    @BeforeEach
    void setUp() {
        director = new Director(1, "First Director");
    }

    @Test
    void findAllDirectorTest() {
        directorDBStorage.createDirector(director);
        Collection<Director> directorsListTest = directorDBStorage.getAllDirectors();
        assertEquals(1, directorsListTest.size());
    }

    @Test
    void getDirectorByIdTest() {
        directorDBStorage.createDirector(director);
        Director directorTest = directorDBStorage.getDirectorById(1);
        assertEquals("First Director", directorTest.getName());
    }

    @Test
    void updateDirectorByIdTest() {
        directorDBStorage.createDirector(director);
        director.setName("Updated Director");
        directorDBStorage.updateDirector(director);
        Director directorTest = directorDBStorage.getDirectorById(1);
        assertEquals("Updated Director", directorTest.getName());
    }

    @Test
    void deleteDirectorByIdTest() {
        directorDBStorage.createDirector(director);
        directorDBStorage.deleteDirector(director.getId());
        Collection<Director> directorsListTest = directorDBStorage.getAllDirectors();
        assertEquals(0, directorsListTest.size());
    }

}