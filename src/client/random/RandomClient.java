package client.random;

import java.util.List;
import java.util.Random;

import client.GameClient;
import model.Board;
import model.PlayerType;
import model.Ply;

public class RandomClient extends GameClient {
	Random randall = new Random();

	public RandomClient(Board initialBoard) {
		super(initialBoard);
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
		System.out.println("And the winner is Player " + winner);
	}

	@Override
	public String getClientDescription() {
		return "Simple Random Client";
	}

}
