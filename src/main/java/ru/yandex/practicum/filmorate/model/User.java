package ru.yandex.practicum.filmorate.model;

import jakarta.validation.constraints.*;
import lombok.*;

import java.time.LocalDate;

@Data
@RequiredArgsConstructor
public class User {
    @PositiveOrZero
    @EqualsAndHashCode.Exclude
    private int id;
    @NotBlank
    @Email
    private String email;
    @NotBlank
    private String login;
    private String name;
    @NotNull
    @PastOrPresent(message = "Некоректная дата рождения")
    private LocalDate birthday;
}
