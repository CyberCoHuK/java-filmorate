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
public class Mpa {
    @PositiveOrZero
    int id;
    @NotNull
    @Size(max = 60, message = "Слишком длинное имя. Максимальное количество символов - 60")
    String name;
}
