package ru.yandex.practicum.filmorate.model;

import lombok.*;
import lombok.experimental.FieldDefaults;

import javax.validation.constraints.*;
import java.time.LocalDate;
import java.util.HashSet;
import java.util.Set;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE)
public class User {
    @PositiveOrZero(message = "ID не может быть меньше ноля")
    @EqualsAndHashCode.Exclude
    int id;
    @NotBlank(message = "Почта не должна быть пустой")
    @Email(message = "Некорректная почта")
    String email;
    @NotBlank(message = "Логин не должен быть пустой")
    String login;
    String name;
    @NotNull
    @PastOrPresent(message = "Некоректная дата рождения")
    LocalDate birthday;
    Set<Integer> friendsList = new HashSet<>();

}
