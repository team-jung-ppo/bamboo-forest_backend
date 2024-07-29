EXPLAIN ANALYZE
SELECT *
FROM chatbot_purchase
WHERE member_id = 1
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
FROM generate_series(1, 100) AS gs;

INSERT INTO chatbot_purchase (chatbot_purchase_id, amount, chat_bot_item, member_id, created_at, modified_at)
SELECT gs AS chatbot_purchase_id,
       CASE WHEN RANDOM() < 0.5 THEN 10 ELSE 20 END,
       CASE WHEN RANDOM() < 0.5 THEN 'UNCLE_CHATBOT' ELSE 'AUNT_CHATBOT' END,
       (RANDOM() * 99)::int + 1,
       TIMESTAMP '2022-01-01 00:00:00' + (RANDOM() * (INTERVAL '365 days')),
       TIMESTAMP '2022-01-01 00:00:00' + (RANDOM() * (INTERVAL '365 days'))
FROM generate_series(1, 200) AS gs;
