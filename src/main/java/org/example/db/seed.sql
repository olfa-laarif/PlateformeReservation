-- Ajout ou mise à jour de clients (sans user_id explicit) 
INSERT INTO user (user_name, first_name, last_name, email, password, user_type)
VALUES
    ('client_jane', 'Jane', 'Doe', 'jane.doe@example.com', 'pass456', 'Client'),
    ('client_paul', 'Paul', 'Dupont', 'paul.dupont@example.com', 'motdepasse', 'Client')
    ON DUPLICATE KEY UPDATE
                         first_name = VALUES(first_name),
                         last_name = VALUES(last_name),
                         password = VALUES(password),
                         user_type = VALUES(user_type);

-- Ajout ou mise à jour de l'organisateur Katia (sans user_id explicit) 
INSERT INTO user (user_name, first_name, last_name, email, password, user_type)
VALUES ('orga_katia', 'Katia', 'Haouchine', 'katiahaouchine@gmail.com', 'secret', 'Organisateur')
    ON DUPLICATE KEY UPDATE
                         first_name = VALUES(first_name),
                         last_name = VALUES(last_name),
                         email = VALUES(email),
                         password = VALUES(password),
                         user_type = VALUES(user_type);

-- Ajout ou mise à jour des catégories 'VIP' et 'Standard' 
INSERT INTO category (category_name)
VALUES ('VIP'), ('Standard')
    ON DUPLICATE KEY UPDATE category_name = VALUES(category_name);

-- Ajout ou mise à jour de l'événement Concert Jazz Nuit 
INSERT INTO event (name, event_type, special_guest, event_date, location, organizer_id, seat_count)
SELECT
    'Concert Jazz Nuit',
    'Concert',
    'The Smooth Band',
    '2025-12-15 20:30:00',
    'France',
    user_id,
    300
FROM user
WHERE user_name = 'orga_katia'
    ON DUPLICATE KEY UPDATE
                         special_guest = VALUES(special_guest),
                         event_date = VALUES(event_date),
                         location = VALUES(location),
                         seat_count = VALUES(seat_count);

-- Ajout ou mise à jour des places (seats_remaining retiré car absent du schéma, seats_total uniquement) 
INSERT INTO place (price, category_id, event_id, seats_total)
VALUES (
           45.0,
           (SELECT category_id FROM category WHERE category_name = 'Standard'),
           (SELECT event_id FROM event WHERE name = 'Concert Jazz Nuit'),
           250
       )
    ON DUPLICATE KEY UPDATE
                         price = VALUES(price),
                         seats_total = VALUES(seats_total);

INSERT INTO place (price, category_id, event_id, seats_total)
VALUES (
           85.0,
           (SELECT category_id FROM category WHERE category_name = 'VIP'),
           (SELECT event_id FROM event WHERE name = 'Concert Jazz Nuit'),
           50
       )
    ON DUPLICATE KEY UPDATE
                         price = VALUES(price),
                         seats_total = VALUES(seats_total);
 