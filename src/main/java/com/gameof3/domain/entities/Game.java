package com.gameof3.domain.entities;

import com.gameof3.application.PlayerMove;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

import java.util.*;

@Getter
@ToString
public class Game {
    private String id;
    private final LinkedList<Player> players;

    private boolean winner;
    private boolean loser;

    @Setter
    private boolean automatic;

    public Game() {
        this.id = UUID.randomUUID().toString();
        this.players = new LinkedList<>();
        this.winner = false;
        this.loser = false;
        this.automatic = true;
    }

    public Game(LinkedList<Player> players) {
        this.players = players;
    }

    public void addPlayer(Player player) {
        addUnique(players, player);
    }

    public void removePlayer(Player player) {
        players.remove(player);
    }

    public Optional<Player> findById(LinkedList<Player> players, String id) {
        return players.stream()
                .filter(player -> player.getId().equals(id))
                .findFirst();
    }

    public Optional<PlayerMove> handleMove(Player player, int opponentIndex, int move) {
        if (isWinner() || isLoser()) {
            return Optional.empty();
        }

        try {
            Move currentMove;
            if (player.getMoves().isEmpty()) {
                currentMove = player.makeMove(
                        players.get(0).getLatestMove().getStartingNumber(),
                        players.get(0).getLatestMove().getCurrentNumber(), move);
            } else {
                currentMove = player.makeMove(
                        player.getLatestMove().getStartingNumber(),
                        players.get(opponentIndex).getLatestMove().getCurrentNumber(), move);
            }

            return Optional.ofNullable(getPlayerMove(player, currentMove));
        } catch (IllegalArgumentException exception) {
            System.err.println("Latest move can not be empty");
        }

        return Optional.empty();
    }

    public Optional<PlayerMove> handleAutoMove(Player player, int opponentIndex) {
        if (isWinner()) {
            return Optional.empty();
        }

        try {
            Move currentMove;
            if (player.getMoves().isEmpty()) {
                int startingNumber = players.get(0).getLatestMove().getStartingNumber();
                int currentNumber = players.get(0).getLatestMove().getCurrentNumber();
                currentMove = player.generateAutoMove(startingNumber, currentNumber);
            } else {
                currentMove = player.generateAutoMove(player.getLatestMove().getStartingNumber(),
                        players.get(opponentIndex).getLatestMove().getCurrentNumber());
            }

            return Optional.of(getPlayerMove(player, currentMove));
        } catch (IllegalArgumentException exception) {
            System.out.println("Latest move can not be empty");
        }

        return Optional.empty();
    }

    private PlayerMove getPlayerMove(Player player, Move currentMove) {
        currentMove.setCurrentNumber(currentMove.getResult());
        player.addMove(currentMove);
        PlayerMove playerMove = notifyMove(player, currentMove);

        this.winner = (player.getLatestMove().getCurrentNumber() == 1);
        this.loser = (player.getLatestMove().getCurrentNumber() < 1);

        return playerMove;
    }

    public PlayerMove notifyMove(Player player, Move move) {
        System.out.println("Player " + player.getId() + " Moves --> " + move);
        Map<String, Object> playerState = Map.of(
                "playerName", player.getName(),
                "playerPoint", move.getCurrentNumber()
        );

        return new PlayerMove(playerState, player);
    }

    public void resetGame() {
        players.clear();
        winner = false;
        loser = false;
        automatic = true;
    }

    public static void addUnique(LinkedList<Player> list, Player player) {
        if (!list.contains(player)) {
            list.add(player);
        }
    }
}
