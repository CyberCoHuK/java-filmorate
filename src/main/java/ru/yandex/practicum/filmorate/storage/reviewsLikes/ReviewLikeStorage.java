package ru.yandex.practicum.filmorate.storage.reviewsLikes;

public interface ReviewLikeStorage {
    void addLike(int reviewId, int userId);

    void addDislike(int reviewId, int userId);

    boolean deleteLike(int reviewId, int userId);

    boolean deleteDislike(int reviewId, int userId);
}
