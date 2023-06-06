package ru.yandex.practicum.filmorate.storage.director;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.BatchPreparedStatementSetter;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.support.GeneratedKeyHolder;
import org.springframework.jdbc.support.KeyHolder;
import org.springframework.stereotype.Component;
import ru.yandex.practicum.filmorate.model.Director;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Collections;
import java.util.List;
import java.util.Objects;
import java.util.Optional;
import java.util.stream.Collectors;

@Slf4j
@Component
public class DirectorDBStorage implements DirectorStorage {

    private final JdbcTemplate jdbcTemplate;

    @Autowired
    public DirectorDBStorage(JdbcTemplate jdbcTemplate){
        this.jdbcTemplate = jdbcTemplate;
    }

    @Override
    public List<Director> getAllDirectors() {
        String sqlQuery = "SELECT * FROM directors;";
        log.info("Получить список директоров");
        return jdbcTemplate.query(sqlQuery, this::makeDirector);
    }

    @Override
    public Optional<Director> getDirectorById(long id) {
        String sqlQuery = "SELECT id, name FROM directors WHERE id = ?;";
        return Optional.ofNullable(jdbcTemplate.queryForObject(sqlQuery, this::makeDirector, id));
    }

    @Override
    public long createDirector(Director director) {
        String sqlQuery = "INSERT INTO directors (name) VALUES (?);";
        KeyHolder keyHolder = new GeneratedKeyHolder();
        jdbcTemplate.update(connection -> {
            PreparedStatement statement = connection.prepareStatement(sqlQuery, new String[]{"id"});
            statement.setString(1, director.getName());
            return statement;
        }, keyHolder);
        return Objects.requireNonNull(keyHolder.getKey()).longValue();
    }

    @Override
    public void updateDirector(Director director) {
        String sqlQuery = "UPDATE directors SET name = ? WHERE id = ?;";
        jdbcTemplate.update(sqlQuery, director.getName(), director.getId());
    }

    @Override
    public void deleteDirector(long id) {
        String sqlQuery = "DELETE FROM directors WHERE id = ?;";
        jdbcTemplate.update(sqlQuery, id);
    }

    @Override
    public List<Director> loadDirectorsByFilmId(long id) {
        String sqlQuery =
                "SELECT * FROM directors d " +
                        "JOIN films_directors f ON f.director_id = d.id " +
                        "WHERE f.film_id = ?;";
        return Collections.singletonList(jdbcTemplate.queryForObject(sqlQuery, this::makeDirector, id));
    }

    @Override
    public void saveFilmDirector(long filmId, List<Director> directors) {
        List<Director> directorsDistinct = directors.stream().distinct().collect(Collectors.toList());
        jdbcTemplate.batchUpdate(
                "INSERT INTO films_directors (film_id, director_id) VALUES (?, ?);",
                new BatchPreparedStatementSetter() {
                    public void setValues(PreparedStatement statement, int i) throws SQLException {
                        statement.setLong(1, filmId);
                        statement.setLong(2, directorsDistinct.get(i).getId());
                    }
                    public int getBatchSize() {
                        return directorsDistinct.size();
                    }
                });
    }

    @Override
    public void deleteFilmDirector(long id) {
        String sqlQuery = "DELETE FROM films_directors WHERE film_id = ?;";
        jdbcTemplate.update(sqlQuery, id);
    }

    private Director makeDirector(ResultSet rs, int rowNum) throws SQLException {
        log.info("Директор создан");
        return new Director(rs.getInt("id"),
                rs.getString("name"));
    }
}
