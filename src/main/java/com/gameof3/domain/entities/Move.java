package com.gameof3.domain.entities;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;

@Getter
@Setter
@NoArgsConstructor
@ToString
public class Move {
    private int startingNumber;
    private int currentNumber;
    private int move;
    private int resultingNumber;
    private int result;

    public Move(int startingNumber, int currentNumber, int move, int resultingNumber, int result) {
        this.startingNumber = startingNumber;
        this.currentNumber = currentNumber;
        this.move = move;
        this.resultingNumber = resultingNumber;
        this.result = result;
    }
}
