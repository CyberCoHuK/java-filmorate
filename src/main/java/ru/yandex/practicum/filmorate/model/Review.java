package ru.yandex.practicum.filmorate.model;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.*;
import lombok.experimental.FieldDefaults;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.PositiveOrZero;
import java.util.HashMap;
import java.util.Map;
import java.util.Objects;

@Builder
@AllArgsConstructor
@NoArgsConstructor
@Setter
@Getter
@FieldDefaults(level = AccessLevel.PRIVATE)
public class Review {
    @PositiveOrZero
    @EqualsAndHashCode.Include
    Long reviewId;
    @NotBlank(message = "Отзыв не может быть пустым и состоять только из пробелов.")
    String content;
    @JsonProperty(value = "isPositive")
    @NotNull(message = "Тип отзыва - обязательное поле.")
    Boolean isPositive;
    @NotNull(message = "Идентификатор пользователя - обязательное поле.")
    Long userId;
    @NotNull(message = "Идентификатор фильма - обязательное поле.")
    Long filmId;
    Long useful;

    public Map<String, Object> toMap() {
        Map<String, Object> fields = new HashMap<>();
        fields.put("review_id", reviewId);
        fields.put("content", content);
        fields.put("is_positive", isPositive);
        fields.put("user_id", userId);
        fields.put("film_id", filmId);
        fields.put("useful", useful);

        return fields;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Review review = (Review) o;
        return Objects.equals(reviewId, review.reviewId);
    }

    @Override
    public int hashCode() {
        return Objects.hash(reviewId);
    }

    @Override
    public String toString() {
        return "Review{" +
                "reviewId=" + reviewId +
                ", content='" + content + '\'' +
                ", isPositive=" + isPositive +
                ", userId=" + userId +
                ", filmId=" + filmId +
                ", useful=" + useful +
                '}';
    }
}
