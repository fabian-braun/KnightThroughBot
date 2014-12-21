package client.random;

import java.util.List;
import java.util.Random;

import model.Board;
import model.PlayerType;
import model.Ply;
import client.GameClient;

/**
 * @author Fabian Braun
 *
 */
public class RandomClient extends GameClient {
	Random randall = new Random();

	public RandomClient(Board initialBoard, PlayerType player) {
		super(initialBoard, player);
	}

	@Override
	public Ply doMove(Board board, PlayerType forPlayer, long maxDuration) {
		List<Ply> possibles = board.getPossiblePlies(forPlayer);
		try {
			// doing some hard calculations
			Thread.sleep(1000);
		} catch (InterruptedException e1) {
			e1.printStackTrace();
		}
		return possibles.get(randall.nextInt(possibles.size()));
	}

	@Override
	public void announceWinner(PlayerType winner) {
	}

	@Override
	public String getClientDescription() {
		return "Simple Random Client";
	}

}
