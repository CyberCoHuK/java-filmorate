package ru.yandex.practicum.filmorate.storage.reviewsLikes;

public interface ReviewLikeStorage {
    void addLike(long reviewId, int userId);

    void addDislike(long reviewId, int userId);

    boolean deleteLike(long reviewId, int userId);

    boolean deleteDislike(long reviewId, int userId);
}
