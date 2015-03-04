CREATE TABLE product (
  pid INT PRIMARY KEY NOT NULL,
  price MONEY NOT NULL
);

CREATE TABLE language (
  langid INT PRIMARY KEY NOT NULL,
  name VARCHAR(50)
);

CREATE TABLE country (
  coid INT PRIMARY KEY NOT NULL,
  name VARCHAR(50),
  tax_rate DOUBLE PRECISION,
  language_id INT REFERENCES language(langid)
);

CREATE TABLE format (
  fid INT PRIMARY KEY NOT NULL,
  extension VARCHAR (20)
);

CREATE TABLE song (
  pid INT PRIMARY KEY REFERENCES product(pid) 
    ON DELETE CASCADE NOT NULL,
  title VARCHAR(100) NOT NULL,
  date_recorded DATE,
  bpm INT,
  lyrics TEXT,
  -- ASSUMES ONE LANGUAGE
  language_id INT REFERENCES language(langid),
  format_id INT REFERENCES format(fid)
);

CREATE TABLE artist (
  artid INT PRIMARY KEY NOT NULL,
  name VARCHAR(100) NOT NULL,
  description TEXT,
  date_formed DATE,
  location_of_origin_id INT REFERENCES country(coid)
);

CREATE TABLE album (
  pid INT PRIMARY KEY REFERENCES product(pid) 
    ON DELETE CASCADE NOT NULL,
  name VARCHAR(100) NOT NULL,
  date_released DATE,
  artwork BYTEA
);

CREATE TABLE genre (
  genid INT PRIMARY KEY NOT NULL,
  name VARCHAR(100) NOT NULL
);

CREATE TABLE customer (
  cid INT PRIMARY KEY NOT NULL,
  firstname VARCHAR(50),
  lastname VARCHAR(50),
  email VARCHAR(100) NOT NULL,
  credit_card_number VARCHAR(20),
  birthdate DATE,
  password VARCHAR(100) NOT NULL,
  location_id INT REFERENCES country(coid),
  language_id INT REFERENCES language(langid),
  CONSTRAINT strong_password 
    CHECK (password ~ '^(?=.*[A-Z])(?=.*[!?@$#&*_-])(?=.*[0-9])(?=.*[a-z]).{8,}$' )
);

CREATE TABLE purchase (
  purchaseid INT PRIMARY KEY NOT NULL,
  cid INT REFERENCES customer(cid) NOT NULL,
  purchase_date DATE,
  price MONEY
);

CREATE TABLE song_artist (
  artid INT REFERENCES artist(artid) NOT NULL,
  sid INT REFERENCES song(pid) NOT NULL,
  PRIMARY KEY (artid, sid)
);

CREATE TABLE album_artist (
  artid INT REFERENCES artist(artid) NOT NULL,
  albid INT REFERENCES album(pid) NOT NULL,
  PRIMARY KEY (artid, albid)
);

CREATE TABLE track (
  -- ASSUMPTION: song can only be in one album
  -- i.e. you *can* buy a song twice if it
  -- were to appear in two distinct albums
  sid INT PRIMARY KEY REFERENCES song(pid) NOT NULL,
  albid INT REFERENCES album(pid) NOT NULL,
  track_number INT
);

CREATE TABLE song_genre (
  genid INT REFERENCES genre(genid) NOT NULL,
  sid INT REFERENCES song(pid) NOT NULL,
  PRIMARY KEY (genid, sid)
);

CREATE TABLE purchase_product (
  purchaseid INT REFERENCES purchase(purchaseid) NOT NULL,
  pid INT REFERENCES product(pid) NOT NULL,
  cost MONEY NOT NULL,
  PRIMARY KEY (purchaseid, pid)
);

-- rating for PRODUCT because can rate albums too 
CREATE TABLE rating (
  pid INT REFERENCES product(pid) 
    ON DELETE CASCADE NOT NULL,
  cid INT REFERENCES customer(cid) NOT NULL,
  PRIMARY KEY (pid, cid),
  rating_amt INT NOT NULL,
  -- ASSUME rating is system from 1 to 5
  CONSTRAINT rating_quantity CHECK ( rating_amt >= 1 AND rating_amt <= 5)
);


