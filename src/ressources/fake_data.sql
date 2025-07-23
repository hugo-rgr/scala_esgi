-- BlablaCar Test Data
-- First, make sure to use the correct database
USE BLABLACAR;

-- Insert Cities
INSERT INTO City (city_name) VALUES
                                 ('Paris'),
                                 ('Lyon'),
                                 ('Marseille'),
                                 ('Toulouse'),
                                 ('Nice'),
                                 ('Nantes'),
                                 ('Strasbourg'),
                                 ('Montpellier'),
                                 ('Bordeaux'),
                                 ('Lille'),
                                 ('Rennes'),
                                 ('Reims'),
                                 ('Le Havre'),
                                 ('Saint-Étienne'),
                                 ('Toulon');

-- Insert Users with varied profiles
INSERT INTO User (user_name, user_hash_pwd, user_vehicule, user_note, user_nb_notes) VALUES
                                                                                         ('alice_martin', 'password123', 'Renault Clio', 85, 17),
                                                                                         ('bob_durand', 'motdepasse456', 'Peugeot 308', 92, 25),
                                                                                         ('claire_bernard', 'secret789', 'Citroën C3', 78, 12),
                                                                                         ('david_petit', 'pass2023', 'Volkswagen Golf', 88, 31),
                                                                                         ('emma_rousseau', 'mypass321', 'Ford Fiesta', 95, 42),
                                                                                         ('florent_moreau', 'secure123', 'Opel Corsa', 82, 19),
                                                                                         ('sophie_laurent', 'password789', 'Renault Megane', 90, 28),
                                                                                         ('julien_simon', 'motdepasse2023', 'Peugeot 207', 76, 8),
                                                                                         ('marie_michel', 'secret456', 'Citroën C4', 93, 35),
                                                                                         ('pierre_leroy', 'mypassword', 'BMW Serie 1', 87, 22),
                                                                                         ('test_user', 'test123', 'Tesla Model 3', 0, 0),
                                                                                         ('demo_driver', 'demo456', 'Toyota Yaris', 89, 15);

-- Insert Trips (mix of past, current, and future trips)
-- Past trips (completed)
INSERT INTO Trip (trip_departure_city_id, trip_arrival_city_id, trip_date, trip_driver_user_id, trip_passengers_seats_number, trip_price) VALUES
                                                                                                                                              (1, 2, '2024-06-15 08:30:00', 1, 0, 25.50),  -- Paris to Lyon
                                                                                                                                              (2, 3, '2024-06-20 14:15:00', 2, 1, 35.00),  -- Lyon to Marseille
                                                                                                                                              (3, 4, '2024-07-01 09:45:00', 3, 0, 42.75),  -- Marseille to Toulouse
                                                                                                                                              (4, 5, '2024-07-10 16:20:00', 4, 2, 28.90),  -- Toulouse to Nice
                                                                                                                                              (1, 6, '2024-07-15 07:00:00', 5, 0, 38.50);  -- Paris to Nantes

-- Current/Future trips (available for booking)
INSERT INTO Trip (trip_departure_city_id, trip_arrival_city_id, trip_date, trip_driver_user_id, trip_passengers_seats_number, trip_price) VALUES
                                                                                                                                              (1, 2, '2025-08-01 09:00:00', 6, 3, 30.00),  -- Paris to Lyon
                                                                                                                                              (2, 1, '2025-08-01 18:30:00', 7, 2, 30.00),  -- Lyon to Paris
                                                                                                                                              (1, 3, '2025-08-02 08:15:00', 8, 4, 55.00),  -- Paris to Marseille
                                                                                                                                              (3, 1, '2025-08-02 19:45:00', 9, 1, 55.00),  -- Marseille to Paris
                                                                                                                                              (2, 4, '2025-08-03 10:30:00', 10, 2, 45.50), -- Lyon to Toulouse
                                                                                                                                              (4, 2, '2025-08-03 17:00:00', 1, 3, 45.50),  -- Toulouse to Lyon
                                                                                                                                              (1, 5, '2025-08-05 07:45:00', 2, 4, 65.75),  -- Paris to Nice
                                                                                                                                              (5, 1, '2025-08-05 20:15:00', 3, 2, 65.75),  -- Nice to Paris
                                                                                                                                              (6, 7, '2025-08-07 11:20:00', 4, 3, 52.25),  -- Nantes to Strasbourg
                                                                                                                                              (7, 6, '2025-08-07 16:40:00', 5, 1, 52.25),  -- Strasbourg to Nantes
                                                                                                                                              (1, 8, '2025-08-10 08:00:00', 11, 3, 48.00), -- Paris to Montpellier
                                                                                                                                              (8, 1, '2025-08-10 19:30:00', 12, 2, 48.00), -- Montpellier to Paris
                                                                                                                                              (2, 9, '2025-08-12 09:15:00', 6, 4, 58.50),  -- Lyon to Bordeaux
                                                                                                                                              (9, 2, '2025-08-12 18:45:00', 7, 3, 58.50),  -- Bordeaux to Lyon
                                                                                                                                              (1, 10, '2025-08-15 07:30:00', 8, 2, 62.00); -- Paris to Lille

