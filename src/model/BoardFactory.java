package model;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.ObjectInputStream;

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

	public static SavedGame readFromFile(String fullPath) {
		File f = new File(fullPath);
		if (!f.exists() && f.canRead()) {
			throw new GameException("Board could not be read from file "
					+ fullPath);
		}
		FileInputStream fis = null;
		ObjectInputStream ois = null;
		try {
			fis = new FileInputStream(f);
			ois = new ObjectInputStream(fis);
			SavedGame b = (SavedGame) ois.readObject();
			ois.close();
			fis.close();
			return b;
		} catch (IOException | ClassNotFoundException e) {
			e.printStackTrace();
		}
		System.out.println("Deserialisation failed. Return standart board");
		// silently close streams
		try {
			if (fis != null)
				fis.close();
		} catch (Exception e2) {

		}
		try {
			if (ois != null)
				ois.close();
		} catch (Exception e2) {

		}
		return new SavedGame(createStandardBoard(), PlayerType.UP);
	}
}
