package knightthrough.evaluate;

import knightthrough.model.Board;
import knightthrough.model.PlayerType;
import knightthrough.model.RatedBoardDevelopment;

/**
 * @author Fabian Braun
 *
 */
public class EvaluationFunctionDevelopment implements EvaluationFunction {

	@Override
	public int evaluate(Board b, PlayerType p) {
		PlayerType winner = b.whosTheWinner();
		if (winner.equals(p)) {
			return infty;
		} else if (winner.equals(p.getOpponent())) {
			return -infty;
		}
		RatedBoardDevelopment board = (RatedBoardDevelopment) b;

		int valuePieceCount = board.getCountFor(p)
				- board.getCountFor(p.getOpponent());
		valuePieceCount *= 1000;
		return valuePieceCount + board.getDevelopmentOfLastPieces(p)
				- board.getDevelopmentOfLastPieces(p.getOpponent());
	}

	@Override
	public Board convertBoard(Board board) {
		return new RatedBoardDevelopment(board);
	}

}
