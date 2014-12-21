package transpositiontable;

import java.util.Random;

import model.PlayerType;

public class Zobrist {

	public static final long[][][] rNumbers = new long[8][8][2];

	static {
		// init random zobrist hashes
		Random randall = new Random();
		for (int y = 0; y < rNumbers.length; y++) {
			for (int x = 0; x < rNumbers[0].length; x++) {
				for (int i = 0; i < rNumbers[0][0].length; i++) {
					rNumbers[y][x][i] = randall.nextLong();
				}
			}
		}
	}

	/**
	 * gives the random hash value for {@link PlayerType} UP and DOWN at a
	 * certain position. {@link PlayerType}.NONE is not a valid argument and can
	 * cause undefined behavior.
	 * 
	 * @param y
	 * @param x
	 * @param player
	 * @return
	 */
	public static long code(int y, int x, PlayerType player) {
		assert !PlayerType.NONE.equals(player) : "zobrist hash for PlayerType.NONE does not exist";
		int pIndex = PlayerType.DOWN.equals(player) ? 0 : 1;
		return rNumbers[y][x][pIndex];
	}
}
