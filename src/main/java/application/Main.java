package application;

import domain.entities.Game;
import domain.services.GameService;
import infrastructure.GameServer;

public class Main {
    public static void main(String[] args) {
        Game game = new Game();
        GameService gameService = new GameService(game);
        GameServer gameServer = new GameServer(gameService);

        gameServer.startGameServer();

        Runtime.getRuntime().addShutdownHook(new Thread(gameServer::stopGameServer));
    }
}
