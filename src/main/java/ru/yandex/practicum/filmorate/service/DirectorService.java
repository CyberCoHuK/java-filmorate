package ru.yandex.practicum.filmorate.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import ru.yandex.practicum.filmorate.model.Director;
import ru.yandex.practicum.filmorate.storage.director.DirectorStorage;

import java.util.List;
import java.util.NoSuchElementException;
import java.util.Optional;

@Slf4j
@Service
@RequiredArgsConstructor
public class DirectorService {
    private final DirectorStorage directorStorage;

    public Director getDirectorOrNotFoundException(int id) {
        Optional<Director> director = directorStorage.getDirectorById(id);
        if (director.isPresent()) {
            log.debug("Load {}", director.get());
            return director.get();
        } else {
            throw new NoSuchElementException("Director #" + id + " not found");
        }
    }

    public List<Director> getFilmDirectorsById(int id) {
        return directorStorage.loadDirectorsByFilmId(id);
    }

    public Director createDirector(Director director) {
        int id = directorStorage.createDirector(director);
        return getDirectorOrNotFoundException(id);
    }

    public Director updateDirector(Director director) {
        Director loadedDirector = getDirectorOrNotFoundException(director.getId());
        loadedDirector.setName(director.getName());
        directorStorage.updateDirector(loadedDirector);
        return getDirectorOrNotFoundException(director.getId());
    }

    public void deleteDirector(int id) {
        directorStorage.deleteDirector(id);
    }

    public void addFilmDirectors(int id, List<Director> directors) {
        directorStorage.saveFilmDirector(id, directors);
    }

    public void updateFilmDirectors(int id, List<Director> directors) {
        directorStorage.deleteFilmDirector(id);
        directorStorage.saveFilmDirector(id, directors);
    }

    public void deleteFilmDirectors(int id) {
        directorStorage.deleteFilmDirector(id);
    }

    public List<Director> getAllDirectors() {
        List<Director> directors = directorStorage.getAllDirectors();
        log.debug("Load {} directors", directors.size());
        return directors;
    }
}
