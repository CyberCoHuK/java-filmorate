package ru.yandex.practicum.filmorate.storage.review;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.simple.SimpleJdbcInsert;
import org.springframework.stereotype.Repository;
import ru.yandex.practicum.filmorate.model.Review;
import ru.yandex.practicum.filmorate.storage.mapper.ReviewMapper;

import java.util.List;
import java.util.Optional;

@Repository
@RequiredArgsConstructor
@Slf4j
public class ReviewDbStorage implements ReviewStorage {
    private final JdbcTemplate jdbcTemplate;
    private final ReviewMapper reviewMapper;

    public List<Review> findMostUsefulReviews(int count) {
        String sql = "SELECT * FROM reviews " +
                "ORDER BY useful DESC " +
                "LIMIT ?";

        log.info("Отправлены наиболее полезные отзывы по всем фильмам. " +
                "Количество запрошенных отзывов: {}", count);

        return jdbcTemplate.query(sql, reviewMapper, count);
    }

    public List<Review> findMostUsefulReviewsByFilmId(int filmId, int count) {
        String sql = "SELECT * FROM reviews " +
                "WHERE film_id = ? " +
                "ORDER BY useful DESC " +
                "LIMIT ?";

        log.info("Отправлены наиболее полезные отзывы на фильм с идентификаторов {}. " +
                "Количество запрошенных отзывов: {}", filmId, count);

        return jdbcTemplate.query(sql, reviewMapper, filmId, count);
    }

    @Override
    public Review updateReview(Review review) {
        String sql = "UPDATE reviews " +
                "SET content = ?, " +
                "is_positive = ? " +
                "WHERE review_id = ?";

        jdbcTemplate.update(sql,
                review.getContent(),
                review.getIsPositive(),
                review.getReviewId()
        );

        log.info("Обновлен отзыв с индентификатором {}", review.getReviewId());

        return findById(review.getReviewId()).get();
    }

    @Override
    public Review createReview(Review review) {
        SimpleJdbcInsert simpleJdbcInsert = new SimpleJdbcInsert(jdbcTemplate)
                .withTableName("reviews")
                .usingGeneratedKeyColumns("review_id");

        Long reviewId = simpleJdbcInsert.executeAndReturnKey(review.toMap()).longValue();

        review.setReviewId(reviewId);

        log.info("Создан отзыв с индентификатором {}", review.getReviewId());

        return review;
    }

    @Override
    public Optional<Review> findById(long reviewId) {
        String sql = "SELECT * FROM reviews " +
                "WHERE review_id = ?";

        List<Review> results = jdbcTemplate.query(sql, reviewMapper, reviewId);

        if (results.size() == 0) {
            return Optional.empty();
        }

        log.info("Отправлен отзыв с индентификатором {}", reviewId);

        return Optional.of(results.get(0));
    }

    @Override
    public boolean existsById(long reviewId) {
        return findById(reviewId).isPresent();
    }

    @Override
    public void deleteById(long reviewId) {
        String sql = "DELETE FROM reviews " +
                "WHERE review_id = ?";

        log.info("Удален отзыв с индентификатором {}", reviewId);

        jdbcTemplate.update(sql, reviewId);
    }

    @Override
    public void changeUseful(long reviewId, int value) {
        String sql = "UPDATE reviews " +
                "SET useful = useful + ? " +
                "WHERE review_id = ?";

        log.info("Значение полезности отзыва с индентификатором {} изменено", reviewId);

        jdbcTemplate.update(sql, value, reviewId);
    }
}
