package com.gameof3.domain.services;

import com.gameof3.application.PlayerMove;
import com.gameof3.domain.entities.Game;
import com.gameof3.domain.entities.Move;
import com.gameof3.domain.entities.Player;

import java.util.*;

public class GameService {
    private final Game game;

    public GameService(Game game) {
        this.game = game;
    }

    public void addPlayer(Player player) {
        game.addPlayer(player);
    }

    public void removePlayer(Player player) {
        game.removePlayer(player);
    }

    public void resetGame() {
        game.resetGame();
    }

    public void setAutoMode(boolean mode) {
        game.setAutomatic(mode);
    }

    public LinkedList<Player> getPlayers() {
        return game.getPlayers();
    }

    public Optional<Player> findPlayerById(String sessionId) {
        LinkedList<Player> players = getPlayers();
        return game.findById(players, sessionId);
    }

    public void startGame(String sessionId) {
        LinkedList<Player> players = game.getPlayers();

        if (!Objects.equals(players.get(0).getId(), sessionId)) {
            swapPlayers();
        }

        int startingNumber = generateRandomNumber();
        Player currentPlayer = getCurrentPlayer();
        Move move = currentPlayer.initialMove(startingNumber);
        currentPlayer.addMove(move);
        game.notifyMove(currentPlayer, move);
    }

    public Player getCurrentPlayer() {
        return game.getPlayers().get(0);
    }

    public Player getOpponent() {
        return game.getPlayers().get(1);
    }

    public boolean isWinner() {
        return game.isWinner();
    }

    public boolean isLoser() {
        return game.isLoser();
    }

    public boolean isManualMode() {
        return !game.isAutomatic();
    }

    public Optional<PlayerMove> handleMove(Player player, int opponentIndex, int move) {
        return game.handleMove(player, opponentIndex, move);
    }

    public Optional<PlayerMove> handleAutoMove(Player player, int opponentIndex) {
        return game.handleAutoMove(player, opponentIndex);
    }

    public void addPlayerWithRandomUsername(String sessionId) {
        String playerName = "Player-" + sessionId.substring(0, 4);
        Player newPlayer = new Player(sessionId, playerName);
        addPlayer(newPlayer);
    }

    public void swapPlayers() {
        LinkedList<Player> players = game.getPlayers();
        if (players != null && players.size() >= 2) {
            Player temp = players.get(0);
            players.set(0, players.get(1));
            players.set(1, temp);
        } else {
            System.out.println("Cannot swap players: Insufficient players in the game.");
        }
    }

    private int generateRandomNumber() {
        Random random = new Random();
        return random.nextInt(Integer.MAX_VALUE);
    }
}

