package net;

import java.io.*;
import java.net.*;

/**
 *
 * @author Dev, revised by Bridjet Walker
 */
public class ClientHandler implements Runnable {

    private final Socket socket;
    private final PeerNode host;
    private PrintWriter out;

    public ClientHandler(Socket socket, PeerNode host){
        this.socket = socket;
        this.host = host;
    }

    @Override
    public void run() {
        try (
            BufferedReader in = new BufferedReader(
                new InputStreamReader(socket.getInputStream())
            )
        ){
            out = new PrintWriter(socket.getOutputStream(), true);

            String line;
            while ((line = in.readLine()) != null){
                NetMessage msg = NetMessage.fromLine(line);
                System.out.println("Host received: " + msg.type + " " + msg.payload);

                // TEMP behavior:
                // if someone joins, tell everybody
                if (msg.type.equals("JOIN")){
                    host.broadcast(new NetMessage("PLAYER_JOINED", msg.payload));
                }

                // Later: route to game controller
            }

        } catch (IOException e){
            e.printStackTrace();
        } finally {
            host.remove(this);
            try { socket.close(); } catch(IOException ignored){}
        }
    }

    public void send(NetMessage msg){
        if(out != null) out.println(msg.toLine());
    }
}