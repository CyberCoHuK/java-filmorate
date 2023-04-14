package ru.yandex.practicum.filmorate.controllers;

import javax.validation.Valid;
import javax.validation.ValidationException;

import lombok.extern.slf4j.Slf4j;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.*;
import ru.yandex.practicum.filmorate.exceptions.UserAlreadyExistException;
import ru.yandex.practicum.filmorate.model.User;

import java.util.Collection;
import java.util.HashMap;


@RestController
@Slf4j
@RequestMapping("/users")
public class UserController {
    private final HashMap<Integer, User> users = new HashMap<>();
    private static int userId = 1;

    @GetMapping
    public Collection<User> findAll() {
        log.info("Текущее количество пользователей: {}", users.size());
        return users.values();
    }

    @PostMapping
    public User create(@Valid @RequestBody User user) {
        if (users.containsKey(user.getId())) {
            throw new UserAlreadyExistException("Пользователь с такой почтой уже существует");
        } else {
            validate(user);
            user.setId(userId++);
            log.info("Добавлен пользователь:{}", user);
            users.put(user.getId(), user);
            return user;
        }
    }


    @PutMapping
    public User update(@Valid @RequestBody User user) {
        if (users.containsValue(user)) {
            throw new ValidationException("Такой пользователь уже существует");
        } else if (users.containsKey(user.getId())) {
            log.info("Обновлен пользователь:{}", user);
            users.replace(user.getId(), user);
        } else {
            validate(user);
            user.setId(userId++);
            log.info("Добавлен пользователь:{}", user);
            users.put(user.getId(), user);
        }
        return user;
    }

    private void validate(User user) {
        if (StringUtils.isEmpty(user.getName())) {
            user.setName(user.getLogin());
        }
    }
}
