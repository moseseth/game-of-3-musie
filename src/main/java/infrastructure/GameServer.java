package infrastructure;

import application.PlayerMove;
import com.corundumstudio.socketio.Configuration;
import com.corundumstudio.socketio.SocketIOServer;
import domain.entities.Player;
import domain.events.GameEvent;
import domain.events.GameStartedEvent;
import domain.services.GameService;

import java.util.Arrays;
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

            if (gameService.isWinner() || gameService.isLoser()) {
                gameService.resetGame();
            }

            gameService.addPlayerWithRandomUsername(sessionId);
        });

        server.addEventListener(GameEvent.START_GAME.getEventName(), GameStartedEvent.class, (client, data, ackRequest) -> {
            String playerId = data.getPlayerId();
            boolean isAutoMode = data.isAutoMode();

            if (isAutoMode) {
                gameService.setAutoMode(false);
            }

            if (gameService.getPlayers().length == 1) {
                server.getClient(client.getSessionId()).sendEvent(GameEvent.WAITING_FOR_PLAYER_2.getEventName());
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

                server.getClient(UUID.fromString(currentPlayerId)).sendEvent(GameEvent.PLAYER_JOINED.getEventName());
                server.getBroadcastOperations().sendEvent(GameEvent.RANDOM_NUMBER.getEventName(), moves);
                server.getBroadcastOperations().sendEvent(GameEvent.GAME_STARTED.getEventName(), playerId);
                client.sendEvent(GameEvent.GAME_STARTED.getEventName(), playerId);

                if (gameService.isManualMode()) {
                    return;
                }

                boolean isOpponentTurn = true;
                while (!gameService.isWinner()) {
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

        server.addEventListener(GameEvent.PLAYER_MOVE.getEventName(), String.class, (client, move, ackRequest) -> {
            Player player = gameService.findPlayerById(client.getSessionId().toString());
            String opponent = gameService.getOpponent().getId();
            int opponentIndex = Objects.equals(opponent, player.getId()) ? 0 : 1;

            PlayerMove playerMove = gameService.handleMove(player, opponentIndex, Integer.parseInt(move));
            handleGameState(playerMove);

            server.getBroadcastOperations().sendEvent(GameEvent.GAME_STARTED.getEventName(), player.getId());
        });

        server.addDisconnectListener(client -> {
            System.out.println("Player disconnected: " + client.getSessionId());

            Player player = gameService.findPlayerById(client.getSessionId().toString());
            gameService.removePlayer(player);
        });
    }

    private void handleGameState(PlayerMove playerMove) {
        if (gameService.isWinner()) {
            server.getBroadcastOperations().sendEvent(GameEvent.GAME_OVER.getEventName(),
                    "Winner: " + playerMove.player().getName());
        }

        if (gameService.isLoser()) {
            server.getBroadcastOperations().sendEvent(GameEvent.GAME_OVER.getEventName(),
                    "Loser: " + playerMove.player().getName());
        }

        server.getBroadcastOperations().sendEvent(GameEvent.GAME_STATE.getEventName(), playerMove.move());
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
