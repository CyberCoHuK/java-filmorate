package ru.yandex.practicum.filmorate.storage;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.test.context.jdbc.Sql;
import org.springframework.test.context.jdbc.SqlGroup;
import ru.yandex.practicum.filmorate.enums.EventTypes;
import ru.yandex.practicum.filmorate.enums.Operations;
import ru.yandex.practicum.filmorate.storage.feed.FeedStorage;

import static org.junit.jupiter.api.Assertions.assertEquals;

@SpringBootTest
@AutoConfigureTestDatabase
@DirtiesContext(classMode = DirtiesContext.ClassMode.BEFORE_EACH_TEST_METHOD)
@SqlGroup({
        @Sql(value = "/test/feed-test.sql", executionPhase = Sql.ExecutionPhase.BEFORE_TEST_METHOD),
        @Sql(value = "/test/clear.sql", executionPhase = Sql.ExecutionPhase.AFTER_TEST_METHOD),
})
public class FeedDbStorageTest {
    @Autowired
    private FeedStorage feedStorage;

    @Test
    public void getFeedByUserId() {
        assertEquals(3, feedStorage.getFeedById(1L).size());
        assertEquals(1, feedStorage.getFeedById(2L).size());
    }

    @Test
    public void addEvent() {
        feedStorage.addEvent(2L, EventTypes.FRIEND, Operations.ADD, 1L);
        feedStorage.getFeedById(2L);
        assertEquals(2, feedStorage.getFeedById(2L).size());
    }

}
