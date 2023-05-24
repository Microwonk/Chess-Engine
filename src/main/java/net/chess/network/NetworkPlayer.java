package net.chess.network;

import net.chess.engine.board.Board;
import net.chess.engine.board.Move;

import java.io.*;
import java.net.ServerSocket;
import java.net.Socket;

import static net.chess.gui.Chess.CHESS;

public class NetworkPlayer {

    private String IP;
    private ServerSocket server;
    private final Socket client;
    private final ObjectOutputStream out;
    private final ObjectInputStream in;

    // server constructor
    public NetworkPlayer() {
        try {
            this.IP = NetworkUtils.getLocalIPAddress();
            this.server = new ServerSocket(NetworkUtils.PORT);
            this.client = server.accept();

            // input from client
            this.in = new ObjectInputStream(client.getInputStream());
            // output from server
            this.out = new ObjectOutputStream(client.getOutputStream());
        } catch (Exception e) {
            throw new RuntimeException();
        }
    }

    // client constructor
    public NetworkPlayer(final String gameCode) {
        try {
            this.client = new Socket(NetworkUtils.decode(gameCode), NetworkUtils.PORT);

            // input from client
            this.in = new ObjectInputStream(client.getInputStream());
            // output from server
            this.out = new ObjectOutputStream(client.getOutputStream());
        } catch (Exception e) {
            throw new RuntimeException();
        }
    }

    private Move receive (final Board board) {
        try {
            return (Move) in.readObject();
        } catch (IOException | ClassNotFoundException e) {
            e.printStackTrace();
        }
        return null;
    }

    private void send (final Move move) {
        try {
            out.writeObject(move);
            out.flush();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private void close () {
        try {
            server.close();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void poll () {
        while (true) {
            Move receive = receive(CHESS.getGameBoard());
            if (receive != null) {
                CHESS.update(CHESS.getGameBoard().currentPlayer().makeMove(receive).getTransitionBoard());
            } else {
                try {
                    Thread.sleep(1000);
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        }
    }

    public enum Type {
        SERVER,
        CLIENT;
    }

}
