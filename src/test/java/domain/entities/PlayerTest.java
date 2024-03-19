package domain.entities;

import com.gameof3.domain.entities.Move;
import com.gameof3.domain.entities.Player;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import java.util.List;
import static org.junit.jupiter.api.Assertions.*;

public class PlayerTest {
    private Player player;

    @BeforeEach
    public void setUp() {
        player = new Player("1", "Alice");
    }

    @Test
    public void testConstructor() {
        assertEquals("1", player.getId());
        assertEquals("Alice", player.getName());
        assertNotNull(player.getMoves());
        assertTrue(player.getMoves().isEmpty());
    }

    @Test
    public void testAddMove() {
        Move move = new Move(1, 1, 1, 2, 0);
        player.addMove(move);

        List<Move> moves = player.getMoves();
        assertEquals(1, moves.size());
        assertTrue(moves.contains(move));
    }

    @Test
    public void testGetLatestMove() {
        try {
            player.getLatestMove().getStartingNumber();
        } catch (IllegalArgumentException e) {
            assertTrue(true);
        }

        Move move1 = new Move(1, 1, 1, 2, 0);
        player.addMove(move1);
        assertEquals(move1, player.getLatestMove());

        Move move2 = new Move(2, 2, -1, 1, 0);
        player.addMove(move2);
        assertEquals(move2, player.getLatestMove());
    }

    @Test
    public void testInitialMove() {
        Move initialMove = player.initialMove(5);

        assertEquals(5, initialMove.getStartingNumber());
        assertEquals(5, initialMove.getCurrentNumber());
        assertEquals(0, initialMove.getMove());
        assertEquals(0, initialMove.getResultingNumber());
        assertEquals(0, initialMove.getResult());
    }

    @Test
    public void testGenerateAutoMove() {
        Move autoMove = player.generateAutoMove(10, 8);

        assertEquals(10, autoMove.getStartingNumber());
        assertEquals(8, autoMove.getCurrentNumber());
        assertTrue(autoMove.getMove() == -1 || autoMove.getMove() == 0 || autoMove.getMove() == 1);
    }

    @Test
    public void testMakeMoveThrowsException() {
        int startNumber = 10;
        int currentNumber = 8;
        int move = 2;
        Player player = new Player("1", "Alice");

        IllegalArgumentException exception = assertThrows(IllegalArgumentException.class, () -> {
            Move manualMove = player.makeMove(startNumber, currentNumber, move);
            manualMove.setResult(1);
            manualMove.setCurrentNumber(-10);
        });

        assertEquals("Number must be either -1, 0, or 1", exception.getMessage());
    }

    @Test
    public void testMakeMove() {
        Move manualMove = player.makeMove(10, 8, 1);

        assertEquals(10, manualMove.getStartingNumber());
        assertEquals(8, manualMove.getCurrentNumber());
        assertEquals(1, manualMove.getMove());
        assertEquals(9, manualMove.getResultingNumber());
        assertEquals(3, manualMove.getResult());
    }
}

