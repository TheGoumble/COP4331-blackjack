# Blackjack — COP4331 Project

This project is a JavaFX-based Blackjack game built for **COP4331**. It demonstrates:
- **JavaFX GUI** with interactive game interface
- **MVC Architecture** (Model-View-Controller)
- **Object-Oriented Design** with proper encapsulation
- **Game logic** for classic Blackjack rules

The application features a complete single-player Blackjack experience with betting, hit/stand actions, and dealer AI.

---

## Features

### Game Modes
- **Single-player Blackjack** against dealer AI
- **Tutorial Mode** to learn the rules
- **Balance tracking** across game sessions
- **Betting system** with configurable bet amounts

### Gameplay Features
- Classic Blackjack rules (dealer hits on 16 or less, stands on 17+)
- Real-time card dealing animations
- Hit and Stand player actions
- Automatic win/loss/push detection
- Balance management with betting
- Game result notifications (Win, Lose, Bust, Blackjack, Push)

### Technical Features
- JavaFX-based GUI using FXML
- Model-View-Controller architecture
- Deck management with card shuffling
- Hand evaluation logic
- Scene routing between menus and game table

---

## Project Structure

```
COP4331-blackjack/
│
├── readme.md
└── BlackjackSingleplayer/
    ├── pom.xml                     # Maven configuration
    ├── nbactions.xml
    └── src/
        ├── main/
        │   └── java/
        │       ├── app/
        │       │   ├── MainApp.java             # Application entry point
        │       │   └── SceneRouter.java         # Navigation controller
        │       ├── controller/
        │       │   ├── DealerTableController.java   # Game logic controller
        │       │   ├── MenuController.java          # Menu navigation
        │       │   └── TutorialController.java      # Tutorial display
        │       ├── model/
        │       │   ├── ActiveGame.java          # Current game state
        │       │   ├── Balance.java             # Player balance
        │       │   ├── BetAmount.java           # Bet management
        │       │   ├── Card.java                # Card representation
        │       │   ├── CardInPlay.java          # Card display state
        │       │   ├── CardTotal.java           # Hand value calculator
        │       │   ├── Deck.java                # Deck management
        │       │   ├── GameResult.java          # Win/loss determination
        │       │   ├── Hand.java                # Player/dealer hands
        │       │   ├── Rank.java                # Card ranks enum
        │       │   └── Suit.java                # Card suits enum
        │       └── view/
        │           ├── DealerTableView.java     # Game table UI
        │           ├── MenuPageView.java        # Main menu UI
        │           └── TutorialView.java        # Tutorial UI
        └── test/
            └── java/                            # Unit tests
```

---

## Requirements
You must have:
- **Java JDK 24+** (Project uses Java 24)
- **Apache Maven 3.9+**
- **JavaFX 24.0.2** (included via Maven)
- **Git**

Verify Java:
```powershell
java -version
```

---

## Installing Maven Manually (Windows)
1. Download the ZIP (working link):  
   https://archive.apache.org/dist/maven/maven-3/3.9.7/binaries/apache-maven-3.9.7-bin.zip

2. Extract to:
```
C:\Program Files\apache-maven-3.9.7
```

3. Add to PATH:
```
C:\Program Files\apache-maven-3.9.7\bin
```

4. Restart PowerShell and verify:
```powershell
mvn -v
```

---

## Cloning the Repository
```powershell
git clone https://github.com/TheGoumble/COP4331-blackjack.git
cd COP4331-blackjack
```

---

## Building and Running the Game

### Quick Start

Navigate to the project directory:
```powershell
cd BlackjackSingleplayer
```

**Run the game:**
```powershell
mvn clean javafx:run
```

The game window will launch automatically with the main menu.

---

## Using the Application

### Main Menu
When you launch the application, you'll see the main menu with options:
- **Create Blackjack Game** — Start a new game session
- **Join Game** — (Reserved for future multiplayer functionality)
- **Tutorial** — Learn how to play Blackjack
- **Close Application** — Exit the game

