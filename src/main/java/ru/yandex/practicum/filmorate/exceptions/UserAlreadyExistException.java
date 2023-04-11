package ru.yandex.practicum.filmorate.exceptions;

public class UserAlreadyExistException extends RuntimeException {
    public UserAlreadyExistException(final String message) {
        super(message);
    }
}
