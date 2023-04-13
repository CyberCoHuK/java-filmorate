package ru.yandex.practicum.filmorate.model;

import javax.validation.constraints.*;

import lombok.*;

import java.time.LocalDate;

@Builder
@Data
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
    @Positive(message = "Неправильная продолжительность фильма")
    private int duration;
}
