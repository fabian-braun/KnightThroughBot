package evaluate;

import model.Board;
import model.PlayerType;
import model.RatedBoardPieceCount;

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
