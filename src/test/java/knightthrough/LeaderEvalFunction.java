package knightthrough;

import knightthrough.model.BoardFactory;
import knightthrough.model.PlayerType;
import knightthrough.model.Ply;
import knightthrough.model.Position;
import knightthrough.model.RatedBoardLeaderPosition;

import org.junit.Test;

public class LeaderEvalFunction {

	@Test
	public void test() {
		RatedBoardLeaderPosition board = new RatedBoardLeaderPosition(
				BoardFactory.createStandardBoard());
		System.out.println(board);
		System.out.println(board.getLeaderY(PlayerType.UP));
		System.out.println(board.getLeaderY(PlayerType.DOWN));
		for (int x = 0; x < 7; x++) {
			board.perform(new Ply(new Position(0, x), new Position(2, x + 1)));
		}
		System.out.println(board);
		System.out.println(board.getLeaderY(PlayerType.UP));
		System.out.println(board.getLeaderY(PlayerType.DOWN));
		board.perform(new Ply(new Position(2, 6), new Position(4, 7)));
		board.perform(new Ply(new Position(0, 7), new Position(2, 6)));
		System.out.println(board);
		System.out.println(board.getLeaderY(PlayerType.UP));
		System.out.println(board.getLeaderY(PlayerType.DOWN));
		for (int x = 0; x < 7; x++) {
			board.perform(new Ply(new Position(7, x), new Position(5, x + 1)));
		}
		board.perform(new Ply(new Position(5, 5), new Position(4, 7)));
		board.perform(new Ply(new Position(5, 6), new Position(4, 4)));
		board.perform(new Ply(new Position(7, 7), new Position(5, 6)));
		board.perform(new Ply(new Position(6, 5), new Position(4, 6)));
		System.out.println(board);
		System.out.println(board.getLeaderY(PlayerType.UP));
		System.out.println(board.getLeaderY(PlayerType.DOWN));

		System.out.println("recalculate");
		board = new RatedBoardLeaderPosition(board);
		System.out.println(board.getLeaderY(PlayerType.UP));
		System.out.println(board.getLeaderY(PlayerType.DOWN));
	}
}
