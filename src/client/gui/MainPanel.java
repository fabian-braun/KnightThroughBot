package client.gui;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Component;
import java.awt.Dimension;
import java.awt.GridBagLayout;
import java.awt.GridLayout;
import java.awt.Image;
import java.awt.Insets;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.image.BufferedImage;
import java.io.File;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;
import java.util.concurrent.LinkedBlockingQueue;

import javax.imageio.ImageIO;
import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.SwingConstants;
import javax.swing.border.CompoundBorder;
import javax.swing.border.EmptyBorder;
import javax.swing.border.LineBorder;

import model.PlayerType;
import model.Ply;
import model.Position;
import model.RestrictiveBoard;

/**
 * @author Fabian Braun
 *
 */
public class MainPanel extends JPanel {

	private static final long serialVersionUID = -7850204442114077596L;

	private static final char[] COLS = new char[] { 'A', 'B', 'C', 'D', 'E',
			'F', 'G', 'H', 'I', 'J', 'K', 'L', 'M', 'N', 'O', 'P' };

	private int rowCount;
	private int colCount;
	// set color of darker tiles
	public static final Color darkColor = new Color(13, 76, 117);
	// set color of shiny tiles
	public static final Color shinyColor = new Color(130, 180, 200);
	// set color of selected tiles
	public static final Color selectedColor = new Color(128, 12, 200);
	// set color of changed tiles
	public static final Color changedColor = new Color(255, 140, 0);

	private JButton[][] tiles;
	private Image downImg;
	private Image upImg;
	private JPanel boardPanel;
	private boolean enabled = false;
	private boolean tileSelected = false;
	private Position selectedPosition;
	List<Position> possibleTargetPositions;

	private PlayerType currentPlayer;

	private RestrictiveBoard board;

	private final LinkedBlockingQueue<Ply> plyContainer;

	public MainPanel(final RestrictiveBoard initialBoard,
			LinkedBlockingQueue<Ply> plyContainer) {
		super(new BorderLayout(3, 3));
		loadGraphics();
		this.plyContainer = plyContainer;
		rowCount = initialBoard.getRowCount();
		colCount = initialBoard.getColCount();
		tiles = new JButton[rowCount][colCount];

		setBorder(new EmptyBorder(5, 5, 5, 5));
		setBackground(Color.white);

		initBoardPanel(initialBoard.getRowCount(), initialBoard.getColCount());
		updateBoardPanel(initialBoard, PlayerType.UP);
		enabled = false;
	}

	private void initBoardPanel(int ySize, int xSize) {
		boardPanel = new JPanel(new GridLayout(0, xSize + 1)) {

			/**
			 * 
			 */
			private static final long serialVersionUID = 654646864L;

			@Override
			public final Dimension getPreferredSize() {
				Dimension d = super.getPreferredSize();
				Dimension prefSize = null;
				Component c = getParent();
				if (c == null) {
					prefSize = new Dimension((int) d.getWidth(),
							(int) d.getHeight());
				} else if (c != null && c.getWidth() > d.getWidth()
						&& c.getHeight() > d.getHeight()) {
					prefSize = c.getSize();
				} else {
					prefSize = d;
				}
				int w = (int) prefSize.getWidth();
				int h = (int) prefSize.getHeight();
				// the smaller of the two sizes
				int s = (w > h ? h : w);
				return new Dimension(s, s);
			}
		};
		boardPanel.setBorder(new CompoundBorder(new EmptyBorder(1, 1, 1, 1),
				new LineBorder(Color.BLACK)));
		// set background color
		Color bgColor = new Color(107, 255, 201);
		boardPanel.setBackground(bgColor);
		JPanel boardConstrain = new JPanel(new GridBagLayout());
		boardConstrain.setBackground(bgColor);
		boardConstrain.add(boardPanel);
		this.add(boardConstrain);

		// create the tiles
		Insets buttonMargin = new Insets(0, 0, 0, 0);
		for (int y = 0; y < ySize; y++) {
			for (int x = 0; x < xSize; x++) {
				JButton b = new JButton();
				final int xFinal = x;
				final int yFinal = y;
				b.addActionListener(new ActionListener() {
					@Override
					public void actionPerformed(ActionEvent e) {
						clickOn(new Position(yFinal, xFinal));
					}
				});
				b.setMargin(buttonMargin);
				ImageIcon icon = new ImageIcon(new BufferedImage(50, 50,
						BufferedImage.TYPE_INT_ARGB));
				b.setIcon(icon);
				tiles[y][x] = b;
			}
		}

		boardPanel.add(new JLabel(""));
		for (int x = 0; x < ySize; x++) {
			boardPanel.add(new JLabel(COLS[x] + "", SwingConstants.CENTER));
		}
		for (int y = 0; y < ySize; y++) {
			for (int x = 0; x < xSize; x++) {
				if (x == 0) {
					boardPanel.add(new JLabel("" + (ySize - y)));
				}
				boardPanel.add(tiles[y][x]);
			}
		}
	}

