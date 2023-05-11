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
public class Film {
    @PositiveOrZero
    @EqualsAndHashCode.Exclude
    int id;
    @NotBlank(message = "Отсутствует название фильма")
    String name;
    @NotNull
    @Size(max = 200, message = "Слишком длинное описание. Максимальное количество символов - 200")
    String description;
    @NotNull
    LocalDate releaseDate;
    @Positive(message = "Продолжительность фильма не может быть отрицательной")
    long duration;
    Set<Integer> likesList = new HashSet<>();
}
