package net;

import java.io.*;
import java.net.*;

/**
 *
 * @author Javier Vargas, revised by Bridjet Walker
 */
public class PeerListener implements Runnable {

    private final Socket socket;

    public PeerListener(Socket socket){
        this.socket = socket;
    }

    @Override
    public void run() {
        try (
            BufferedReader in = new BufferedReader(
                new InputStreamReader(socket.getInputStream())
            )
        ){
            String line;
            while ((line = in.readLine()) != null){
                NetMessage msg = NetMessage.fromLine(line);
                System.out.println("Client received: " + msg.type + " " + msg.payload);

                // Later: send to UI/controller
            }
        } catch(IOException e){
            e.printStackTrace();
        }
    }
}