package infrastructure;

import application.PlayerMove;
import com.corundumstudio.socketio.Configuration;
import com.corundumstudio.socketio.SocketIOServer;
import com.corundumstudio.socketio.listener.ConnectListener;
import com.corundumstudio.socketio.listener.DataListener;
import com.corundumstudio.socketio.listener.DisconnectListener;
import domain.entities.Move;
import domain.entities.Player;
import domain.events.GameEvent;
import domain.events.GameStartedEvent;
import domain.services.GameService;
import lombok.Setter;

import java.util.Map;
import java.util.Objects;
import java.util.Optional;
import java.util.concurrent.TimeUnit;

public class GameServer {
    private final GameService gameService;

    @Setter
    private SocketIOServer server;

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
        // event listeners
        server.addConnectListener(getConnectListener());
        server.addEventListener(GameEvent.START_GAME.getEventName(), GameStartedEvent.class, getStartGameEventListener());
        server.addEventListener(GameEvent.PLAYER_MOVE.getEventName(), String.class, getPlayerMoveEventListener());
        server.addDisconnectListener(getDisconnectListener());
    }

    public ConnectListener getConnectListener() {
        return client -> {
            String sessionId = client.getSessionId().toString();
            System.out.println("Player connected: " + client.getSessionId());

            if (gameService.isWinner() || gameService.isLoser()) {
                gameService.resetGame();
            }

            if (gameService.getPlayers().size() < 2) {
                gameService.addPlayerWithRandomUsername(sessionId);
            }
        };
    }

    public DisconnectListener getDisconnectListener() {
        return client -> {
            System.out.println("Player disconnected: " + client.getSessionId());

            Optional<Player> player = gameService.findPlayerById(client.getSessionId().toString());
            player.ifPresent(gameService::removePlayer);
        };
    }

    public DataListener<GameStartedEvent> getStartGameEventListener() {
        return (client, data, ackRequest) -> {
            String playerId = data.getPlayerId();
            boolean isAutoMode = data.isAutoMode();

            if (isAutoMode) {
                gameService.setAutoMode(false);
            }

            if (gameService.getPlayers().size() == 1) {
                server.getClient(client.getSessionId()).sendEvent(GameEvent.WAITING_FOR_PLAYER_2.getEventName());
            }

            if (gameService.getPlayers().size() == 2) {
                System.out.println("Starting the game...");

                gameService.startGame(client.getSessionId().toString());
                Player currentPlayer = gameService.getCurrentPlayer();
                String currentPlayerId = currentPlayer.getId();

                Move latestMove = currentPlayer.getLatestMove();
                int randomNumber = latestMove.getCurrentNumber();

                Map<String, Object> moves = Map.of(
                        "id", currentPlayerId,
                        "randomNumber", randomNumber
                );

                server.getBroadcastOperations().sendEvent(GameEvent.RANDOM_NUMBER.getEventName(), moves);
                server.getBroadcastOperations().sendEvent(GameEvent.GAME_STARTED.getEventName(), playerId);
                client.sendEvent(GameEvent.GAME_STARTED.getEventName(), playerId);

                if (gameService.isManualMode()) {
                    return;
                }

                autoPlay();
            }
        };
    }

    private void autoPlay() {
        boolean isOpponentTurn = true;
        while (!gameService.isWinner()) {
            Player player = isOpponentTurn ? gameService.getOpponent() : gameService.getCurrentPlayer();
            int opponentIndex = isOpponentTurn ? 0 : 1;
            Optional<PlayerMove> playerMove = gameService.handleAutoMove(player, opponentIndex);
            playerMove.ifPresent(this::handleGameState);

            try {
                TimeUnit.SECONDS.sleep(1);
            } catch (InterruptedException e) {
                System.err.println(e.getMessage());
            }

            isOpponentTurn = !isOpponentTurn;
        }
    }

    public DataListener<String> getPlayerMoveEventListener() {
        return (client, move, ackRequest) -> {
            Optional<Player> player = gameService.findPlayerById(client.getSessionId().toString());
            String opponent = gameService.getOpponent().getId();

            if (player.isPresent()) {
                int opponentIndex = Objects.equals(opponent, player.get().getId()) ? 0 : 1;
                Optional<PlayerMove> playerMove = gameService.handleMove(player.get(), opponentIndex, Integer.parseInt(move));
                playerMove.ifPresent(this::handleGameState);

                server.getBroadcastOperations().sendEvent(GameEvent.GAME_STARTED.getEventName(), player.get().getId());
            }
        };
    }

    public void handleGameState(PlayerMove playerMove) {
        String playerName = playerMove.player().getName();
        String gameStateEvent = GameEvent.GAME_STATE.getEventName();

        if (gameService.isWinner()) {
            String message = "Winner: " + playerName;
            server.getBroadcastOperations().sendEvent(GameEvent.GAME_OVER.getEventName(), message);
        } else if (gameService.isLoser()) {
            String message = "Loser: " + playerName;
            server.getBroadcastOperations().sendEvent(GameEvent.GAME_OVER.getEventName(), message);
        }

        server.getBroadcastOperations().sendEvent(gameStateEvent, playerMove.move());
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
