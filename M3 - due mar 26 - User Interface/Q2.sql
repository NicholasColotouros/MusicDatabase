CREATE FUNCTION country_stats_by_extension(extension_name varchar, country_name varchar) RETURNS INT AS $$
DECLARE
    songs_by_extension CURSOR FOR 
    SELECT song.pid, country.name FROM song 
        INNER JOIN song_artist ON song.pid = song_artist.sid
        INNER JOIN artist ON artist.artid = song_artist.artid
        INNER JOIN country ON country.coid = artist.location_of_origin_id
        INNER JOIN format ON format.fid = song.format_id
        WHERE format.extension like extension_name
    ;
    song_count INTEGER DEFAULT 0;
BEGIN
    FOR recordvar IN songs_by_extension LOOP
        IF recordvar.name like country_name
        THEN
            song_count := song_count + 1;
        END IF;
    END LOOP;
RETURN song_count;

END;
$$ LANGUAGE plpgsql;


CREATE FUNCTION create_songs_extension_by_country(extension_name varchar) RETURNS void AS $$
DECLARE
    country_names CURSOR FOR
    SELECT country.name FROM country;
BEGIN

    EXECUTE 'DROP TABLE IF EXISTS extension_songs_by_country';
    EXECUTE 'CREATE TABLE extension_songs_by_country(cname varchar(50), song_count int)';
    
    FOR recordvar IN country_names LOOP
            INSERT INTO extension_songs_by_country VALUES(recordvar.name, country_stats_by_extension(extension_name, recordvar.name));
    END LOOP;

END;
$$ LANGUAGE plpgsql;

-- example of value-returning function
SELECT country_stats_by_extension('mp3', 'United Kingdom');

--example of table-creating function
SELECT create_songs_extension_by_country('mp3');
SELECT * FROM extension_songs_by_country;

-- these storedproc's include: CURSORS, PARAMS, LOCAL VARS, MULTIPLE SQL, LOOPS, CREATING RELATIONS
