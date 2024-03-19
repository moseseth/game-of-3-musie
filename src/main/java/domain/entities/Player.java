package domain.entities;

import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@Getter
@ToString
@EqualsAndHashCode
public class Player {
    private final String id;
    private final String name;

    @Setter
    private List<Move> moves;

    public Player(String id,
                  String name) {
        this.id = id;
        this.name = name;
        this.moves = new ArrayList<>();
    }

    public void addMove(Move move) {
        moves.add(move);
    }

    public Move getLatestMove() {
        return moves.isEmpty() ? null : moves.get(moves.size() - 1);
    }

    public Move initialMove(int startNumber) {
        return new Move(startNumber, startNumber, 0, 0, 0);
    }

    public Move generateAutoMove(int startNumber, int currentNumber) {
        int remainder = currentNumber % 3;
        int moveValue = (remainder == 0) ? 0 : (remainder == 1) ? -1 : 1;
        int resultingNumber = currentNumber + moveValue;
        int result = resultingNumber / 3;

        return new Move(startNumber, currentNumber, moveValue, resultingNumber, result);
    }

    public Move makeMove(int startNumber, int currentNumber, int move) {
        if (Math.abs(move) != 1 && move != 0) {
            throw new IllegalArgumentException("Number must be either -1, 0, or 1");
        }

        int resultingNumber = currentNumber + move;
        int result = resultingNumber / 3;

        return new Move(startNumber, currentNumber, move, resultingNumber, result);
    }
}
