package ru.yandex.practicum.filmorate.storage.review;

import ru.yandex.practicum.filmorate.model.Review;

import java.util.Collection;
import java.util.Optional;

public interface ReviewStorage {
    Review updateReview(Review review);

    Review saveReview(Review review);

    Optional<Review> findById(Long reviewId);

    Collection<Review> findMostUsefulReviews(int count);

    Collection<Review> findMostUsefulReviewsByFilmId(Long filmId, int count);

    boolean existsById(Long reviewId);

    void deleteById(Long reviewId);

    void changeUseful(Long reviewId, int value);
}
