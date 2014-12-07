package model;

import java.io.Serializable;

public enum PlayerType implements Serializable {

	NONE, DOWN, UP;

	private PlayerType opponent;

	static {
		NONE.opponent = NONE;
		DOWN.opponent = UP;
		UP.opponent = DOWN;
	}

	public PlayerType getOpponent() {
		return opponent;
	}

}
