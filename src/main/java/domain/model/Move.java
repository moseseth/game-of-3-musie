package domain.model;

import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

@Getter
@Setter
@ToString
public class Move {
    private int startingNumber;
    private int currentNumber;
    private Integer move;
    private Integer resultingNumber;
    private Integer result;

    public Move(int startingNumber, int currentNumber, Integer move, Integer resultingNumber, Integer result) {
        this.startingNumber = startingNumber;
        this.currentNumber = currentNumber;
        this.move = move;
        this.resultingNumber = resultingNumber;
        this.result = result;
    }
}
