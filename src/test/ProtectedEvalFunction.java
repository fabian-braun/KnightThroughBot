package test;

import model.BoardFactory;
import model.PlayerType;
import model.RatedBoardProtected;

import org.junit.Test;

public class ProtectedEvalFunction {

	@Test
	public void test() {
		RatedBoardProtected board = new RatedBoardProtected(
				BoardFactory.createStandardBoard());
		System.out.println(board);
		System.out.println(board.getCountFor(PlayerType.UP));
		System.out.println(board.getCountFor(PlayerType.DOWN));
	}

}
