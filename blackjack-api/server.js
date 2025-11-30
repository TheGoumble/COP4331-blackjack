const express = require('express');
const cors = require('cors');

const app = express();

// Middleware
app.use(cors());
app.use(express.json());

// In-memory storage
const games = new Map(); // gameCode -> { sessionId, hostId, address, port, playerCount, createdAt }
const MAX_PLAYERS = 4;

// Generate 6-character game code
function generateGameCode() {
    const chars = 'ABCDEFGHJKLMNPQRSTUVWXYZ23456789';
    let code = '';
    for (let i = 0; i < 6; i++) {
        code += chars.charAt(Math.floor(Math.random() * chars.length));
    }
    return code;
}

// No WebSocket - P2P handles all gameplay communication

// REST API Endpoints

// Register a new game (host registers their P2P connection info)
app.post('/api/games/register', (req, res) => {
    const { gameCode, hostId, address, port } = req.body;
    
    if (!gameCode || !hostId || !address || !port) {
        return res.status(400).json({ 
            error: 'gameCode, hostId, address, and port required' 
        });
    }
    
    const sessionId = `game_${Date.now()}`;
    
    games.set(gameCode.toUpperCase(), {
        sessionId,
        hostId,
        address,
        port,
        playerCount: 1, // Host counts as first player
        createdAt: Date.now()
    });
    
    console.log(`[API] Game registered: ${gameCode} by ${hostId} at ${address}:${port} (1/${MAX_PLAYERS} players)`);
    
    res.json({
        success: true,
        gameCode: gameCode.toUpperCase(),
        sessionId
    });
});

// List all registered games (only show games with available slots)
app.get('/api/games/list', (req, res) => {
    const gamesList = [];
    
    for (const [gameCode, game] of games.entries()) {
        // Only show games that aren't full
        if (game.playerCount < MAX_PLAYERS) {
            gamesList.push({
                gameCode,
                sessionId: game.sessionId,
                hostId: game.hostId,
                address: game.address,
                port: game.port,
                playerCount: game.playerCount,
                maxPlayers: MAX_PLAYERS,
                createdAt: game.createdAt
            });
        }
    }
    
    console.log(`[API] Listed ${gamesList.length} available game(s) (${games.size} total)`);
    console.log(`[API] Available games: ${gamesList.map(g => `${g.gameCode}(${g.playerCount}/${MAX_PLAYERS})`).join(', ') || 'none'}`);
    
    res.json({
        success: true,
        games: gamesList
    });
});

// Get game connection info (for joining)
app.get('/api/games/:gameCode', (req, res) => {
    const { gameCode } = req.params;
    const game = games.get(gameCode.toUpperCase());
    
    if (!game) {
        return res.status(404).json({ error: 'Game not found' });
    }
    
    res.json({
        success: true,
        game: {
            gameCode: gameCode.toUpperCase(),
            sessionId: game.sessionId,
            hostId: game.hostId,
            address: game.address,
            port: game.port,
            playerCount: game.playerCount,
            maxPlayers: MAX_PLAYERS,
            connectionString: `${game.address}:${game.port}`
        }
    });
});

// Player joins a game (increment player count)
app.post('/api/games/:gameCode/join', (req, res) => {
    const { gameCode } = req.params;
    const game = games.get(gameCode.toUpperCase());
    
    if (!game) {
        return res.status(404).json({ error: 'Game not found' });
    }
    
    if (game.playerCount >= MAX_PLAYERS) {
        return res.status(400).json({ 
            error: 'Game is full',
            playerCount: game.playerCount,
            maxPlayers: MAX_PLAYERS
        });
    }
    
    game.playerCount++;
    console.log(`[API] Player joined ${gameCode}: ${game.playerCount}/${MAX_PLAYERS}`);
    
    res.json({
        success: true,
        playerCount: game.playerCount,
        maxPlayers: MAX_PLAYERS,
        isFull: game.playerCount >= MAX_PLAYERS
    });
});

// Player leaves a game (decrement player count)
app.post('/api/games/:gameCode/leave', (req, res) => {
    const { gameCode } = req.params;
    const game = games.get(gameCode.toUpperCase());
    
    if (!game) {
        return res.status(404).json({ error: 'Game not found' });
    }
    
    game.playerCount--;
    console.log(`[API] Player left ${gameCode}: ${game.playerCount}/${MAX_PLAYERS}`);
    
    // If no players left, remove game
    if (game.playerCount <= 0) {
        games.delete(gameCode.toUpperCase());
        console.log(`[API] Game removed (no players): ${gameCode}`);
        return res.json({
            success: true,
            removed: true
        });
    }
    
    res.json({
        success: true,
        playerCount: game.playerCount,
        maxPlayers: MAX_PLAYERS
    });
});

// Unregister a game (when host closes)
app.delete('/api/games/:gameCode', (req, res) => {
    const { gameCode } = req.params;
    
    const deleted = games.delete(gameCode.toUpperCase());
    
    if (deleted) {
        console.log(`[API] Game unregistered: ${gameCode}`);
        res.json({ success: true });
    } else {
        res.status(404).json({ error: 'Game not found' });
    }
});

// Health check
app.get('/health', (req, res) => {
    res.json({ 
        status: 'ok',
        activeGames: games.size,
        uptime: process.uptime()
    });
});

// Cleanup old games (every 10 minutes)
setInterval(() => {
    const now = Date.now();
    const maxAge = 60 * 60 * 1000; // 60 minutes
    
    for (const [gameCode, game] of games.entries()) {
        if (now - game.createdAt > maxAge) {
            games.delete(gameCode);
            console.log(`[CLEANUP] Removed stale game: ${gameCode}`);
        }
    }
}, 10 * 60 * 1000);

const PORT = process.env.PORT || 3000;

app.listen(PORT, () => {
    console.log('===========================================');
    console.log(`Blackjack Matchmaking API on port ${PORT}`);
    console.log(`HTTP: http://localhost:${PORT}`);
    console.log(`Mode: Registry-only (P2P gameplay)`);
    console.log('===========================================');
});
