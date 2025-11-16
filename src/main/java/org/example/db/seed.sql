
INSERT INTO `user` (user_name, first_name, last_name, email, password, user_type)
VALUES
('john_doe', 'John', 'Doe', 'john@example.com', 'password123', 'Client'),
('alice_admin', 'Alice', 'Admin', 'alice@example.com', 'adminpass', 'Organisateur');


INSERT INTO `event` (name, event_type, special_guest, event_date, location, organizer_id)
VALUES
('Rock Night', 'Concert', 'The Rockers', '2025-12-20 20:00:00', 'Stadium A', 2),
('Cirque Fantastique', 'Spectacle', 'Cirque Fantastique', '2025-11-28 19:00:00', 'Grand Théâtre', 2),
('Tech Conference 2025', 'Conference', 'Dr. Dupont', '2025-11-25 09:00:00', 'Convention Center', 2);


INSERT INTO category (category_name, price, available_seats, event_id)
VALUES
('Standard', 50.00, 100, 1),
('VIP', 120.00, 20, 1),
('Standard', 40.00, 150, 2),
('Premium', 80.00, 50, 2),
('Standard', 30.00, 200, 3),
('Premium', 70.00, 50, 3);


INSERT INTO reservation (reservation_date, ticket_count, total_amount, client_id, event_id, category_id)
VALUES
('2025-11-10 10:00:00', 2, 100.00, 1, 1, 1),
('2025-11-12 15:30:00', 1, 80.00, 1, 2, 4);


INSERT INTO payment (card_name, card_number, payment_date, reservation_id)
VALUES
('John Doe', '4111111111111111', '2025-11-10 10:05:00', 1),
('John Doe', '4111111111111111', '2025-11-12 15:35:00', 2);
