package ru.yandex.practicum.filmorate.storage.user;

import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Primary;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.simple.SimpleJdbcInsert;
import org.springframework.jdbc.support.rowset.SqlRowSet;
import org.springframework.stereotype.Component;
import ru.yandex.practicum.filmorate.exceptions.ObjectNotFoundException;
import ru.yandex.practicum.filmorate.model.User;
import ru.yandex.practicum.filmorate.storage.mapper.UserMapper;

import java.util.Collection;
import java.util.HashMap;
import java.util.Map;

@Slf4j
@Component
@Primary
public class UserDbStorage implements UserStorage {

    private final JdbcTemplate jdbcTemplate;
    private final UserMapper userMapper;

    @Autowired
    public UserDbStorage(JdbcTemplate jdbcTemplate, UserMapper userMapper) {
        this.jdbcTemplate = jdbcTemplate;
        this.userMapper = userMapper;
    }

    @Override
    public Collection<User> findAllUsers() {
        final String sql = "SELECT * FROM users";
        Collection<User> users = jdbcTemplate.query(sql, userMapper);
        log.info("Отправлен список пользователей. Количество пользователей в списке = {}", users.size());
        return users;
    }

    @Override
    public User createUser(User user) {
        if (StringUtils.isEmpty(user.getName())) {
            user.setName(user.getLogin());
        }
        SimpleJdbcInsert jdbcInsert = new SimpleJdbcInsert(jdbcTemplate);
        Number key = jdbcInsert.withTableName("users")
                .usingGeneratedKeyColumns("user_id")
                .executeAndReturnKey(getUserFields(user));
        user.setId(key.longValue());
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
    public User getById(Long userId) {
        isExist(userId);
        final String sql = "SELECT * FROM users WHERE user_id = ?";
        log.info("Отправлен пользователь с индентификатором {} ", userId);
        return jdbcTemplate.queryForObject(sql, userMapper, userId);
    }

    @Override
    public User addFriend(Long userId, Long friendId) {
        final String sqlQuery = "INSERT INTO friends (user_id, friend_id, status) VALUES (?, ?, ?)";
        final String checkQuery = "SELECT * FROM friends WHERE user_id = ? AND friend_id = ?";

        SqlRowSet userRows = jdbcTemplate.queryForRowSet(checkQuery, friendId, userId);

        if (!userRows.next()) {
            jdbcTemplate.update(sqlQuery, userId, friendId, false);
            log.info("Пользователь {} отправил запрос на добавления в друзья {}", userId, friendId);
        } else {
            jdbcTemplate.update(sqlQuery, userId, friendId, true);
            jdbcTemplate.update(sqlQuery, friendId, userId, true);
            log.info("Пользователь {} добавил в друзья {}", userId, friendId);
        }
        return getById(userId);
    }

    @Override
    public void deleteFriend(Long userId, Long friendId) {
        final String sqlQuery = "DELETE FROM friends WHERE user_id = ? AND friend_id = ?";

        jdbcTemplate.update(sqlQuery, userId, friendId);
        log.info("Пользователь {} удалил из друзей {}", userId, friendId);
    }

    @Override
    public Collection<User> getFriends(Long userId) {
        isExist(userId);
        final String sqlQuery = "SELECT u.* FROM users AS u " +
                "LEFT JOIN friends AS f ON u.user_id = f.friend_id " +
                "WHERE f.user_id = ?";
        log.info("Запрос получения списка друзей пользователя {} выполнен", userId);
        return jdbcTemplate.query(sqlQuery, userMapper, userId);
    }

    @Override
    public Collection<User> getMutualFriends(Long userId, Long secondUserId) {
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
        return jdbcTemplate.query(sqlQuery, userMapper, userId, secondUserId);
    }

    public String deleteUserById(Long userId) {
        String sqlQuery = "DELETE FROM users WHERE user_id = ? ";
        isExist(userId);
        jdbcTemplate.update(sqlQuery, userId);
        return "Пользователь user_id=" + userId + " успешно удален.";
    }

    public void isExist(Long userId) {
        final String checkUserQuery = "SELECT * FROM users WHERE user_id = ?";
        SqlRowSet userRows = jdbcTemplate.queryForRowSet(checkUserQuery, userId);

        if (!userRows.next()) {
            log.warn("Пользователь с идентификатором {} не найден.", userId);
            throw new ObjectNotFoundException("Пользователь с идентификатором " + userId + " не найден.");
        }
    }

    private Map<String, Object> getUserFields(User user) {
        Map<String, Object> fields = new HashMap<>();
        fields.put("NAME", user.getName());
        fields.put("EMAIL", user.getEmail());
        fields.put("LOGIN", user.getLogin());
        fields.put("BIRTHDAY", user.getBirthday());
        return fields;
    }
}
