INSERT INTO USERS (user_id, email, login, name, birthday)
VALUES (1, 'email@email.ru', 'SuperJoe', 'Joe', '2000-02-01');

INSERT INTO USERS (user_id, email, login, name, birthday)
VALUES (2, 'email@email.ru', 'SuperBob', 'Bob', '2000-02-01');

INSERT INTO USERS (user_id, email, login, name, birthday)
VALUES (3, 'email@email.ru', 'SuperDen', 'Den', '2000-02-01');

INSERT INTO FILM (film_id, name, description, release_date, duration, rating_id)
VALUES (1, 'First film', 'First film', '1998-03-30', 120, 1);

INSERT INTO FILM (film_id, name, description, release_date, duration, rating_id)
VALUES (2, 'Second film', 'Second film', '1999-09-10', 180, 2);

INSERT INTO FILM (film_id, name, description, release_date, duration, rating_id)
VALUES (3, 'Last film', 'Last film', '2009-09-09', 154, 1);

INSERT INTO REVIEWS (content, is_positive, user_id, film_id, useful)
VALUES ('Good film', true, 1, 1, 1);

INSERT INTO REVIEWS (content, is_positive, user_id, film_id, useful)
VALUES ('Bad film', false, 2, 2, 0);

INSERT INTO REVIEWS_LIKES (review_id, user_id, is_like)
VALUES (1, 2, true);