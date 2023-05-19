package ru.yandex.practicum.filmorate.controllers;

import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.bind.annotation.PathVariable;
import ru.yandex.practicum.filmorate.service.GenreService;
import org.springframework.web.bind.annotation.GetMapping;
import ru.yandex.practicum.filmorate.model.Genre;
import lombok.RequiredArgsConstructor;
import java.util.Collection;

@RestController
@RequiredArgsConstructor
@RequestMapping("/genres")
public class GenreController {

    private final GenreService genreService;

    @GetMapping
    public Collection<Genre> findAll() {
        return genreService.findAll();
    }

    @GetMapping("/{id}")
    public Genre getById(@PathVariable int id) {
        return genreService.getById(id);
    }
}