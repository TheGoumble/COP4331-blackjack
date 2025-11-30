# Blackjack — COP4331 Project

A **JavaFX-based multiplayer Blackjack game** with online matchmaking built for **COP4331**. Play against dealer AI solo, or create games and challenge friends anywhere via the internet.

## Features

### Game Modes
- **Single-player Blackjack** against dealer AI
- **Multiplayer (Online)** — Create or join games with 1-4 players globally
- **Tutorial Mode** to learn the rules
- **Balance tracking** with betting system

### Multiplayer Highlights
- **Online Matchmaking API** — Discover games globally without port forwarding
- **6-Character Game Codes** — Easy sharing for internet play
- **P2P Gameplay** — Direct connections for low-latency game sessions
- **Auto-Discovery** — Games appear automatically in lobby
- **Real-time Sync** — All players see game state updates instantly

### Gameplay Features
- Classic Blackjack rules (dealer hits on 16 or less, stands on 17+)
- Real-time card dealing
- Hit and Stand actions
- Automatic win/loss/push detection
- Balance management with betting
- Multi-player synchronization

### Technical Features
- **Hybrid Architecture**: API for matchmaking + P2P for gameplay
- **Cloud Deployment**: Google Cloud Run hosted API
- **MVC Architecture** with JavaFX GUI
- **Command Pattern** for networked game actions
- **Observer Pattern** for real-time updates

---

## Project Structure

```
COP4331-blackjack/
│
├── readme.md
├── INTERNET_PLAY_GUIDE.md
├── secrets.properties.example       # API URL template (copy to secrets.properties)
│
├── blackjack-api/                   # Node.js matchmaking API
│   ├── server.js                    # Express server
│   ├── package.json
│   ├── Dockerfile                   # Cloud deployment
│   └── DEPLOYMENT.md                # Google Cloud Run guide
│
└── BlackjackSingleplayer/
    ├── pom.xml                      # Maven configuration
    └── src/main/java/
        ├── app/
        │   ├── MainApp.java         # Application entry point
        │   └── SceneRouter.java     # Navigation + cleanup
        ├── controller/
        │   ├── DealerTableController.java
        │   ├── MenuController.java
        │   ├── GameLobbyController.java       # Multiplayer lobby
        │   ├── MultiplayerTableController.java
        │   └── TutorialController.java
        ├── model/
        │   ├── GameEngine.java      # Multiplayer game state
        │   ├── Card.java, Deck.java, Hand.java
        │   └── [other game models]
        ├── network/
        │   ├── ApiClient.java       # HTTP matchmaking client
        │   ├── ApiConfig.java       # API configuration
        │   ├── DesignatedHost.java  # P2P game host
        │   ├── BlackjackPeer.java   # P2P client
        │   └── GameUpdateMessage.java
        ├── command/
        │   ├── Command.java         # Command pattern interface
        │   ├── HitCommand.java
        │   ├── StandCommand.java
        │   └── SetBetCommand.java
        ├── observer/
        │   ├── CreateGameObserver.java
        │   └── JoinGameObserver.java
        └── view/
            ├── DealerTableView.java
            ├── GameLobbyView.java   # Multiplayer browser
            ├── MultiplayerTableView.java
            └── [other views]
```

---

## Requirements
- **Java JDK 24+**
- **Apache Maven 3.9+**
- **JavaFX 24.0.2** (auto-downloaded via Maven)
- **Git**

Verify Java:
```powershell
java -version
```

---

## Quick Start

### 1. Clone the Repository
```powershell
git clone https://github.com/TheGoumble/COP4331-blackjack.git
cd COP4331-blackjack
```

### 2. Setup Secrets File
```powershell
cd BlackjackSingleplayer
Copy-Item secrets.properties.example secrets.properties
```

The `secrets.properties` file contains the API URL. **Do not commit this file.**

### 3. Run the Game
```powershell
mvn clean javafx:run
```

The game launches with the main menu.

---

## Using the Application

### Main Menu
- **Create Blackjack Game** — Start single-player or host multiplayer
- **Join Game** — Browse/join multiplayer games
- **Tutorial** — Learn Blackjack rules
- **Close Application** — Exit

