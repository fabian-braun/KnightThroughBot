package evaluate;

import model.Board;
import model.PlayerType;
import model.RatedBoardProtected;

public class EvaluationFunctionProtected implements EvaluationFunction {

	@Override
	public int evaluate(Board b, PlayerType p, int depth) {
		// check first if won or lost
		PlayerType winner = b.whosTheWinner();
		if (winner.equals(p)) {
			return infty;
		} else if (winner.equals(p.getOpponent())) {
			return -infty;
		}
		RatedBoardProtected board = (RatedBoardProtected) b;
		return board.getCountFor(p) - board.getCountFor(p.getOpponent());
	}

	@Override
	public Board convertBoard(Board board) {
		return new RatedBoardProtected(board);
	}
}