	public final void updateBoardPanel(RestrictiveBoard board,
			PlayerType currentPlayer) {
		Set<Position> changes = board.findDifferencesTo(this.board);
		this.board = board;
		for (int y = 0; y < board.getRowCount(); y++) {
			for (int x = 0; x < board.getColCount(); x++) {
				if (changes.contains(new Position(y, x))) {
					tiles[y][x].setBackground(changedColor);
				} else if (x % 2 == y % 2) {
					tiles[y][x].setBackground(shinyColor);
				} else {
					tiles[y][x].setBackground(darkColor);
				}
				switch (board.getPlayerType(new Position(y, x))) {
				case NONE:
					tiles[y][x].setIcon(null);
					break;
				case DOWN:
					tiles[y][x].setIcon(new ImageIcon(downImg));
					break;
				case UP:
					tiles[y][x].setIcon(new ImageIcon(upImg));
					break;
				default:
					break;
				}
			}
		}
		// save the player which moves next
		this.currentPlayer = currentPlayer;
		tileSelected = false;
		// enable interaction with GUI, last step of this method!
		if (!PlayerType.NONE.equals(currentPlayer)) {
			enabled = true;
		}
	}

	private void clickOn(Position p) {
		if (!enabled) {
			// only react to click if enabled
			return;
		}
		if (!tileSelected) {
			List<Ply> possiblePlies = board.getPossiblePlies(currentPlayer, p);
			if (possiblePlies.size() > 0) {
				selectedPosition = p;
				possibleTargetPositions = new ArrayList<Position>();
				for (Ply ply : possiblePlies) {
					possibleTargetPositions.add(ply.to);
				}
				highlightTiles(possibleTargetPositions);
				highlightTiles(p); // the current pos should also be highlighted
			}
		} else { // moving frog is already selected
			if (possibleTargetPositions.contains(p)) {
				enabled = false; // other player moves next

				switch (board.getPlayerType(selectedPosition)) {
				case DOWN:
					tiles[p.y][p.x].setIcon(new ImageIcon(downImg));
					break;
				case UP:
					tiles[p.y][p.x].setIcon(new ImageIcon(upImg));
					break;
				default:
					break;
				}
				tiles[selectedPosition.y][selectedPosition.x].setIcon(null);
				Ply performed = new Ply(selectedPosition, p);
				RestrictiveBoard copy = new RestrictiveBoard(board);
				copy.perform(performed);
				updateBoardPanel(copy, PlayerType.NONE);
				plyContainer.add(performed);
			} else {
				// not a valid target
				// reset selected frog
				resetTiles();
			}
		}
	}

	private void highlightTiles(List<Position> positions) {
		tileSelected = true;
		for (Position p : positions) {
			tiles[p.y][p.x].setBackground(selectedColor);
		}
	}

	private void highlightTiles(Position... positions) {
		tileSelected = true;
		for (Position p : positions) {
			tiles[p.y][p.x].setBackground(selectedColor);
		}
	}

	private void resetTiles() {
		tileSelected = false;
		for (int y = 0; y < rowCount; y++) {
			for (int x = 0; x < colCount; x++) {
				if (x % 2 == y % 2) {
					tiles[y][x].setBackground(shinyColor);
				} else {
					tiles[y][x].setBackground(darkColor);
				}
			}
		}
	}

	private void loadGraphics() {
		try {
			File f = new File("resources/icon_down.png");
			downImg = ImageIO.read(f);
			f = new File("resources/icon_up.png");
			upImg = ImageIO.read(f);
		} catch (Exception e) {
			e.printStackTrace();
			System.exit(1);
		}
	}

}