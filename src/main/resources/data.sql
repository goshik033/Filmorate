
SET REFERENTIAL_INTEGRITY FALSE;

TRUNCATE TABLE friends;
TRUNCATE TABLE user_likes_film;
TRUNCATE TABLE film_genre;
TRUNCATE TABLE films;
TRUNCATE TABLE users;
TRUNCATE TABLE genres;
TRUNCATE TABLE mpa_rating;
TRUNCATE TABLE review_likes;
TRUNCATE TABLE reviews;
TRUNCATE TABLE film_director;
TRUNCATE TABLE directors;

ALTER TABLE mpa_rating
    ALTER COLUMN id RESTART WITH 1;
ALTER TABLE genres
    ALTER COLUMN id RESTART WITH 1;
ALTER TABLE users
    ALTER COLUMN id RESTART WITH 1;
ALTER TABLE films
    ALTER COLUMN id RESTART WITH 1;
ALTER TABLE film_genre
    ALTER COLUMN id RESTART WITH 1;
ALTER TABLE user_likes_film
    ALTER COLUMN id RESTART WITH 1;
ALTER TABLE friends
    ALTER COLUMN id RESTART WITH 1;
ALTER TABLE reviews
    ALTER COLUMN id RESTART WITH 1;
ALTER TABLE directors
    ALTER COLUMN id RESTART WITH 1;

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

-- ---------- Фильмы
INSERT INTO films (id, name, description, release_date, duration_minutes, mpa_rating_id)
VALUES (1, 'The Dark Knight', 'Batman vs. Joker в авторском триллере Нолана.', DATE '2008-07-18', 152, 3),
       (2, 'Titanic', 'Эпическая мелодрама на фоне трагедии лайнера.', DATE '1997-12-19', 195, 3),
       (3, 'The Godfather', 'Классика мафиозной саги о семье Корлеоне.', DATE '1972-03-24', 175, 4),
       (4, 'Pulp Fiction', 'Нелинейный криминальный фильм с культовыми диалогами.', DATE '1994-10-14', 154, 4),
       (5, 'Inception', 'Команда проникает в сны, чтобы внедрить идею.', DATE '2010-07-16', 148, 3),
       (6, 'Avatar', 'Колонизаторы против народа На’ви на Пандоре.', DATE '2009-12-18', 162, 3),
       (7, 'The Lord of the Rings: The Return of the King',
        'Финал эпопеи о войне за Кольцо.', DATE '2003-12-17', 201, 3),
       (8, 'The Matrix', 'Хакер нео узнаёт правду о реальности и Матрице.', DATE '1999-03-31', 136, 4);

-- ---------- Жанры фильмов ----------
INSERT INTO film_genre (id, film_id, genre_id)
VALUES (1, 1, 1),
       (2, 1, 7),
       (3, 2, 6),
       (4, 2, 3),
       (5, 3, 3),
       (6, 3, 7),
       (7, 4, 7),
       (8, 4, 3),
       (9, 5, 4),
       (10, 5, 7),
       (11, 6, 4),
       (12, 6, 1),
       (13, 7, 1),
       (14, 7, 3),
       (15, 8, 4),
       (16, 8, 1);


-- ---------- Режиссёры ----------
INSERT INTO directors (id, name)
VALUES (1, 'Christopher Nolan'),
       (2, 'James Cameron'),
       (3, 'Francis Ford Coppola'),
       (4, 'Quentin Tarantino'),
       (5, 'Peter Jackson'),
       (6, 'Lana Wachowski'),
       (7, 'Lilly Wachowski');

-- ---------- Связь фильм—режиссёр
INSERT INTO film_director (film_id, director_id)
VALUES (1, 1),
       (2, 2),
       (3, 3),
       (4, 4),
       (5, 1),
       (6, 2),
       (7, 5),
       (8, 6),
       (8, 7);

-- ---------- Лайки фильмов пользователями ----------
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

-- ---------- Друзья ----------
INSERT INTO friends (id, user_id, friend_id)
VALUES (1, 1, 2),
       (2, 2, 1),
       (3, 3, 4),
       (4, 4, 3),
       (5, 1, 3),
       (6, 5, 1),
       (7, 6, 2);

-- ---------- Отзывы ----------
INSERT INTO reviews (id, film_id, user_id, content, is_positive, useful)
VALUES (1, 1, 1, 'Очень понравилось — динамично и без воды', TRUE, 1),
       (2, 1, 2, 'Слишком затянуто местами', FALSE, -1),
       (3, 2, 3, 'Отличная работа актёров, рекомендую', TRUE, 3),
       (4, 3, 4, 'Картинка супер, сюжет средний', TRUE, -1),
       (5, 7, 5, 'Эпично и масштабно, но длинновато', FALSE, 1),
       (6, 8, 6, 'Атмосфера огонь, концовка спорная', TRUE, 0);

-- ---------- Лайки/дизлайки отзывов ----------
INSERT INTO review_likes (review_id, user_id, is_like)
VALUES (1, 2, TRUE),
       (1, 3, TRUE),
       (1, 4, FALSE);

INSERT INTO review_likes (review_id, user_id, is_like)
VALUES (2, 1, FALSE);

INSERT INTO review_likes (review_id, user_id, is_like)
VALUES (3, 1, TRUE),
       (3, 2, TRUE),
       (3, 4, TRUE);

INSERT INTO review_likes (review_id, user_id, is_like)
VALUES (4, 2, TRUE),
       (4, 5, FALSE),
       (4, 6, FALSE);

INSERT INTO review_likes (review_id, user_id, is_like)
VALUES (5, 1, TRUE);

INSERT INTO review_likes (review_id, user_id, is_like)
VALUES (6, 3, TRUE),
       (6, 2, FALSE);

ALTER TABLE reviews
    ALTER COLUMN id RESTART WITH 7;

ALTER TABLE mpa_rating
    ALTER COLUMN id RESTART WITH 6;
ALTER TABLE genres
    ALTER COLUMN id RESTART WITH 11;
ALTER TABLE users
    ALTER COLUMN id RESTART WITH 7;
ALTER TABLE films
    ALTER COLUMN id RESTART WITH 9;
ALTER TABLE film_genre
    ALTER COLUMN id RESTART WITH 17;
ALTER TABLE user_likes_film
    ALTER COLUMN id RESTART WITH 13;
ALTER TABLE friends
    ALTER COLUMN id RESTART WITH 8;
ALTER TABLE directors
    ALTER COLUMN id RESTART WITH 8;
