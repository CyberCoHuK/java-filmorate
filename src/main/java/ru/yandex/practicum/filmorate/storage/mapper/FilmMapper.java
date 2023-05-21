package ru.yandex.practicum.filmorate.storage.mapper;

import lombok.AccessLevel;
import lombok.experimental.FieldDefaults;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.stereotype.Component;
import ru.yandex.practicum.filmorate.model.Film;
import ru.yandex.practicum.filmorate.model.Genre;
import ru.yandex.practicum.filmorate.model.Mpa;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.HashSet;
import java.util.List;

@Component("filmMapper")
@FieldDefaults(level = AccessLevel.PRIVATE)
public class FilmMapper implements RowMapper<Film> {
    final JdbcTemplate jdbcTemplate;
    final MpaMapper mpaMapper = new MpaMapper();
    final GenreMapper genreMapper = new GenreMapper();

    public FilmMapper(JdbcTemplate jdbcTemplate) {
        this.jdbcTemplate = jdbcTemplate;
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
}
