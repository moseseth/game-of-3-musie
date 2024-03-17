package infrastructure;

import application.GameService;
import com.corundumstudio.socketio.Configuration;
import com.corundumstudio.socketio.SocketIOServer;
import domain.entities.Game;
import domain.entities.Player;

import java.util.Objects;
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
        // Configure Socket.IO server
        Configuration config = new Configuration();
        config.setHostname("localhost");
        config.setPort(9092);
        return new SocketIOServer(config);
    }

    private void initializeSocketEventListeners() {
        // Add event listeners
        server.addConnectListener(client -> {
            System.out.println("Player connected: " + client.getSessionId());

            if (gameService.getPlayers().size() > 2) {
                // Inform the client that the game is full and they cannot join
                client.sendEvent("game-full", "Sorry, the game is full. Please try again later.");
                return;
            }

            // Add the player to the game when they connect
            Player newPlayer = new Player(client.getSessionId().toString());
            gameService.addPlayer(newPlayer);
        });

        server.addEventListener("start-game", String.class, (client, data, ackRequest) -> {
            Player player1 = gameService.getPlayers().stream().findFirst().orElse(null);
            Player player2 = gameService.getPlayers().stream().skip(1).findFirst().orElse(null);

            if (gameService.getPlayers().size() == 1) {
                // Notify player 1 that they are waiting for player 2 to join
                server.getClient(UUID.fromString(player1.getSessionId())).sendEvent("waiting-for-player-2");
            }

            if (gameService.getPlayers().size() == 2 && player2 != null) {
                System.out.println("Starting the game...");

                Game game = gameService.getGame();
                if (Objects.equals(player1.getSessionId(), data)) {
                    game.setCurrentPlayer(player1);
                    game.setOpponent(player2);
                } else {
                    game.setCurrentPlayer(player2);
                    game.setOpponent(player1);
                }

                game.setGameStarted(false);
                gameService.startGame();

                // Notify the initiating player that the other player has joined
                server.getClient(UUID.fromString(player1.getSessionId())).sendEvent("player-joined");

                // client.sendEvent("start-game", new PlayerLabel("Player 1", "Player 2"));

                // Send the random number to both players
                server.getClient(UUID.fromString(player1.getSessionId())).sendEvent("random-number",
                        player1.getCurrentNumber());
                server.getClient(UUID.fromString(player2.getSessionId())).sendEvent("random-number",
                        player2.getCurrentNumber());

                // Send the "game-started" event to both players along with the initiator's ID
                client.sendEvent("game-started", data);
                server.getBroadcastOperations().sendEvent("game-started", data);

                boolean currentPlayerTurn = false;
                while (!gameService.isGameOver()) {
                    String sessionId;
                    if (currentPlayerTurn) {
                        sessionId = game.getCurrentPlayer().getSessionId();
//                        game.setCurrentPlayer(game.getCurrentPlayer());
//                        game.setOpponent(game.getOpponent());
                    } else {
                        sessionId = game.getOpponent().getSessionId();
//                        game.setCurrentPlayer(game.getOpponent());
//                        game.setOpponent(game.getCurrentPlayer());
                    }

                    gameService.handleAutomaticMove(sessionId);
                    gameService.getGameState();
                    handleGameState();

                    // Sleep for a short duration to simulate delay between moves
                    try {
                        TimeUnit.SECONDS.sleep(1); // Adjust the delay time as needed
                    } catch (InterruptedException e) {
                        System.out.println(e.getMessage());
                    }

                    currentPlayerTurn = !currentPlayerTurn;
                }
            }
        });

        server.addEventListener("player-move", String.class, (client, move, ackRequest) -> {
            System.out.println("Player " + client.getSessionId() + " made a move: " + move);

            // Call the GameService to handle the move
            gameService.handleManualMove(client.getSessionId().toString(), Integer.parseInt(move));
            handleGameState();
        });

        server.addDisconnectListener(client -> {
            System.out.println("Player disconnected: " + client.getSessionId());

            // Remove the player from the game when they disconnect
            gameService.removePlayer(client.getSessionId().toString());

            // If the game is ongoing and a player disconnects, end the game
            if (gameService.isGameStarted()) {
                server.getBroadcastOperations().sendEvent("game-over", "Game ended: Opponent disconnected");
            }
        });
    }

    private void handleGameState() {
        if (gameService.isGameOver()) {
            Player winner = gameService.getWinner();
            server.getBroadcastOperations().sendEvent("game-over", "Winner: " + winner.getSessionId());
        } else {
            // Notify both players of the updated game state
            server.getBroadcastOperations().sendEvent("game-state", gameService.getGameState());

            // Notify the next player that it's their turn
            server.getClient(UUID.fromString(gameService.getCurrentPlayer().getSessionId())).sendEvent("your-turn");
            // Notify the opponent that it's not their turn anymore
            server.getClient(UUID.fromString(gameService.getOpponent().getSessionId())).sendEvent("opponent-turn");
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
