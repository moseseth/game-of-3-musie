package com.gameof3.domain.events;

import lombok.Getter;
import lombok.Setter;

@Setter
@Getter
public class GameStartedEvent {
    private String playerId;
    private boolean autoMode;
    public GameStartedEvent() {}

    public GameStartedEvent(String playerId, boolean autoMode) {
        this.playerId = playerId;
        this.autoMode = autoMode;
    }

}
