package ru.yandex.practicum.filmorate.controllers;

import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.GetMapping;
import ru.yandex.practicum.filmorate.service.MpaService;
import ru.yandex.practicum.filmorate.model.Mpa;
import lombok.RequiredArgsConstructor;
import java.util.Collection;

@RestController
@RequiredArgsConstructor
@RequestMapping("/mpa")
public class MpaController {

    private final MpaService mpaService;

    @GetMapping
    public Collection<Mpa> findAll() {
        return mpaService.findAll();
    }

    @GetMapping("/{id}")
    public Mpa getById(@PathVariable int id) {
        return mpaService.getById(id);
    }
}