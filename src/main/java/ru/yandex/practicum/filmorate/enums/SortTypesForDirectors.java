package ru.yandex.practicum.filmorate.enums;

public enum SortTypesForDirectors {
    YEAR("year"),
    LIKES("likes");

    private final String sortBy;

    SortTypesForDirectors(String sortBy) {
        this.sortBy = sortBy;
    }

    public static SortTypesForDirectors valueOfSortBy(String sort) {
        for (SortTypesForDirectors type : SortTypesForDirectors.values()) {
            if (type.sortBy.equals(sort)) {
                return type;
            }
        }

        throw new IllegalArgumentException("Задан не корректный параметр сортировки");
    }
}
