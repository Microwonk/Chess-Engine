package net.chess.network.test;

import net.chess.network.NetworkPlayer;

import java.util.Scanner;

public class Client {
    public static void main(String[] args) {
        NetworkPlayer client = new NetworkPlayer(new Scanner(System.in).nextLine());
        Thread receive = new Thread(client::poll);
        receive.start();
    }
}

