package ru.yandex.practicum.filmorate.storage.genre;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.support.rowset.SqlRowSet;
import org.springframework.stereotype.Repository;
import ru.yandex.practicum.filmorate.exceptions.ObjectNotFoundException;
import ru.yandex.practicum.filmorate.model.Genre;
import ru.yandex.practicum.filmorate.storage.mapper.GenreMapper;

import java.util.Collection;

@Slf4j
@Repository
@RequiredArgsConstructor
public class GenreDbStorage implements GenreStorage {

    private final JdbcTemplate jdbcTemplate;
    private final GenreMapper genreMapper;

    @Override
    public Collection<Genre> findAll() {
        String sqlQuery = "SELECT * FROM genre";
        Collection<Genre> genres = jdbcTemplate.query(sqlQuery, genreMapper);
        log.info("Отправлен список жанров. Количество жанров в списке = {}", genres.size());
        return genres;
    }

    @Override
    public Genre getById(int id) {
        final String sqlQuery = "SELECT * FROM genre WHERE genre_id = ?";
        SqlRowSet genreRows = jdbcTemplate.queryForRowSet(sqlQuery, id);

        if (!genreRows.next()) {
            log.warn("Жанр {} не найден.", id);
            throw new ObjectNotFoundException("Жанр не найден");

        }
        return jdbcTemplate.queryForObject(sqlQuery, genreMapper, id);
    }
}