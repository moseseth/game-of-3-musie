package infrastructure;

import application.PlayerMove;
import com.corundumstudio.socketio.Configuration;
import com.corundumstudio.socketio.SocketIOServer;
import domain.entities.Player;
import domain.services.GameService;

import java.util.Map;
import java.util.Objects;
import java.util.UUID;
import java.util.concurrent.TimeUnit;

public class GameServer {
    private final GameService gameService;
    private final SocketIOServer server;

    public GameServer(GameService gameService) {
        this.gameService = gameService;
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
            String sessionId = client.getSessionId().toString();
            System.out.println("Player connected: " + client.getSessionId());

            if(gameService.isGameOver()) {
                gameService.resetGame();
            }

            gameService.addPlayerWithRandomUsername(sessionId);
        });

        server.addEventListener("start-game", String.class, (client, data, ackRequest) -> {
            if (gameService.getPlayers().length == 1) {
                server.getClient(client.getSessionId()).sendEvent("waiting-for-player-2");
            }

            if (gameService.getPlayers().length == 2) {
                System.out.println("Starting the game...");

                gameService.startGame(client.getSessionId().toString());
                Player currentPlayer = gameService.getCurrentPlayer();
                String currentPlayerId = currentPlayer.getId();

                Map<String, Object> moves = Map.of(
                        "id", currentPlayerId,
                        "randomNumber", currentPlayer.getLatestMove().getCurrentNumber()
                );

                server.getClient(UUID.fromString(currentPlayerId)).sendEvent("player-joined");
                server.getBroadcastOperations().sendEvent("random-number", moves);
                server.getBroadcastOperations().sendEvent("game-started", data);
                client.sendEvent("game-started", data);

                if (!gameService.isAutomatic()) {
                    return;
                }

                boolean isOpponentTurn = true;
                while (!gameService.isGameOver()) {
                    Player player = isOpponentTurn ? gameService.getOpponent() : gameService.getCurrentPlayer();
                    int opponentIndex = isOpponentTurn ? 0 : 1;
                    PlayerMove playerMove = gameService.handleAutoMove(player, opponentIndex);
                    handleGameState(playerMove);

                    try {
                        TimeUnit.SECONDS.sleep(1);
                    } catch (InterruptedException e) {
                        System.err.println(e.getMessage());
                    }

                    isOpponentTurn = !isOpponentTurn;
                }

            }
        });

        server.addEventListener("player-move", String.class, (client, move, ackRequest) -> {
            Player player = gameService.findPlayerById(client.getSessionId().toString());
            String opponent = gameService.getOpponent().getId();
            int opponentIndex = Objects.equals(opponent, player.getId()) ? 0 : 1;

            PlayerMove playerMove = gameService.handleMove(player,  opponentIndex, Integer.parseInt(move));
            handleGameState(playerMove);
        });

        server.addDisconnectListener(client -> {
            System.out.println("Player disconnected: " + client.getSessionId());

            Player player = gameService.findPlayerById(client.getSessionId().toString());
            gameService.removePlayer(player);
        });
    }

    private void handleGameState(PlayerMove playerMove) {
        if (playerMove.move().containsValue(1)) {
            server.getBroadcastOperations().sendEvent("game-over", "Winner: " + playerMove.player().getName());
        } else {
            server.getClient(UUID.fromString(playerMove.player().getId())).sendEvent("your-turn");
        }

        server.getBroadcastOperations().sendEvent("game-state", playerMove.move());
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
