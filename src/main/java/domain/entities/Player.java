package domain.entities;

import lombok.*;

@Getter
@Setter
@NoArgsConstructor
@ToString
public class Player {
    private String sessionId;
    private int currentNumber;
    private boolean isAutomatic;

    public Player(String sessionId) {
        this.sessionId = sessionId;
        this.isAutomatic = true;
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj) return true;
        if (obj == null || getClass() != obj.getClass()) return false;
        Player player = (Player) obj;
        return sessionId.equals(player.sessionId);
    }

    @Override
    public int hashCode() {
        return sessionId.hashCode();
    }
}
