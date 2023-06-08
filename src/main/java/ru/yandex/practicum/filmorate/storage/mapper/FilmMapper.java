package ru.yandex.practicum.filmorate.storage.mapper;

import lombok.AccessLevel;
import lombok.experimental.FieldDefaults;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.stereotype.Component;
import ru.yandex.practicum.filmorate.model.Director;
import ru.yandex.practicum.filmorate.model.Film;
import ru.yandex.practicum.filmorate.model.Genre;
import ru.yandex.practicum.filmorate.model.Mpa;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.HashSet;
import java.util.List;

@Component
@FieldDefaults(level = AccessLevel.PRIVATE)
public class FilmMapper implements RowMapper<Film> {
    final JdbcTemplate jdbcTemplate;
    final MpaMapper mpaMapper;
    final GenreMapper genreMapper;
    final DirectorMapper directorMapper;

    @Autowired
    public FilmMapper(JdbcTemplate jdbcTemplate, MpaMapper mpaMapper, GenreMapper genreMapper,
                      DirectorMapper directorMapper) {
        this.jdbcTemplate = jdbcTemplate;
        this.mpaMapper = mpaMapper;
        this.genreMapper = genreMapper;
        this.directorMapper = directorMapper;
    }

    @Override
    public Film mapRow(ResultSet rs, int rowNum) throws SQLException {
        return Film.builder()
                .id(rs.getInt("film_id"))
                .name(rs.getString("name"))
                .description(rs.getString("description"))
                .releaseDate(rs.getDate("release_date").toLocalDate())
                .duration(rs.getLong("duration"))
                .mpa(findMpa(rs.getInt("rating_id")))
                .genres(findGenres(rs.getInt("film_id")))
                .likesList(new HashSet<>())
                .directors(findDirector(rs.getInt("film_id")))
                .build();
    }

    public Mpa findMpa(int ratingId) {
        final String mpaSql = "SELECT id, name " +
                "FROM rating_mpa " +
                "WHERE id = ?";

        return jdbcTemplate.queryForObject(mpaSql, mpaMapper, ratingId);
    }

    protected List<Genre> findGenres(int filmId) {
        final String genreSql = "SELECT genre.genre_id, genre.name " +
                "FROM genre " +
                "LEFT JOIN film_genre AS fg ON genre.genre_id = fg.genre_id " +
                "WHERE film_id = ?";

        return jdbcTemplate.query(genreSql, genreMapper, filmId);
    }


    public List<Director> findDirector(int id) {
        String sqlQuery =
                "SELECT * FROM directors d " +
                        "JOIN films_directors f ON f.director_id = d.id " +
                        "WHERE f.film_id = ?;";
        return jdbcTemplate.query(sqlQuery, directorMapper, id);
    }
}
