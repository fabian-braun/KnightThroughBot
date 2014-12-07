package client.gui;

import model.Board;
import model.PlayerType;
import model.Ply;
import control.GameClient;

public class MockClient extends GameClient {

	public MockClient(Board initialBoard) {
		super(initialBoard);
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
