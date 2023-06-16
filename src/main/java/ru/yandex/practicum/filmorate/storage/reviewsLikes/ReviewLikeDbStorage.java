package ru.yandex.practicum.filmorate.storage.reviewsLikes;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Repository;

@Repository
@RequiredArgsConstructor
@Slf4j
public class ReviewLikeDbStorage implements ReviewLikeStorage {
    private final JdbcTemplate jdbcTemplate;

    @Override
    public void addLike(int reviewId, int userId) {
        String sqlAddLike = "INSERT INTO reviews_likes (review_id, user_id, is_like) " +
                "VALUES (?, ?, ?)";

        log.info("Пользователь {} поставил лайк отзыву {}", reviewId, userId);

        jdbcTemplate.update(sqlAddLike, reviewId, userId, Boolean.TRUE);
    }

    @Override
    public void addDislike(int reviewId, int userId) {
        String sqlAddLike = "INSERT INTO reviews_likes (review_id, user_id, is_like) " +
                "VALUES (?, ?, ?)";

        log.info("Пользователь {} поставил дизлайк отзыву {}", reviewId, userId);

        jdbcTemplate.update(sqlAddLike, reviewId, userId, Boolean.FALSE);
    }

    @Override
    public boolean deleteLike(int reviewId, int userId) {
        String sql = "DELETE FROM reviews_likes " +
                "WHERE review_id = ? " +
                "AND user_id = ? ";

        log.info("Пользователь {} удалил лайк у отзыва {}", reviewId, userId);

        return jdbcTemplate.update(sql, reviewId, userId) != 0;
    }

    @Override
    public boolean deleteDislike(int reviewId, int userId) {
        String sql = "DELETE FROM reviews_likes " +
                "WHERE review_id = ?" +
                "AND user_id = ? ";

        log.info("Пользователь {} удалил дизлайк у отзыва {}", reviewId, userId);

        return jdbcTemplate.update(sql, reviewId, userId) != 0;
    }
}
