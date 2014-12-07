package model;

import java.util.List;

public class RatedBoardProtected extends RatedBoardPieceCount {

	/**
	 * 
	 */
	private static final long serialVersionUID = -6315945644332925907L;
	private int protectedUpCount = 0;
	private int protectedDownCount = 0;

	public RatedBoardProtected(Board copy) {
		super(copy);
		rateBoardProtection();
	}

	private void rateBoardProtection() {
		// count protected pieces for each player
		for (int y = 0; y < board.length; y++) {
			for (int x = 0; x < board.length; x++) {
				switch (getPlayerType(y, x)) {
				case DOWN:
					protectedDownCount += getProtectedPieces(y, x,
							PlayerType.DOWN);
					break;
				case UP:
					protectedUpCount += getProtectedPieces(y, x, PlayerType.UP);
					break;
				default:
					break;
				}
			}
		}
	}

	private int getProtectedPieces(int y, int x, PlayerType ofPlayer) {
		List<Position> protectedSquares = lookupLegalPositions(new Position(y,
				x), ofPlayer);
		int count = 0;
		for (Position position : protectedSquares) {
			if (getPlayerTypeSave(position.y, position.x).equals(ofPlayer)) {
				count++;
			}
		}
		return count;
	}

	public RatedBoardProtected(RatedBoardProtected copy) {
		super(copy);
		this.protectedDownCount = copy.protectedDownCount;
		this.protectedUpCount = copy.protectedUpCount;
	}

	@Override
	public PlayerType perform(Ply ply) {
		PlayerType result = super.perform(ply);
		rateBoardProtection();
		return result;
	}

	@Override
	public void undo(Ply ply, PlayerType recoverCapturedPiece) {
		super.undo(ply, recoverCapturedPiece);
		rateBoardProtection();
	}

	@Override
	public int getCountFor(PlayerType player) {
		switch (player) {
		case DOWN:
			return downCount * 10 + protectedDownCount;
		case UP:
			return upCount * 10 + protectedUpCount;
		default:
			return 0;
		}
	}
}
