package network;

import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.time.Duration;
import com.google.gson.Gson;
import com.google.gson.JsonObject;
import com.google.gson.JsonArray;
import java.util.ArrayList;
import java.util.List;

/**
 * HTTP client for matchmaking API
 * Handles game registration and discovery
 * 
 * @author COP4331 Team
 */
public class ApiClient {
    private final String apiUrl;
    private final HttpClient httpClient;
    private final Gson gson;
    
    public ApiClient() {
        this(ApiConfig.getApiUrl());
    }
    
    public ApiClient(String apiUrl) {
        this.apiUrl = apiUrl;
        this.httpClient = HttpClient.newBuilder()
            .connectTimeout(Duration.ofSeconds(10))
            .build();
        this.gson = new Gson();
    }
    
    /**
     * Register a game with the matchmaking server
     */
    public RegisterResponse registerGame(String gameCode, String hostId, String address, int port) {
        try {
            JsonObject payload = new JsonObject();
            payload.addProperty("gameCode", gameCode);
            payload.addProperty("hostId", hostId);
            payload.addProperty("address", address);
            payload.addProperty("port", port);
            
            HttpRequest request = HttpRequest.newBuilder()
                .uri(URI.create(apiUrl + "/games/register"))
                .header("Content-Type", "application/json")
                .POST(HttpRequest.BodyPublishers.ofString(payload.toString()))
                .build();
            
            HttpResponse<String> response = httpClient.send(request, 
                HttpResponse.BodyHandlers.ofString());
            
            if (response.statusCode() == 200) {
                JsonObject json = gson.fromJson(response.body(), JsonObject.class);
                System.out.println("[API] Game registered: " + gameCode);
                return new RegisterResponse(
                    true,
                    json.get("gameCode").getAsString(),
                    json.get("sessionId").getAsString(),
                    null
                );
            } else {
                JsonObject json = gson.fromJson(response.body(), JsonObject.class);
                String error = json.has("error") ? json.get("error").getAsString() : "Unknown error";
                System.err.println("[API] Registration failed: " + error);
                return new RegisterResponse(false, null, null, error);
            }
            
        } catch (Exception e) {
            System.err.println("[API] Error registering game: " + e.getMessage());
            return new RegisterResponse(false, null, null, e.getMessage());
        }
    }
    
    /**
     * Get list of all available games
     */
    public List<GameInfo> listGames() {
        try {
            HttpRequest request = HttpRequest.newBuilder()
                .uri(URI.create(apiUrl + "/games/list"))
                .GET()
                .build();
            
            HttpResponse<String> response = httpClient.send(request,
                HttpResponse.BodyHandlers.ofString());
            
            if (response.statusCode() == 200) {
                JsonObject json = gson.fromJson(response.body(), JsonObject.class);
                JsonArray gamesArray = json.getAsJsonArray("games");
                
                List<GameInfo> games = new ArrayList<>();
                for (int i = 0; i < gamesArray.size(); i++) {
                    JsonObject game = gamesArray.get(i).getAsJsonObject();
                    games.add(new GameInfo(
                        game.get("gameCode").getAsString(),
                        game.get("sessionId").getAsString(),
                        game.get("hostId").getAsString(),
                        game.get("address").getAsString(),
                        game.get("port").getAsInt(),
                        game.get("createdAt").getAsLong()
                    ));
                }
                
                System.out.println("[API] Listed " + games.size() + " game(s)");
                return games;
            } else {
                System.err.println("[API] Failed to list games: " + response.statusCode());
                return new ArrayList<>();
            }
            
        } catch (Exception e) {
            System.err.println("[API] Error listing games: " + e.getMessage());
            return new ArrayList<>();
        }
    }
    
    /**
     * Get connection info for a specific game
     */
    public GameInfo getGame(String gameCode) {
        try {
            HttpRequest request = HttpRequest.newBuilder()
                .uri(URI.create(apiUrl + "/games/" + gameCode.toUpperCase()))
                .GET()
                .build();
            
            HttpResponse<String> response = httpClient.send(request,
                HttpResponse.BodyHandlers.ofString());
            
            if (response.statusCode() == 200) {
                JsonObject json = gson.fromJson(response.body(), JsonObject.class);
                JsonObject game = json.getAsJsonObject("game");
                
                return new GameInfo(
                    game.get("gameCode").getAsString(),
                    game.get("sessionId").getAsString(),
                    game.get("hostId").getAsString(),
                    game.get("address").getAsString(),
                    game.get("port").getAsInt(),
                    0 // createdAt not included in single game response
                );
            } else {
                System.err.println("[API] Game not found: " + gameCode);
                return null;
            }
            
        } catch (Exception e) {
            System.err.println("[API] Error getting game: " + e.getMessage());
            return null;
        }
    }
    
