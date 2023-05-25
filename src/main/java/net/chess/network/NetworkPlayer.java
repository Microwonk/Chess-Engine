package net.chess.network;

import net.chess.engine.board.Move;

import java.io.*;
import java.net.ServerSocket;
import java.net.Socket;
import java.nio.ByteBuffer;
import java.util.Arrays;

import static net.chess.gui.Chess.CHESS;

public class NetworkPlayer {

    private final static int BUFFER_SIZE = 8;

    private String IP;
    private ServerSocket server;
    private final Socket client;
    private final OutputStream out;
    private final InputStream in;

    // server constructor
    public NetworkPlayer() {
        try {
            this.IP = NetworkUtils.getLocalIPAddress();
            System.out.println(NetworkUtils.encode(IP));
            this.server = new ServerSocket(NetworkUtils.PORT);
            System.out.println("Server started. Waiting for a client...");
            this.client = server.accept();
            System.out.println("Client connected: " + client);

            // input from client
            this.in = client.getInputStream();
            // output from server
            this.out = client.getOutputStream();
        } catch (Exception e) {
            throw new RuntimeException();
        }
    }

    // client constructor
    public NetworkPlayer(final String gameCode) {
        try {
            this.client = new Socket(NetworkUtils.decode(gameCode), NetworkUtils.PORT);
            System.out.println("Connected to server: " + client);
            // input from client
            this.in = client.getInputStream();
            // output from server
            this.out = client.getOutputStream();
        } catch (Exception e) {
            throw new RuntimeException();
        }
    }

    public int[] receive () {
        try {
            System.out.println("Receiving");
            byte[] receivedBytes = new byte[BUFFER_SIZE];
            int bytesRead = in.read(receivedBytes);
            ByteBuffer receivedBuffer = ByteBuffer.wrap(receivedBytes, 0, bytesRead);
            int[] receivedNumbers = new int[bytesRead / 4]; // Assuming each number is an integer (4 bytes)
            for (int i = 0; i < receivedNumbers.length; i++) {
                receivedNumbers[i] = receivedBuffer.getInt();
            }
            //return CHESS.getGameBoard().getAllLegalMoves().stream().filter(move -> move.getCurrentCoordinate() == receivedNumbers[0]
            //        && move.getDestinationCoordinate() == receivedNumbers[1]).toList().get(0);
            return receivedNumbers;
        } catch (IOException e) {
            e.printStackTrace();
        }
        return null;
    }

    public void send (final Move move) {
        try {
            System.out.println("Sending");
            int[] toSend = {move.getCurrentCoordinate(), move.getDestinationCoordinate()};
            ByteBuffer buffer = ByteBuffer.allocate(BUFFER_SIZE);
            Arrays.stream(toSend).forEach(buffer::putInt);
            out.write(buffer.array());
            out.flush();
        } catch (IOException e) {
            System.out.println("Client closed or not Connected.");
        }
    }

    public void close () {
        try {
            client.close();
            if (this.server != null) {
                server.close();
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void poll () {
        try {
            while (true) {
                int[] receive = receive();
                System.out.println(Arrays.toString(receive));
                //CHESS.update(CHESS.getGameBoard().currentPlayer().makeMove(receive).getTransitionBoard());
            }
        } catch (Exception ignored) {

        }
    }

    public String getIP () {
        return IP;
    }
}
