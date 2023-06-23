package ru.yandex.practicum.filmorate.storage;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.test.context.jdbc.Sql;
import org.springframework.test.context.jdbc.SqlGroup;
import ru.yandex.practicum.filmorate.model.Review;
import ru.yandex.practicum.filmorate.storage.mapper.ReviewMapper;
import ru.yandex.practicum.filmorate.storage.review.ReviewStorage;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
@AutoConfigureTestDatabase
@DirtiesContext(classMode = DirtiesContext.ClassMode.BEFORE_EACH_TEST_METHOD)
@SqlGroup({
        @Sql(value = "/test/review-test.sql", executionPhase = Sql.ExecutionPhase.BEFORE_TEST_METHOD),
        @Sql(value = "/test/clear.sql", executionPhase = Sql.ExecutionPhase.AFTER_TEST_METHOD),
})
public class ReviewDbStorageTest {
    @Autowired
    private ReviewStorage reviewStorage;
    @Autowired
    private ReviewMapper reviewMapper;
    @Autowired
    private JdbcTemplate jdbcTemplate;

    @Test
    public void shouldSaveReviewInDataBase() {
        Review review = Review.builder()
                .content("The best film")
                .isPositive(Boolean.FALSE)
                .userId(3L)
                .filmId(3L)
                .build();

        reviewStorage.saveReview(review);

        String sql = "SELECT * FROM reviews " +
                "WHERE content = ? " +
                "AND is_positive = ? " +
                "AND user_id = ? " +
                "AND film_id = ? ";

        List<Review> savedReviews = jdbcTemplate.query(sql,
                reviewMapper,
                review.getContent(),
                review.getIsPositive(),
                review.getUserId(),
                review.getFilmId()
        );

        int expectedSize = 1;

        assertEquals(expectedSize, savedReviews.size());
        assertEquals(3, savedReviews.get(0).getReviewId());
    }

    @Test
    public void shouldThrowExceptionWhenSaveReviewWithUnknownUserId() {
        Long incorrectUserId = 30L;
        Review review = Review.builder()
                .content("The best film")
                .isPositive(Boolean.FALSE)
                .userId(incorrectUserId)
                .filmId(3L)
                .build();

        assertThrows(DataIntegrityViolationException.class, () -> reviewStorage.saveReview(review));
    }

    @Test
    public void shouldThrowExceptionWhenSaveReviewWithUnknownFilmId() {
        Long incorrectFilmId = 30L;
        Review review = Review.builder()
                .content("The best film")
                .isPositive(Boolean.FALSE)
                .userId(3L)
                .filmId(incorrectFilmId)
                .build();

        assertThrows(DataIntegrityViolationException.class, () -> reviewStorage.saveReview(review));
    }

    @Test
    public void shouldReturnSavedReviewWithId() {
        Review review = Review.builder()
                .content("The best film")
                .isPositive(Boolean.FALSE)
                .userId(3L)
                .filmId(3L)
                .build();

        Review savedReview = reviewStorage.saveReview(review);

        assertNotNull(savedReview.getReviewId());
    }

    @Test
    public void shouldUpdateReviewInDataBase() {
        Long reviewId = 1L;

        Review review = Review.builder()
                .reviewId(reviewId)
                .content("Updated Good film")
                .isPositive(Boolean.FALSE)
                .userId(1L)
                .filmId(1L)
                .useful(1L)
                .build();

        reviewStorage.updateReview(review);

        String sql = "SELECT * FROM reviews " +
                "WHERE review_id = ?";

        Review updatedReview = jdbcTemplate.queryForObject(sql, reviewMapper, reviewId);


        assertNotNull(updatedReview);
        assertEquals(review.getReviewId(), updatedReview.getReviewId());
        assertEquals(review.getUseful(), updatedReview.getUseful());
        assertEquals(review.getUserId(), updatedReview.getUserId());
        assertEquals(review.getFilmId(), updatedReview.getFilmId());
        assertEquals(review.getIsPositive(), updatedReview.getIsPositive());
        assertEquals(review.getContent(), updatedReview.getContent());
    }

