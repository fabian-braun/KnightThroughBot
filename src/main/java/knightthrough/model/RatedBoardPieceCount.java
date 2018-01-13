package knightthrough.model;

/**
 * @author Fabian Braun
 *
 */
public class RatedBoardPieceCount extends Board {

	private static final long serialVersionUID = 232865211349908221L;
	protected int upCount = 0;
	protected int downCount = 0;

	public RatedBoardPieceCount(Board copy) {
		super(copy);
		// count pieces for each player
		for (int y = 0; y < board.length; y++) {
			for (int x = 0; x < board.length; x++) {
				switch (getPlayerType(y, x)) {
				case DOWN:
					downCount++;
					break;
				case UP:
					upCount++;
					break;
				default:
					break;
				}
			}
		}
	}

	public RatedBoardPieceCount(RatedBoardPieceCount copy) {
		super(copy);
		this.upCount = copy.upCount;
		this.downCount = copy.downCount;
	}

	@Override
	public PlayerType perform(Ply ply) {
		PlayerType capturedByThisPly = board[ply.to.y][ply.to.x];
		switch (capturedByThisPly) {
		case DOWN:
			downCount--;
			break;
		case UP:
			upCount--;
			break;
		default:
			break;
		}
		return super.perform(ply);
	}

	@Override
	public void undo(Ply ply, PlayerType recoverCapturedPiece) {
		super.undo(ply, recoverCapturedPiece);
		switch (recoverCapturedPiece) {
		case DOWN:
			downCount++;
			break;
		case UP:
			upCount++;
			break;
		default:
			break;
		}
	}

	@Override
	public int getCountFor(PlayerType player) {
		switch (player) {
		case DOWN:
			return downCount;
		case UP:
			return upCount;
		default:
			return 0;
		}
	}
}
