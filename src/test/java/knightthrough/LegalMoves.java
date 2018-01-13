package knightthrough;

import java.util.List;

import knightthrough.model.Board;
import knightthrough.model.BoardFactory;
import knightthrough.model.PlayerType;
import knightthrough.model.Ply;
import knightthrough.model.Position;

import org.junit.Assert;
import org.junit.Test;

public class LegalMoves {

	@Test
	public void testEquals() {
		Position from1 = new Position(2, 4);
		Position from2 = new Position(2, 4);
		Position to1 = new Position(1, 3);
		Position to2 = new Position(1, 3);
		Assert.assertTrue(from1.equals(from2));
		Assert.assertTrue(to1.equals(to2));
		Ply ply1 = new Ply(from1, to1);
		Ply ply2 = new Ply(from2, to2);
		Assert.assertTrue(ply1.equals(ply2));
	}

	@Test
	public void testFactory() {
		Board b = BoardFactory.createStandardBoard();
		System.out.println(b);
	}

	@Test
	public void test() {
		Board b = new Board(8, 8);
		b.set(4, 2, PlayerType.UP);
		b.set(2, 1, PlayerType.UP);
		List<Ply> plies = b.getPossiblePlies(PlayerType.UP);

		System.out.println(b);
		for (Ply ply : plies) {
			System.out.println(ply);
		}

		Ply toPerform = new Ply(new Position(2, 1), new Position(0, 0));
		b.perform(toPerform);
		System.out.println("do move: " + toPerform);
		System.out.println(b);
	}
}
