package ru.yandex.practicum.filmorate.controllers;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;
import ru.yandex.practicum.filmorate.exceptions.ObjectNotFoundException;
import ru.yandex.practicum.filmorate.model.User;
import ru.yandex.practicum.filmorate.service.UserService;
import ru.yandex.practicum.filmorate.storage.feed.FeedStorage;
import ru.yandex.practicum.filmorate.storage.film.FilmStorage;
import ru.yandex.practicum.filmorate.storage.film.InMemoryFilmStorage;
import ru.yandex.practicum.filmorate.storage.user.InMemoryUserStorage;
import ru.yandex.practicum.filmorate.storage.user.UserStorage;

import javax.validation.ConstraintViolation;
import javax.validation.Validation;
import javax.validation.Validator;
import java.time.LocalDate;
import java.util.Set;

import static org.assertj.core.api.AssertionsForClassTypes.assertThat;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

@SpringBootTest
public class UserControllerTest {
    private UserController userController;
    private FeedStorage feedStorage;
    private UserStorage userStorage;
    private User user;
    private User user2;
    private final Validator validator = Validation.buildDefaultValidatorFactory().getValidator();

    @BeforeEach
    @Deprecated
    void beforeEach() {
        userStorage = new InMemoryUserStorage();
        FilmStorage filmStorage = new InMemoryFilmStorage(userStorage);
        UserService userService = new UserService(userStorage, filmStorage, feedStorage);
        userController = new UserController(userService);
        user = User.builder()
                .name("nametest")
                .email("test@mail.ru")
                .login("logintest")
                .birthday(LocalDate.of(1956, 12, 1))
                .build();
        user2 = User.builder()
                .name("nametest2")
                .email("asdf@mail.ru")
                .login("logintest2")
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
        assertEquals("Некорректная дата рождения", violation.getMessage());
    }

    @Test
    public void addFriendCheck() {
        userController.createUser(user);
        userController.createUser(user2);
        assertEquals(0, user.getFriendsList().size());
        assertEquals(0, user2.getFriendsList().size());
        userStorage.addFriend(user.getId(), user2.getId());
        assertEquals(1, user.getFriendsList().size());
        assertEquals(1, user2.getFriendsList().size());
    }

    @Test
    public void deleteFriendCheck() {
        userController.createUser(user);
        userController.createUser(user2);
        userStorage.addFriend(user.getId(), user2.getId());
        assertEquals(1, user.getFriendsList().size());
        assertEquals(1, user2.getFriendsList().size());
        userStorage.deleteFriend(user.getId(), user2.getId());
        assertEquals(0, user.getFriendsList().size());
        assertEquals(0, user2.getFriendsList().size());
    }

    @Test
    public void findAllUserCheck() {
        userController.createUser(user);
        userController.createUser(user2);
        assertEquals(2, userController.findAllUsers().size());
    }

    @Test
    public void getByIdCheck() {
        User checkUser = user;
        userController.createUser(user);
        assertEquals(checkUser, userController.getById(user.getId()));
    }

    @Test
    public void getFriendCheck() {
        User user3 = User.builder()
                .name("nametest3")
                .email("zxcv@mail.ru")
                .login("logintest3")
                .birthday(LocalDate.of(1956, 12, 1))
                .build();
        userController.createUser(user);
        userController.createUser(user2);
        userController.createUser(user3);
        userStorage.addFriend(user.getId(), user2.getId());
        userStorage.addFriend(user.getId(), user3.getId());
        assertEquals(1, userController.getMutualFriends(user2.getId(), user3.getId()).size());
        assertEquals(2, userController.getFriends(user.getId()).size());
    }

    @Test
    public void isExistCheck() {
        ObjectNotFoundException ex = assertThrows(ObjectNotFoundException.class, () -> userController.getById(888));
        assertEquals("Пользователя с таким 888 не существует", ex.getMessage());
    }

    @Test
    public void deleteUserByIdCheck() {
        userController.createUser(user);
        userController.deleteUserById(user.getId());
        assertThat(userController.findAllUsers().isEmpty());
    }
}