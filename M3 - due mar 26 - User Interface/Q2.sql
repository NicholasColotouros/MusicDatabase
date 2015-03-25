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


SELECT country_stats_by_extension('mp3', 'United Kingdom');

-- includes: COUNTER, PARAMS, LOCAL VAR, MULTIPLE SQL, LOOPS
-- thus conforms to specs afaik