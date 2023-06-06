package ru.yandex.practicum.filmorate.storage.director;

import ru.yandex.practicum.filmorate.model.Director;

import java.util.List;
import java.util.Optional;

public interface DirectorStorage {
    long createDirector(Director director);

    Optional<Director> getDirectorById(long id);

    List<Director> getAllDirectors();

    void updateDirector(Director director);

    void deleteDirector(long id);

    List<Director> loadDirectorsByFilmId(long id);

    void saveFilmDirector(long id, List<Director> directors);

    void deleteFilmDirector(long id);

    void isExist(long directorId);
}