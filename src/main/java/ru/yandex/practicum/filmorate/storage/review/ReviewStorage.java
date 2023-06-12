package ru.yandex.practicum.filmorate.storage.review;

import ru.yandex.practicum.filmorate.model.Review;

import java.util.Collection;
import java.util.Optional;

public interface ReviewStorage {
    Review updateReview(Review review);

    Review saveReview(Review review);

    Optional<Review> findById(long reviewId);

    Collection<Review> findMostUsefulReviews(int count);

    Collection<Review> findMostUsefulReviewsByFilmId(int filmId, int count);

    boolean existsById(long reviewId);

    void deleteById(long reviewId);

    void changeUseful(long reviewId, int value);
}
