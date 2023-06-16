package ru.yandex.practicum.filmorate.controllers;

import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;
import ru.yandex.practicum.filmorate.model.Director;
import ru.yandex.practicum.filmorate.service.DirectorService;

import javax.validation.Valid;
import java.util.Collection;

@RestController
@RequiredArgsConstructor
@RequestMapping("/directors")
public class DirectorController {

    private final DirectorService directorService;

    @GetMapping
    public Collection<Director> getAllDirectors() {
        return directorService.getAllDirectors();
    }

    @GetMapping("/{id}")
    public Director get(@PathVariable int id) {
        return directorService.getDirectorById(id);
    }

    @PostMapping
    public Director createDirector(@Valid @RequestBody Director director) {
        return directorService.createDirector(director);
    }

    @PutMapping
    public Director update(@Valid @RequestBody Director director) {
        return directorService.updateDirector(director);
    }

    @DeleteMapping("/{id}")
    public void delete(@PathVariable int id) {
        directorService.deleteDirector(id);
    }
}