-- Q3

INSERT INTO product VALUES (1, '$200.00');
INSERT INTO language VALUES (1, 'English');
INSERT INTO format VALUES (1, 'mp3');
INSERT INTO song VALUES (1, 'The First Song', '1999-02-25', 120, 'These are the lyrics to the first song!', 1, 1);
INSERT INTO country VALUES (1, 'Canada', 1);

-- Q4 TODO?
-- Languages
INSERT INTO language VALUES (2, 'French');
INSERT INTO language VALUES (3, 'German');
INSERT INTO language VALUES (4, 'Mandarin');
INSERT INTO language VALUES (5, 'Greek');
INSERT INTO language VALUES (6, 'Klingon');

-- Formats
INSERT INTO format VALUES (2, 'FLAC');
INSERT INTO format VALUES (3, 'WMA');

-- Countries
INSERT INTO country VALUES (2, 'USA', 1);
INSERT INTO country VALUES (3, 'Australia', 1);
INSERT INTO country VALUES (4, 'United Kingdom', 1);
INSERT INTO country VALUES (5, 'Germany', 3);
INSERT INTO country VALUES (6, 'Greece', 5);

-- Products
INSERT INTO product VALUES ( 2, '$12.00');
INSERT INTO product VALUES ( 3, '$2.00');
INSERT INTO product VALUES ( 4, '$2.00');
INSERT INTO product VALUES ( 5, '$2.00');
INSERT INTO product VALUES ( 6, '$2.00');
INSERT INTO product VALUES ( 7, '$2.00');
INSERT INTO product VALUES ( 8, '$21.00');

INSERT INTO product VALUES ( 9, '$1.00');
INSERT INTO product VALUES ( 10, '$1.00');
INSERT INTO product VALUES ( 11, '$1.50');
INSERT INTO product VALUES ( 12, '$2.40');
INSERT INTO product VALUES ( 13, '$2.30');
INSERT INTO product VALUES ( 14, '$2.20');
INSERT INTO product VALUES ( 15, '$1.10');

-- Songs
INSERT INTO song VALUES ( 2, '2112', '1976-02-01', 121, 'And the meek shall inherit the Earth...', 1, 1);
INSERT INTO song VALUES ( 3, 'A Passage to Bangkok', '1976-02-01', 92, 'Our first stop is in Bogota...', 1, 1);
INSERT INTO song VALUES ( 4, 'The Twilight Zone', '1976-02-01', 100, 'A pleasant faced man steps up to greet you...', 1, 1);
INSERT INTO song VALUES ( 5, 'Lessons', '1976-02-01', 110, 'Sweet memories,\nI never thought it would be like this...', 1, 1);
INSERT INTO song VALUES ( 6, 'Tears', '1976-02-01', 90, 'All of the seasons,\nAnd all of the days...', 1, 1);
INSERT INTO song VALUES ( 7, 'Something for Nothing', '1976-02-01', 130, 'Waiting for the winds of change to sweep the clouds away...', 1, 1);

INSERT INTO song VALUES ( 9, 'Tom Sawyer', '1980-10-01', 120, 'A modern day warrior of mean, mean stride...', 1, 1);
INSERT INTO song VALUES ( 10, 'Red Barchetta', '1980-10-01', 110, 'My uncle has a country place, that no one knows about...', 1, 1);
INSERT INTO song VALUES ( 11, 'YYZ', '1980-10-01', 747, null, 1, 1);
INSERT INTO song VALUES ( 12, 'Limelight', '1980-10-01', 120, 'Living on a lighted stages approaches the unreal...', 1, 1);
INSERT INTO song VALUES ( 13, 'Witch Hunt', '1980-10-01', 100, 'The night is black, without a moon...', 1, 1);
INSERT INTO song VALUES ( 14, 'Vital Signs', '1980-10-01', 120, 'Unstable condition. A sympton of life...', 1, 1);

