package knightthrough.client.gui;

import knightthrough.model.Board;
import knightthrough.model.PlayerType;
import knightthrough.model.Ply;
import knightthrough.client.GameClient;

/**
 * @author Fabian Braun
 *
 */
public class MockClient extends GameClient {

	public MockClient(Board initialBoard, PlayerType player) {
		super(initialBoard, player);
	}

	@Override
	public Ply doMove(Board board, PlayerType forPlayer, long duration) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public void announceWinner(PlayerType winner) {
		System.out.println("And the winner is Player " + winner);
	}

	@Override
	public String getClientDescription() {
		return "Mock Client. Does not do very much.";
	}

}
