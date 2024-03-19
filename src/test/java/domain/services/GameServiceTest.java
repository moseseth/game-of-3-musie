package domain.services;

import com.gameof3.domain.entities.Game;
import com.gameof3.domain.entities.Player;
import com.gameof3.domain.services.GameService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.LinkedList;

import static org.junit.jupiter.api.Assertions.*;

public class GameServiceTest {
    private GameService gameService;
    private Player player1;
    private Player player2;

    @BeforeEach
    public void setUp() {
        Game game = new Game();
        gameService = new GameService(game);
        player1 = new Player("1", "Alice");
        player2 = new Player("2", "Bob");
    }

    @Test
    public void testAddPlayer() {
        gameService.addPlayer(player1);
        gameService.addPlayer(player2);

        LinkedList<Player> players = gameService.getPlayers();
        assertEquals(player1, players.get(0));
        assertEquals(player2, players.get(1));
    }

    @Test
    public void testRemovePlayer() {
        gameService.addPlayer(player1);
        gameService.addPlayer(player2);

        gameService.removePlayer(player1);

        LinkedList<Player> players = gameService.getPlayers();
        assertEquals(player2, players.get(0));
    }

    @Test
    public void testResetGame() {
        gameService.addPlayer(player1);
        gameService.addPlayer(player2);

        gameService.resetGame();

        LinkedList<Player> players = gameService.getPlayers();
        assertTrue(players.isEmpty());
    }

    @Test
    public void testStartGame() {
        gameService.addPlayer(player1);
        gameService.addPlayer(player2);

        gameService.startGame(player1.getId());

        Player currentPlayer = gameService.getCurrentPlayer();
        assertNotNull(currentPlayer);
        assertEquals(player1, currentPlayer);
    }

    @Test
    public void testSwapPlayers() {
        gameService.addPlayer(player1);
        gameService.addPlayer(player2);

        gameService.swapPlayers();

        LinkedList<Player> players = gameService.getPlayers();
        assertEquals(player2, players.get(0));
        assertEquals(player1, players.get(1));
    }
}
