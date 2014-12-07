package model;

import java.io.Serializable;

public class SavedGame implements Serializable {

	/**
	 * 
	 */
	private static final long serialVersionUID = -4710967260446983107L;
	private Board board;
	private PlayerType playerToMove;
	private long remainingTimeUp = Long.MAX_VALUE - 5;
	private long remainingTimeDown = Long.MAX_VALUE - 5;

	/**
	 * @param board
	 * @param playerToMove
	 */
	public SavedGame(Board board, PlayerType playerToMove) {
		this.board = board;
		this.playerToMove = playerToMove;
	}

	/**
	 * @param board
	 * @param playerToMove
	 * @param remainingTimeUp
	 * @param remainingTimeDown
	 */
	public SavedGame(Board board, PlayerType playerToMove,
			long remainingTimeUp, long remainingTimeDown) {
		this.board = board;
		this.playerToMove = playerToMove;
		this.remainingTimeUp = remainingTimeUp;
		this.remainingTimeDown = remainingTimeDown;
	}

	public Board getBoard() {
		return board;
	}

	public PlayerType getPlayerToMove() {
		return playerToMove;
	}

	public long getRemainingTimeUp() {
		return remainingTimeUp;
	}

	public long getRemainingTimeDown() {
		return remainingTimeDown;
	}
}
