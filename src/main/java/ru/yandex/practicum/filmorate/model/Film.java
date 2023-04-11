package ru.yandex.practicum.filmorate.model;

import javax.validation.constraints.*;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.RequiredArgsConstructor;

import java.time.LocalDate;


@Data
@RequiredArgsConstructor
public class Film {
    @PositiveOrZero
    @EqualsAndHashCode.Exclude
    private int id;
    @NotBlank(message = "Отсутствует название фильма")
    private String name;
    @NotNull
    @Size(max = 200, message = "Слишком длинное описание. Максимальное количество символов - 200")
    private String description;
    @NotNull
    private LocalDate releaseDate;
    @Min(value = 1, message = "Неправильная продолжительность фильма")
    @Positive
    private int duration;
}
