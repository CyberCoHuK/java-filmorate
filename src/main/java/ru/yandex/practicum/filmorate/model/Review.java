package ru.yandex.practicum.filmorate.model;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.*;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import java.util.HashMap;
import java.util.Map;
import java.util.Objects;

@Builder
@AllArgsConstructor
@NoArgsConstructor
@Setter
@Getter
public class Review {
    private Long reviewId;
    @NotBlank(message = "Отзыв не может быть пустым и состоять только из пробелов.")
    private String content;
    @JsonProperty(value = "isPositive")
    @NotNull(message = "Тип отзыва - обязательное поле.")
    private Boolean isPositive;
    @NotNull(message = "Идентификатор пользователя - обязательное поле.")
    private Integer userId;
    @NotNull(message = "Идентификатор фильма - обязательное поле.")
    private Integer filmId;
    private Integer useful;

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