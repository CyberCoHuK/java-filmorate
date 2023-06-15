package ru.yandex.practicum.filmorate.storage.director;

import ru.yandex.practicum.filmorate.model.Director;

import java.util.Collection;

public interface DirectorStorage {
    Director createDirector(Director director);

    Director getDirectorById(int id);

    Collection<Director> getAllDirectors();

    Director updateDirector(Director director);

    void deleteDirector(int id);

    void saveFilmDirector(int id, Collection<Director> directors);

    void deleteFilmDirector(int id);

    void isExist(int directorId);
}