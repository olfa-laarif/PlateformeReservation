INSERT INTO user (user_name, first_name, last_name, email, password, user_type)
VALUES ('orga_katia', 'Katia', 'Haouchine', 'katiahaouchine@gmail.com', 'secret', 'Organisateur')
    ON DUPLICATE KEY UPDATE email = VALUES(email);

INSERT INTO event (name, event_type, special_guest, event_date, location, organizer_id, seat_count)
SELECT 'Concert Jazz Nuit', 'Concert', 'The Smooth Band', '2025-12-15 20:30:00', 'France', user_id, 300
FROM user
WHERE user_name = 'orga_katia'
    ON DUPLICATE KEY UPDATE location = VALUES(location);

INSERT INTO category (category_name)
VALUES ('Standard'), ('VIP')
    ON DUPLICATE KEY UPDATE category_name = VALUES(category_name);

INSERT INTO place (price, category_id, event_id, seats_total)
VALUES (
           45.0,
           (SELECT category_id FROM category WHERE category_name = 'Standard'),
           (SELECT event_id FROM event WHERE name = 'Concert Jazz Nuit'),
           250
       )
    ON DUPLICATE KEY UPDATE price = VALUES(price), seats_total = VALUES(seats_total);

INSERT INTO place (price, category_id, event_id, seats_total)
VALUES (
           85.0,
           (SELECT category_id FROM category WHERE category_name = 'VIP'),
           (SELECT event_id FROM event WHERE name = 'Concert Jazz Nuit'),
           50
       )
    ON DUPLICATE KEY UPDATE price = VALUES(price), seats_total = VALUES(seats_total);

