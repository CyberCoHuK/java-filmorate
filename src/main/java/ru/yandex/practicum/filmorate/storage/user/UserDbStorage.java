package ru.yandex.practicum.filmorate.storage.user;

import lombok.extern.slf4j.Slf4j;
import org.springframework.context.annotation.Primary;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.support.GeneratedKeyHolder;
import org.springframework.jdbc.support.KeyHolder;
import org.springframework.jdbc.support.rowset.SqlRowSet;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;
import ru.yandex.practicum.filmorate.exceptions.ObjectNotFoundException;
import ru.yandex.practicum.filmorate.model.User;

import java.sql.Date;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Collection;
import java.util.HashSet;

@Slf4j
@Component
@Primary
public class UserDbStorage implements UserStorage {

    private final JdbcTemplate jdbcTemplate;

    public UserDbStorage(JdbcTemplate jdbcTemplate) {
        this.jdbcTemplate = jdbcTemplate;
    }

    @Override
    public Collection<User> findAllUsers() {
        final String sql = "SELECT * FROM users";
        log.info("Отправлены все пользователи");
        return jdbcTemplate.query(sql, this::makeUser);
    }

    @Override
    public User createUser(User user) {
        final String sql = "INSERT INTO users (email, login, name, birthday) " +
                "VALUES (?, ?, ?, ?)";

        KeyHolder keyHolder = new GeneratedKeyHolder();
        if (StringUtils.isEmpty(user.getName())) {
            user.setName(user.getLogin());
        }
        jdbcTemplate.update(connection -> {
            PreparedStatement stmt = connection.prepareStatement(sql, new String[]{"user_id"});
            stmt.setString(1, user.getEmail());
            stmt.setString(2, user.getLogin());
            stmt.setString(3, user.getName());
            stmt.setDate(4, Date.valueOf(user.getBirthday()));
            return stmt;
        }, keyHolder);

        if (keyHolder.getKey() != null) {
            user.setId((Integer) keyHolder.getKey());
        }
        log.info("Создан пользователь с индентификатором {} ", user.getId());
        return user;
    }

    @Override
    public User updateUser(User user) {
        isExist(user.getId());
        final String sqlQuery = "UPDATE users SET email = ?, login = ?, name = ?, birthday = ? " +
                "WHERE user_id = ?";
        jdbcTemplate.update(sqlQuery, user.getEmail(), user.getLogin(), user.getName(), user.getBirthday(),
                user.getId());
        log.info("Обновлен пользователь с индентификатором {} ", user.getId());
        return user;
    }

    @Override
    public User getById(int userId) {
        isExist(userId);
        final String sql = "SELECT * FROM users WHERE user_id = ?";
        log.info("Отправлен пользователь с индентификатором {} ", userId);
        return jdbcTemplate.queryForObject(sql, this::makeUser, userId);
    }

    @Override
    public User addFriend(int userId, int friendId) {
        isExist(userId);
        isExist(friendId);
        final String sqlQuery = "INSERT INTO friends (user_id, friend_id, status) VALUES (?, ?, ?)";
        final String checkQuery = "SELECT * FROM friends WHERE user_id = ? AND friend_id = ?";

        SqlRowSet friendRows = jdbcTemplate.queryForRowSet(checkQuery, userId, friendId);
        SqlRowSet userRows = jdbcTemplate.queryForRowSet(checkQuery, friendId, userId);
        if (!friendRows.next()) {
            if (!userRows.next()) {
                jdbcTemplate.update(sqlQuery, userId, friendId, false);
                log.info("Пользователь {} отправил запрос на добавления в друзья {}", userId, friendId);
            } else {
                jdbcTemplate.update(sqlQuery, userId, friendId, true);
                jdbcTemplate.update(sqlQuery, friendId, userId, true);
                log.info("Пользователь {} добавил в друзья {}", userId, friendId);
            }
        }
        return getById(userId);
    }

    @Override
    public void deleteFriend(int userId, int friendId) {
        final String sqlQuery = "DELETE FROM friends WHERE user_id = ? AND friend_id = ?";

        jdbcTemplate.update(sqlQuery, userId, friendId);
        log.info("Пользователь {} удалил из друзей {}", userId, friendId);
    }

    @Override
    public Collection<User> getFriends(int userId) {
        isExist(userId);
        final String sqlQuery = "SELECT u.* FROM users AS u " +
                "LEFT JOIN friends AS f ON u.user_id = f.friend_id " +
                "WHERE f.user_id = ?";
        log.info("Запрос получения списка друзей пользователя {} выполнен", userId);
        return jdbcTemplate.query(sqlQuery, this::makeUser, userId);
    }

    @Override
    public Collection<User> getMutualFriends(int userId, int secondUserId) {
        isExist(userId);
        isExist(secondUserId);
        final String sqlQuery = "SELECT u.* FROM friends AS f " +
                "LEFT JOIN users u ON u.user_id = f.friend_id " +
                "WHERE f.user_id = ? " +
                "AND f.friend_id IN " +
                "( " +
                "SELECT f.friend_id " +
                "FROM friends AS f " +
                "LEFT JOIN users AS u ON u.user_id = f.friend_id " +
                "WHERE f.user_id = ?" +
                ")";
        log.info("Отправлен одинаковые друзья пользователей {} и {} ", userId, secondUserId);
        return jdbcTemplate.query(sqlQuery, this::makeUser, userId, secondUserId);
    }

    private User makeUser(ResultSet rs, int rowNum) throws SQLException {
        return User.builder()
                .id(rs.getInt("user_id"))
                .name(rs.getString("name"))
                .email(rs.getString("email"))
                .login(rs.getString("login"))
                .birthday(rs.getDate("birthday").toLocalDate())
                .friendsList(new HashSet<>())
                .build();
    }

    public void isExist(int userId) {
        final String checkUserQuery = "SELECT * FROM users WHERE user_id = ?";
        SqlRowSet userRows = jdbcTemplate.queryForRowSet(checkUserQuery, userId);

        if (!userRows.next()) {
            log.warn("Пользователь с идентификатором {} не найден.", userId);
            throw new ObjectNotFoundException("Пользователь с идентификатором " + userId + " не найден.");
        }
    }
}
