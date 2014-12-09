package evaluate;

import model.Board;
import model.PlayerType;
import model.RatedBoardPieceCountAdvanced;

public class EvaluationFunctionPieceCountAdvanced implements EvaluationFunction {

	@Override
	public int evaluate(Board b, PlayerType p, int depth) {
		PlayerType winner = b.whosTheWinner();
		if (winner.equals(p)) {
			return infty - depth;
		} else if (winner.equals(p.getOpponent())) {
			return -infty + depth;
		}
		RatedBoardPieceCountAdvanced board = (RatedBoardPieceCountAdvanced) b;

		int valuePieceCount = board.getCountFor(p)
				- board.getCountFor(p.getOpponent());
		valuePieceCount *= 1000;
		return valuePieceCount + board.getStructureQuality(p)
				- board.getStructureQuality(p.getOpponent());
	}

	@Override
	public Board convertBoard(Board board) {
		return new RatedBoardPieceCountAdvanced(board);
	}
}
