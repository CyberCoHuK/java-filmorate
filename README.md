# java-filmorate
Template repository for Filmorate project.
![db_filmorate](https://github.com/CyberCoHuK/java-filmorate/assets/108213849/79b17a6d-d406-451b-8e5c-26903346927b)

- Получение количества лайков у фильма

```roomsql
    SELECT COUNT(user_id)
    FROM likes
    WHERE film_id = {id}
```
- Получение списка понравившихся фильмов

```roomsql
    SELECT f.title
    FROM likes AS l
    INNER JOIN film AS f ON (l.film_id = f.film_id)
    WHERE user_id = {id}
```

- Получение списка отправленных запросов на добавление в друзья

```roomsql
    SELECT friend_id,
    status
    FROM friends
    WHERE user_id = {id}
```

- Получение списка подтвержденных отправленных запросов на добавление в друзья

```roomsql
    SELECT friend_id
    FROM friends
    WHERE user_id = {id}
    AND status = 'true'
```

- Получение списка фильмов с жанром {genre}

```roomsql
    SELECT f.title
    FROM genre AS g
    INNER JOIN film_genre AS fg ON (g.genre_id = fg.genre_id)
    INNER JOIN film AS f ON (fg.film_id = f.film_id)
    WHERE name = '{genre}'
```
