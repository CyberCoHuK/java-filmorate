package ru.yandex.practicum.filmorate.storage.director;

import ru.yandex.practicum.filmorate.model.Director;

import java.util.Collection;

public interface DirectorStorage {
    Director createDirector(Director director);

    Director getDirectorById(Long id);

    Collection<Director> getAllDirectors();

    Director updateDirector(Director director);

    void deleteDirector(Long id);

    void isExist(Long directorId);
}