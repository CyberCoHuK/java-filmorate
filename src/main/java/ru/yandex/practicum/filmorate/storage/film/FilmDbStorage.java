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
        final String sql = "SELECT * FROM film WHERE film_id = ?";

        SqlRowSet filmRows = jdbcTemplate.queryForRowSet(sql, film.getId());

        if (!filmRows.next()) {
            log.warn("Фильм с идентификатором {} не найден.", film.getId());
            throw new ObjectNotFoundException("Фильм с идентификатором " + film.getId() + " не найден.");
        }

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
            for (Director d: film.getDirectors()) {
                SqlRowSet directorRows = jdbcTemplate.queryForRowSet(sqlCheck, film.getId(), d.getId());
                if (!directorRows.next()) {
                    jdbcTemplate.update(updateFilmDirectorQuery, film.getId(), d.getId());
                }
            }
        }

        log.info("Создан фильм с индентификатором {} ", film.getId());
        return getById(film.getId());
    }

    @Override
    public Film getById(int filmId) {
        final String sql = "SELECT * FROM film WHERE film_id = ?";

        SqlRowSet filmRows = jdbcTemplate.queryForRowSet(sql, filmId);

        if (!filmRows.next()) {
            log.warn("Фильм с идентификатором {} не найден.", filmId);
            throw new ObjectNotFoundException("Фильм с идентификатором " + filmId + " не найден.");
        } else {
            log.info("Отправлен фильм с индентификатором {} ", filmId);
            return jdbcTemplate.queryForObject(sql, filmMapper, filmId);
        }
    }

    @Override
    public Film addLike(int filmId, int userId) {
        validate(filmId, userId);
        final String sqlQuery = "INSERT INTO likes (film_id, user_id) VALUES (?, ?)";

        jdbcTemplate.update(sqlQuery, filmId, userId);

        log.info("Пользователь {} поставил лайк к фильму {}", userId, filmId);

        return getById(filmId);
    }

    @Override
    public Film deleteLike(int filmId, int userId) {
        validate(filmId, userId);
        final String sqlQuery = "DELETE FROM likes WHERE film_id = ? AND user_id = ?";

        jdbcTemplate.update(sqlQuery, filmId, userId);

        log.info("Пользователь {} удалил лайк к фильму {}", userId, filmId);

        return getById(filmId);
    }

    @Override
    public Collection<Film> getListOfTopFilms(int count) {
        String sqlQuery = "SELECT film.*, COUNT(l.film_id) as count FROM film " +
                "LEFT JOIN likes AS l ON film.film_id=l.film_id " +
                "GROUP BY film.film_id " +
                "ORDER BY count DESC " +
                "LIMIT ?";
        log.info("Отправлен топ {} фильмов", count);
        return jdbcTemplate.query(sqlQuery, filmMapper, count);

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


    private void validate(int filmId, int userId) {
        final String checkFilmQuery = "SELECT * FROM film WHERE film_id = ?";
        final String checkUserQuery = "SELECT * FROM users WHERE user_id = ?";

        SqlRowSet filmRows = jdbcTemplate.queryForRowSet(checkFilmQuery, filmId);
        SqlRowSet userRows = jdbcTemplate.queryForRowSet(checkUserQuery, userId);

        if (!filmRows.next() || !userRows.next()) {
            log.warn("Фильм {} и(или) пользователь {} не найден.", filmId, userId);
            throw new ObjectNotFoundException("Фильм или пользователь не найдены");
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

    @Override
    public List<Film> loadFilmsOfDirectorSortedByYears(long directorId) {
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
    public List<Film> loadFilmsOfDirectorSortedByRating(long directorId) {
        String sqlQuery =
                "SELECT f.*, m.id, count(l.user_id) AS top " +
                        "FROM film AS f " +
                        "JOIN rating_mpa AS m ON m.id = f.rating_id " +
                        "JOIN films_directors AS d ON d.film_id = f.film_id " +
                        "JOIN likes AS l ON l.film_id = f.film_id " +
                        "WHERE d.director_id = ? " +
                        "GROUP BY f.film_id " +
                        "ORDER BY top ASC;";
        return jdbcTemplate.query(sqlQuery, filmMapper, directorId);
    }
}