    @Test
    public void shouldReturnReviewById() {
        Review expectedReview = new Review(
                2L,
                "Bad film",
                Boolean.FALSE,
                2L,
                2L,
                0L
        );

        Optional<Review> foundReview = reviewStorage.findById(2L);

        assertThat(foundReview)
                .isPresent()
                .hasValueSatisfying(review -> {
                            assertThat(review).hasFieldOrPropertyWithValue("reviewId", expectedReview.getReviewId());
                            assertThat(review).hasFieldOrPropertyWithValue("content", expectedReview.getContent());
                            assertThat(review).hasFieldOrPropertyWithValue("isPositive", expectedReview.getIsPositive());
                            assertThat(review).hasFieldOrPropertyWithValue("userId", expectedReview.getUserId());
                            assertThat(review).hasFieldOrPropertyWithValue("filmId", expectedReview.getFilmId());
                            assertThat(review).hasFieldOrPropertyWithValue("useful", expectedReview.getUseful());
                        }
                );
    }

    @Test
    public void shouldDeleteReviewById() {
        String sql = "SELECT * FROM reviews WHERE review_id = ?";
        List<Review> foundReviews = jdbcTemplate.query(sql, reviewMapper, 1L);

        assertEquals(1, foundReviews.size());

        reviewStorage.deleteById(1L);

        foundReviews = jdbcTemplate.query(sql, reviewMapper, 1L);

        assertTrue(foundReviews.isEmpty());
    }

    @Test
    public void shouldIncreaseUsefulByOne() {
        long reviewId = 1L;
        String sql = "SELECT useful FROM reviews WHERE review_id = ?";

        Integer useful = jdbcTemplate.queryForObject(sql, Integer.class, reviewId);

        assertEquals(1, useful);

        reviewStorage.changeUseful(reviewId, 1);

        sql = "SELECT useful FROM reviews WHERE review_id = ?";

        useful = jdbcTemplate.queryForObject(sql, Integer.class, reviewId);

        assertEquals(2, useful);
    }

    @Test
    public void shouldDecreaseUsefulByOne() {
        long reviewId = 1L;
        String sql = "SELECT useful FROM reviews WHERE review_id = ?";

        Integer useful = jdbcTemplate.queryForObject(sql, Integer.class, reviewId);

        assertEquals(1, useful);

        reviewStorage.changeUseful(reviewId, -1);

        sql = "SELECT useful FROM reviews WHERE review_id = ?";

        useful = jdbcTemplate.queryForObject(sql, Integer.class, reviewId);

        assertEquals(0, useful);
    }

    @Test
    public void shouldReturnTrueWhenReviewExist() {
        long reviewId = 1L;

        assertTrue(reviewStorage.existsById(reviewId));
    }

    @Test
    public void shouldReturnFalseWhenReviewNotExist() {
        long reviewId = 10L;

        assertFalse(reviewStorage.existsById(reviewId));
    }

    @Test
    public void shouldReturnOnlyOneMostUsefulReview() {
        String sql = "INSERT INTO reviews (content, is_positive, user_id, film_id, useful) " +
                "VALUES ('Bad film', false, 2, 2, 5)";

        jdbcTemplate.update(sql);

        List<Review> review = new ArrayList<>(reviewStorage.findMostUsefulReviews(1));

        assertEquals(1, review.size());
        assertEquals(5, review.get(0).getUseful());
        assertEquals(2, review.get(0).getUserId());
        assertEquals(2, review.get(0).getFilmId());
        assertEquals(3, review.get(0).getReviewId());
    }

    @Test
    public void shouldReturnOnlyOneMostUsefulReviewByFilmId() {
        String sql = "INSERT INTO reviews (content, is_positive, user_id, film_id, useful) " +
                "VALUES ('Bad film', false, 2, 3, 5)";

        jdbcTemplate.update(sql);

        List<Review> review = new ArrayList<>(reviewStorage.findMostUsefulReviewsByFilmId(3L, 1));

        assertEquals(1, review.size());
        assertEquals(5, review.get(0).getUseful());
        assertEquals(2, review.get(0).getUserId());
        assertEquals(3, review.get(0).getFilmId());
        assertEquals(3, review.get(0).getReviewId());
    }
}
