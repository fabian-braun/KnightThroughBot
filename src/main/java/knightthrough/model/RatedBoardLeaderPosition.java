package knightthrough.model;

import java.util.Stack;

/**
 * @author Fabian Braun
 *
 */
public class RatedBoardLeaderPosition extends RatedBoardPieceCount {

	private static final long serialVersionUID = -45262807273088644L;

	private int downLeaderY = 1;
	private int upLeaderY = 1;
	private Stack<Integer> historyDown = new Stack<Integer>();
	private Stack<Integer> historyUp = new Stack<Integer>();

	public RatedBoardLeaderPosition(Board copy) {
		super(copy);
		// down player
		rows: for (int y = 2; y < board.length; y++) {
			for (int x = 0; x < board[y].length; x++) {
				if (board[y][x].equals(PlayerType.DOWN)) {
					// some player in this row
					continue rows;
				}
			}
			// no player in this row! Leader must be in previous row
			downLeaderY = y - 1;
			break;
		}
		// up player
		rows: for (int y = 5; y >= 0; y--) {
			for (int x = 0; x < board[y].length; x++) {
				if (board[y][x].equals(PlayerType.UP)) {
					// some player in this row
					continue rows;
				}
			}
			// no player in this row! Leader must be in previous row
			upLeaderY = 7 - (y + 1);
			break;
		}
	}

	@Override
	public PlayerType perform(Ply ply) {
		PlayerType aboutToMove = board[ply.from.y][ply.from.x];
		if (aboutToMove.equals(PlayerType.UP)) {
			historyUp.push(upLeaderY);
			int y = 7 - ply.to.y;
			if (y > upLeaderY) {
				upLeaderY = y;
			}
		} else if (aboutToMove.equals(PlayerType.DOWN)) {
			historyDown.push(downLeaderY);
			int y = ply.to.y;
			if (y > downLeaderY) {
				downLeaderY = y;
			}
		}
		return super.perform(ply);
	}

	@Override
	public void undo(Ply ply, PlayerType recoverCapturedPiece) {
		PlayerType aboutToMove = board[ply.to.y][ply.to.x];
		if (aboutToMove.equals(PlayerType.UP)) {
			upLeaderY = historyUp.pop();
		} else if (aboutToMove.equals(PlayerType.DOWN)) {
			downLeaderY = historyDown.pop();
		}
		super.undo(ply, recoverCapturedPiece);
	}

	public int getLeaderY(PlayerType forPlayer) {
		if (forPlayer.equals(PlayerType.DOWN))
			return downLeaderY;
		else
			return upLeaderY;
	}

	@Override
	public Position getThreateningPiece(PlayerType player) {
		if (!(getLeaderY(player) > 4))
			return null;
		return super.getThreateningPiece(player);
	}
}
