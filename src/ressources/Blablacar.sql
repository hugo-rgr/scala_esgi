CREATE DATABASE BLABLACAR;
USE BLABLACAR;
-- Create City table
CREATE TABLE City (
                      city_id INT PRIMARY KEY AUTO_INCREMENT,
                      city_name VARCHAR(50) NOT NULL
);

-- Create User table
CREATE TABLE User (
                      user_id INT PRIMARY KEY AUTO_INCREMENT,
                      user_name VARCHAR(255) NOT NULL,
                      user_hash_pwd VARCHAR(255) NOT NULL,
                      user_vehicule VARCHAR(30) NULL,
                      user_note DECIMAL NULL,
                      user_nb_notes INT NULL
);

-- Create Payment table (created before Reservation due to FK dependency)
CREATE TABLE Payment (
                         payment_id INT PRIMARY KEY AUTO_INCREMENT,
                         amount FLOAT NOT NULL
);

-- Create Reservation table
CREATE TABLE Reservation (
                             reservation_id INT PRIMARY KEY AUTO_INCREMENT,
                             passenger_user_id INT NOT NULL,
                             is_canceled BOOLEAN NOT NULL DEFAULT FALSE,
                             payment_id INT NOT NULL,
                             reservation_date DATETIME NOT NULL,
                             is_rated FLOAT NULL,
                             FOREIGN KEY (passenger_user_id) REFERENCES User(user_id),
                             FOREIGN KEY (payment_id) REFERENCES Payment(payment_id)
);

-- Create Trip table
CREATE TABLE Trip (
                      trip_id INT PRIMARY KEY AUTO_INCREMENT,
                      trip_departure_city_id INT NOT NULL,
                      trip_arrival_city_id INT NOT NULL,
                      trip_date DATETIME NOT NULL,
                      trip_driver_user_id INT NOT NULL,
                      trip_reservation_id INT NOT NULL,
                      trip_passengers_seats_number INT NOT NULL,
                      trip_price FLOAT NOT NULL,
                      FOREIGN KEY (trip_departure_city_id) REFERENCES City(city_id),
                      FOREIGN KEY (trip_arrival_city_id) REFERENCES City(city_id),
                      FOREIGN KEY (trip_driver_user_id) REFERENCES User(user_id),
                      FOREIGN KEY (trip_reservation_id) REFERENCES Reservation(reservation_id)
);

-- Create Message table
CREATE TABLE Message (
                         message_id INT PRIMARY KEY AUTO_INCREMENT,
                         message_content VARCHAR(255) NOT NULL,
                         sender_user_id INT NOT NULL,
                         recipient_user_id INT NOT NULL,
                         message_date DATETIME NOT NULL,
                         FOREIGN KEY (sender_user_id) REFERENCES User(user_id),
                         FOREIGN KEY (recipient_user_id) REFERENCES User(user_id)
);

-- Add the missing foreign key to Payment table (circular reference)
ALTER TABLE Payment
    ADD COLUMN reservation_id INT NOT NULL,
ADD FOREIGN KEY (reservation_id) REFERENCES Reservation(reservation_id);