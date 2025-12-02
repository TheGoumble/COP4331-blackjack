package network;

import command.Command;
import java.io.*;
import java.net.*;
import java.util.ArrayList;
import java.util.List;
import java.util.function.Consumer;

/**
 * Client peer that sends commands and receives game updates
 * Represents a single player's connection to the game
 * 
 * @author Javier Vargas, Group 12
 */
public class ClientPeer {
    private final String userId;
    private String displayName;
    private Socket socket;
    private ObjectOutputStream out;
    private ObjectInputStream in;
    private Thread listenerThread;
    private boolean connected;
    private final List<Consumer<GameUpdateMessage>> updateListeners;
    
    public ClientPeer(String userId) {
        this.userId = userId;
        this.displayName = userId; // Default to userId
        this.updateListeners = new ArrayList<>();
        this.connected = false;
    }
    
    /**
     * Constructor for server-side client peer (created by DesignatedHost)
     */
    public ClientPeer(String userId, Socket socket, ObjectInputStream in, ObjectOutputStream out) {
        this.userId = userId;
        this.displayName = userId; // Default to userId
        this.socket = socket;
        this.in = in;
        this.out = out;
        this.updateListeners = new ArrayList<>();
        this.connected = true;
    }
    
    /**
     * Connect to a host by IP address and port
     */
    public void connectToHost(String hostAddress, int port) {
        try {
            System.out.println("[CLIENT] Connecting to " + hostAddress + ":" + port);
            
            // Create socket with connection timeout
            socket = new Socket();
            socket.connect(new InetSocketAddress(hostAddress, port), 10000); // 10 second timeout
            // No read timeout - wait indefinitely for game updates
            
            out = new ObjectOutputStream(socket.getOutputStream());
            out.flush();
            in = new ObjectInputStream(socket.getInputStream());
            
            // Send our user ID and display name to the host
            out.writeObject(userId);
            out.writeObject(displayName);
            out.flush();
            
            connected = true;
            System.out.println("[CLIENT] Connected successfully");
            
            // Start listening for updates from the host
            listenerThread = new Thread(this::listenForUpdates);
            listenerThread.setDaemon(true);
            listenerThread.start();
            
        } catch (SocketTimeoutException e) {
            System.err.println("[CLIENT] Connection timeout - host may be unreachable or behind firewall");
            connected = false;
            throw new RuntimeException("Connection timeout - unable to reach host");
        } catch (IOException e) {
            System.err.println("[CLIENT] Failed to connect: " + e.getMessage());
            e.printStackTrace();
            connected = false;
            throw new RuntimeException("Connection failed: " + e.getMessage());
        }
    }
    
    /**
     * Connect to a local host (for when host is also a player)
     */
    public void connectToHost(DesignatedHost host) {
        try {
            connectToHost("localhost", host.getPort());
        } catch (Exception e) {
            System.err.println("[CLIENT] Failed to connect to local host: " + e.getMessage());
        }
    }
    
    /**
     * Listen for game updates from the host
     */
    private void listenForUpdates() {
        while (connected && !socket.isClosed()) {
            try {
                Object obj = in.readObject();
                if (obj instanceof GameUpdateMessage) {
                    GameUpdateMessage update = (GameUpdateMessage) obj;
                    // Notify all listeners on JavaFX thread
                    javafx.application.Platform.runLater(() -> {
                        for (Consumer<GameUpdateMessage> listener : updateListeners) {
                            listener.accept(update);
                        }
                    });
                }
            } catch (EOFException | SocketException e) {
                // Connection closed - only notify if we didn't initiate the disconnect
                if (connected) {
                    System.err.println("[CLIENT] Host disconnected");
                    connected = false;
                    // Notify listeners of host disconnect
                    javafx.application.Platform.runLater(() -> {
                        GameUpdateMessage disconnectMsg = new GameUpdateMessage(
                            GameUpdateMessage.MessageType.HOST_DISCONNECTED,
                            "Host has disconnected from the game"
                        );
                        for (Consumer<GameUpdateMessage> listener : updateListeners) {
                            listener.accept(disconnectMsg);
                        }
                    });
                }
                break;
            } catch (Exception e) {
                System.err.println("[CLIENT] Error receiving update: " + e.getMessage());
                connected = false;
                break;
            }
        }
    }
    
    /**
     * Disconnect from host
     */
    public void disconnect() {
        connected = false;
        try {
            if (socket != null && !socket.isClosed()) {
                socket.close();
            }
            if (listenerThread != null) {
                listenerThread.interrupt();
            }
        } catch (IOException e) {
            System.err.println("[CLIENT] Error disconnecting: " + e.getMessage());
        }
    }
    
    /**
     * Send a command to the host
     */
    public void sendCommand(Command command) {
        if (!connected || out == null) {
            throw new IllegalStateException("Not connected to a host");
        }
        
        try {
            out.writeObject(command);
            out.flush();
        } catch (IOException e) {
            System.err.println("[CLIENT] Failed to send command: " + e.getMessage());
            connected = false;
        }
    }
    
    /**
     * Receive a game update from the host (called by server-side)
     */
    public void receiveGameUpdate(GameUpdateMessage update) {
        if (out != null) {
            try {
                out.writeObject(update);
                out.flush();
            } catch (IOException e) {
                System.err.println("[CLIENT] Failed to send update: " + e.getMessage());
            }
        }
    }
    
    /**
     * Register a listener for game updates
     */
    public void addUpdateListener(Consumer<GameUpdateMessage> listener) {
        updateListeners.add(listener);
    }
    
    /**
     * Remove a listener
     */
    public void removeUpdateListener(Consumer<GameUpdateMessage> listener) {
        updateListeners.remove(listener);
    }
    
    public String getUserId() {
        return userId;
    }
    
    public String getDisplayName() {
        return displayName;
    }
    
    public void setDisplayName(String displayName) {
        this.displayName = displayName;
    }
    
    public boolean isConnected() {
        return connected;
    }
}
