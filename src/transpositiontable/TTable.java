package transpositiontable;

import java.util.Random;

import model.PlayerType;

public class TTable {

	public static final long[][][] rNumbers = new long[8][8][2];

	static {
		Random randall = new Random();
		for (int y = 0; y < rNumbers.length; y++) {
			for (int x = 0; x < rNumbers[0].length; x++) {
				for (int i = 0; i < rNumbers[0][0].length; i++) {
					rNumbers[y][x][i] = randall.nextLong();
				}
			}
		}

	}

	public static long code(int y, int x, PlayerType player) {
		// this check can be uncommented as this never happens
		// if (PlayerType.NONE.equals(player))
		// throw new GameException(
		// "zobrist hash should not be changed for PlayerType.NONE");
		int pIndex = PlayerType.DOWN.equals(player) ? 0 : 1;
		return rNumbers[y][x][pIndex];
	}
}