-- Artists
INSERT INTO artist VALUES ( 1, 'Rush', 'A Canadian power trio formed in Toronto, Ontario', '1968-08-01', 1);
INSERT INTO artist VALUES ( 2, 'Triumph', 'A Canadian power trio formed in Mississauga, Ontario', '1975-08-01', 1);
INSERT INTO artist VALUES ( 3, 'Genesis', 'An English rock band known for members such as Peter Gabriel and Phil Colline', '1967-01-01', 1);
INSERT INTO artist VALUES ( 4, 'AC/DC', 'aalsidhf kosdv diofgre cifg', '1973-11-01', 3);
INSERT INTO artist VALUES ( 5, 'Rammstein', 'dndng aifgn akg alha', '1994-01-01', 5);

-- Albums
INSERT INTO album VALUES ( 8, '2112', '1976-04-01', null);
INSERT INTO album VALUES (15, 'Moving Pictures', '1981-02-12', null);

-- Genres
INSERT INTO genre VALUES ( 1, 'Rock and Roll');
INSERT INTO genre VALUES ( 2, 'Hard Rock');
INSERT INTO genre VALUES ( 3, 'Progressive Rock');
INSERT INTO genre VALUES ( 4, 'Classical');
INSERT INTO genre VALUES ( 5, 'Jazz');
INSERT INTO genre VALUES ( 6, 'Blues');
INSERT INTO genre VALUES ( 7, 'Heavy Metal');
INSERT INTO genre VALUES ( 8, 'Grunge');
INSERT INTO genre VALUES ( 9, 'Pop');
INSERT INTO genre VALUES ( 10, 'Dance');

-- song artist
INSERT INTO song_artist VALUES ( 1, 2);
INSERT INTO song_artist VALUES ( 1, 3);
INSERT INTO song_artist VALUES ( 1, 4);
INSERT INTO song_artist VALUES ( 1, 5);
INSERT INTO song_artist VALUES ( 1, 6);
INSERT INTO song_artist VALUES ( 1, 7);

INSERT INTO song_artist VALUES ( 1, 9);
INSERT INTO song_artist VALUES ( 1, 10);
INSERT INTO song_artist VALUES ( 1, 11);
INSERT INTO song_artist VALUES ( 1, 12);
INSERT INTO song_artist VALUES ( 1, 13);
INSERT INTO song_artist VALUES ( 1, 14);

-- album artist
INSERT INTO album_artist VALUES (1, 8);
INSERT INTO album_artist VALUES (1, 15);

-- tracks
INSERT INTO track VALUES (2, 8, 1);
INSERT INTO track VALUES (3, 8, 2);
INSERT INTO track VALUES (4, 8, 3);
INSERT INTO track VALUES (5, 8, 4);
INSERT INTO track VALUES (6, 8, 5);
INSERT INTO track VALUES (7, 8, 6);

INSERT INTO track VALUES (9, 15, 1);
INSERT INTO track VALUES (10, 15, 2);
INSERT INTO track VALUES (11, 15, 3);
INSERT INTO track VALUES (12, 15, 4);
INSERT INTO track VALUES (13, 15, 5);
INSERT INTO track VALUES (14, 15, 6);

-- song Genres
INSERT INTO song_genre VALUES ( 3, 2);
INSERT INTO song_genre VALUES ( 2, 3);
INSERT INTO song_genre VALUES ( 2, 4);
INSERT INTO song_genre VALUES ( 2, 5);
INSERT INTO song_genre VALUES ( 2, 6);
INSERT INTO song_genre VALUES ( 2, 7);

INSERT INTO song_genre VALUES ( 1, 9);
INSERT INTO song_genre VALUES ( 1, 10);
INSERT INTO song_genre VALUES ( 1, 11);
INSERT INTO song_genre VALUES ( 1, 12);
INSERT INTO song_genre VALUES ( 1, 13);
INSERT INTO song_genre VALUES ( 1, 14);

-- Q5 TODO --> try to do stuff that is different from the Views

-- Q6

-- 10% off on all products that are rated poorly
-- yes, I know this would be very easy to exploit IRL

UPDATE product
SET price = price - price/10
WHERE product.pid = ANY (
  SELECT product.pid
  FROM product, rating
  WHERE product.pid = rating.pid
    AND rating.rating_amt < 2
)

