# Blackjack Multiplayer API

Express.js matchmaking server with dynamic player tracking for P2P Blackjack games.

## Features

- Game registration with 6-character codes
- **Dynamic player count tracking** (1-4 players)
- **Auto-hide full games** from lobby
- **Auto-show when slot opens** (player leaves)
- Automatic cleanup of stale games
- P2P gameplay (API only handles discovery)

## Setup

1. Install dependencies:
```bash
npm install
```

2. Run the server:
```bash
npm start
```

For development with auto-reload:
```bash
npm run dev
```

## API Endpoints

### POST `/api/games/register`
Register a new game (host only).

**Request:**
```json
{
  "gameCode": "ABC123",
  "hostId": "player1",
  "address": "192.168.1.100",
  "port": 54321
}
```

**Response:**
```json
{
  "success": true,
  "gameCode": "ABC123",
  "sessionId": "game_1234567890"
}
```

### GET `/api/games/list`
List all available games (only shows games with < 4 players).

**Response:**
```json
{
  "success": true,
  "games": [
    {
      "gameCode": "ABC123",
      "sessionId": "game_1234567890",
      "hostId": "player1",
      "address": "192.168.1.100",
      "port": 54321,
      "playerCount": 2,
      "maxPlayers": 4,
      "createdAt": 1234567890
    }
  ]
}
```

### GET `/api/games/:gameCode`
Get connection info for a specific game.

**Response:**
```json
{
  "success": true,
  "game": {
    "gameCode": "ABC123",
    "sessionId": "game_1234567890",
    "hostId": "player1",
    "address": "192.168.1.100",
    "port": 54321,
    "playerCount": 2,
    "maxPlayers": 4,
    "connectionString": "192.168.1.100:54321"
  }
}
```

### POST `/api/games/:gameCode/join`
Notify API that a player joined (increments count).

**Response:**
```json
{
  "success": true,
  "playerCount": 3,
  "maxPlayers": 4,
  "isFull": false
}
```

### POST `/api/games/:gameCode/leave`
Notify API that a player left (decrements count).

**Response:**
```json
{
  "success": true,
  "playerCount": 2,
  "maxPlayers": 4
}
```

If last player leaves:
```json
{
  "success": true,
  "removed": true
}
```

### DELETE `/api/games/:gameCode`
Unregister a game (host closes).

### GET `/health`
Health check endpoint.

**Response:**
```json
{
  "status": "ok",
  "activeGames": 3,
  "uptime": 3600.5
}
```

## Game Lifecycle

1. **Host creates game** → Registers with API (playerCount=1)
2. **Player 1 joins** → API increments count (playerCount=2)
3. **Player 2 joins** → API increments count (playerCount=3)
4. **Player 3 joins** → API increments count (playerCount=4, game hidden from list)
5. **Player 2 leaves** → API decrements count (playerCount=3, game re-appears in list)
6. **Host closes** → API removes game entirely

## Architecture

```
┌─────────────┐
│   API       │  Registry-only matchmaking
│  (Express)  │  - Tracks player counts
└──────┬──────┘  - Shows/hides full games
       │
       │ HTTP (discovery only)
       │
┌──────┴──────────────────┐
│                         │
▼                         ▼
┌──────────┐         ┌──────────┐
│  Host    │◄───────►│  Client  │  P2P Direct Connection
│  (TCP)   │  Game   │  (TCP)   │  - Low latency
└──────────┘  Data   └──────────┘  - No server bottleneck
```

## WebSocket Events

### Client -> Server

**Register:**
```json
{
  "type": "register",
  "userId": "player123"
}
```

**Game Action:**
```json
{
  "type": "gameAction",
  "gameCode": "ABC123",
  "userId": "player123",
  "action": "HIT",
  "value": null
}
```

### Server -> Client

**Game Update:**
```json
{
  "type": "gameUpdate",
  "action": "HIT",
  "userId": "player123",
  "value": null,
  "timestamp": 1234567890
}
```

**Player Joined:**
```json
{
  "type": "playerJoined",
  "userId": "player456",
  "playerCount": 2
}
```

**Player Left:**
```json
{
  "type": "playerLeft",
  "userId": "player456",
  "playerCount": 1
}
```

## Deployment

### Google Cloud Run

1. Install Google Cloud SDK
2. Build and deploy:
```bash
gcloud run deploy blackjack-api --source . --region us-central1
```

### Environment Variables

- `PORT` - Server port (default: 3000)

## Testing

**Start the server:**
```powershell
npm start
```

**Test with PowerShell:**
```powershell
# Register a game
irm http://localhost:3000/api/games/register -Method Post -ContentType "application/json" -Body '{"gameCode":"TEST01","hostId":"player1","address":"192.168.1.100","port":54321}'

# List games (should show 1 player)
irm http://localhost:3000/api/games/list

# Player joins
irm http://localhost:3000/api/games/TEST01/join -Method Post

# List games (should show 2 players)
irm http://localhost:3000/api/games/list

# Player leaves
irm http://localhost:3000/api/games/TEST01/leave -Method Post

# List games (should show 1 player)
irm http://localhost:3000/api/games/list
```
