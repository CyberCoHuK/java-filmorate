package ru.yandex.practicum.filmorate.storage.director;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.jdbc.core.BatchPreparedStatementSetter;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.support.GeneratedKeyHolder;
import org.springframework.jdbc.support.KeyHolder;
import org.springframework.jdbc.support.rowset.SqlRowSet;
import org.springframework.stereotype.Component;
import ru.yandex.practicum.filmorate.exceptions.ObjectNotFoundException;
import ru.yandex.practicum.filmorate.model.Director;
import ru.yandex.practicum.filmorate.storage.mapper.DirectorMapper;

import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

@Slf4j
@Component
@RequiredArgsConstructor
public class DirectorDBStorage implements DirectorStorage {

    private final JdbcTemplate jdbcTemplate;
    private final DirectorMapper directorMapper;

    @Override
    public List<Director> getAllDirectors() {
        String sqlQuery = "SELECT * FROM directors;";
        log.info("Получить список директоров");
        return jdbcTemplate.query(sqlQuery, directorMapper);
    }

    @Override
    public Director directorExistById(int id) {
        isExist(id);
        String sqlQuery = "SELECT id, name FROM directors WHERE id = ?;";
        return jdbcTemplate.queryForObject(sqlQuery, directorMapper, id);
    }

    @Override
    public Director createDirector(Director director) {
        String sqlQuery = "INSERT INTO directors (name) VALUES (?);";
        KeyHolder keyHolder = new GeneratedKeyHolder();
        jdbcTemplate.update(connection -> {
            PreparedStatement statement = connection.prepareStatement(sqlQuery, new String[]{"id"});
            statement.setString(1, director.getName());
            return statement;
        }, keyHolder);
        return getDirectorById(Objects.requireNonNull(keyHolder.getKey()).intValue());
    }

    @Override
    public Director updateDirector(Director director) {
        isExist(director.getId());
        String sqlQuery = "UPDATE directors SET name = ? WHERE id = ?;";
        jdbcTemplate.update(sqlQuery, director.getName(), director.getId());
        return getDirectorById(director.getId());
    }

    @Override
    public void deleteDirector(int id) {
        isExist(id);
        String sqlQuery = "DELETE FROM directors WHERE id = ?;";
        jdbcTemplate.update(sqlQuery, id);
    }

    @Override
    public void saveFilmDirector(int filmId, List<Director> directors) {
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
    public void deleteFilmDirector(int id) {
        isExist(id);
        String sqlQuery = "DELETE FROM films_directors WHERE film_id = ?;";
        jdbcTemplate.update(sqlQuery, id);
    }

    public void isExist(int directorId) {
        final String checkUserQuery = "SELECT * FROM directors WHERE id = ?";
        SqlRowSet userRows = jdbcTemplate.queryForRowSet(checkUserQuery, directorId);

        if (!userRows.next()) {
            log.warn("Директор с идентификатором {} не найден.", directorId);
            throw new ObjectNotFoundException("Директор с идентификатором " + directorId + " не найден.");
        }
    }
}
