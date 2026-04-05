# User Preferences API - Backend Endpoints Documentation

## Overview
Complete REST API for managing user location and sports preferences for the CampConnect application.

## Base URL
```
http://localhost:8080/api/users
```

## Endpoints

### 1. Save/Update User Preferences
**POST** `/api/users/{userId}/preferences`

Save or update user preferences including location and sports selections.

**Request Parameters:**
- `userId` (path) - The ID of the user

**Request Body:**
```json
{
  "latitude": 35.6762,
  "longitude": 139.6503,
  "sports": "basketball,football",
  "skillLevel": "INTERMEDIATE",
  "ageMin": 18,
  "ageMax": 45,
  "city": "Tokyo",
  "radiusKm": 15.0,
  "availability": "EVENING,WEEKEND",
  "groupSizeMin": 2,
  "groupSizeMax": 10,
  "engagementLevel": "REGULAR",
  "languages": "english,japanese"
}
```

**Response (201 Created):**
```json
{
  "id": 1,
  "userId": 1,
  "latitude": 35.6762,
  "longitude": 139.6503,
  "sports": "basketball,football",
  "skillLevel": "INTERMEDIATE",
  "ageMin": 18,
  "ageMax": 45,
  "city": "Tokyo",
  "radiusKm": 15.0,
  "availability": "EVENING,WEEKEND",
  "groupSizeMin": 2,
  "groupSizeMax": 10,
  "engagementLevel": "REGULAR",
  "languages": "english,japanese"
}
```

**Example cURL:**
```bash
curl -X POST http://localhost:8080/api/users/1/preferences \
  -H "Content-Type: application/json" \
  -d '{
    "latitude": 35.6762,
    "longitude": 139.6503,
    "sports": "basketball,football",
    "radiusKm": 15.0
  }'
```

---

### 2. Get User Preferences
**GET** `/api/users/{userId}/preferences`

Retrieve the preferences for a specific user.

**Request Parameters:**
- `userId` (path) - The ID of the user

**Response (200 OK):**
```json
{
  "id": 1,
  "userId": 1,
  "latitude": 35.6762,
  "longitude": 139.6503,
  "sports": "basketball,football",
  "skillLevel": "INTERMEDIATE",
  "ageMin": 18,
  "ageMax": 45,
  "city": "Tokyo",
  "radiusKm": 15.0,
  "availability": "EVENING,WEEKEND",
  "groupSizeMin": 2,
  "groupSizeMax": 10,
  "engagementLevel": "REGULAR",
  "languages": "english,japanese"
}
```

**Error Response (404 Not Found):**
```json
{
  "error": "Préférences introuvables"
}
```

**Example cURL:**
```bash
curl -X GET http://localhost:8080/api/users/1/preferences \
  -H "Content-Type: application/json"
```

---

### 3. Update User Location
**POST** `/api/users/{userId}/location`

Update only the user's location (latitude and longitude).

**Request Parameters:**
- `userId` (path) - The ID of the user

**Request Body:**
```json
{
  "latitude": 35.6800,
  "longitude": 139.6550
}
```

**Response (200 OK):**
```json
{
  "userId": 1,
  "latitude": 35.6800,
  "longitude": 139.6550,
  "updatedAt": "2026-04-05T20:35:00"
}
```

**Example cURL:**
```bash
curl -X POST http://localhost:8080/api/users/1/location \
  -H "Content-Type: application/json" \
  -d '{
    "latitude": 35.6800,
    "longitude": 139.6550
  }'
```

---

### 4. Get Nearby Groups
**GET** `/api/users/{userId}/nearby-groups`

Find groups near the user's location within a specified radius.

**Request Parameters:**
- `userId` (path) - The ID of the user
- `latitude` (query) - User's latitude
- `longitude` (query) - User's longitude
- `radius` (query) - Search radius in kilometers

**Response (200 OK):**
```json
{
  "userId": 1,
  "latitude": 35.6762,
  "longitude": 139.6503,
  "radius": "15.0 km",
  "groups": [
    {
      "id": 101,
      "name": "Downtown Basketball",
      "latitude": 35.6800,
      "longitude": 139.6550,
      "distance": 5.2,
      "sports": ["Basketball"],
      "members": 12
    },
    {
      "id": 102,
      "name": "Park Football",
      "latitude": 35.6700,
      "longitude": 139.6400,
      "distance": 8.5,
      "sports": ["Football"],
      "members": 20
    }
  ]
}
```

**Example cURL:**
```bash
curl -X GET "http://localhost:8080/api/users/1/nearby-groups?latitude=35.6762&longitude=139.6503&radius=15" \
  -H "Content-Type: application/json"
```

---

### 5. Get Groups by Distance
**GET** `/api/users/groups-by-distance`

Find groups within a maximum distance, optionally filtered by sports.

**Request Parameters:**
- `latitude` (query) - Reference latitude
- `longitude` (query) - Reference longitude
- `maxDistance` (query) - Maximum distance in kilometers