### Single-Player Mode
1. Click **Create Blackjack Game** → **Single-Player**
2. Place bets, hit/stand, play against dealer AI
3. Balance starts at $10,000

### Multiplayer Mode

#### Creating a Game (Host)
1. Click **Create Blackjack Game** → **Multiplayer**
2. A **6-character game code** appears (e.g., `ABC123`)
3. Share this code with friends anywhere in the world
4. Wait for players to join (1-4 players max)
5. Click **Start Round** when ready

#### Joining a Game (Client)
1. Click **Join Game**
2. Games appear automatically in the list
3. **Option A**: Click a game → **Join Selected Game**
4. **Option B**: Enter 6-character code → **Direct Connect**
5. Wait for host to start the round

### Gameplay (Multiplayer)
1. Host clicks **Start Round**
2. All players place bets and play simultaneously
3. Hit/Stand actions sync across all clients
4. Dealer plays when all players finish
5. Results shown for everyone
6. Host starts next round

---

## Architecture

### Hybrid Multiplayer Design
- **API (Express.js)**: Matchmaking registry only
  - Game registration/discovery
  - Player count tracking
  - Auto-cleanup on disconnect
  
- **P2P (Java TCP)**: Actual gameplay
  - Direct socket connections
  - Low-latency game state sync
  - Command validation by host

### Key Patterns
- **MVC**: Model-View-Controller separation
- **Command Pattern**: Networked player actions
- **Observer Pattern**: Real-time UI updates
- **Singleton**: API client, configuration

---

## Multiplayer Technical Details

## Multiplayer Technical Details

### Network Flow
1. **Host creates game** → Registers with API (returns 6-char code)
2. **API lists games** → Clients see available games in lobby
3. **Client joins** → API provides host IP:port
4. **P2P connection** → Client connects directly to host via TCP
5. **Gameplay** → All commands validated by host, synced to clients
6. **Cleanup** → Window close unregisters game from API

### Components
- **ApiClient.java**: HTTP client for matchmaking API
- **ApiConfig.java**: Environment configuration loader
- **DesignatedHost.java**: Authoritative P2P game server
- **BlackjackPeer.java**: P2P client with local state cache
- **GameEngine.java**: Server-side game logic
- **Command Pattern**: HitCommand, StandCommand, SetBetCommand

---

## Game Rules

### Card Values
- **Number cards (2-10)**: Face value
- **Face cards (J, Q, K)**: 10 points
- **Ace**: 1 or 11 (automatically calculated for best hand)

### Win Conditions
- **Blackjack**: 21 with first 2 cards (pays 3:2)
- **Win**: Higher total than dealer without busting
- **Push**: Tie with dealer (bet returned)
- **Lose**: Lower total than dealer or bust (over 21)

### Dealer Rules
- Dealer must hit on 16 or less
- Dealer must stand on 17 or more

---

## Troubleshooting

### `mvn` not recognized
Add Maven to PATH and restart PowerShell. See installation guide above.

### JavaFX runtime components are missing
```powershell
mvn clean install
mvn javafx:run
```

### Application won't launch
Ensure you're in the correct directory:
```powershell
cd BlackjackSingleplayer
mvn clean javafx:run
```

### Connection issues in multiplayer
- Verify `secrets.properties` exists with API URL
- See `INTERNET_PLAY_GUIDE.md` for detailed troubleshooting

### "No games found"
- Click **Refresh** in the lobby
- Ensure host has created a game

---

## Development

### Building
```powershell
cd BlackjackSingleplayer
mvn clean compile
```

### Testing
```powershell
mvn test
```

### Deploying API
See `blackjack-api/DEPLOYMENT.md` for Google Cloud Run deployment guide.

---

## Credits
**Authors**: Luca Lombardo, Olivia Strandberg, Javier Vargas, Bridjet Walker  
**Course**: COP4331 — Processes of Object-Oriented Programming  
**Technology**: JavaFX 24.0.2, Java 24, Maven, Node.js, Express, Google Cloud Run

---

## License
This project is for educational purposes as part of COP4331 final project.
