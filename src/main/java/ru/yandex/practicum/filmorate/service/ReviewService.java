package ru.yandex.practicum.filmorate.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import ru.yandex.practicum.filmorate.enums.EventTypes;
import ru.yandex.practicum.filmorate.enums.Operations;
import ru.yandex.practicum.filmorate.exceptions.ObjectNotFoundException;
import ru.yandex.practicum.filmorate.model.Review;
import ru.yandex.practicum.filmorate.storage.feed.FeedStorage;
import ru.yandex.practicum.filmorate.storage.film.FilmStorage;
import ru.yandex.practicum.filmorate.storage.review.ReviewStorage;
import ru.yandex.practicum.filmorate.storage.reviewsLikes.ReviewLikeStorage;
import ru.yandex.practicum.filmorate.storage.user.UserStorage;

import java.util.Collection;
import java.util.Objects;

@Service
@RequiredArgsConstructor
@Slf4j
public class ReviewService {
    private final ReviewStorage reviewStorage;
    private final UserStorage userStorage;
    private final FilmStorage filmStorage;
    private final ReviewLikeStorage reviewLikeStorage;

    private final FeedStorage feedStorage;

    private static final int INCREMENT = 1;
    private static final int DECREMENT = -1;

    public Review getReviewById(long reviewId) {
        return reviewStorage.findById(reviewId)
                .orElseThrow(() -> new ObjectNotFoundException("Отзыв с id = " + reviewId + " не найден."));
    }

    public Collection<Review> getReviews(Integer filmId, int count) {
        if (Objects.isNull(filmId)) {
            return reviewStorage.findMostUsefulReviews(count);
        }

        return reviewStorage.findMostUsefulReviewsByFilmId(filmId, count);
    }

    public Review createReview(Review review) {
        review.setUseful(0);

        userStorage.isExist(review.getUserId());
        filmStorage.isExist(review.getFilmId());
        Review saveReview = reviewStorage.saveReview(review);
        feedStorage.addEvent(saveReview.getUserId(), EventTypes.REVIEW, Operations.ADD, saveReview.getReviewId());

        return saveReview;
    }

    public Review updateReview(Review review) {
        if (!reviewStorage.existsById(review.getReviewId())) {
            throw new ObjectNotFoundException("Отзыв с id = " + review.getReviewId() + " не найден.");
        }

        userStorage.isExist(review.getUserId());
        filmStorage.isExist(review.getFilmId());
        Review updateReview = reviewStorage.updateReview(review);
        feedStorage.addEvent(updateReview.getUserId(), EventTypes.REVIEW, Operations.UPDATE, updateReview.getReviewId());
        return updateReview;
    }

    public void deleteReviewById(long reviewId) {
        if (!reviewStorage.existsById(reviewId)) {
            throw new ObjectNotFoundException("Отзыв с id = " + reviewId + " не найден.");
        }
        feedStorage.addEvent(getReviewById(reviewId).getUserId(), EventTypes.REVIEW, Operations.REMOVE, reviewId);

        reviewStorage.deleteById(reviewId);
    }

    public void addLike(int reviewId, int userId) {
        if (!reviewStorage.existsById(reviewId)) {
            throw new ObjectNotFoundException("Отзыв с id = " + reviewId + " не найден.");
        }

        userStorage.isExist(userId);

        reviewLikeStorage.addLike(reviewId, userId);

        reviewStorage.changeUseful(reviewId, INCREMENT);
    }

    public void addDislike(int reviewId, int userId) {
        if (!reviewStorage.existsById(reviewId)) {
            throw new ObjectNotFoundException("Отзыв с id = " + reviewId + " не найден.");
        }

        userStorage.isExist(userId);

        reviewLikeStorage.addDislike(reviewId, userId);

        reviewStorage.changeUseful(reviewId, DECREMENT);
    }

    public void deleteLike(int reviewId, int userId) {
        if (!reviewStorage.existsById(reviewId)) {
            throw new ObjectNotFoundException("Отзыв с id = " + reviewId + " не найден.");
        }

        userStorage.isExist(userId);

        if (!reviewLikeStorage.deleteLike(reviewId, userId)) {
            throw new ObjectNotFoundException("Пользователь с id = " + userId +
                    " не ставил лайк отзыву с id = " + reviewId + ".");
        }

        reviewStorage.changeUseful(reviewId, DECREMENT);
    }

    public void deleteDislike(int reviewId, int userId) {
        if (!reviewStorage.existsById(reviewId)) {
            throw new ObjectNotFoundException("Отзыв с id = " + reviewId + " не найден.");
        }

        userStorage.isExist(userId);

        if (!reviewLikeStorage.deleteDislike(reviewId, userId)) {
            throw new ObjectNotFoundException("Пользователь с id = " + userId +
                    " не ставил дизлайк отзыву с id = " + reviewId + ".");
        }

        reviewStorage.changeUseful(reviewId, INCREMENT);
    }
}
