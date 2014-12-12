package model;

public class RatedBoardDevelopment extends RatedBoardPieceCount {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1775925609879812881L;

	public RatedBoardDevelopment(Board copy) {
		super(copy);
	}

	public int getDevelopmentOfLastPieces(PlayerType p) {
		switch (p) {
		case DOWN:
			for (int y = 0; y < board.length; y++) {
				int count = 0;
				for (int x = 0; x < board[y].length; x++) {
					if (p.equals(board[y][x])) {
						count++;
					}
				}
				if (count > 0) {
					return 10 * y + (8 - count);
				}
			}
			assert false : "Unexpected case in Evaluation function";
		case UP:
			for (int y = board.length - 1; y >= 0; y--) {
				int count = 0;
				for (int x = 0; x < board[y].length; x++) {
					if (p.equals(board[y][x])) {
						count++;
					}
				}
				if (count > 0) {
					return 10 * (7 - y) + (8 - count);
				}
			}
			assert false : "Unexpected case in Evaluation function";
		default:
			assert false : "Unexpected case in Evaluation function";
		}
		return 0;
	}
}
