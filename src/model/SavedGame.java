package model;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.PrintWriter;
import java.io.Serializable;
import java.util.NoSuchElementException;
import java.util.Scanner;

import error.GameException;

public class SavedGame implements Serializable {

	/**
	 * 
	 */
	private static final long serialVersionUID = -4710967260446983107L;
	private Board board;
	private PlayerType playerToMove;
	private long usedTimeUp = Long.MAX_VALUE - 5;
	private long usedTimeDown = Long.MAX_VALUE - 5;
	private long totalTimeUp = Long.MAX_VALUE - 5;
	private long totalTimeDown = Long.MAX_VALUE - 5;
	private int turnIndex;

	/**
	 * @param board
	 * @param playerToMove
	 * @param usedTimeUp
	 * @param usedTimeDown
	 */
	public SavedGame(Board board, PlayerType playerToMove, long usedTimeUp,
			long usedTimeDown, long totalTimeUp, long totalTimeDown,
			int turnIndex) {
		this.board = board;
		this.playerToMove = playerToMove;
		this.usedTimeUp = usedTimeUp;
		this.usedTimeDown = usedTimeDown;
		this.totalTimeDown = totalTimeDown;
		this.totalTimeUp = totalTimeUp;
		this.turnIndex = turnIndex;
	}

	public long getUsedTimeUp() {
		return usedTimeUp;
	}

	public long getUsedTimeDown() {
		return usedTimeDown;
	}

	public long getTotalTimeUp() {
		return totalTimeUp;
	}

	public long getTotalTimeDown() {
		return totalTimeDown;
	}

	public Board getBoard() {
		return board;
	}

	public PlayerType getPlayerToMove() {
		return playerToMove;
	}

	public int getTurnIndex() {
		return turnIndex;
	}

	public static void store(SavedGame save, String toFile) throws IOException {
		StringBuilder sb = new StringBuilder();
		for (int y = 0; y < save.board.ySize; y++) {
			for (int x = 0; x < save.board.xSize; x++) {
				PlayerType p = save.board.getPlayerType(y, x);
				switch (p) {
				case DOWN:
					sb.append('v');
					break;
				case UP:
					sb.append('^');
					break;
				default:
					sb.append('-');
					break;
				}
				if (x < save.board.xSize - 1) {
					sb.append(',');
				}
			}
			if (y < save.board.ySize - 1) {
				sb.append(';');
			}
		}
		sb.append("\n");
		sb.append(save.usedTimeUp + ":" + save.totalTimeUp);
		sb.append("\n");
		sb.append(save.usedTimeDown + ":" + save.totalTimeDown);
		sb.append("\n");
		sb.append(save.playerToMove);
		sb.append("\n");
		sb.append(save.turnIndex);
		File f = new File(toFile);
		f.createNewFile();
		PrintWriter pw = new PrintWriter(f);
		pw.append(sb);
		pw.close();
	}

	public static SavedGame load(String fromFile) throws FileNotFoundException {
		Scanner sc = null;
		try {
			File f = new File(fromFile);
			sc = new Scanner(f);
			String bRep = sc.nextLine();
			String[] rows = bRep.split(";");
			int colCount = rows[0].split(",").length;
			Board board = new Board(rows.length, colCount);
			int y = 0;
			for (String row : rows) {
				String[] cols = row.split(",");
				int x = 0;
				for (String col : cols) {
					if (col.equals("^")) {
						board.set(y, x, PlayerType.UP);
					} else if (col.equals("v")) {
						board.set(y, x, PlayerType.DOWN);
					} else {
						board.set(y, x, PlayerType.NONE);
					}
					x++;
				}
				y++;
			}
			// for player UP:
			String[] times = sc.nextLine().split(":");
			long usedTimeUp = Long.parseLong(times[0]);
			long totalTimeUp = Long.parseLong(times[1]);
			// for player DOWN:
			times = sc.nextLine().split(":");
			long usedTimeDown = Long.parseLong(times[0]);
			long totalTimeDown = Long.parseLong(times[1]);
			PlayerType toMove = PlayerType.valueOf(sc.nextLine());
			int turnIndex = Integer.parseInt(sc.nextLine());
			sc.close();
			return new SavedGame(board, toMove, usedTimeUp, usedTimeDown,
					totalTimeUp, totalTimeDown, turnIndex);
		} catch (NoSuchElementException e) {
			sc.close();
		}
		throw new GameException("unexpected error during io");
	}
}
