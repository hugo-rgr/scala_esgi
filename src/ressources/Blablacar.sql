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

-- Create Trip table
CREATE TABLE Trip (
                      trip_id INT PRIMARY KEY AUTO_INCREMENT,
                      trip_departure_city_id INT NOT NULL,
                      trip_arrival_city_id INT NOT NULL,
                      trip_date DATETIME NOT NULL,
                      trip_driver_user_id INT NOT NULL,
                      trip_passengers_seats_number INT NOT NULL,
                      trip_price FLOAT NOT NULL,
                      FOREIGN KEY (trip_departure_city_id) REFERENCES City(city_id),
                      FOREIGN KEY (trip_arrival_city_id) REFERENCES City(city_id),
                      FOREIGN KEY (trip_driver_user_id) REFERENCES User(user_id)
);

-- Create Reservation table
CREATE TABLE Reservation (
                             res_id INT PRIMARY KEY AUTO_INCREMENT,
                             trip_id INT NOT NULL,
                             res_passenger_user_id INT NOT NULL,
                             res_is_canceled BIT NOT NULL DEFAULT 0,
                             res_passenger_trip_price FLOAT NOT NULL,
                             res_date DATETIME NOT NULL,
                             res_is_rated BIT NULL DEFAULT 0,
                             FOREIGN KEY (trip_id) REFERENCES Trip(trip_id),
                             FOREIGN KEY (res_passenger_user_id) REFERENCES User(user_id)
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