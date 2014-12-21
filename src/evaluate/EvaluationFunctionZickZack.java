package evaluate;

import model.Board;
import model.PlayerType;
import model.RatedBoardZickZack;

/**
 * @author Fabian Braun
 *
 */
public class EvaluationFunctionZickZack implements EvaluationFunction {

	@Override
	public int evaluate(Board b, PlayerType p) {
		PlayerType winner = b.whosTheWinner();
		if (winner.equals(p)) {
			return infty;
		} else if (winner.equals(p.getOpponent())) {
			return -infty;
		}
		RatedBoardZickZack board = (RatedBoardZickZack) b;

		int valuePieceCount = board.getCountFor(p)
				- board.getCountFor(p.getOpponent());
		valuePieceCount *= 1000;
		return valuePieceCount
				+ 10
				* (board.getOccupiedZickZackCount(p) - board
						.getOccupiedZickZackCount(p.getOpponent()));
	}

	@Override
	public Board convertBoard(Board board) {
		return new RatedBoardZickZack(board);
	}

}
