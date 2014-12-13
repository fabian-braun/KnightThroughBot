package model;

public class RatedBoardZickZackDevelopment extends RatedBoardDevelopment {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1775925609879812881L;

	public RatedBoardZickZackDevelopment(Board copy) {
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

}
