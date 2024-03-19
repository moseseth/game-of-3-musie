package domain.services;

import application.PlayerMove;
import domain.entities.Game;
import domain.entities.Move;
import domain.entities.Player;

import java.util.HashSet;
import java.util.Objects;
import java.util.Random;
import java.util.Set;

public class GameService {
    private final Game game;
    private final Set<String> uniqueSessionIds;

    public GameService(Game game) {
        this.game = game;
        this.uniqueSessionIds = new HashSet<>();
    }

    public void addPlayer(Player player) {
        if (uniqueSessionIds.add(player.getId())) {
            game.addPlayer(player);
        } else {
            System.out.println("Player with sessionId " + player.getId() + " already exists.");
        }
    }

    public void removePlayer(Player player) {
        game.removePlayer(player);
    }

    public void resetGame() {
        game.resetGame();
    }

    public boolean isAutomatic() {
        return game.isAutomatic();
    }

    public Player[] getPlayers() {
        return game.getPlayers();
    }

    public Player findPlayerById(String sessionId) {
        return game.findPlayerById(sessionId);
    }

    public void startGame(String sessionId) {
        Player[] players = game.getPlayers();
        if (players.length != 2 || Objects.equals(players[0].getId(), sessionId)) {
            int startingNumber = generateRandomNumber();
            Player currentPlayer = getCurrentPlayer();

            Move move = currentPlayer.initialMove(startingNumber);
            currentPlayer.addMove(move);

            game.notifyMove(currentPlayer, move);
        } else {
            swapPlayers();
        }
    }

    public Player getCurrentPlayer() {
        return game.getPlayers()[0];
    }

    public Player getOpponent() {
        return game.getPlayers()[1];
    }

    public boolean isGameOver() {
        return game.isGameOver();
    }

    public PlayerMove handleMove(Player player, int opponentIndex, int move) {
        return game.handleMove(player, opponentIndex, move);
    }

    public PlayerMove handleAutoMove(Player player, int opponentIndex) {
        return game.handleAutoMove(player, opponentIndex);
    }

    public void addPlayerWithRandomUsername(String sessionId) {
        String playerName = "Player-" + sessionId.substring(0, 4);
        Player newPlayer = new Player(sessionId, playerName);
        addPlayer(newPlayer);
    }

    public void swapPlayers() {
        Player[] players = game.getPlayers();
        Player temp = players[0];
        players[0] = players[1];
        players[1] = temp;
    }

    private int generateRandomNumber() {
//         Random random = new Random();
//         return random.nextInt(Integer.MAX_VALUE);

        return 56;
    }
}

