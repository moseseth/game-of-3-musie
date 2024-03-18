package infrastructure;

import com.corundumstudio.socketio.Configuration;
import com.corundumstudio.socketio.SocketIOServer;
import domain.model.Player;
import domain.services.GameService;

import java.util.UUID;
import java.util.concurrent.TimeUnit;

public class GameServer {
    private final GameService gameService;
    private final SocketIOServer server;

    public GameServer() {
        this.gameService = new GameService();
        this.server = createSocketIOServer();
        initializeSocketEventListeners();
    }

    private SocketIOServer createSocketIOServer() {
        Configuration config = new Configuration();
        config.setHostname("localhost");
        config.setPort(9092);
        return new SocketIOServer(config);
    }

    private void initializeSocketEventListeners() {
        // Add event listeners
        server.addConnectListener(client -> {
            System.out.println("Player connected: " + client.getSessionId());
            gameService.addPlayerWithRandomUsername(client.getSessionId().toString());
        });

        server.addEventListener("start-game", String.class, (client, data, ackRequest) -> {
            if (gameService.getPlayers().length == 1) {
                // Notify player 1 that they are waiting for player 2 to join
                server.getClient(client.getSessionId()).sendEvent("waiting-for-player-2");
            }

            if (gameService.getPlayers().length == 2) {
                System.out.println("Starting the game...");
                gameService.startGame(client.getSessionId().toString());

                // Notify the initiating player that the other player has joined
                server.getClient(UUID.fromString(gameService.getCurrentPlayer().getId())).sendEvent("player-joined");

                server.getClient(UUID.fromString(gameService.getCurrentPlayer().getId())).sendEvent("random-number",
                        gameService.getCurrentPlayer().getLatestMove().getCurrentNumber());
                server.getClient(UUID.fromString(gameService.getOpponent().getId())).sendEvent("random-number",
                        gameService.getCurrentPlayer().getLatestMove().getCurrentNumber());

                // Send the "game-started" event to both players along with the initiator's ID
                client.sendEvent("game-started", data);
                server.getBroadcastOperations().sendEvent("game-started", data);

                boolean isOpponentTurn = true;
                while (!gameService.isGameOver()) {
                    if (isOpponentTurn) {
                        gameService.handleAutoMove(gameService.getOpponent(), 0);
                    } else {
                        gameService.handleAutoMove(gameService.getCurrentPlayer(), 1);
                    }

                    handleGameState();

                    try {
                        TimeUnit.SECONDS.sleep(1); // Adjust the delay time as needed
                    } catch (InterruptedException e) {
                        System.out.println(e.getMessage());
                    }

                    isOpponentTurn = !isOpponentTurn;
                }

            }
        });

//        server.addEventListener("player-move", String.class, (client, move, ackRequest) -> {
//            System.out.println("Player " + client.getSessionId() + " made a move: " + move);
//
//            // Call the GameService to handle the move
//            gameService.handleManualMove(Integer.parseInt(move));
//            handleGameState();
//        });

        server.addDisconnectListener(client -> {
            System.out.println("Player disconnected: " + client.getSessionId());

            // Remove the player from the game when they disconnect
            Player player = gameService.findPlayerById(client.getSessionId().toString());
            gameService.removePlayer(player);
        });
    }

    private void handleGameState() {
        if (gameService.isGameOver()) {
            Player winner = gameService.getWinner();
            server.getBroadcastOperations().sendEvent("game-over", "Winner: " + winner.getName());
        } else {
            // Notify the next player that it's their turn
            server.getClient(UUID.fromString(gameService.getCurrentPlayer().getId())).sendEvent("your-turn");
            // Notify the opponent that it's not their turn anymore
            server.getClient(UUID.fromString(gameService.getOpponent().getId())).sendEvent("opponent-turn");
        }
    }

    public void startGameServer() {
        this.server.start();
        System.out.println("Socket.IO server started");
    }

    public void stopGameServer() {
        server.stop();
        System.out.println("Socket.IO server stopped");
    }
}
