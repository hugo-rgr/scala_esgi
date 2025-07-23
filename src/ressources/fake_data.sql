-- BlablaCar Test Data - Comprehensive test dataset for two users
-- First, make sure to use the correct database
USE BLABLACAR;

-- Clear existing data (optional, uncomment if needed)
-- DELETE FROM Message;
-- DELETE FROM Reservation;
-- DELETE FROM Trip;
-- DELETE FROM User;
-- DELETE FROM City;

-- Insert Cities (enough for varied testing)
INSERT INTO City (city_name) VALUES
                                 ('Paris'),
                                 ('Lyon'),
                                 ('Marseille'),
                                 ('Toulouse'),
                                 ('Nice'),
                                 ('Nantes'),
                                 ('Bordeaux'),
                                 ('Lille'),
                                 ('Strasbourg'),
                                 ('Montpellier');

-- Insert Test Users
INSERT INTO User (user_name, user_hash_pwd, user_vehicule, user_note, user_nb_notes) VALUES
-- Main test users
('test_user1', 'password123', 'Renault Clio Bleue', 4.2, 17),  -- Mainly driver (4.2/5 with 17 ratings)
('test_user2', 'password456', 'Peugeot 308 Grise', 4.6, 12),   -- Mainly passenger (4.6/5 with 12 ratings)

-- Additional users for more realistic testing
('alice_martin', 'alice123', 'Citroën C3', 3.8, 25),    -- 3.8/5 with 25 ratings
('bob_durand', 'bob456', 'Volkswagen Golf', 4.5, 30),   -- 4.5/5 with 30 ratings
('claire_bernard', 'claire789', 'Ford Fiesta', 3.9, 15); -- 3.9/5 with 15 ratings

-- Insert Trips for comprehensive testing

-- === PAST TRIPS (for rating testing) ===

-- 1. Past trip: test_user1 (driver) -> test_user2 was passenger (NOT YET RATED by passenger)
INSERT INTO Trip (trip_departure_city_id, trip_arrival_city_id, trip_date, trip_driver_user_id, trip_passengers_seats_number, trip_price) VALUES
    (1, 2, '2025-07-15 09:00:00', 1, 0, 25.50);  -- Paris to Lyon, driver: test_user1
SET @past_trip_1 = LAST_INSERT_ID();

-- 2. Past trip: test_user1 (driver) -> test_user2 was passenger (ALREADY RATED by passenger)
INSERT INTO Trip (trip_departure_city_id, trip_arrival_city_id, trip_date, trip_driver_user_id, trip_passengers_seats_number, trip_price) VALUES
    (2, 3, '2025-07-10 14:30:00', 1, 1, 35.00);  -- Lyon to Marseille, driver: test_user1
SET @past_trip_2 = LAST_INSERT_ID();

-- 3. Past trip: test_user2 (driver) -> test_user1 was passenger (NOT YET RATED by passenger)
INSERT INTO Trip (trip_departure_city_id, trip_arrival_city_id, trip_date, trip_driver_user_id, trip_passengers_seats_number, trip_price) VALUES
    (3, 4, '2025-07-12 16:00:00', 2, 2, 40.00);  -- Marseille to Toulouse, driver: test_user2
SET @past_trip_3 = LAST_INSERT_ID();

-- 4. Past trip: test_user1 (driver) with multiple passengers (for testing driver rating passengers)
INSERT INTO Trip (trip_departure_city_id, trip_arrival_city_id, trip_date, trip_driver_user_id, trip_passengers_seats_number, trip_price) VALUES
    (1, 5, '2025-07-08 08:00:00', 1, 0, 45.75);  -- Paris to Nice, driver: test_user1
SET @past_trip_4 = LAST_INSERT_ID();

-- === FUTURE TRIPS (for booking and reservation testing) ===

-- 5. Future trip: test_user1 (driver) - available for test_user2 to book
INSERT INTO Trip (trip_departure_city_id, trip_arrival_city_id, trip_date, trip_driver_user_id, trip_passengers_seats_number, trip_price) VALUES
                                                                                                                                              (1, 2, '2025-08-15 10:00:00', 1, 3, 28.00),  -- Paris to Lyon, driver: test_user1
                                                                                                                                              (2, 1, '2025-08-16 18:30:00', 1, 2, 28.00),  -- Lyon to Paris, driver: test_user1
                                                                                                                                              (1, 6, '2025-08-20 07:45:00', 1, 4, 38.50),  -- Paris to Nantes, driver: test_user1
                                                                                                                                              (4, 5, '2025-08-25 15:20:00', 1, 3, 42.00);  -- Toulouse to Nice, driver: test_user1

-- 6. Future trip: test_user2 (driver) - available for test_user1 to book
INSERT INTO Trip (trip_departure_city_id, trip_arrival_city_id, trip_date, trip_driver_user_id, trip_passengers_seats_number, trip_price) VALUES
                                                                                                                                              (6, 7, '2025-08-18 09:30:00', 2, 3, 55.00),  -- Nantes to Bordeaux, driver: test_user2
                                                                                                                                              (7, 8, '2025-08-22 13:15:00', 2, 2, 48.75);  -- Bordeaux to Lille, driver: test_user2

