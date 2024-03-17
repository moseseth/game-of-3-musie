package application;

import domain.entities.Game;
import domain.entities.Player;
import lombok.Getter;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

@Getter
public class GameService {
    private final Game game;

    @Getter
    private final Set<Player> players;

    private final Set<String> uniqueSessionIds;

    public GameService() {
        this.game = new Game();
        this.players = new HashSet<>();
        this.uniqueSessionIds = new HashSet<>();
    }

    public void addPlayer(Player player) {
        if (uniqueSessionIds.add(player.getSessionId())) {
            players.add(player);
        } else {
            System.out.println("Player with sessionId " + player.getSessionId() + " already exists.");
        }
    }

    public void removePlayer(String sessionId) {
        Player playerToRemove = null;
        for (Player player : players) {
            if (player.getSessionId().equals(sessionId)) {
                playerToRemove = player;
                break;
            }
        }

        if (playerToRemove != null) {
            players.remove(playerToRemove);
        }
    }

    public Map<String, Object> getGameState() {
        Map<String, Object> gameState = new HashMap<>();
        gameState.put("currentNumber", getCurrentPlayer().getCurrentNumber());
        gameState.put("currentPlayer", getCurrentPlayer().getSessionId());
        return gameState;
    }

    public void switchTurns() {
        Player temp = game.getCurrentPlayer();
        game.setCurrentPlayer(game.getOpponent());
        game.setOpponent(temp);
    }

    public Player getCurrentPlayer() {
        return game.getCurrentPlayer();
    }

    public Player getOpponent() {
        return game.getOpponent();
    }

    public void startGame() {
        game.start();
    }

    public boolean isGameStarted() {
        return game.isGameStarted();
    }

    public boolean isGameOver() {
        return game.isGameOver();
    }

    public Player getWinner() {
        return game.getWinner();
    }

    public void handleManualMove(String sessionId, int move) {
        Player currentPlayer = findPlayerBySessionId(sessionId);
        if (currentPlayer != null) {
            game.playRound(currentPlayer, move);
            game.generateOutput(currentPlayer, move);
        } else {
            System.out.println("Player not found for session ID: " + sessionId);
        }
    }

    public void handleAutomaticMove(String sessionId) {
        Player currentPlayer = findPlayerBySessionId(sessionId);
        if (currentPlayer != null) {
            int currentNumber = currentPlayer.getCurrentNumber();
            int remainder = currentNumber % 3;
            int move;
            if (remainder == 0) {
                move = 0;
            } else {
                move = (remainder == 1) ? -1 : 1;
            }

            game.playRound(currentPlayer, move);
            game.generateOutput(currentPlayer, move);
        } else {
            System.out.println("Player not found for session ID: " + sessionId);
        }
    }

    private Player findPlayerBySessionId(String sessionId) {
        for (Player player : players) {
            if (player.getSessionId().equals(sessionId)) {
                return player;
            }
        }

        return null;
    }
}

