package application;

import domain.entities.Player;

import java.util.Map;

public record PlayerMove(Map<String, Object> move, Player player) {
}
