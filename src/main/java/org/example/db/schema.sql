CREATE TABLE `user` (
    user_id        INT AUTO_INCREMENT PRIMARY KEY,
    user_name      VARCHAR(100) NOT NULL UNIQUE,
    first_name     VARCHAR(100) NOT NULL,
    last_name      VARCHAR(100) NOT NULL,
    email          VARCHAR(150) NOT NULL UNIQUE,
    password       VARCHAR(100) NOT NULL,
    user_type      ENUM('Client', 'Organisateur') NOT NULL
);

CREATE TABLE category (
    category_id     INT AUTO_INCREMENT PRIMARY KEY,
    category_name   VARCHAR(50) NOT NULL
);

CREATE TABLE `event` (
    event_id       INT AUTO_INCREMENT PRIMARY KEY,
    name           VARCHAR(150) NOT NULL,
    event_type     ENUM('Concert', 'Spectacle', 'Conference') NOT NULL,
    special_guest  VARCHAR(150),
    event_date     DATETIME NOT NULL,
    location       VARCHAR(150) NOT NULL,
    organizer_id   INT NOT NULL,
    seat_count      INT NOT NULL,

    FOREIGN KEY (organizer_id)
        REFERENCES `user`(user_id)
        ON UPDATE CASCADE
        ON DELETE CASCADE
);

CREATE TABLE place (
    place_id      INT AUTO_INCREMENT PRIMARY KEY,
    price         DECIMAL(10,2) NOT NULL,
    category_id   INT NOT NULL,
    event_id      INT NOT NULL,

    FOREIGN KEY (category_id)
        REFERENCES category(category_id)
        ON UPDATE CASCADE
        ON DELETE CASCADE,

    FOREIGN KEY (event_id)
        REFERENCES event(event_id)
        ON UPDATE CASCADE
        ON DELETE CASCADE
);

CREATE TABLE reservation (
    reservation_id   INT AUTO_INCREMENT PRIMARY KEY,
    reservation_date DATETIME NOT NULL,
    client_id        INT NOT NULL,

    FOREIGN KEY (client_id)
        REFERENCES user(user_id)
        ON UPDATE CASCADE
        ON DELETE CASCADE
);

CREATE TABLE payment (
    payment_id       INT AUTO_INCREMENT PRIMARY KEY,
    card_name        VARCHAR(100) NOT NULL,
    card_number      VARCHAR(16) NOT NULL,
    payment_date     DATETIME NOT NULL,
    reservation_id   INT NOT NULL UNIQUE,

    FOREIGN KEY (reservation_id)
        REFERENCES reservation(reservation_id)
        ON UPDATE CASCADE
        ON DELETE CASCADE
);

CREATE TABLE reservation_has_place (
    reservation_id  INT NOT NULL,
    place_id        INT NOT NULL,

    PRIMARY KEY (reservation_id, place_id),

    FOREIGN KEY (reservation_id)
        REFERENCES reservation(reservation_id)
        ON UPDATE CASCADE
        ON DELETE CASCADE,

    FOREIGN KEY (place_id)
        REFERENCES place(place_id)
        ON UPDATE CASCADE
        ON DELETE CASCADE
);
