
INSERT INTO category (category_id, category_name) VALUES
(1, 'VIP'),
(2, 'Gold'),
(3, 'Silver'),
(4, 'Standard');


INSERT INTO `user` (user_id, user_name, first_name, last_name, email, password, user_type) VALUES
(1, 'john_doe', 'John', 'Doe', 'john@example.com', 'pass123', 'Client'),
(2, 'alice_smith', 'Alice', 'Smith', 'alice@example.com', 'pass123', 'Client'),
(3, 'organizer_mike', 'Mike', 'Jordan', 'mike@example.com', 'pass123', 'Organisateur');


INSERT INTO event (event_id, name, event_type, special_guest, event_date, location, organizer_id, seat_count)
VALUES
(1, 'Rock Festival', 'Concert', 'Metallica', '2025-07-15 20:00:00', 'Stadium Arena', 3, 5000),
(2, 'Magic Show', 'Spectacle', 'David Copperfield', '2025-08-01 19:00:00', 'Royal Theater', 3, 1200),
(3, 'Tech Innovations 2025', 'Conference', 'Elon Musk', '2025-09-10 09:00:00', 'Expo Center', 3, 800);


INSERT INTO place (place_id, price, category_id, event_id) VALUES
(1, 150.00, 1, 1),
(2, 100.00, 2, 1),
(3, 70.00, 3, 1),
(4, 40.00, 4, 1),

(5, 200.00, 1, 2),
(6, 120.00, 2, 2),
(7, 80.00, 3, 2),
(8, 50.00, 4, 2),

(9, 300.00, 1, 3),
(10, 180.00, 2, 3),
(11, 100.00, 3, 3),
(12, 60.00, 4, 3);


INSERT INTO reservation (reservation_id, reservation_date, client_id) VALUES
(1, '2025-06-01 10:30:00', 1),
(2, '2025-06-02 15:45:00', 2);


INSERT INTO reservation_has_place (reservation_id, place_id) VALUES
(1, 1),
(1, 2),
(2, 6);


INSERT INTO payment (payment_id, card_name, card_number, payment_date, reservation_id) VALUES
(1, 'John Doe', '1234567890123456', '2025-06-01 11:00:00', 1),
(2, 'Alice Smith', '9876543210987654', '2025-06-02 16:00:00', 2);
