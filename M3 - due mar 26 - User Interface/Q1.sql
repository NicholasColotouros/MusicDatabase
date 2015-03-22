-- trigger deletes all ratings for a product when the product is deleted
CREATE OR REPLACE FUNCTION delete_rating() RETURNS TRIGGER AS $_$
	BEGIN
        	DELETE FROM rating WHERE rating.pid = OLD.pid;
		RETURN OLD;
	END $_$ LANGUAGE 'plpgsql';

CREATE TRIGGER deleteRating BEFORE
	DELETE ON song
	FOR EACH ROW EXECUTE PROCEDURE delete_rating();

-- before modification: show that dabatase contain ratings of product with pid of 1
SELECT * FROM rating WHERE pid = 1;

-- database modification that fires trigger
DELETE FROM song WHERE song.pid = 1;

-- show effects of deletion: we will not see any ratings of product with pid of 1
SELECT * FROM rating WHERE pid = 1;



-- before modification: show that database contains ratings from customer with id of 1
SELECT * FROM rating WHERE pid = 2;

-- database modification that doesn't fire trigger
UPDATE song SET title = 'New Song' WHERE pid = 2;

-- show effects of deletion: we will still see ratings from customer with cid of 1
SELECT * FROM rating WHERE pid = 2;