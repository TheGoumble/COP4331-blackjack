package net;

/**
 *
 * @author Bridjet Walker
 */
public class NetMessage {
    public final String type;
    public final String payload; // optional text for now

    public NetMessage(String type, String payload){
        this.type = type;
        this.payload = payload;
    }

    // super simple line format: TYPE|payload
    // ex: JOIN|Bridjet
    public String toLine(){
        return type + "|" + (payload == null ? "" : payload);
    }

    public static NetMessage fromLine(String line){
        String[] parts = line.split("\\|", 2);
        String t = parts[0];
        String p = parts.length > 1 ? parts[1] : "";
        return new NetMessage(t, p);
    }
}
