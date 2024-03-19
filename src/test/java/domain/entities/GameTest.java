package domain.entities;

import application.PlayerMove;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;

public class GameTest {
    private Game game;
    private Player player1;
    private Player player2;

    @BeforeEach
    public void setUp() {
        game = new Game();
        player1 = new Player("1", "Alice");
        player2 = new Player("2", "Bob");
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
    public void testFindPlayerById() {
        game.addPlayer(player1);
        game.addPlayer(player2);

        Optional<Player> foundPlayer = game.findById(game.getPlayers(), "1");

        assertTrue(foundPlayer.isPresent());
        assertEquals(player1, foundPlayer.get());
    }

//    @Test
//    public void testHandleMove() {
//        game.addPlayer(player1);
//        game.addPlayer(player2);
//
//        Optional<PlayerMove> playerMove = game.handleMove(player1, 1, -1);
//
//        assertNotNull(playerMove);
//    }
//
//    @Test
//    public void testHandleAutoMove() {
//        game.addPlayer(player1);
//        game.addPlayer(player2);
//
//        Optional<PlayerMove> playerMove = game.handleAutoMove(player1, 1);
//
//        assertNotNull(playerMove);
//    }

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
