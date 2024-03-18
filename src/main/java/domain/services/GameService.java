package domain.services;

import domain.model.Game;
import domain.model.Move;
import domain.model.Player;
import lombok.Setter;
import util.UsernameGenerator;

import java.util.*;

public class GameService {
    @Setter
    private Game game;
    private final Set<String> uniqueSessionIds;

    public GameService() {
        this.game = new Game();
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
            game.displayResult(currentPlayer, move);

            currentPlayer.addMove(move);
            players[0] = currentPlayer;
            setGame(new Game(players));
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

    public Player getWinner() {
        return game.getWinner();
    }

//    public void handleManualMove(int move) {
//        game.playRound(game.getCurrentPlayer(), move);
//    }

    public void handleAutoMove(Player player, int opponentIndex) {
        game.handleAutoMove(player, opponentIndex);
    }

    public void addPlayerWithRandomUsername(String sessionId) {
        String username = UsernameGenerator.generateUniqueRandomUsername();
        Player newPlayer = new Player(sessionId, username);
        addPlayer(newPlayer);
    }

    public void swapPlayers() {
        Player[] players = game.getPlayers();
        Player temp = players[0];
        players[0] = players[1];
        players[1] = temp;
    }

    private int generateRandomNumber() {
        // Random random = new Random();
        // return random.nextInt(Integer.MAX_VALUE); // Generate a random number
        return 100;
    }
}

