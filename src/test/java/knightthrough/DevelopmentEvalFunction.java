package knightthrough;

import knightthrough.model.BoardFactory;
import knightthrough.model.PlayerType;
import knightthrough.model.Ply;
import knightthrough.model.Position;
import knightthrough.model.RatedBoardDevelopment;

import org.junit.Test;

import knightthrough.evaluate.EvaluationFunctionDevelopment;

public class DevelopmentEvalFunction {

	@Test
	public void test() {
		RatedBoardDevelopment board = new RatedBoardDevelopment(
				BoardFactory.createStandardBoard());
		System.out.println(board);
		System.out.println(board.getDevelopmentOfLastPieces(PlayerType.UP));
		System.out.println(board.getDevelopmentOfLastPieces(PlayerType.DOWN));
		for (int x = 0; x < 7; x++) {
			board.perform(new Ply(new Position(0, x), new Position(2, x + 1)));
		}
		System.out.println(board);
		System.out.println(board.getDevelopmentOfLastPieces(PlayerType.UP));
		System.out.println(board.getDevelopmentOfLastPieces(PlayerType.DOWN));
		board.perform(new Ply(new Position(2, 6), new Position(4, 7)));
		board.perform(new Ply(new Position(0, 7), new Position(2, 6)));
		System.out.println(board);
		System.out.println(board.getDevelopmentOfLastPieces(PlayerType.UP));
		System.out.println(board.getDevelopmentOfLastPieces(PlayerType.DOWN));
		for (int x = 0; x < 7; x++) {
			board.perform(new Ply(new Position(7, x), new Position(5, x + 1)));
		}
		board.perform(new Ply(new Position(5, 5), new Position(4, 7)));
		board.perform(new Ply(new Position(5, 6), new Position(4, 4)));
		board.perform(new Ply(new Position(7, 7), new Position(5, 6)));
		board.perform(new Ply(new Position(6, 5), new Position(4, 6)));
		System.out.println(board);
		System.out.println(board.getDevelopmentOfLastPieces(PlayerType.UP));
		System.out.println(board.getDevelopmentOfLastPieces(PlayerType.DOWN));

		board.perform(new Ply(new Position(6, 0), new Position(4, 1)));
		board.perform(new Ply(new Position(6, 1), new Position(4, 2)));
		board.perform(new Ply(new Position(6, 2), new Position(4, 3)));
		board.perform(new Ply(new Position(4, 4), new Position(2, 3)));
		System.out.println(board);
		System.out.println(board.getDevelopmentOfLastPieces(PlayerType.UP));
		System.out.println(board.getDevelopmentOfLastPieces(PlayerType.DOWN));

		EvaluationFunctionDevelopment devel = new EvaluationFunctionDevelopment();
		int eval = devel.evaluate(board, PlayerType.UP);
		System.out.println(eval);
	}
}