    /**
     * Notify API that a player joined
     */
    public boolean notifyPlayerJoin(String gameCode) {
        try {
            HttpRequest request = HttpRequest.newBuilder()
                .uri(URI.create(apiUrl + "/games/" + gameCode.toUpperCase() + "/join"))
                .POST(HttpRequest.BodyPublishers.noBody())
                .build();
            
            HttpResponse<String> response = httpClient.send(request,
                HttpResponse.BodyHandlers.ofString());
            
            if (response.statusCode() == 200) {
                JsonObject json = gson.fromJson(response.body(), JsonObject.class);
                int playerCount = json.get("playerCount").getAsInt();
                System.out.println("[API] Player join recorded for " + gameCode + ": " + playerCount + " players");
                return true;
            } else {
                System.err.println("[API] Failed to record join for: " + gameCode);
                return false;
            }
            
        } catch (Exception e) {
            System.err.println("[API] Error recording join: " + e.getMessage());
            return false;
        }
    }
    
    /**
     * Notify API that a player left
     */
    public boolean notifyPlayerLeave(String gameCode) {
        try {
            HttpRequest request = HttpRequest.newBuilder()
                .uri(URI.create(apiUrl + "/games/" + gameCode.toUpperCase() + "/leave"))
                .POST(HttpRequest.BodyPublishers.noBody())
                .build();
            
            HttpResponse<String> response = httpClient.send(request,
                HttpResponse.BodyHandlers.ofString());
            
            if (response.statusCode() == 200) {
                JsonObject json = gson.fromJson(response.body(), JsonObject.class);
                if (json.has("removed") && json.get("removed").getAsBoolean()) {
                    System.out.println("[API] Game removed (no players): " + gameCode);
                } else {
                    int playerCount = json.get("playerCount").getAsInt();
                    System.out.println("[API] Player leave recorded for " + gameCode + ": " + playerCount + " players");
                }
                return true;
            } else {
                System.err.println("[API] Failed to record leave for: " + gameCode);
                return false;
            }
            
        } catch (Exception e) {
            System.err.println("[API] Error recording leave: " + e.getMessage());
            return false;
        }
    }
    
    /**
     * Unregister a game (when host closes)
     */
    public boolean unregisterGame(String gameCode) {
        try {
            HttpRequest request = HttpRequest.newBuilder()
                .uri(URI.create(apiUrl + "/games/" + gameCode.toUpperCase()))
                .DELETE()
                .build();
            
            HttpResponse<String> response = httpClient.send(request,
                HttpResponse.BodyHandlers.ofString());
            
            if (response.statusCode() == 200) {
                System.out.println("[API] Game unregistered: " + gameCode);
                return true;
            } else {
                System.err.println("[API] Failed to unregister: " + gameCode);
                return false;
            }
            
        } catch (Exception e) {
            System.err.println("[API] Error unregistering game: " + e.getMessage());
            return false;
        }
    }
    
    /**
     * Registration response
     */
    public static class RegisterResponse {
        public final boolean success;
        public final String gameCode;
        public final String sessionId;
        public final String error;
        
        public RegisterResponse(boolean success, String gameCode, String sessionId, String error) {
            this.success = success;
            this.gameCode = gameCode;
            this.sessionId = sessionId;
            this.error = error;
        }
    }
    
    /**
     * Game information from API
     */
    public static class GameInfo {
        public final String gameCode;
        public final String sessionId;
        public final String hostId;
        public final String address;
        public final int port;
        public final long createdAt;
        
        public GameInfo(String gameCode, String sessionId, String hostId, 
                       String address, int port, long createdAt) {
            this.gameCode = gameCode;
            this.sessionId = sessionId;
            this.hostId = hostId;
            this.address = address;
            this.port = port;
            this.createdAt = createdAt;
        }
        
        public String getConnectionString() {
            return address + ":" + port;
        }
    }
}
