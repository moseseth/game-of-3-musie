package domain.entities;

import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

@Getter
@Setter
@ToString
public class Game {
    private Player currentPlayer;
    private Player opponent;
    private boolean gameStarted;
    private int initialNumber;

    @Getter
    private boolean gameOver;

    public Game() {
        this.gameStarted = false;
        this.gameOver = false;
    }

    public void start() {
        if (!gameStarted) {
            int randomNumber = generateRandomNumber();
            currentPlayer.setCurrentNumber(randomNumber);
            opponent.setCurrentNumber(randomNumber);
            this.setGameStarted(true);
        }
    }

    private int generateRandomNumber() {
        // Random random = new Random();
        // return random.nextInt(Integer.MAX_VALUE); // Generate a random number

        return 56;
    }

    public void playRound(Player player, int move) {
        if (!gameOver && gameStarted) {
            int currentPlayerNumber = player.getCurrentNumber();
            this.initialNumber = currentPlayerNumber;
            if (currentPlayerNumber == 1) {
                gameOver = true;
                return; // Game already over
            }

            currentPlayerNumber += move;
            int result = currentPlayerNumber / 3;

            if (result == 1) {
                gameOver = true;
                player.setCurrentNumber(1); // Game over
            } else {
                player.setCurrentNumber(result);
                System.out.println("CURRUENT-->" + getCurrentPlayer());
                System.out.println("OPPON-->" + getOpponent());
            }
        }
    }

    public Player getWinner() {
        // Logic to determine the winner
        if (currentPlayer.getCurrentNumber() == 1) {
            return currentPlayer;
        } else if (opponent.getCurrentNumber() == 1) {
            return opponent;
        } else {
            return null; // Game not over yet
        }
    }

    public void generateOutput(Player player, int move) {
        int currentNumber = player.getCurrentNumber();
        int addedNumber = this.getInitialNumber() + move;

        System.out.println("Player " + player.getSessionId() + " added " + move + " to " + this.getInitialNumber() + ", resulting in " + addedNumber);
        System.out.println("Resulting number after division: " + currentNumber);
    }
}
