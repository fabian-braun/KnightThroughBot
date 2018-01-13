package knightthrough.model;

import java.io.Serializable;

/**
 * @author Fabian Braun
 *
 */
public enum PlayerType implements Serializable {

	NONE, DOWN, UP;

	private PlayerType opponent;

	static {
		NONE.opponent = NONE;
		DOWN.opponent = UP;
		UP.opponent = DOWN;
	}

	/**
	 * NONE.getOpponent() == NONE,<br\>
	 * DOWN.getOpponent() == UP,<br\>
	 * UP.getOpponent() == DOWN<br\>
	 * 
	 * @return
	 */
	public PlayerType getOpponent() {
		return opponent;
	}

}
