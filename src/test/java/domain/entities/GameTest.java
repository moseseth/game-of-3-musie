package domain.entities;

import com.gameof3.application.PlayerMove;
import com.gameof3.domain.entities.Game;
import com.gameof3.domain.entities.Move;
import com.gameof3.domain.entities.Player;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.Map;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;

public class GameTest {
    private Game game;
    private Player player1;
    private Player player2;

    @BeforeEach
    public void setUp() {
        game = new Game();
        player1 = new Player("cc969531-dde3-4f43-a90e-7210ec7e099e", "Alice");
        player2 = new Player("49c60490-8fed-43da-9174-f900d876c43f", "Bob");
    }

    @Test
    public void testAddPlayer() {
        game.addPlayer(player1);
        game.addPlayer(player2);

        assertEquals(player1, game.getPlayers().get(0));
        assertEquals(player2, game.getPlayers().get(1));
    }

    @Test
    public void testRemovePlayer() {
        game.addPlayer(player1);
        game.addPlayer(player2);

        game.removePlayer(player1);

        assertEquals(player2, game.getPlayers().get(0));
    }

    @Test
    public void testHandleMove() {
        player1.addMove(new Move(12, 10, 0, 0, 0));
        player2.addMove(new Move(12, 9, 1, 0, 0));
        game.addPlayer(player1);
        game.addPlayer(player2);

        player1.makeMove(10, 10, 1);
        player2.makeMove(10, 3, 0);

        Optional<PlayerMove> playerMove = game.handleMove(player1, 1, -1);

        assertNotNull(playerMove);
        assertTrue(playerMove.isPresent());
        PlayerMove move = playerMove.get();

        assertEquals(player1, move.player());
        assertEquals(12, move.player().getLatestMove().getStartingNumber());
        assertEquals(2, move.player().getLatestMove().getCurrentNumber());
        assertEquals(Map.of("playerPoint", 2, "playerName", "Alice"), move.move());
    }

    @Test
    public void testHandleAutoMove() {
        player1.addMove(new Move(22, 22, 0, 0, 0));
        player2.addMove(new Move(22, 22, 0, 0, 0));
        game.addPlayer(player1);
        game.addPlayer(player2);

        Optional<PlayerMove> playerMove = game.handleAutoMove(player1, 1);

        assertNotNull(playerMove);
        assertTrue(playerMove.isPresent());
        PlayerMove move = playerMove.get();

        assertEquals(player1, move.player());
        assertEquals(22, move.player().getLatestMove().getStartingNumber());
        assertEquals(7, move.player().getLatestMove().getCurrentNumber());
        assertEquals(Map.of("playerPoint", 7, "playerName", "Alice"), move.move());
    }

    @Test
    public void testResetGame() {
        game.addPlayer(player1);
        game.addPlayer(player2);
        game.resetGame();

        assertTrue(game.getPlayers().isEmpty());
        assertFalse(game.isWinner());
        assertFalse(game.isLoser());
        assertTrue(game.isAutomatic());
    }
}
