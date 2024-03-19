package com.gameof3.application;

import com.corundumstudio.socketio.Configuration;
import com.corundumstudio.socketio.SocketIOServer;
import com.gameof3.domain.entities.Game;
import com.gameof3.domain.services.GameService;
import com.gameof3.infrastructure.GameServer;
import io.github.cdimascio.dotenv.Dotenv;

public class Main {

    private static SocketIOServer createSocketIOServer(String hostname, int port) {
        Configuration config = new Configuration();
        config.setHostname(hostname);
        config.setPort(port);
        return new SocketIOServer(config);
    }

    public static void main(String[] args) {
        Dotenv dotenv = Dotenv.load();

        String hostname = dotenv.get("HOSTNAME");
        int port = Integer.parseInt(dotenv.get("PORT"));

        SocketIOServer server = createSocketIOServer(hostname, port);

        Game game = new Game();
        GameService gameService = new GameService(game);

        new GameServer(gameService, server);

        server.start();

        Runtime.getRuntime().addShutdownHook(new Thread(server::stop));
    }
}