-- 7. Additional future trips by other users (for search testing)
INSERT INTO Trip (trip_departure_city_id, trip_arrival_city_id, trip_date, trip_driver_user_id, trip_passengers_seats_number, trip_price) VALUES
                                                                                                                                              (1, 2, '2025-08-15 08:00:00', 3, 2, 30.00),  -- Paris to Lyon, driver: alice_martin (same route as test_user1)
                                                                                                                                              (1, 2, '2025-08-15 12:00:00', 4, 4, 26.00),  -- Paris to Lyon, driver: bob_durand (same route, different time)
                                                                                                                                              (2, 3, '2025-08-17 11:00:00', 5, 3, 38.00);  -- Lyon to Marseille, driver: claire_bernard

-- === RESERVATIONS FOR TESTING ===

-- Past reservations (completed trips)
INSERT INTO Reservation (trip_id, res_passenger_user_id, res_is_canceled, res_passenger_trip_price, res_date, res_is_rated) VALUES
-- Trip 1: test_user2 reserved test_user1's trip (NOT YET RATED - can test passenger rating driver)
(@past_trip_1, 2, 0, 25.50, '2025-07-10 14:20:00', 0),

-- Trip 2: test_user2 reserved test_user1's trip (ALREADY RATED - test that re-rating is blocked)
(@past_trip_2, 2, 0, 35.00, '2025-07-05 11:30:00', 1),

-- Trip 3: test_user1 reserved test_user2's trip (NOT YET RATED - can test passenger rating driver)
(@past_trip_3, 1, 0, 40.00, '2025-07-11 13:45:00', 0),

-- Trip 4: Multiple passengers for test_user1's trip (test driver rating passengers)
(@past_trip_4, 2, 0, 45.75, '2025-07-05 16:30:00', 0),  -- test_user2 (NOT YET RATED by driver)
(@past_trip_4, 3, 0, 45.75, '2025-07-06 09:15:00', 1),  -- alice_martin (ALREADY RATED by driver)
(@past_trip_4, 4, 0, 45.75, '2025-07-07 12:40:00', 0);  -- bob_durand (NOT YET RATED by driver)

-- Future reservations (upcoming trips)
-- Get the IDs of future trips for reservations
SET @future_trip_1 = (SELECT trip_id FROM Trip WHERE trip_date = '2025-08-15 10:00:00' AND trip_driver_user_id = 1);
SET @future_trip_2 = (SELECT trip_id FROM Trip WHERE trip_date = '2025-08-18 09:30:00' AND trip_driver_user_id = 2);

INSERT INTO Reservation (trip_id, res_passenger_user_id, res_is_canceled, res_passenger_trip_price, res_date, res_is_rated) VALUES
-- Future reservations (test viewing upcoming reservations)
(@future_trip_1, 2, 0, 28.00, '2025-08-10 10:15:00', 0),  -- test_user2 reserved test_user1's future trip
(@future_trip_2, 1, 0, 55.00, '2025-08-12 15:20:00', 0);  -- test_user1 reserved test_user2's future trip

-- Update trip passenger counts after reservations
UPDATE Trip SET trip_passengers_seats_number = trip_passengers_seats_number - 1 WHERE trip_id = @future_trip_1;
UPDATE Trip SET trip_passengers_seats_number = trip_passengers_seats_number - 1 WHERE trip_id = @future_trip_2;

-- === MESSAGES FOR TESTING ===
INSERT INTO Message (message_content, sender_user_id, recipient_user_id, message_date) VALUES
-- Conversation between test users
('Salut ! J''ai réservé votre trajet Paris-Lyon du 15 août. À quelle heure exacte partons-nous ?', 2, 1, '2025-08-10 10:20:00'),
('Bonjour ! Le départ est prévu à 10h précises. Rendez-vous devant la gare Montparnasse, sortie 2.', 1, 2, '2025-08-10 11:45:00'),
('Parfait, merci pour l''info ! À bientôt', 2, 1, '2025-08-10 12:00:00'),

('Hello ! Concernant votre trajet Nantes-Bordeaux, avez-vous de la place pour un gros bagage ?', 1, 2, '2025-08-12 16:30:00'),
('Salut ! Oui pas de problème, j''ai un grand coffre. Bon voyage !', 2, 1, '2025-08-12 17:15:00'),

('Merci pour l''excellent trajet Paris-Nice la semaine dernière ! Conduite très sûre.', 2, 1, '2025-07-16 09:30:00'),
('Merci beaucoup pour votre message ! J''espère vous revoir bientôt sur mes trajets.', 1, 2, '2025-07-16 10:15:00'),

('Petite question : acceptez-vous les animaux de compagnie pour le prochain trajet ?', 3, 1, '2025-08-13 14:20:00'),
('Bonjour Alice ! Désolé mais je ne peux pas prendre d''animaux cette fois.', 1, 3, '2025-08-13 15:45:00'),

('Bonjour, y a-t-il un arrêt possible en cours de route ?', 4, 2, '2025-08-14 11:30:00'),
('Bonjour Bob ! On peut voir ça ensemble, envoyez-moi un message privé.', 2, 4, '2025-08-14 12:20:00');

