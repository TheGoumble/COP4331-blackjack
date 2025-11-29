package network;

import java.io.*;
import java.net.*;
import java.util.*;
import java.util.concurrent.*;

/**
 * UDP-based game discovery service for local network
 * Hosts broadcast their presence, clients discover available games
 * 
 * @author COP4331 Team
 */
public class GameDiscoveryService {
    private static final int DISCOVERY_PORT = 25566;
    private static final String BROADCAST_ADDRESS = "255.255.255.255";
    private static final int BROADCAST_INTERVAL = 3000; // 3 seconds
    
    private DatagramSocket broadcastSocket;
    private DatagramSocket listenSocket;
    private Thread broadcastThread;
    private Thread listenerThread;
    private boolean running = false;
    
    private final Map<String, GameAnnouncement> discoveredGames = new ConcurrentHashMap<>();
    private final List<Runnable> updateListeners = new CopyOnWriteArrayList<>();
    
    // Singleton instance
    private static final GameDiscoveryService instance = new GameDiscoveryService();
    
    private GameDiscoveryService() {}
    
    public static GameDiscoveryService getInstance() {
        return instance;
    }
    
    /**
     * Start broadcasting this host's game
     */
    public void startBroadcasting(String sessionId, String hostId, String address, int port) {
        try {
            if (broadcastSocket == null || broadcastSocket.isClosed()) {
                broadcastSocket = new DatagramSocket();
                broadcastSocket.setBroadcast(true);
            }
            
            running = true;
            
            broadcastThread = new Thread(() -> broadcast(sessionId, hostId, address, port));
            broadcastThread.setDaemon(true);
            broadcastThread.start();
            
            System.out.println("[DISCOVERY] Started broadcasting game: " + sessionId);
        } catch (IOException e) {
            System.err.println("[DISCOVERY] Failed to start broadcasting: " + e.getMessage());
        }
    }
    
    /**
     * Start listening for game announcements
     */
    public void startListening() {
        try {
            if (listenSocket == null || listenSocket.isClosed()) {
                listenSocket = new DatagramSocket(DISCOVERY_PORT);
            }
            
            running = true;
            
            listenerThread = new Thread(this::listen);
            listenerThread.setDaemon(true);
            listenerThread.start();
            
            System.out.println("[DISCOVERY] Started listening for games");
        } catch (IOException e) {
            System.err.println("[DISCOVERY] Failed to start listening: " + e.getMessage());
        }
    }
    
    /**
     * Broadcast game announcement periodically
     */
    private void broadcast(String sessionId, String hostId, String address, int port) {
        while (running) {
            try {
                String message = String.format("BLACKJACK_GAME|%s|%s|%s|%d", 
                    sessionId, hostId, address, port);
                
                byte[] buffer = message.getBytes();
                InetAddress broadcastAddr = InetAddress.getByName(BROADCAST_ADDRESS);
                DatagramPacket packet = new DatagramPacket(buffer, buffer.length, 
                    broadcastAddr, DISCOVERY_PORT);
                
                broadcastSocket.send(packet);
                
                Thread.sleep(BROADCAST_INTERVAL);
            } catch (InterruptedException e) {
                break;
            } catch (Exception e) {
                System.err.println("[DISCOVERY] Broadcast error: " + e.getMessage());
            }
        }
    }
    
    /**
     * Listen for game announcements
     */
    private void listen() {
        byte[] buffer = new byte[1024];
        
        while (running) {
            try {
                DatagramPacket packet = new DatagramPacket(buffer, buffer.length);
                listenSocket.receive(packet);
                
                String message = new String(packet.getData(), 0, packet.getLength());
                
                if (message.startsWith("BLACKJACK_GAME|")) {
                    parseAnnouncement(message);
                }
            } catch (IOException e) {
                if (running) {
                    System.err.println("[DISCOVERY] Listen error: " + e.getMessage());
                }
            }
        }
    }
    
