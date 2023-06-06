package ru.yandex.practicum.filmorate.storage.mpa;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.support.rowset.SqlRowSet;
import org.springframework.stereotype.Repository;
import ru.yandex.practicum.filmorate.exceptions.ObjectNotFoundException;
import ru.yandex.practicum.filmorate.model.Mpa;
import ru.yandex.practicum.filmorate.storage.mapper.MpaMapper;

import java.util.Collection;

@Slf4j
@Repository
@RequiredArgsConstructor
public class MpaDbStorage implements MpaStorage {

    private final JdbcTemplate jdbcTemplate;
    private final MpaMapper mpaMapper;

    @Override
    public Collection<Mpa> findAll() {
        String sqlQuery = "SELECT * FROM rating_mpa";
        log.info("Отправлены все рейтинги");
        return jdbcTemplate.query(sqlQuery, mpaMapper);
    }

    @Override
    public Mpa getById(int id) {
        String sqlQuery = "SELECT * FROM rating_mpa WHERE id = ?";
        SqlRowSet mpaRows = jdbcTemplate.queryForRowSet(sqlQuery, id);

        if (!mpaRows.next()) {
            log.warn("Рейтинг {} не найден.", id);
            throw new ObjectNotFoundException("Рейтинг не найден");
        }

        return jdbcTemplate.queryForObject(sqlQuery, mpaMapper, id);
    }
}