package ru.yandex.practicum.filmorate.storage.director;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.simple.SimpleJdbcInsert;
import org.springframework.jdbc.support.rowset.SqlRowSet;
import org.springframework.stereotype.Component;
import ru.yandex.practicum.filmorate.exceptions.ObjectNotFoundException;
import ru.yandex.practicum.filmorate.model.Director;
import ru.yandex.practicum.filmorate.storage.mapper.DirectorMapper;

import java.util.Collection;

@Slf4j
@Component
@RequiredArgsConstructor
public class DirectorDBStorage implements DirectorStorage {

    private final JdbcTemplate jdbcTemplate;
    private final DirectorMapper directorMapper;

    @Override
    public Collection<Director> getAllDirectors() {
        String sqlQuery = "SELECT * FROM directors;";
        Collection<Director> directors = jdbcTemplate.query(sqlQuery, directorMapper);
        log.info("Отправлен список режиссеров. Количество режиссеров в списке = {}", directors.size());
        return directors;
    }

    @Override
    public Director getDirectorById(Long id) {
        isExist(id);
        String sqlQuery = "SELECT * FROM directors WHERE director_id = ?;";
        log.warn("Директор с идентификатором {} отправлен.", id);
        return jdbcTemplate.queryForObject(sqlQuery, directorMapper, id);
    }

    @Override
    public Director createDirector(Director director) {
        SimpleJdbcInsert jdbcInsert = new SimpleJdbcInsert(jdbcTemplate);
        Number key = jdbcInsert.withTableName("directors")
                .usingGeneratedKeyColumns("director_id")
                .executeAndReturnKey(director.toMap());
        director.setId(key.longValue());
        log.info("Создан отзыв с индентификатором {}", director.getId());
        return director;
    }

    @Override
    public Director updateDirector(Director director) {
        isExist(director.getId());
        String sqlQuery = "UPDATE directors SET name = ? WHERE director_id = ?;";
        jdbcTemplate.update(sqlQuery, director.getName(), director.getId());
        log.warn("Директор с идентификатором {} обновлен.", director.getId());
        return getDirectorById(director.getId());
    }

    @Override
    public void deleteDirector(Long id) {
        isExist(id);
        String sqlQuery = "DELETE FROM directors WHERE director_id = ?;";
        log.warn("Директор с идентификатором {} удален.", id);
        jdbcTemplate.update(sqlQuery, id);
    }

    public void isExist(Long directorId) {
        final String checkUserQuery = "SELECT * FROM directors WHERE director_id = ?";
        SqlRowSet userRows = jdbcTemplate.queryForRowSet(checkUserQuery, directorId);

        if (!userRows.next()) {
            log.warn("Директор с идентификатором {} не найден.", directorId);
            throw new ObjectNotFoundException("Директор с идентификатором " + directorId + " не найден.");
        }
    }
}
