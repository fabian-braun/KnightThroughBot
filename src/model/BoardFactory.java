package model;

import error.GameException;

public class BoardFactory {

	public static Board createStandardBoard() {
		Board b = new Board(8, 8);
		for (int i = 0; i < 1; i++) {
			for (int x = 0; x < 8; x++) {
				b.set(i, x, PlayerType.DOWN);
				b.set(i + 1, x, PlayerType.DOWN);
				b.set(i + 6, x, PlayerType.UP);
				b.set(i + 7, x, PlayerType.UP);
			}
		}
		return b;
	}

	public static Board createBoard(int ySize, int xSize, int pieceRowsCount) {
		if (ySize < 2 || xSize < 2 || ySize > 16 || xSize > 20) {
			throw new GameException(
					"fieldsize out of allowed range. Do you want to drive the AI crazy???");
		}
		if (pieceRowsCount < 1 || pieceRowsCount > ySize / 2) {
			throw new GameException(
					"pieceRowsCount out of allowed range. Must be in [1,ySize/2]");
		}
		Board b = new Board(ySize, xSize);
		for (int i = 0; i < pieceRowsCount; i++) {
			for (int x = 0; x < xSize; x++) {
				b.set(i, x, PlayerType.DOWN);
				b.set(ySize - i - 1, x, PlayerType.UP);
			}
		}
		return b;
	}
}
