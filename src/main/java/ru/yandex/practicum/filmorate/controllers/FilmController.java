package ru.yandex.practicum.filmorate.controllers;

import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;
import ru.yandex.practicum.filmorate.model.Film;
import ru.yandex.practicum.filmorate.service.FilmService;

import javax.validation.Valid;
import java.util.Collection;
import java.util.List;

@RestController
@RequiredArgsConstructor
@RequestMapping("/films")
public class FilmController {
    private final FilmService filmService;

    @GetMapping
    public Collection<Film> getAllFilms() {
        return filmService.getAllFilms();
    }

    @GetMapping("/{filmId}")
    public Film getFilmById(@PathVariable int filmId) {
        return filmService.getFilmById(filmId);
    }

    @GetMapping("/popular")
    public Collection<Film> getPopular(@RequestParam(defaultValue = "10") int count,
                                       @RequestParam(defaultValue = "9999") Integer genreId,
                                       @RequestParam(defaultValue = "9999") Integer year) {
        return filmService.getPopular(count, genreId, year);
    }

    @PutMapping
    public Film updateFilm(@Valid @RequestBody Film film) {
        return filmService.updateFilm(film);
    }

    @PutMapping("/{id}/like/{userId}")
    public Film addLike(@PathVariable int id, @PathVariable int userId) {
        return filmService.addLike(id, userId);
    }

    @DeleteMapping("/{id}/like/{userId}")
    public Film deleteLike(@PathVariable int id, @PathVariable int userId) {
        return filmService.deleteLike(id, userId);
    }

    @PostMapping
    public Film createFilm(@Valid @RequestBody Film film) {
        return filmService.createFilm(film);
    }

    @DeleteMapping("/{filmId}")
    public String deleteFilmById(@PathVariable("filmId") int filmId) {
        return filmService.deleteFilmById(filmId);
    }

    @GetMapping("/director/{directorId}")
    public List<Film> getByDirectorId(@PathVariable int directorId, @RequestParam String sortBy) {
        return filmService.getSortedFilmsByDirectorId(directorId, sortBy.toLowerCase());
    }

    @GetMapping("/search")
    public List<Film> searchFilmByParameter(@RequestParam(name = "query") String query,
                                            @RequestParam(name = "by") String filmSearchParameter) {
        return filmService.searchFilmByParameter(query.toLowerCase(), filmSearchParameter.toLowerCase());
    }

    @GetMapping("/common")
    public List<Film> getFriendsCommonFilms(@RequestParam(name = "userId") int userId,
                                            @RequestParam(name = "friendId") int friendId) {
        return filmService.getFriendsCommonFilms(userId, friendId);
    }
}
