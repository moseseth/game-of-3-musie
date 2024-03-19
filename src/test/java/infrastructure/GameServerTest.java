package infrastructure;

import com.corundumstudio.socketio.SocketIOClient;
import com.gameof3.domain.entities.Player;
import com.gameof3.domain.services.GameService;
import com.gameof3.infrastructure.GameServer;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import java.util.UUID;

import static org.mockito.Mockito.*;

public class GameServerTest {
    @Mock
    private GameService gameServiceMock;

    private GameServer gameServer;

    @BeforeEach
    public void setUp() {
        MockitoAnnotations.openMocks(this);
        gameServer = new GameServer(gameServiceMock);
    }

    @Test
    public void testConnectEventListener() {
        String sessionId = "400bf517-aef1-45d3-9826-0ca3cd313643";
        when(gameServiceMock.isWinner()).thenReturn(false);
        when(gameServiceMock.isLoser()).thenReturn(false);

        gameServer.getConnectListener().onConnect(mockClient(sessionId));

        verify(gameServiceMock).addPlayerWithRandomUsername(sessionId);
    }

    @Test
    public void testPlayerMoveEventListener() throws Exception {
        String moveData = "5";
        String playerId = "6264a5fd-a5c9-4fa2-b2db-70514ad731bb";
        Player currentPlayer = new Player(playerId, "Player1");

        when(gameServiceMock.findPlayerById(playerId)).thenReturn(java.util.Optional.of(currentPlayer));
        when(gameServiceMock.getOpponent()).thenReturn(new Player("player2", "Player2"));

        gameServer.getPlayerMoveEventListener().onData(mockClient(playerId), moveData, null);

        verify(gameServiceMock).handleMove(currentPlayer, 1, 5);
    }

    @Test
    public void testDisconnectEventListener() {
        String sessionId = "6264a5fd-a5c9-4fa2-b2db-70514ad731bb";
        Player disconnectedPlayer = new Player(sessionId, "DisconnectedPlayer");

        when(gameServiceMock.findPlayerById(sessionId)).thenReturn(java.util.Optional.of(disconnectedPlayer));
        gameServer.getDisconnectListener().onDisconnect(mockClient(sessionId));

        verify(gameServiceMock).removePlayer(disconnectedPlayer);
    }

    private SocketIOClient mockClient(String sessionId) {
        SocketIOClient client = mock(SocketIOClient.class);
        when(client.getSessionId()).thenReturn(UUID.fromString(sessionId));
        return client;
    }
}

