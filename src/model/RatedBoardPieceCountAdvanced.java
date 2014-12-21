package model;

import java.util.List;

public class RatedBoardPieceCountAdvanced extends RatedBoardPieceCount {

	/**
	 * 
	 */
	private static final long serialVersionUID = 3217965840720872609L;

	public RatedBoardPieceCountAdvanced(Board copy) {
		super(copy);
	}

	public int getStructureQuality(PlayerType player) {
		byte[][] controlledBy = new byte[ySize][xSize];
		for (int y = 0; y < ySize; y++) {
			for (int x = 0; x < xSize; x++) {
				if (board[y][x].equals(player)) {
					List<Position> positions = lookupLegalPositions(
							new Position(y, x), player);
					for (Position target : positions) {
						controlledBy[target.y][target.x]++;
					}
				}
			}
		}
		int val = 0;
		for (int y = 0; y < board.length; y++) {
			for (int x = 0; x < board.length; x++) {
				// squared
				val += controlledBy[y][x] * controlledBy[y][x];
			}
		}

		return val;
	}
}
