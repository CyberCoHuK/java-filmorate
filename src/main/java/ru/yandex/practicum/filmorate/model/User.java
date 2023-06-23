package ru.yandex.practicum.filmorate.model;

import lombok.*;
import lombok.experimental.FieldDefaults;

import javax.validation.constraints.Email;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.PastOrPresent;
import java.time.LocalDate;
import java.util.HashSet;
import java.util.Set;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE)
public class User {
    @EqualsAndHashCode.Include
    Long id;
    @NotBlank(message = "Почта не должна быть пустой")
    @Email(message = "Некорректная почта")
    String email;
    @NotBlank(message = "Логин не должен быть пустой")
    String login;
    String name;
    @NotNull
    @PastOrPresent(message = "Некорректная дата рождения")
    LocalDate birthday;
    Set<Long> friendsList = new HashSet<>();
}
