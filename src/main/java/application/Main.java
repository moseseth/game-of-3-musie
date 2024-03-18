package application;

import infrastructure.GameServer;

public class Main {
    public static void main(String[] args) {
        GameServer gameServer = new GameServer();
        gameServer.startGameServer();

        Runtime.getRuntime().addShutdownHook(new Thread(gameServer::stopGameServer));
    }
}
