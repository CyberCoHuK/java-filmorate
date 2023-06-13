package ru.yandex.practicum.filmorate.storage.film;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Primary;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.simple.SimpleJdbcInsert;
import org.springframework.jdbc.support.rowset.SqlRowSet;
import org.springframework.stereotype.Component;
import ru.yandex.practicum.filmorate.exceptions.ObjectNotFoundException;
import ru.yandex.practicum.filmorate.model.Director;
import ru.yandex.practicum.filmorate.model.Film;
import ru.yandex.practicum.filmorate.model.Genre;
import ru.yandex.practicum.filmorate.storage.mapper.FilmMapper;

import java.util.*;

@Slf4j
@Component
@Primary
public class FilmDbStorage implements FilmStorage {
    private final JdbcTemplate jdbcTemplate;
    private final FilmMapper filmMapper;

    @Autowired
    public FilmDbStorage(JdbcTemplate jdbcTemplate, FilmMapper filmMapper) {
        this.jdbcTemplate = jdbcTemplate;
        this.filmMapper = filmMapper;
    }

    @Override
    public Collection<Film> getAllFilms() {
        final String sql = "SELECT * FROM film";
        log.info("Отправлены все фильмы");
        return jdbcTemplate.query(sql, filmMapper);
    }

    @Override
    public Film updateFilm(Film film) {
        isExist(film.getId());

        final String sqlQuery = "UPDATE film SET name = ?, description = ?, release_date = ?, duration = ? " +
                "WHERE film_id = ?";

        if (film.getMpa() != null) {
            final String updateMpa = "UPDATE film SET rating_id = ? WHERE film_id = ?";
            jdbcTemplate.update(updateMpa, film.getMpa().getId(), film.getId());
        }

        if (film.getGenres() != null) {
            final String deleteGenresQuery = "DELETE FROM film_genre WHERE film_id = ?";
            final String updateGenresQuery = "INSERT INTO film_genre (film_id, genre_id) VALUES (?, ?)";
            final String sqlCheck = "SELECT * FROM film_genre WHERE film_id = ? AND genre_id = ?";

            jdbcTemplate.update(deleteGenresQuery, film.getId());
            for (Genre g : film.getGenres()) {
                SqlRowSet genreRows = jdbcTemplate.queryForRowSet(sqlCheck, film.getId(), g.getId());
                if (!genreRows.next()) {
                    jdbcTemplate.update(updateGenresQuery, film.getId(), g.getId());
                }
            }
        }

        if (film.getDirectors() != null) {
            final String deleteDirectorQuery = "DELETE FROM films_directors WHERE film_id = ?";
            final String updateDirectorQuery = "INSERT INTO films_directors (film_id, director_id) VALUES (?, ?)";
            final String sqlCheck = "SELECT * FROM films_directors WHERE film_id = ? AND director_id = ?";

            jdbcTemplate.update(deleteDirectorQuery, film.getId());
            for (Director d : film.getDirectors()) {
                SqlRowSet genreRows = jdbcTemplate.queryForRowSet(sqlCheck, film.getId(), d.getId());
                if (!genreRows.next()) {
                    jdbcTemplate.update(updateDirectorQuery, film.getId(), d.getId());
                }
            }
        }

        jdbcTemplate.update(sqlQuery, film.getName(), film.getDescription(), film.getReleaseDate(), film.getDuration(),
                film.getId());
        log.info("Обновлен фильм с индентификатором {} ", film.getId());
        return getById(film.getId());
    }

    @Override
    public Film createFilm(Film film) {
        SimpleJdbcInsert jdbcInsert = new SimpleJdbcInsert(jdbcTemplate);
        Number key = jdbcInsert.withTableName("film")
                .usingGeneratedKeyColumns("film_id")
                .executeAndReturnKey(getFilmFields(film));
        film.setId(key.intValue());

        if (film.getGenres() != null) {
            final String updateGenresQuery = "INSERT INTO film_genre (film_id, genre_id) VALUES (?, ?)";
            final String sqlCheck = "SELECT * FROM film_genre  WHERE film_id = ? AND genre_id = ?";
            for (Genre g : film.getGenres()) {
                SqlRowSet genreRows = jdbcTemplate.queryForRowSet(sqlCheck, film.getId(), g.getId());
                if (!genreRows.next()) {
                    jdbcTemplate.update(updateGenresQuery, film.getId(), g.getId());
                }
            }
        }

        if (film.getDirectors() != null) {
            final String updateFilmDirectorQuery = "INSERT INTO films_directors (film_id, director_id) VALUES (?, ?)";
            final String sqlCheck = "SELECT * FROM films_directors  WHERE film_id = ? AND director_id = ?";
            for (Director d : film.getDirectors()) {
                SqlRowSet directorRows = jdbcTemplate.queryForRowSet(sqlCheck, film.getId(), d.getId());
                if (!directorRows.next()) {
                    jdbcTemplate.update(updateFilmDirectorQuery, film.getId(), d.getId());
                }
            }
        }

        if (film.getLikesList() != null) {
            final String updateLikesDirectorQuery = "INSERT INTO likes (film_id, user_id) VALUES (?, ?)";
            final String sqlCheck = "SELECT * FROM likes  WHERE film_id = ? AND user_id = ?";
            for (int like : film.getLikesList()) {
                SqlRowSet directorRows = jdbcTemplate.queryForRowSet(sqlCheck, film.getId(), like);
                if (!directorRows.next()) {
                    jdbcTemplate.update(updateLikesDirectorQuery, film.getId(), like);
                }
            }
        }

        log.info("Создан фильм с индентификатором {} ", film.getId());
        return getById(film.getId());
    }

