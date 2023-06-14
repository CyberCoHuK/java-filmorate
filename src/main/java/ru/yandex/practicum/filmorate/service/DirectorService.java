package ru.yandex.practicum.filmorate.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import ru.yandex.practicum.filmorate.model.Director;
import ru.yandex.practicum.filmorate.storage.director.DirectorStorage;

import java.util.Collection;


@Slf4j
@Service
@RequiredArgsConstructor
public class DirectorService {
    private final DirectorStorage directorStorage;

    public Director getDirectorById(int id) {
        return directorStorage.getDirectorById(id);
    }

    public Director createDirector(Director director) {
        return directorStorage.createDirector(director);
    }

    public Director updateDirector(Director director) {
        return directorStorage.updateDirector(director);
    }

    public void deleteDirector(int id) {
        directorStorage.deleteDirector(id);
    }

    public void addFilmDirectors(int id, Collection<Director> directors) {
        directorStorage.saveFilmDirector(id, directors);
    }

    public void updateFilmDirectors(int id, Collection<Director> directors) {
        directorStorage.deleteFilmDirector(id);
        directorStorage.saveFilmDirector(id, directors);
    }

    public void deleteFilmDirectors(int id) {
        directorStorage.deleteFilmDirector(id);
    }

    public Collection<Director> getAllDirectors() {
        return directorStorage.getAllDirectors();
    }
}
