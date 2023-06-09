INSERT INTO USERS (user_id, email, login, name, birthday)
VALUES (1, 'email@email.ru', 'SuperJoe', 'Joe', '2000-02-01');

INSERT INTO USERS (user_id, email, login, name, birthday)
VALUES (2, 'email@email.ru', 'SuperBob', 'Bob', '2000-02-01');

INSERT INTO FILM (film_id, name, description, release_date, duration, rating_id)
VALUES (1, 'name', 'desc', '1999-08-17', 136, 1);

INSERT INTO FILM (film_id, name, description, release_date, duration, rating_id)
VALUES (2, 'SecondName', 'SecondDesc', '1997-03-13', 46, 1);

INSERT INTO FILM (film_id, name, description, release_date, duration, rating_id)
VALUES (3, 'ThirdName', 'ThirdDesc', '1977-02-23', 13, 1);

INSERT INTO LIKES (film_id, user_id)
VALUES (1, 1);

INSERT INTO LIKES (film_id, user_id)
VALUES (1, 2);

INSERT INTO LIKES (film_id, user_id)
VALUES (2, 1);

INSERT INTO LIKES (film_id, user_id)
VALUES (3, 2);