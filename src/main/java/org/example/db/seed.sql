-- seed.sql : données de démonstration pour PlateformeReservation

-- Categories
INSERT INTO category (category_id, category_name) VALUES
  (1, 'VIP'), (2, 'Gradin'), (3, 'Standard'), (4, 'Fosse');

-- Organisateur (id explicite pour faciliter le seed)
INSERT INTO `user` (user_id, user_name, first_name, last_name, email, password, user_type)
  VALUES (1, 'org1', 'Org', 'One', 'org1@example.com', 'orgpass', 'Organisateur');

-- Événements (utilise organizer_id = 1)
INSERT INTO `event` (event_id, name, event_type, special_guest, event_date, location, organizer_id, seat_count)
  VALUES (1, 'Rock Night', 'Concert', 'The Band', NOW() + INTERVAL 10 DAY, 'Stade', 1, 300),
         (2, 'Tech Talk', 'Conference', NULL, NOW() + INTERVAL 20 DAY, 'Centre Conf', 1, 150),
         (3, 'Cirque', 'Spectacle', NULL, NOW() + INTERVAL 5 DAY, 'Théâtre', 1, 80);

-- Places / catégories pour events (fixe place_id 1..4)
INSERT INTO place (place_id, price, category_id, event_id, places_total, places_remaining)
  VALUES
    (1, 120.00, 1, 1, 10, 10),
    (2, 50.00, 2, 1, 200, 200),
    (3, 20.00, 3, 2, 150, 150),
    (4, 70.00, 4, 3, 80, 80);

-- Client de test (id explicite)
INSERT INTO `user` (user_id, user_name, first_name, last_name, email, password, user_type)
  VALUES (2, 'jdupont', 'Jean', 'Dupont', 'jean@example.com', 'pass', 'Client');
