package net.chess.network;

import java.net.InetAddress;
import java.util.Base64;

public class NetworkUtils {

    public static final int PORT = 8080;

    public static String encode (String address) {
        return Base64.getEncoder().encodeToString(address.getBytes());
    }

    public static String decode(String address) {
        return new String(Base64.getDecoder().decode(address.getBytes()));
    }

    public static String getLocalIPAddress () throws Exception {
        return InetAddress.getLocalHost().getHostAddress();
    }
}
