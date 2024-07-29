EXPLAIN ANALYZE
SELECT *
FROM payment
WHERE member_id = 1
  AND status = 'COMPLETED'
ORDER BY created_at DESC;

INSERT INTO member (member_id, name, o_auth2, username, profile_image, role, created_at, modified_at, version,
                    battery_count, chat_bots)
SELECT gs AS member_id,
       'user' || gs,
       'OAUTH2_GITHUB',
       'user' || gs,
       'profile' || gs || '.jpg',
       'ROLE_USER',
       NOW(),
       NOW(),
       0,
       100,
       ''
FROM generate_series(1, 100) gs;

INSERT INTO payment (payment_id, status, battery_item, member_id, key, provider, amount, created_at, modified_at)
SELECT uuid_generate_v4(),
       CASE WHEN RANDOM() < 0.5 THEN 'COMPLETED' ELSE 'PENDING' END,
       CASE
           WHEN RANDOM() < 0.2 THEN 'SMALL_BATTERY'
           WHEN RANDOM() < 0.4 THEN 'MEDIUM_BATTERY'
           WHEN RANDOM() < 0.6 THEN 'LARGE_BATTERY'
           WHEN RANDOM() < 0.8 THEN 'SMALL_BATTERY_PACK'
           ELSE 'MEDIUM_BATTERY_PACK'
           END,
       (RANDOM() * 99)::int + 1,
       md5(random()::text),
       'provider' || (RANDOM() * 10)::int,
       CASE
           WHEN RANDOM() < 0.2 THEN 3000
           WHEN RANDOM() < 0.4 THEN 5000
           WHEN RANDOM() < 0.6 THEN 10000
           WHEN RANDOM() < 0.8 THEN 20000
           ELSE 50000
           END::numeric,
       TIMESTAMP '2022-01-01 00:00:00' + (RANDOM() * (INTERVAL '365 days')),
       TIMESTAMP '2022-01-01 00:00:00' + (RANDOM() * (INTERVAL '365 days'))
FROM generate_series(1, 1000) AS gs;
