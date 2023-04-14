package ru.yandex.practicum.filmorate.model;

import javax.validation.constraints.*;

import lombok.*;

import java.time.LocalDate;

@Builder
@Data
public class User {
    @PositiveOrZero(message = "ID не может быть меньше ноля")
    @EqualsAndHashCode.Exclude
    private int id;
    @NotBlank(message = "Почта не должна быть пустой")
    @Email(message = "Некорректная почта")
    private String email;
    @NotBlank(message = "Логин не должен быть пустой")
    private String login;
    private String name;
    @NotNull
    @PastOrPresent(message = "Некоректная дата рождения")
    private LocalDate birthday;
}