    @Override
    public Film getById(int filmId) {
        isExist(filmId);
        final String sql = "SELECT * FROM film WHERE film_id = ?";
        log.info("Отправлен фильм с индентификатором {} ", filmId);
        return jdbcTemplate.queryForObject(sql, filmMapper, filmId);
    }

    @Override
    public Film addLike(int filmId, int userId) {
        final String sqlQuery = "MERGE INTO likes (film_id, user_id) VALUES (?, ?)";

        jdbcTemplate.update(sqlQuery, filmId, userId);

        log.info("Пользователь {} поставил лайк к фильму {}", userId, filmId);

        return getById(filmId);
    }

    @Override
    public Film deleteLike(int filmId, int userId) {
        final String sqlQuery = "DELETE FROM likes WHERE film_id = ? AND user_id = ?";

        jdbcTemplate.update(sqlQuery, filmId, userId);

        log.info("Пользователь {} удалил лайк к фильму {}", userId, filmId);

        return getById(filmId);
    }

    @Override
    public List<Film> getPopular(Integer count, Integer genreId, Integer year) {

        if (genreId == 9999 && year == 9999) {
            String sq = "SELECT film.*, COUNT(l.film_id) as count " +
                    "FROM FILM  " +
                    "LEFT JOIN Film_Genre fg on film.film_id = fg.film_id " +
                    "LEFT JOIN Likes as l on film.film_id = l.film_id  " +
                    "GROUP BY film.film_id " +
                    "ORDER BY COUNT DESC " +
                    "LIMIT ?";
            return jdbcTemplate.query(sq, filmMapper, count);

        } else if (genreId == 9999) {
            String sq = "SELECT film.*, COUNT(l.film_id) as count " +
                    "FROM film " +
                    "LEFT JOIN likes AS l ON film.film_id=l.film_id " +
                    "WHERE EXTRACT(YEAR FROM release_date) = ? " +
                    "GROUP BY film.film_id " +
                    "ORDER BY count DESC " +
                    "LIMIT ?";
            return jdbcTemplate.query(sq, filmMapper, year, count);
        } else if (year == 9999) {
            String sq = "SELECT film.*, COUNT(l.film_id) as count " +
                    "FROM film " +
                    "LEFT JOIN likes AS l ON film.film_id=l.film_id " +
                    "LEFT JOIN FILM_GENRE fg on film.film_id = fg.film_id " +
                    "WHERE genre_id = ? " +
                    "GROUP BY film.film_id " +
                    "ORDER BY count DESC " +
                    "LIMIT ?";
            return jdbcTemplate.query(sq, filmMapper, genreId, count);
        } else {
            String sq = "SELECT film.*, COUNT(l.film_id) as count " +
                    "FROM film LEFT JOIN likes AS l ON film.film_id=l.film_id " +
                    "LEFT JOIN FILM_GENRE fg on film.film_id = fg.film_id " +
                    "WHERE genre_id = ? AND EXTRACT(YEAR FROM release_date) = ? " +
                    "GROUP BY film.film_id " +
                    "ORDER BY count DESC " +
                    "LIMIT ?";
            return jdbcTemplate.query(sq, filmMapper, genreId, year, count);
        }
    }

    public Set<Integer> getLikesForCurrentFilm(int filmId) {
        final String sqlQuery = "SELECT user_id FROM likes WHERE film_id = ?";
        SqlRowSet likesRows = jdbcTemplate.queryForRowSet(sqlQuery, filmId);
        Set<Integer> likes = new HashSet<>();
        while (likesRows.next()) {
            likes.add(likesRows.getInt("user_id"));
        }
        log.info("Количество лайков фильму {}", likes.size());
        return likes;
    }

    public Collection<Film> getUserRecommendations(int userId) {
        final String sql = "SELECT f.* " +
                "FROM likes AS l1 " +
                "INNER JOIN film AS f ON l1.film_id = f.film_id " +
                "WHERE l1.user_id = (" +
                "SELECT l2.user_id FROM likes AS l2 WHERE l2.user_id <> ? " +
                "AND l2.film_id IN (" +
                "SELECT l3.film_id FROM likes AS l3 WHERE l3.user_id = ?)" +
                "GROUP BY l2.user_id " +
                "ORDER BY COUNT (l2.film_id) DESC " +
                "LIMIT 1)" +
                "AND l1.film_id NOT IN (SELECT l4.film_id FROM likes AS l4 WHERE l4.user_id = ?)";
        log.info("Отправлены рекомендованные фильмы для пользователя с индентификатором {}", userId);
        return jdbcTemplate.query(sql, filmMapper, userId, userId, userId);
    }

