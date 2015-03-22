-- 10% off on all products that are rated poorly
-- yes, I know this would be very easy to exploit

UPDATE product
SET price = price - price/10
WHERE product.pid = ANY (
  SELECT product.pid
  FROM product, rating
  WHERE product.pid = rating.pid
    AND rating.rating_amt < 2
);

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
WHERE ps.pid = product.pid;


-- lower a purchase amount by the customer's tax rate (a tax rebate)

UPDATE purchase
SET price = price - price * country.tax_rate
FROM customer
INNER JOIN country
ON customer.location_id = country.coid
WHERE purchase.cid = customer.cid;


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
);