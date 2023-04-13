package ru.yandex.practicum.filmorate.controllers;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;
import ru.yandex.practicum.filmorate.model.User;

import javax.validation.ConstraintViolation;
import javax.validation.Validation;
import javax.validation.Validator;
import java.time.LocalDate;
import java.util.Set;

import static org.junit.jupiter.api.Assertions.assertEquals;

@SpringBootTest
public class UserControllerTest {
    private UserController userController;
    private User user;
    private final Validator validator = Validation.buildDefaultValidatorFactory().getValidator();

    @BeforeEach
    void beforeEach() {
        userController = new UserController();
        user = User.builder()
                .id(1)
                .name("nametest")
                .email("test@mail.ru")
                .login("logintest")
                .birthday(LocalDate.of(1956, 12, 1))
                .build();
    }

    @Test
    public void addUser() {
        Set<ConstraintViolation<User>> violations = validator.validate(user);
        assertEquals(0, violations.size());
    }

    @Test
    public void emptyEmailOfUser() {
        user.setEmail("");
        Set<ConstraintViolation<User>> violations = validator.validate(user);
        assertEquals(1, violations.size());
        ConstraintViolation<User> violation = violations.iterator().next();
        assertEquals("Почта не должна быть пустой", violation.getMessage());
    }

    @Test
    public void wrongEmailOfUser() {
        user.setEmail("testmail.ru");
        Set<ConstraintViolation<User>> violations = validator.validate(user);
        assertEquals(1, violations.size());
        ConstraintViolation<User> violation = violations.iterator().next();
        assertEquals("Некорректная почта", violation.getMessage());
    }

    @Test
    public void emptyLoginOfUser() {
        user.setLogin("");
        Set<ConstraintViolation<User>> violations = validator.validate(user);
        assertEquals(1, violations.size());
        ConstraintViolation<User> violation = violations.iterator().next();
        assertEquals("Логин не должен быть пустой", violation.getMessage());
    }

    @Test
    public void wrongBirthday() {
        user.setBirthday(LocalDate.of(2385, 12, 1));
        Set<ConstraintViolation<User>> violations = validator.validate(user);
        assertEquals(1, violations.size());
        ConstraintViolation<User> violation = violations.iterator().next();
        assertEquals("Некоректная дата рождения", violation.getMessage());
    }
}