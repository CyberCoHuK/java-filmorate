package ru.yandex.practicum.filmorate.storage.director;

import ru.yandex.practicum.filmorate.model.Director;

import java.util.List;

public interface DirectorStorage {
    Director createDirector(Director director);

    Director getDirectorById(int id);

    List<Director> getAllDirectors();

    Director updateDirector(Director director);

    void deleteDirector(int id);

    void saveFilmDirector(int id, List<Director> directors);

    void deleteFilmDirector(int id);

    void isExist(int directorId);
}