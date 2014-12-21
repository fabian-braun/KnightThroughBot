package client.gui;

import java.util.concurrent.LinkedBlockingQueue;

import model.Board;
import model.PlayerType;
import model.Ply;
import model.RestrictiveBoard;
import client.GameClient;
import error.GameException;

/**
 * @author Fabian Braun
 *
 */
public class GuiGameClient extends GameClient {

	LinkedBlockingQueue<Ply> plyQueue;
	private MainFrame mainFrame;

	public GuiGameClient(Board initialBoard, PlayerType player) {
		super(initialBoard, player);
		plyQueue = new LinkedBlockingQueue<Ply>();
		mainFrame = new MainFrame(new RestrictiveBoard(initialBoard), plyQueue);
		mainFrame.setVisible(true);
	}

	@Override
	public Ply move(Board board, PlayerType forPlayer, long maxDuration,
			int turnIndex) {
		// override to skip time control
		// gui player may always take as long as he wishes
		return doMove(board, forPlayer, maxDuration);
	}

	@Override
	public Ply doMove(Board board, PlayerType forPlayer, long maxDuration) {
		mainFrame.updateBoard(new RestrictiveBoard(board), forPlayer);
		if (PlayerType.NONE.equals(forPlayer)) {
			return null;
		}
		try {
			return plyQueue.take();
		} catch (InterruptedException e) {
			// horrible code
			throw new GameException(e.getMessage());
		}
	}

	@Override
	public void announceWinner(PlayerType winner) {
		System.out.println("And the winner is Player " + winner);
		plyQueue.clear();
	}

	@Override
	public String getClientDescription() {
		return "Gui Client. Let's test how smart you are!";
	}
}
