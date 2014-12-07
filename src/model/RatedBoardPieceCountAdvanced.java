package model;

import java.util.List;

public class RatedBoardPieceCountAdvanced extends RatedBoardPieceCount {

	/**
	 * 
	 */
	private static final long serialVersionUID = 3217965840720872609L;

	private byte[][] controlledBy;

	public RatedBoardPieceCountAdvanced(Board copy) {
		super(copy);
		controlledBy = new byte[ySize][xSize];
	}

	public int getStructureQuality(PlayerType player) {
		for (int y = 0; y < ySize; y++) {
			for (int x = 0; x < xSize; x++) {
				if (board[y][x].equals(player)) {
					List<Position> positions = lookupLegalPositions(
							new Position(y, x), player);
					for (Position target : positions) {
						controlledBy[target.y][target.x]++;
					}
				} else if (board[y][x].equals(player.getOpponent())) {
					List<Position> positions = lookupLegalPositions(
							new Position(y, x), player.getOpponent());
					for (Position target : positions) {
						controlledBy[target.y][target.x]--;
					}
				}
			}
		}
		int val = 0;
		for (int y = 0; y < board.length; y++) {
			for (int x = 0; x < board.length; x++) {
				val += controlledBy[y][x] * 5;
			}
		}

		return val;
	}
}