-- Insert Reservations (some for past trips, some for future trips)
INSERT INTO Reservation (trip_id, res_passenger_user_id, res_is_canceled, res_passenger_trip_price, res_date, res_is_rated) VALUES
-- Past reservations (completed and rated)
(1, 3, 0, 25.50, '2024-06-10 14:20:00', 1),  -- Claire reserved Alice's trip
(1, 4, 0, 25.50, '2024-06-11 09:15:00', 1),  -- David reserved Alice's trip
(1, 5, 0, 25.50, '2024-06-12 16:45:00', 1),  -- Emma reserved Alice's trip
(2, 6, 0, 35.00, '2024-06-18 11:30:00', 1),  -- Florent reserved Bob's trip
(2, 7, 0, 35.00, '2024-06-19 08:45:00', 1),  -- Sophie reserved Bob's trip
(3, 8, 0, 42.75, '2024-06-28 13:20:00', 1),  -- Julien reserved Claire's trip
(3, 9, 0, 42.75, '2024-06-29 10:10:00', 1),  -- Marie reserved Claire's trip
(3, 10, 0, 42.75, '2024-06-30 15:35:00', 1), -- Pierre reserved Claire's trip

-- Future reservations (not yet rated)
(6, 11, 0, 30.00, '2025-07-25 14:30:00', 0),  -- test_user reserved Florent's trip
(7, 12, 0, 30.00, '2025-07-26 09:45:00', 0),  -- demo_driver reserved Sophie's trip
(8, 1, 0, 55.00, '2025-07-27 11:20:00', 0),   -- Alice reserved Julien's trip
(9, 2, 0, 55.00, '2025-07-28 16:15:00', 0),   -- Bob reserved Marie's trip
(10, 3, 0, 45.50, '2025-07-29 13:40:00', 0),  -- Claire reserved Pierre's trip
(10, 4, 0, 45.50, '2025-07-30 10:25:00', 0),  -- David reserved Pierre's trip
(11, 5, 0, 45.50, '2025-07-31 12:10:00', 0),  -- Emma reserved Alice's trip
(12, 6, 0, 65.75, '2025-08-01 15:50:00', 0),  -- Florent reserved Bob's trip
(12, 7, 0, 65.75, '2025-08-02 08:30:00', 0),  -- Sophie reserved Bob's trip
(13, 8, 0, 65.75, '2025-08-03 14:15:00', 0);  -- Julien reserved Claire's trip

-- Insert Messages between users
INSERT INTO Message (message_content, sender_user_id, recipient_user_id, message_date) VALUES
-- Messages related to trips and general communication
('Salut ! J''ai réservé votre trajet Paris-Lyon. À quelle heure exacte partons-nous ?', 3, 1, '2024-06-10 14:25:00'),
('Bonjour Claire ! Le départ est prévu à 8h30 précises. Rendez-vous devant la gare Montparnasse.', 1, 3, '2024-06-10 15:10:00'),
('Parfait, merci pour l''info ! À demain', 3, 1, '2024-06-10 15:15:00'),

('Bonsoir, pouvez-vous me confirmer le point de rendez-vous pour le trajet de demain ?', 6, 2, '2024-06-19 20:30:00'),
('Bonsoir ! Rendez-vous place Bellecour à Lyon, côté parking. Bon voyage !', 2, 6, '2024-06-19 21:15:00'),

('Merci pour le super trajet aujourd''hui ! Très ponctuel et conduite agréable.', 8, 3, '2024-07-01 18:45:00'),
('Merci beaucoup pour votre message ! Bon séjour à Toulouse', 3, 8, '2024-07-01 19:20:00'),

('Hello ! Est-ce possible d''ajouter un arrêt rapide en route ?', 11, 6, '2025-07-25 16:30:00'),
('Salut ! Malheureusement ce ne sera pas possible cette fois, désolé.', 6, 11, '2025-07-25 17:45:00'),
('Pas de souci, merci quand même !', 11, 6, '2025-07-25 18:00:00'),

('Avez-vous de la place pour un bagage supplémentaire ?', 12, 7, '2025-07-26 12:20:00'),
('Oui pas de problème, le coffre est assez grand.', 7, 12, '2025-07-26 13:10:00'),

('Bonjour, y a-t-il la climatisation dans votre véhicule ?', 1, 8, '2025-07-27 14:15:00'),
('Bonjour Alice ! Oui bien sûr, clim et musique disponibles.', 8, 1, '2025-07-27 15:30:00'),

('Salut ! Pouvons-nous partir 15 minutes plus tôt si possible ?', 2, 9, '2025-07-28 19:00:00'),
('Salut Bob ! Malheureusement l''horaire est fixe, désolée.', 9, 2, '2025-07-28 19:45:00'),

('Excellent trajet la semaine dernière ! Merci encore', 5, 1, '2024-06-22 10:30:00'),
('Avec plaisir Emma ! À bientôt sur les routes', 1, 5, '2024-06-22 11:15:00'),

('Petite question : acceptez-vous les animaux de compagnie ?', 4, 10, '2025-07-30 16:20:00'),
('Bonjour David ! Désolé mais pas d''animaux cette fois.', 10, 4, '2025-07-30 17:05:00'),

('Merci pour le trajet rapide et sécurisé hier !', 9, 3, '2024-07-02 09:15:00'),
('Merci Marie ! Bon week-end à Toulouse', 3, 9, '2024-07-02 10:30:00');