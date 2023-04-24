package ru.yandex.practicum.filmorate.exceptions;

public class ObjectAlreadyExistException extends RuntimeException {
    public ObjectAlreadyExistException(final String message) {
        super(message);
    }
}