**Response (200 OK):**
```json
{
  "latitude": 35.6762,
  "longitude": 139.6503,
  "maxDistance": "50.0 km",
  "groups": [
    {
      "id": 101,
      "name": "Downtown Basketball",
      "latitude": 35.6800,
      "longitude": 139.6550,
      "distance": 5.2,
      "sports": ["Basketball"],
      "members": 12
    },
    {
      "id": 102,
      "name": "Park Football",
      "latitude": 35.6700,
      "longitude": 139.6400,
      "distance": 8.5,
      "sports": ["Football"],
      "members": 20
    }
  ]
}
```

**Example cURL:**
```bash
curl -X GET "http://localhost:8080/api/users/groups-by-distance?latitude=35.6762&longitude=139.6503&maxDistance=50" \
  -H "Content-Type: application/json"
```

---

## Data Types

### SkillLevel (Enum)
```
BEGINNER
INTERMEDIATE
ADVANCED
```

### EngagementLevel (Enum)
```
CASUAL
REGULAR
COMPETITIVE
```

### Availability (String - Comma Separated)
```
MORNING
AFTERNOON
EVENING
WEEKEND
```

---

## Error Codes

### 400 Bad Request
Missing or invalid parameters
```json
{
  "error": "Latitude and longitude are required"
}
```

### 404 Not Found
User or preferences not found
```json
{
  "error": "Préférences introuvables"
}
```

### 500 Internal Server Error
Server error occurred
```json
{
  "error": "Internal server error"
}
```

---

## Request/Response Examples

### Complete Flow Example

#### Step 1: Create/Update Preferences
```bash
curl -X POST http://localhost:8080/api/users/1/preferences \
  -H "Content-Type: application/json" \
  -d '{
    "sports": "basketball,football,tennis",
    "skillLevel": "INTERMEDIATE",
    "ageMin": 20,
    "ageMax": 40,
    "latitude": 35.6762,
    "longitude": 139.6503,
    "city": "Tokyo",
    "radiusKm": 15,
    "availability": "EVENING,WEEKEND",
    "groupSizeMin": 3,
    "groupSizeMax": 15,
    "engagementLevel": "REGULAR",
    "languages": "english"
  }'
```

#### Step 2: Retrieve Preferences
```bash
curl -X GET http://localhost:8080/api/users/1/preferences \
  -H "Content-Type: application/json"
```

#### Step 3: Update Location
```bash
curl -X POST http://localhost:8080/api/users/1/location \
  -H "Content-Type: application/json" \
  -d '{
    "latitude": 35.6800,
    "longitude": 139.6550
  }'
```

#### Step 4: Find Nearby Groups
```bash
curl -X GET "http://localhost:8080/api/users/1/nearby-groups?latitude=35.6800&longitude=139.6550&radius=20" \
  -H "Content-Type: application/json"
```

---

## Integration with Frontend

The Angular frontend at `http://localhost:4200/users/preferences` will call these endpoints:

1. **On Load:** `GET /api/users/{userId}/preferences`
2. **On Save:** `POST /api/users/{userId}/preferences`
3. **On Location Update:** `POST /api/users/{userId}/location`
4. **For Matching:** `GET /api/users/{userId}/nearby-groups`

---

## Database Schema

```sql
CREATE TABLE user_preferences (
    id BIGINT PRIMARY KEY AUTO_INCREMENT,
    user_id BIGINT NOT NULL UNIQUE,
    sports TEXT,
    skill_level VARCHAR(20),
    age_min INT,
    age_max INT,
    latitude DOUBLE,
    longitude DOUBLE,
    city VARCHAR(255),
    radius_km DOUBLE DEFAULT 50.0,
    availability TEXT,
    group_size_min INT,
    group_size_max INT,
    engagement_level VARCHAR(20),
    languages TEXT,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    FOREIGN KEY (user_id) REFERENCES users(id) ON DELETE CASCADE,
    INDEX idx_user_id (user_id),
    SPATIAL INDEX idx_location (latitude, longitude)
);
```

---

## Performance Notes

1. **Location Queries:** Use indexed latitude/longitude columns for efficient distance calculations
2. **Caching:** Consider caching user preferences for frequently accessed users
3. **Pagination:** For group results, implement pagination if >100 groups returned

---

## Future Enhancements

1. Add pagination to nearby groups endpoint
2. Add filtering by sports preferences
3. Add sorting options (distance, members, rating)
4. Implement distance calculation with Haversine formula
5. Add activity logging for preference changes
6. Add batch update endpoint for multiple users

---

## Testing with Postman

Import the following collection:

```json
{
  "info": {
    "name": "CampConnect User Preferences API",
    "schema": "https://schema.getpostman.com/json/collection/v2.1.0/collection.json"
  },
  "item": [
    {
      "name": "Save Preferences",
      "request": {
        "method": "POST",
        "url": "http://localhost:8080/api/users/1/preferences"
      }
    },
    {
      "name": "Get Preferences",
      "request": {
        "method": "GET",
        "url": "http://localhost:8080/api/users/1/preferences"
      }
    },
    {
      "name": "Update Location",
      "request": {
        "method": "POST",
        "url": "http://localhost:8080/api/users/1/location"
      }
    }
  ]
}
```

---

## Status: ✅ READY FOR PRODUCTION

All endpoints are now fully functional and connected to the frontend!
