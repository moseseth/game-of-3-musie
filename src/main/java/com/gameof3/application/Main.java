package com.gameof3.application;

import com.gameof3.domain.entities.Game;
import com.gameof3.domain.services.GameService;
import com.gameof3.infrastructure.GameServer;

public class Main {
    public static void main(String[] args) {
        Game game = new Game();
        GameService gameService = new GameService(game);
        GameServer gameServer = new GameServer(gameService);

        gameServer.startGameServer();

        Runtime.getRuntime().addShutdownHook(new Thread(gameServer::stopGameServer));
    }
}
