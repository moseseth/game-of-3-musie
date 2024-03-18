package domain.model;

import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

import java.util.Arrays;
import java.util.List;
import java.util.UUID;

@Getter
@ToString
public class Game {
    private final String id;
    private Player[] players;
    private boolean gameOver;

    @Setter
    private boolean automatic;

    private static final int MAX_PLAYERS = 2;

    public Game() {
        this.id = UUID.randomUUID().toString();
        this.players = new Player[MAX_PLAYERS];
        this.gameOver = false;
        this.automatic = true;
    }

    public Game(Player[] players) {
        this();
        if (players.length <= MAX_PLAYERS) {
            this.players = Arrays.copyOf(players, MAX_PLAYERS);
        }
    }

    public void addPlayer(Player player) {
        for (int i = 0; i < MAX_PLAYERS; i++) {
            if (players[i] == null) {
                players[i] = player;
                break;
            }
        }
    }

    public void removePlayer(Player player) {
        List<Player> playerList = Arrays.asList(players);
        playerList.remove(player);
        players = playerList.toArray(new Player[0]);
    }

    public Player getWinner() {
        for (Player player : players) {
            if (player != null && player.getLatestMove().getCurrentNumber() == 1) {
                return player;
            }
        }

        return null; // No winner found
    }

    public Player findPlayerById(String id) {
        for (Player player : players) {
            if (player != null && player.getId().equals(id)) {
                return player;
            }
        }

        return null; // Player not found
    }

    public void handleAutoMove(Player player, int opponentIndex) {
        if (isGameOver()) {
            return;
        }

        Move currentMove;
        if (player.getMoves().isEmpty()) {
            currentMove = player.generateAutoMove(
                    getPlayers()[0].getLatestMove().getStartingNumber(),
                    getPlayers()[0].getLatestMove().getCurrentNumber());
        } else {
            currentMove = player.generateAutoMove(
                    player.getLatestMove().getStartingNumber(),
                    getPlayers()[opponentIndex].getLatestMove().getCurrentNumber());
        }

        currentMove.setCurrentNumber(currentMove.getResult());
        player.addMove(currentMove);
        displayResult(player, currentMove);

        if (player.getLatestMove().getCurrentNumber() == 1) {
            gameOver = true;
        }
    }

    public void displayResult(Player player, Move move) {
        System.out.println("Player " + player.getId() + " Moves--> " + move);
    }
}
