package net;

import java.io.*;
import java.net.*;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;

/**
 *
 * @author Javier Vargas, revised by Bridjet Walker
 */
public class PeerNode {
    
 private final int myPort;
    private ServerSocket serverSocket;

    // only used if I'm host:
    private final Set<ClientHandler> clients = ConcurrentHashMap.newKeySet();

    public PeerNode(int myPort){
        this.myPort = myPort;
    }

    // HOST MODE
    public void startHost() throws IOException {
        serverSocket = new ServerSocket(myPort);
        System.out.println("Hosting on port " + myPort);

        new Thread(() -> {
            while (true){
                try {
                    Socket s = serverSocket.accept();
                    ClientHandler ch = new ClientHandler(s, this);
                    clients.add(ch);
                    new Thread(ch).start();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }).start();
    }

    // CLIENT MODE
    public Socket connectToHost(String host, int port) throws IOException {
        Socket socket = new Socket(host, port);
        System.out.println("Connected to host " + host + ":" + port);

        // start listener thread
        new Thread(new PeerListener(socket)).start();
        return socket;
    }

    // HOST broadcast to all clients
    public void broadcast(NetMessage msg){
        for (ClientHandler ch: clients){
            ch.send(msg);
        }
    }

    public void remove(ClientHandler ch){
        clients.remove(ch);
    }
}