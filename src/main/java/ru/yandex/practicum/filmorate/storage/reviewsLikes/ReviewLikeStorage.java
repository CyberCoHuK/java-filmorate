package ru.yandex.practicum.filmorate.storage.reviewsLikes;

public interface ReviewLikeStorage {
    void addLike(Long reviewId, Long userId);

    void addDislike(Long reviewId, Long userId);

    boolean deleteLike(Long reviewId, Long userId);

    boolean deleteDislike(Long reviewId, Long userId);
}
