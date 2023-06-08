package ru.yandex.practicum.filmorate.storage;

import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.test.annotation.DirtiesContext;
import ru.yandex.practicum.filmorate.model.User;
import ru.yandex.practicum.filmorate.storage.user.UserDbStorage;

import java.time.LocalDate;
import java.util.HashSet;
import java.util.List;

import static org.assertj.core.api.AssertionsForClassTypes.assertThat;
import static org.junit.jupiter.api.Assertions.assertEquals;

@SpringBootTest
@AutoConfigureTestDatabase
@RequiredArgsConstructor(onConstructor_ = @Autowired)
@DirtiesContext(classMode = DirtiesContext.ClassMode.BEFORE_EACH_TEST_METHOD)
@FieldDefaults(level = AccessLevel.PRIVATE)
class UserDbStorageTest {
    final UserDbStorage userDbStorage;
    final JdbcTemplate jdbcTemplate;
    User user;
    User friend;
    User mutualFriend;

    @BeforeEach
    public void setUp() {
        jdbcTemplate.update("DELETE FROM users");
        jdbcTemplate.update("DELETE FROM friends");
        user = createUser(1);
        friend = createUser(2);
        mutualFriend = createUser(0);
    }


    @Test
    void shouldCreateAndUpdateAndGetUser() {
        userDbStorage.createUser(user);
        assertEquals(user, userDbStorage.getById(user.getId()));
        assertEquals(user.getLogin(), userDbStorage.getById(user.getId()).getName());

        user.setEmail("mail@mail.mail");
        userDbStorage.updateUser(user);
        assertEquals(user, userDbStorage.getById(user.getId()));

        assertEquals(1, userDbStorage.findAllUsers().size());
        assertEquals(user, userDbStorage.getById(user.getId()));
    }


    @Test
    void shouldAddAndDeleteFriends() {
        userDbStorage.createUser(user);
        userDbStorage.createUser(friend);
        userDbStorage.addFriend(user.getId(), friend.getId());
        assertEquals(1, userDbStorage.getFriends(user.getId()).size());
        assertEquals(0, userDbStorage.getFriends(friend.getId()).size());

        userDbStorage.deleteFriend(user.getId(), friend.getId());
        assertEquals(0, userDbStorage.getFriends(user.getId()).size());
        assertEquals(0, userDbStorage.getFriends(friend.getId()).size());
    }


    @Test
    void shouldGetMutualFriends() {
        userDbStorage.createUser(user);
        userDbStorage.createUser(friend);
        userDbStorage.createUser(mutualFriend);
        userDbStorage.addFriend(user.getId(), mutualFriend.getId());
        userDbStorage.addFriend(friend.getId(), mutualFriend.getId());
        assertEquals(List.of(mutualFriend), userDbStorage.getMutualFriends(user.getId(), friend.getId()));
    }

    @Test
    void deleteUserByIdInStorageCheck() {
        userDbStorage.createUser(user);
        userDbStorage.deleteUserById(user.getId());
        assertThat(userDbStorage.findAllUsers().isEmpty());
    }

    protected static User createUser(int num) {
        if (num == 1) {
            return User.builder()
                    .email("mail@mail.mail")
                    .login("login")
                    .name("login")
                    .birthday(LocalDate.of(1999, 8, 17))
                    .friendsList(new HashSet<>())
                    .build();
        } else if (num == 2) {
            return User.builder()
                    .email("nemail@mail.mail")
                    .login("nelogin")
                    .name("nelogin")
                    .birthday(LocalDate.of(1999, 8, 17))
                    .friendsList(new HashSet<>())
                    .build();
        } else {
            return User.builder()
                    .email("mutual@mail.mail")
                    .login("mutual")
                    .name("mutual")
                    .birthday(LocalDate.of(1999, 8, 17))
                    .friendsList(new HashSet<>())
                    .build();
        }
    }
}