package com.gameof3.domain.events;

import lombok.Getter;

@Getter
public enum GameEvent {
    START_GAME("start-game"),
    RANDOM_NUMBER("random-number"),
    GAME_STARTED("game-started"),
    GAME_OVER("game-over"),
    GAME_STATE("game-state"),
    PLAYER_MOVE("player-move"),
    WAITING_FOR_PLAYER_2("waiting-for-player-2"),
    GAME_FULL("game-full"),
    PLAYER_JOINED("player-joined");

    private final String eventName;

    GameEvent(String eventName) {
        this.eventName = eventName;
    }
}