### Starting a Game
1. Click **Create Blackjack Game**
2. The dealer table will appear with:
   - Your current balance (starts at $1000)
   - Betting input field
   - Dealer's hand (top)
   - Your hand (bottom)
   - Action buttons (Hit/Stand)

### Gameplay Flow
1. **Place Bet**: Enter amount and click **Bet**
2. **Initial Deal**: You receive 2 cards, dealer receives 2 cards (1 face down)
3. **Your Turn**:
   - **Hit** — Draw another card
   - **Stand** — End your turn
4. **Dealer's Turn**: Dealer reveals hidden card and plays (hits on 16 or less)
5. **Result**: Win, lose, push, bust, or blackjack is determined
6. **Continue**: Place another bet to play again

### Tutorial Mode
Click **Tutorial** from the main menu to view:
- Game rules
- Card values
- Win conditions
- Betting instructions

Click **Exit Tutorial** to return to the main menu.

---

## Project Configuration

### Maven Configuration
The `pom.xml` is already configured with:
```xml
<properties>
    <maven.compiler.source>24</maven.compiler.source>
    <maven.compiler.target>24</maven.compiler.target>
    <exec.mainClass>app.MainApp</exec.mainClass>
    <javafx.version>24.0.2</javafx.version>
</properties>
```

### JavaFX Dependencies
JavaFX modules are automatically downloaded via Maven:
- `javafx-controls` — UI components
- `javafx-fxml` — FXML support

---

## Architecture Overview

### MVC Pattern
- **Model**: Game state, card logic, deck management (`model/`)
- **View**: JavaFX UI components (`view/`)
- **Controller**: Event handlers and game logic flow (`controller/`)

### Key Components

#### Application Layer (`app/`)
- `MainApp.java` — JavaFX application entry point
- `SceneRouter.java` — Manages navigation between scenes

#### Controllers (`controller/`)
- `DealerTableController.java` — Handles game logic, betting, hit/stand actions
- `MenuController.java` — Main menu navigation
- `TutorialController.java` — Tutorial display logic

#### Models (`model/`)
- `ActiveGame.java` — Current game session state
- `Balance.java` — Tracks player's money
- `BetAmount.java` — Current bet validation and management
- `Card.java` — Individual card (rank + suit)
- `Deck.java` — 52-card deck with shuffle
- `Hand.java` — Collection of cards for player/dealer
- `CardTotal.java` — Calculates hand value (Ace = 1 or 11)
- `GameResult.java` — Determines winner (WIN, LOSE, PUSH, BUST, BLACKJACK)
- `Rank.java` — Card ranks (ACE through KING)
- `Suit.java` — Card suits (HEARTS, DIAMONDS, CLUBS, SPADES)

#### Views (`view/`)
- `MenuPageView.java` — Main menu UI
- `DealerTableView.java` — Game table with cards, buttons, balance display
- `TutorialView.java` — Tutorial information display

---

## Troubleshooting

### `mvn` not recognized
Add Maven to PATH and restart PowerShell.

### JavaFX runtime components are missing
Maven should automatically download JavaFX. If you encounter errors:
```powershell
mvn clean install
mvn javafx:run
```

### Application won't launch
Ensure you're in the `BlackjackSingleplayer` directory:
```powershell
cd BlackjackSingleplayer
mvn clean javafx:run
```

### Java version mismatch
This project uses Java 24. If you have a different version:
1. Install JDK 24 or modify `pom.xml` to match your Java version
2. Update `<maven.compiler.source>` and `<maven.compiler.target>` properties

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

## Future Enhancements
- Multiplayer support (Join Game functionality)
- Double down and split actions
- Insurance bet option
- Statistics tracking (games played, win rate)
- Save/load game state
- Customizable betting limits
- Sound effects and animations

---

## Credits
**Authors**: Luca Lombardo, Olivia Strandberg, Javier Vargas, Bridjet Walker 
**Course**: COP4331 — Processes of Object-Oriented Programming  
**Technology**: JavaFX 24.0.2, Java 24, Maven

---

## License
This project is for educational purposes as part of COP4331 final project.