    /**
     * Parse a game announcement
     */
    private void parseAnnouncement(String message) {
        try {
            String[] parts = message.split("\\|");
            if (parts.length == 5) {
                String sessionId = parts[1];
                String hostId = parts[2];
                String address = parts[3];
                int port = Integer.parseInt(parts[4]);
                
                GameAnnouncement announcement = new GameAnnouncement(
                    sessionId, hostId, address, port, System.currentTimeMillis());
                
                boolean isNew = !discoveredGames.containsKey(sessionId);
                discoveredGames.put(sessionId, announcement);
                
                if (isNew) {
                    System.out.println("[DISCOVERY] Found game: " + sessionId + 
                        " at " + address + ":" + port);
                }
                
                // Always notify listeners to refresh UI
                notifyListeners();
                
                // Clean up old games (not seen in 10 seconds)
                cleanupOldGames();
            }
        } catch (Exception e) {
            System.err.println("[DISCOVERY] Error parsing announcement: " + e.getMessage());
        }
    }
    
    /**
     * Remove games that haven't been seen recently
     */
    private void cleanupOldGames() {
        long now = System.currentTimeMillis();
        boolean changed = false;
        
        Iterator<Map.Entry<String, GameAnnouncement>> iter = 
            discoveredGames.entrySet().iterator();
        
        while (iter.hasNext()) {
            Map.Entry<String, GameAnnouncement> entry = iter.next();
            if (now - entry.getValue().timestamp > 10000) {
                System.out.println("[DISCOVERY] Removed stale game: " + entry.getKey());
                iter.remove();
                changed = true;
            }
        }
        
        // Always notify if cleanup happened (to refresh timestamps even if no removal)
        if (changed) {
            notifyListeners();
        }
    }
    
    /**
     * Stop broadcasting and clean up
     */
    public void stopBroadcasting() {
        running = false;
        if (broadcastThread != null) {
            broadcastThread.interrupt();
        }
        discoveredGames.clear();
        notifyListeners();
    }
    
    /**
     * Stop listening for announcements
     */
    public void stopListening() {
        running = false;
        if (listenerThread != null) {
            listenerThread.interrupt();
        }
        if (listenSocket != null && !listenSocket.isClosed()) {
            listenSocket.close();
        }
        discoveredGames.clear();
        notifyListeners();
    }
    
    /**
     * Get all discovered games
     */
    public Map<String, GameAnnouncement> getDiscoveredGames() {
        cleanupOldGames();
        return new HashMap<>(discoveredGames);
    }
    
    /**
     * Add a listener for game list updates
     */
    public void addUpdateListener(Runnable listener) {
        updateListeners.add(listener);
    }
    
    /**
     * Remove an update listener
     */
    public void removeUpdateListener(Runnable listener) {
        updateListeners.remove(listener);
    }
    
    /**
     * Notify all listeners of game list changes
     */
    private void notifyListeners() {
        for (Runnable listener : updateListeners) {
            try {
                javafx.application.Platform.runLater(listener);
            } catch (Exception e) {
                // Ignore if not in JavaFX context
                listener.run();
            }
        }
    }
    
    /**
     * Stop broadcasting and listening
     */
    public void stop() {
        running = false;
        
        if (broadcastThread != null) {
            broadcastThread.interrupt();
        }
        
        if (listenerThread != null) {
            listenerThread.interrupt();
        }
        
        if (listenSocket != null && !listenSocket.isClosed()) {
            listenSocket.close();
        }
        
        discoveredGames.clear();
        System.out.println("[DISCOVERY] Stopped discovery service");
    }
    
    /**
     * Game announcement data
     */
    public static class GameAnnouncement {
        public final String sessionId;
        public final String hostId;
        public final String address;
        public final int port;
        public final long timestamp;
        
        public GameAnnouncement(String sessionId, String hostId, String address, 
                               int port, long timestamp) {
            this.sessionId = sessionId;
            this.hostId = hostId;
            this.address = address;
            this.port = port;
            this.timestamp = timestamp;
        }
        
        public String getConnectionString() {
            return address + ":" + port;
        }
    }
}
