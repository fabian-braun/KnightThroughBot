package knightthrough.evaluate;

import knightthrough.model.Board;
import knightthrough.model.PlayerType;
import knightthrough.model.RatedBoardZickZackDevelopment;

/**
 * @author Fabian Braun
 *
 */
public class EvaluationFunctionZickZackDevelopment implements
		EvaluationFunction {

	@Override
	public int evaluate(Board b, PlayerType p) {
		PlayerType winner = b.whosTheWinner();
		if (winner.equals(p)) {
			return infty;
		} else if (winner.equals(p.getOpponent())) {
			return -infty;
		}
		RatedBoardZickZackDevelopment board = (RatedBoardZickZackDevelopment) b;

		int valuePieceCount = board.getCountFor(p)
				- board.getCountFor(p.getOpponent());
		valuePieceCount *= 1000;
		valuePieceCount += 10 * (board.getOccupiedZickZackCount(p) - board
				.getOccupiedZickZackCount(p.getOpponent()));
		valuePieceCount += board.getDevelopmentOfLastPieces(p)
				- board.getDevelopmentOfLastPieces(p.getOpponent());
		return valuePieceCount;
	}

	@Override
	public Board convertBoard(Board board) {
		return new RatedBoardZickZackDevelopment(board);
	}

}
