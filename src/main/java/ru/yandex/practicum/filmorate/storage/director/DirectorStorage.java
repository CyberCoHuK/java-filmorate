package ru.yandex.practicum.filmorate.storage.director;

import ru.yandex.practicum.filmorate.model.Director;

import java.util.List;
import java.util.Optional;

public interface DirectorStorage {
    int createDirector(Director director);

    Optional<Director> getDirectorById(int id);

    List<Director> getAllDirectors();

    void updateDirector(Director director);

    void deleteDirector(int id);

    List<Director> loadDirectorsByFilmId(int id);

    void saveFilmDirector(int id, List<Director> directors);

    void deleteFilmDirector(int id);

    void isExist(int directorId);
}