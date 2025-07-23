# BlablaCar App Testing Guide

## Setup
1. Run the SQL scripts in order:
    - `src/ressources/Blablacar.sql` (creates database structure)
    - `src/ressources/fake_data.sql` (populates test data)
2. Configure `.env` file with database credentials
3. Start the application

## Test User Credentials
- **Primary user**: `test_user1` / `password123`
- **Secondary user**: `test_user2` / `password456`

## Testing Scenarios

### 1. Authentication
```
Login: test_user1 / password123
```

### 2. Trip Management
**View existing trips:**
- Menu → "2: Mes trajets" → "2: Trajets à venir"
- Menu → "2: Mes trajets" → "3: Trajets passés"

**Create new trip:**
- Menu → "2: Mes trajets" → "1: Nouveau trajet"
- Enter: Paris → Lyon, 2025-08-30, 10:00, 3 places, 30€

### 3. Trip Search & Booking
- Menu → "1: Rechercher un trajet"
- Choose: Paris (1) → Lyon (2), date: 2025-08-15
- Book the available trip from alice_martin or bob_durand

### 4. Reservations
**View future reservations:**
- Menu → "3: Mes réservations" → "1: Réservations à venir"

**View past reservations:**
- Menu → "3: Mes réservations" → "2: Réservations passées"
- Select trip to Toulouse (not yet rated)
- Rate the driver (test_user2)

### 5. Rating System
**Rate passengers (as driver):**
- Menu → "2: Mes trajets" → "3: Trajets passés"
- Select Paris → Nice trip
- Choose "1: Noter les passagers"
- Rate test_user2 and bob_durand (alice_martin already rated)

### 6. Messaging
**Send message:**
- Menu → "4: Mes messages" → "1: Rédiger message"
- Recipient: `test_user2`
- Send any message

**View messages:**
- Menu → "4: Mes messages" → "2: Afficher messages reçus"
- Menu → "4: Mes messages" → "3: Afficher messages envoyés"

### 7. Account Management
- Menu → "5: Mon compte"
- Test updating vehicle or username
- Test password change

## Optional: Switch to test_user2
For complete messaging testing:
1. Logout and login as `test_user2`
2. Check received messages
3. Reply to test_user1

## Expected Data in System
- **Cities**: Paris, Lyon, Marseille, Toulouse, Nice, Nantes, Bordeaux, Lille, Strasbourg, Montpellier
- **Past trips**: test_user1 drove to Lyon, Marseille, Nice (some with unrated passengers)
- **Future trips**: Multiple available trips on 2025-08-15 and later dates
- **Existing reservations**: test_user1 has bookings with test_user2
- **Messages**: Conversation history between test users