    public String deleteFilmById(int filmId) {
        String sqlQuery = "DELETE FROM film WHERE film_id = ? ";
        int filmRows = jdbcTemplate.update(sqlQuery, filmId);
        if (filmRows == 0) {
            log.warn("Фильм {} не найден.", filmId);
            throw new ObjectNotFoundException("Фильм не найден. Удаление не может быть осуществлено");
        }
        return "Фильм film_id=" + filmId + " успешно удален.";
    }

    @Override
    public List<Film> loadFilmsOfDirectorSortedByYears(int directorId) {
        String sqlQuery =
                "SELECT f.*, m.id " +
                        "FROM film AS f " +
                        "JOIN rating_mpa AS m ON m.id = f.rating_id " +
                        "JOIN films_directors AS d ON d.film_id = f.film_id " +
                        "WHERE d.director_id = ? " +
                        "ORDER BY f.release_date ASC;";
        return jdbcTemplate.query(sqlQuery, filmMapper, directorId);
    }

    @Override
    public List<Film> loadFilmsOfDirectorSortedByLikes(int directorId) {
        String sqlQuery =
                "SELECT f.*, m.id, count(l.user_id) AS top " +
                        "FROM film AS f " +
                        "LEFT JOIN rating_mpa AS m ON m.id = f.rating_id " +
                        "LEFT JOIN films_directors AS d ON d.film_id = f.film_id " +
                        "LEFT JOIN likes AS l ON l.film_id = f.film_id " +
                        "WHERE d.director_id = ? " +
                        "GROUP BY f.film_id " +
                        "ORDER BY top ASC;";
        return jdbcTemplate.query(sqlQuery, filmMapper, directorId);
    }

    @Override
    public List<Film> getFriendsCommonFilms(int userId, int friendId) {
        String sqlQuery = "SELECT f.*, count(l.user_id) AS top FROM likes AS l " +
                "JOIN film AS f ON f.film_id=l.film_id " +
                "WHERE l.user_id  in (?, ?) " +
                "GROUP BY l.film_id " +
                "HAVING COUNT(l.user_id) > 1;";
        return jdbcTemplate.query(sqlQuery, filmMapper, userId, friendId);
    }

    @Override
    public List<Film> searchFilmByParameter(String query, String filmSearchParameter) {
        String sqlQuery;
        switch (filmSearchParameter) {
            case "director":
                sqlQuery = "SELECT f.* " +
                        "FROM film AS f " +
                        "LEFT JOIN LIKES AS l ON f.film_id=l.film_id " +
                        "LEFT JOIN FILMS_DIRECTORS  AS fd ON f.film_id=fd.film_id " +
                        "LEFT JOIN DIRECTORS AS d ON d.id=fd.director_id " +
                        "WHERE d.NAME LIKE ? " +
                        "GROUP BY f.film_id " +
                        "ORDER BY count(l.user_id);";
                return jdbcTemplate.query(sqlQuery, filmMapper, '%' + query + '%');
            case "title":
                sqlQuery = "SELECT f.* " +
                        "FROM film AS f " +
                        "LEFT JOIN LIKES AS l ON f.film_id=l.film_id " +
                        "WHERE LOWER(f.NAME) LIKE ? " +
                        "GROUP BY f.film_id " +
                        "ORDER BY count(l.user_id);";
                return jdbcTemplate.query(sqlQuery, filmMapper, '%' + query + '%');
            case "director,title":
            case "title,director":
                sqlQuery = "SELECT f.* " +
                        "FROM film AS f " +
                        "LEFT JOIN LIKES AS l ON f.film_id=l.film_id " +
                        "LEFT JOIN FILMS_DIRECTORS  AS fd ON f.film_id=fd.film_id " +
                        "LEFT JOIN DIRECTORS AS d ON d.id=fd.director_id " +
                        "WHERE d.NAME LIKE ? " +
                        "OR LOWER(f.NAME) LIKE ? " +
                        "GROUP BY f.film_id " +
                        "ORDER BY count(l.user_id) DESC;";
                return jdbcTemplate.query(sqlQuery, filmMapper, '%' + query + '%', '%' + query + '%');
            default:
                throw new IllegalArgumentException("Задан не корректный параметр сортировки");
        }

    }

    public void isExist(int filmId) {
        final String checkFilmQuery = "SELECT * FROM film WHERE film_id = ?";

        SqlRowSet filmRows = jdbcTemplate.queryForRowSet(checkFilmQuery, filmId);

        if (!filmRows.next()) {
            log.warn("Фильм с идентификатором {} не найден.", filmId);
            throw new ObjectNotFoundException("Фильм с идентификатором " + filmId + " не найден.");
        }
    }

    private Map<String, Object> getFilmFields(Film film) {
        Map<String, Object> fields = new HashMap<>();
        fields.put("NAME", film.getName());
        fields.put("DESCRIPTION", film.getDescription());
        fields.put("DURATION", film.getDuration());
        fields.put("RELEASE_DATE", film.getReleaseDate());
        if (film.getMpa() != null) {
            fields.put("RATING_ID", film.getMpa().getId());
        }
        return fields;
    }
}
