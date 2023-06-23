package ru.yandex.practicum.filmorate.model;

import lombok.*;
import lombok.experimental.FieldDefaults;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.PositiveOrZero;
import java.util.HashMap;
import java.util.Map;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE)
public class Director {
    @PositiveOrZero
    @EqualsAndHashCode.Include
    private Long id;
    @NotBlank(message = "Отсутствует имя директора")
    String name;

    public Map<String, Object> toMap() {
        Map<String, Object> fields = new HashMap<>();
        fields.put("id", id);
        fields.put("name", name);
        return fields;
    }
}
