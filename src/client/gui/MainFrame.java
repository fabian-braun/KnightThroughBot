package client.gui;

import java.util.concurrent.LinkedBlockingQueue;

import javax.swing.JFrame;
import javax.swing.JLabel;

import model.PlayerType;
import model.Ply;
import model.RestrictiveBoard;

/**
 * @author Fabian Braun
 *
 */
public class MainFrame extends JFrame {

	/**
	 * 
	 */
	private static final long serialVersionUID = -8026416994513756565L;
	static protected JLabel label;
	MainPanel mainPanel;

	public MainFrame(RestrictiveBoard initialBoard,
			LinkedBlockingQueue<Ply> plyContainer) {
		super("FrogThrough");
		mainPanel = new MainPanel(initialBoard, plyContainer);
		add(mainPanel);
		// Ensures JVM closes after frame(s) closed and
		// all non-daemon threads are finished
		setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
		// See http://stackoverflow.com/a/7143398/418556 for demo.
		setLocationByPlatform(true);

		// ensures the frame is the minimum size it needs to be
		// in order display the components within it
		pack();
		// ensures the minimum size is enforced.
		setMinimumSize(getSize());
		setVisible(true);
	}

	public void updateBoard(RestrictiveBoard currentBoard,
			PlayerType currentPlayer) {
		mainPanel.updateBoardPanel(currentBoard, currentPlayer);
	}
}
