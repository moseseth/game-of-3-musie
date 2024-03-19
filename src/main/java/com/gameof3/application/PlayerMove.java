package com.gameof3.application;

import com.gameof3.domain.entities.Player;

import java.util.Map;

public record PlayerMove(Map<String, Object> move, Player player) {
}
