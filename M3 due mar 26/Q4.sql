-- index on genre.name, run 1st and 4th query from Milestone 2 Q5 to see effects
CREATE INDEX genrename ON genre(name);

-- index on artist.name, run 3rd query from Milestone 2 Q5 to see effects
CREATE INDEX artistname ON artist(name);