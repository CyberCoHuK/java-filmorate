package ru.yandex.practicum.filmorate.enums;



public enum FilmParameter {
    DIRECTOR("director"),
    TITLE("title"),
    DIR_AND_TITLE("director,title"),
    TITLE_AND_DIR("title,director"),
    ERROR_MESSAGE("Указан не корректный параметр: ");

    private final String value;

    FilmParameter(String value) {
        this.value = value;
    }

    public static FilmParameter validateFilmParameter(String parameter) {
        for (FilmParameter filmParameter : FilmParameter.values()) {
            if (filmParameter.value.equals(parameter)) {
                return filmParameter;
            }
        }
        return ERROR_MESSAGE;
    }
}