-- I haven't actually tested that this works
-- updates the price of an album
-- to the sum of the prices of its songs

UPDATE product
SET price = ps.total_price
FROM (
  SELECT product.pid, SUM(product.price) as total_price
  FROM product, song, track
  WHERE product.pid = track.albid
    AND track.sid = song.pid
  GROUP BY product.pid
) AS ps
WHERE ps.pid = product.pid

-- I haven't actually tested that this works
-- lower a purchase amount by the customer's tax rate (a tax rebate)

UPDATE purchase
SET price = price - price * country.tax_rate
FROM customer
INNER JOIN country
ON customer.location_id = country.coid
WHERE purchase.cid = customer.cid

-- I haven't actually tested that this works
-- remove all ratings from users who only give negative ratings ( < 3 )
-- in large enough quantities ( > 100 )

DELETE FROM rating
WHERE rating.cid = ANY (
  SELECT neg_reviewers.cid
  FROM (
    SELECT customer.cid, COUNT(*) as neg_reviews
    FROM customer, rating
    WHERE rating.cid = customer.cid
      AND rating.rating_amt < 3
    GROUP BY customer.cid
  ) as neg_reviewers
  WHERE neg_reviews > 100
)

-- Q7 VIEWS

-- This view represents the basic info a music player were to display
-- if we had a preview of the song on the site
CREATE VIEW SongPlayerInfo ( Song, Artist, Album, Genre, TrackNo)
AS SELECT S.title, ART.name, ALB.name, G.name, T.track_number
FROM song S
INNER JOIN track T ON S.pid = T.sid
INNER JOIN album ALB ON T.albid = ALB.pid
INNER JOIN album_artist AR ON ALB.pid = AR.albid
INNER JOIN artist ART ON AR.artid = ART.artid
INNER JOIN song_genre SG ON S.pid = SG.sid
INNER JOIN genre G ON SG.genid = G.genid;

-- let's say we want to feature the album 2112 in a music player
SELECT * 
FROM SongPlayerInfo
WHERE Album = '2112'
ORDER BY TrackNo ASC;

-- This view returns the highest rated products which is something
-- a lot of customers look at in stores
CREATE VIEW HighestRatings ( ProductID, Rating )
AS SELECT P.pid, AVG(R.rating_amt)
FROM product P
INNER JOIN rating R ON P.pid = R.pid
GROUP BY P.pid
ORDER BY 2, 1;

-- Let's say we want to get the highest rated albums
SELECT A.name AS AlbumName, HR.Rating
FROM HighestRatings HR
INNER JOIN Album A ON HR.productID = A.pid
ORDER BY HR.Rating;

-- Q8 has been incorporated into the create table's

-- first is the strong password enforcement in customer
-- strong password MUST have one lowercase alpha,
-- one lowercase alpha, one numeric,
-- one special character (!?$#@_-), and must
-- be at least of length 8

-- example that will violate the constraint:

-- prep
INSERT INTO language VALUES (1, 'English');
INSERT INTO country VALUES (1, 'UK', 1);

INSERT INTO customer VALUES
(1, 'bob', 'stevenson', 'bob.s@email.com', NULL, NULL, 'notgoodpassword', 1, 1);

-- second is the rating constraint,
-- forcing a rating amount to be between 1 and 5

-- example that will violate the constraint:

-- prep
INSERT INTO language VALUES (1, 'English');
INSERT INTO country VALUES (1, 'UK', 1);
INSERT INTO customer VALUES
(1, 'bob', 'stevenson', 'bob.s@email.com', NULL, NULL, 'g0od_P@assword', 1, 1);
INSERT INTO product VALUES (1, '$200.00');
INSERT INTO format VALUES (1, 'mp3');
INSERT INTO song VALUES (1, 'The First Song', '1999-02-25', 120, 'These are the lyrics to the first song!', 1, 1);

INSERT INTO rating VALUES (1, 1, 6);

-- DB CLEANUP STUFF, REMOVE FROM FINAL VERSION

DROP SCHEMA PUBLIC CASCADE;
CREATE SCHEMA PUBLIC;