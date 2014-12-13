package model;

@SuppressWarnings("unused")
public class RatedBoardZickZack extends RatedBoardPieceCount {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1775925609879812881L;

	public RatedBoardZickZack(Board copy) {
		super(copy);
	}

	public int getOccupiedZickZackCount(PlayerType p) {
		int count = 0;
		switch (p) {
		case DOWN:
			// count = max(getOccupiedZickZackCountDownLeft(),
			// getOccupiedZickZackCountDownRight());
			count = getOccupiedZickZackCountDownLeft();
			break;
		case UP:
			// count = max(getOccupiedZickZackCountUpLeft(),
			// getOccupiedZickZackCountUpRight());
			count = getOccupiedZickZackCountUpRight();
			break;
		default:
			assert false : "Unexpected case in Evaluation function";
		}
		if (count == 4)
			count = 8;
		return count;
	}

	private int getOccupiedZickZackCountDownLeft() {
		int count = 0;
		if (board[2][0].equals(PlayerType.DOWN))
			count++;
		if (board[3][1].equals(PlayerType.DOWN))
			count++;
		if (board[2][2].equals(PlayerType.DOWN))
			count++;
		if (board[3][3].equals(PlayerType.DOWN))
			count++;
		return count;
	}

	private int getOccupiedZickZackCountDownRight() {
		int count = 0;
		if (board[3][4].equals(PlayerType.DOWN))
			count++;
		if (board[2][5].equals(PlayerType.DOWN))
			count++;
		if (board[3][6].equals(PlayerType.DOWN))
			count++;
		if (board[2][7].equals(PlayerType.DOWN))
			count++;
		return count;
	}

	private int getOccupiedZickZackCountUpLeft() {
		int count = 0;
		if (board[5][0].equals(PlayerType.UP))
			count++;
		if (board[4][1].equals(PlayerType.UP))
			count++;
		if (board[5][2].equals(PlayerType.UP))
			count++;
		if (board[4][3].equals(PlayerType.UP))
			count++;
		return count;
	}

	private int getOccupiedZickZackCountUpRight() {
		int count = 0;
		if (board[4][4].equals(PlayerType.UP))
			count++;
		if (board[5][5].equals(PlayerType.UP))
			count++;
		if (board[4][6].equals(PlayerType.UP))
			count++;
		if (board[5][7].equals(PlayerType.UP))
			count++;
		return count;
	}

	private int max(int i1, int i2) {
		return i1 > i2 ? i1 : i2;
	}
}
