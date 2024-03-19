package domain.entities;

import application.PlayerMove;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.UUID;

@Getter
@ToString
public class Game {
    private String id;
    private Player[] players;

    private boolean winner;
    private boolean loser;

    @Setter
    private boolean automatic;

    private static final int MAX_PLAYERS = 2;

    public Game() {
        this.id = UUID.randomUUID().toString();
        this.players = new Player[MAX_PLAYERS];
        this.winner = false;
        this.loser = false;
        this.automatic = true;
    }

    public Game(Player[] players) {
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

    public Player findPlayerById(String id) {
        for (Player player : players) {
            if (player != null && player.getId().equals(id)) {
                return player;
            }
        }

        return null; // Player not found
    }

    public PlayerMove handleMove(Player player, int opponentIndex, int move) {
        if (isWinner() || isLoser()) {
            return null;
        }

        Move currentMove;
        if (player.getMoves().isEmpty()) {
            currentMove = player.makeMove(
                    getPlayers()[0].getLatestMove().getStartingNumber(),
                    getPlayers()[0].getLatestMove().getCurrentNumber(), move);
        } else {
            currentMove = player.makeMove(
                    player.getLatestMove().getStartingNumber(),
                    getPlayers()[opponentIndex].getLatestMove().getCurrentNumber(), move);
        }

        return getPlayerMove(player, currentMove);
    }

    public PlayerMove handleAutoMove(Player player, int opponentIndex) {
        if (isWinner()) {
            return null;
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

        return getPlayerMove(player, currentMove);
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
        Arrays.fill(players, null);
        winner = false;
        loser = false;
    }
}
