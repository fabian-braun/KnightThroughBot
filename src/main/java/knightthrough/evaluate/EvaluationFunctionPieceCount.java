package knightthrough.evaluate;

import knightthrough.model.Board;
import knightthrough.model.PlayerType;
import knightthrough.model.RatedBoardPieceCount;

/**
 * @author Fabian Braun
 *
 */
public class EvaluationFunctionPieceCount implements EvaluationFunction {

	@Override
	public int evaluate(Board b, PlayerType p) {
		PlayerType winner = b.whosTheWinner();
		if (winner.equals(p)) {
			return infty;
		} else if (winner.equals(p.getOpponent())) {
			return -infty;
		}
		RatedBoardPieceCount board = (RatedBoardPieceCount) b;
		return board.getCountFor(p) - board.getCountFor(p.getOpponent());
	}

	@Override
	public Board convertBoard(Board board) {
		return new RatedBoardPieceCount(board);
	}
}
