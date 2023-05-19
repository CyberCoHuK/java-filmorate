package ru.yandex.practicum.filmorate.model;

import lombok.*;
import lombok.experimental.FieldDefaults;

import javax.validation.constraints.NotNull;
import javax.validation.constraints.PositiveOrZero;
import javax.validation.constraints.Size;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE)
public class Genre {
    @PositiveOrZero
    int id;
    @NotNull
    @Size(max = 100, message = "Слишком длинное название жанра. Максимальное количество символов - 100")
    String name;
}
