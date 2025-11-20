package Peer2Peer;

import java.io.*;
import java.net.*;

/**
 * Setting up the peer 2 peer service that will allow for black jack multiplayer
 *
 * @author Javier Vargas
 */
public class Peer {

    private int port;
    private ServerSocket serverSocket;

    public Peer(int port) throws IOException {
        this.port = port;
        this.serverSocket = new ServerSocket(port);
    }

    public void start() throws IOException {
        System.out.println("Peer started on port " + port);
        while (true) {
            Socket clientSocket = serverSocket.accept();
            new Thread(new ClientHandler(clientSocket)).start();
        }
    }

    public void connectToPeer(String host, int port) {
        try (Socket socket = new Socket(host, port); PrintWriter out = new PrintWriter(socket.getOutputStream(), true); BufferedReader in = new BufferedReader(new InputStreamReader(socket.getInputStream()))) {
            out.println(
                    "Hello from Peer on port " + this.port);
            String response = in.readLine();

            System.out.println(
                    "Received from another Peer: " + response);
        } catch (IOException e) {
            e.printStackTrace();
        }

    }
    
    public static void main(String[] args){
        if (args.length != 1){
            System.out.println("Usage: java Peer <port>");
            return;
        }
        int port = Integer.parseInt(args[0]);
        try{
            Peer peer = new Peer(port);
            peer.start();
        }catch (IOException e){
            e.printStackTrace();
        }
    }

}
