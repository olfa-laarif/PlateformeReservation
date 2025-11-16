

CREATE TABLE `user` (
    user_id            INT AUTO_INCREMENT PRIMARY KEY,
    user_name              VARCHAR(100)        NOT NULL UNIQUE,
    first_name              VARCHAR(100)        NOT NULL,
    last_name               VARCHAR(100)        NOT NULL,
    email              VARCHAR(150)        NOT NULL UNIQUE,
    password           VARCHAR(100)        NOT NULL,
    user_type          ENUM('Client', 'Organisateur') NOT NULL
);



CREATE TABLE `event` (
    event_id       INT AUTO_INCREMENT PRIMARY KEY,
    name           VARCHAR(150) NOT NULL,
    event_type     ENUM('Concert', 'Spectacle', 'Conference') NOT NULL,
    special_guest  VARCHAR(150),  -- artiste, troupe ou intervenant selon le type
    event_date     DATETIME NOT NULL,
    location       VARCHAR(150) NOT NULL,
    organizer_id   INT NOT NULL,

    FOREIGN KEY (organizer_id)
        REFERENCES `user`(user_id)
        ON UPDATE CASCADE
        ON DELETE CASCADE
);



CREATE TABLE category (
    category_id        INT AUTO_INCREMENT PRIMARY KEY,
    category_name      VARCHAR(50)         NOT NULL,
    price              DECIMAL(10,2)       NOT NULL,
    available_seats    INT                 NOT NULL,
    event_id           INT                 NOT NULL,

    FOREIGN KEY (event_id)
        REFERENCES event(event_id)
        ON UPDATE CASCADE
        ON DELETE CASCADE
);



CREATE TABLE reservation (
    reservation_id     INT AUTO_INCREMENT PRIMARY KEY,
    reservation_date   DATETIME            NOT NULL,
    ticket_count       INT                 NOT NULL,
    total_amount       DECIMAL(10,2)       NOT NULL,
    client_id          INT                 NOT NULL,
    event_id           INT                 NOT NULL,
    category_id        INT                 NOT NULL,

    FOREIGN KEY (client_id)
        REFERENCES user(user_id)
        ON UPDATE CASCADE
        ON DELETE CASCADE,

    FOREIGN KEY (event_id)
        REFERENCES event(event_id)
        ON UPDATE CASCADE
        ON DELETE CASCADE,

    FOREIGN KEY (category_id)
        REFERENCES category(category_id)
        ON UPDATE CASCADE
        ON DELETE CASCADE
);


CREATE TABLE payment (
    payment_id         INT AUTO_INCREMENT PRIMARY KEY,
    card_name          VARCHAR(100)        NOT NULL,
    card_number        VARCHAR(16)         NOT NULL,
    payment_date       DATETIME            NOT NULL,
    reservation_id     INT UNIQUE          NOT NULL,

    FOREIGN KEY (reservation_id)
        REFERENCES reservation(reservation_id)
        ON UPDATE CASCADE
        ON DELETE CASCADE
);
