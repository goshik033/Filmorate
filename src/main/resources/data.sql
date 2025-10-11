-- Чистая загрузка + сброс счётчиков
SET REFERENTIAL_INTEGRITY FALSE;

TRUNCATE TABLE friends;
TRUNCATE TABLE user_likes_film;
TRUNCATE TABLE film_genre;
TRUNCATE TABLE films;
TRUNCATE TABLE users;
TRUNCATE TABLE genres;
TRUNCATE TABLE mpa_rating;


ALTER TABLE mpa_rating      ALTER COLUMN id RESTART WITH 1;
ALTER TABLE genres          ALTER COLUMN id RESTART WITH 1;
ALTER TABLE users           ALTER COLUMN id RESTART WITH 1;
ALTER TABLE films           ALTER COLUMN id RESTART WITH 1;
ALTER TABLE film_genre      ALTER COLUMN id RESTART WITH 1;
ALTER TABLE user_likes_film ALTER COLUMN id RESTART WITH 1;
ALTER TABLE friends         ALTER COLUMN id RESTART WITH 1;

SET REFERENTIAL_INTEGRITY TRUE;


INSERT INTO mpa_rating (id, name)
VALUES (1, 'G'),
       (2, 'PG'),
       (3, 'PG-13'),
       (4, 'R'),
       (5, 'NC-17');


INSERT INTO genres (id, name)
VALUES (1, 'Action'),
       (2, 'Comedy'),
       (3, 'Drama'),
       (4, 'Sci-Fi'),
       (5, 'Horror'),
       (6, 'Romance'),
       (7, 'Thriller'),
       (8, 'Animation'),
       (9, 'Documentary'),
       (10, 'Family');


INSERT INTO users (id, email, login, name, birthday)
VALUES (1, 'alice@example.com', 'alice', 'Alice Johnson', DATE '1995-04-12'),
       (2, 'bob@example.com', 'bob', 'Bob Smith', DATE '1990-11-03'),
       (3, 'carol@example.com', 'carol', 'Carol Davis', DATE '1988-02-20'),
       (4, 'dan@example.com', 'dan', 'Dan Miller', DATE '2001-07-15'),
       (5, 'erin@example.com', 'erin', 'Erin Wilson', DATE '1997-12-01'),
       (6, 'frank@example.com', 'frank', 'Frank Thompson', DATE '1993-08-25');


INSERT INTO films (id, name, description, release_date, duration_minutes, mpa_rating_id)
VALUES (1, 'Stellar Voyage', 'Indie sci-fi about a deep-space survey.', DATE '2019-09-20', 118, 3),
       (2, 'Love in the City', 'Romantic story of two strangers in a metro.', DATE '2021-02-14', 102, 2),
       (3, 'Midnight Chase', 'Crime thriller through neon streets.', DATE '2020-06-05', 109, 4),
       (4, 'Laugh Factory', 'Sketch-comedy feature with improv stars.', DATE '2018-04-01', 95, 2),
       (5, 'The Last Fortress', 'Epic action about a besieged stronghold.', DATE '2022-11-11', 126, 4),
       (6, 'Waves of Silence', 'Slow-burn drama set in a coastal town.', DATE '2017-03-10', 112, 3),
       (7, 'Pixel Pals', 'Animated adventure for all ages.', DATE '2023-05-27', 88, 1),
       (8, 'Into the Abyss', 'Psychological horror in an abandoned hospital.', DATE '2020-10-30', 101, 4);


INSERT INTO film_genre (id, film_id, genre_id)
VALUES (1, 1, 4),
       (2, 1, 7),
       (3, 2, 6),
       (4, 2, 2),
       (5, 3, 7),
       (6, 3, 1),
       (7, 4, 2),
       (8, 5, 1),
       (9, 5, 7),
       (10, 6, 3),
       (11, 7, 8),
       (12, 7, 10),
       (13, 8, 5),
       (14, 8, 7);


INSERT INTO user_likes_film (id, film_id, user_id)
VALUES (1, 1, 1),
       (2, 1, 2),
       (3, 2, 1),
       (4, 2, 3),
       (5, 3, 2),
       (6, 3, 4),
       (7, 5, 2),
       (8, 5, 5),
       (9, 7, 1),
       (10, 7, 6),
       (11, 6, 3),
       (12, 8, 4);


INSERT INTO friends (id, user_id, friend_id)
VALUES (1, 1, 2),
       (2, 2, 1),
       (3, 3, 4),
       (4, 4, 3),
       (5, 1, 3),
       (6, 5, 1),
       (7, 6, 2);


ALTER TABLE mpa_rating      ALTER COLUMN id RESTART WITH 6;
ALTER TABLE genres          ALTER COLUMN id RESTART WITH 11;
ALTER TABLE users           ALTER COLUMN id RESTART WITH 7;
ALTER TABLE films           ALTER COLUMN id RESTART WITH 9;
ALTER TABLE film_genre      ALTER COLUMN id RESTART WITH 15;
ALTER TABLE user_likes_film ALTER COLUMN id RESTART WITH 13;
ALTER TABLE friends         ALTER COLUMN id RESTART WITH 8;
