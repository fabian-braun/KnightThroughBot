package control;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

import model.Board;
import model.BoardFactory;
import model.PlayerType;
import model.Ply;
import model.RestrictiveBoard;
import model.SavedGame;
import client.GameClient;
import client.GameClientFactory;
import client.gui.GuiGameClient;
import client.gui.MockClient;
import config.Config;
import error.GameException;

/**
 * @author Fabian Braun
 *
 */
public class GameEngine {

	private Board initialBoard;
	private Date startTime = new Date();
	private Map<PlayerType, Long> usedTime = new HashMap<PlayerType, Long>();
	private Map<PlayerType, Long> totalTime = new HashMap<PlayerType, Long>();
	private Map<PlayerType, Long> tempTime = new HashMap<PlayerType, Long>();
	private Map<PlayerType, Long> countPlies = new HashMap<PlayerType, Long>();
	private int turnIndex;
	PlayerType startingPlayer = PlayerType.UP;
	private boolean saveGameBetweenTurns = false;
	private boolean showGui = false;

	public GameEngine() {
		String savedGameFilePath = Config.getStringValue(
				Config.keySavedGameFilePath, "");
		if (savedGameFilePath.isEmpty()) {
			initNewGame();
		} else {
			initSavedGame(savedGameFilePath);
		}
		tempTime.put(PlayerType.DOWN, 0l);
		tempTime.put(PlayerType.UP, 0l);
		countPlies.put(PlayerType.DOWN, 0l);
		countPlies.put(PlayerType.UP, 0l);
		saveGameBetweenTurns = Config
				.getBooleanValue(Config.keySaveGame, false);
		showGui = Config.getBooleanValue(Config.keyShowGui, true);
	}

	private void initNewGame() {
		initialBoard = BoardFactory.createStandardBoard();
		startingPlayer = getStartingPlayer();
		totalTime.put(PlayerType.DOWN, 60 * 1000 * Config.readNumber(
				Config.keyTotalTimePlayerDown, 15));
		totalTime.put(PlayerType.UP,
				60 * 1000 * Config.readNumber(Config.keyTotalTimePlayerUp, 15));
		usedTime.put(PlayerType.DOWN, 0l);
		usedTime.put(PlayerType.UP, 0l);
		turnIndex = 0;
	}

	private void initSavedGame(String savedGameFilePath) {
		try {
			SavedGame saved = SavedGame.load(savedGameFilePath);
			initialBoard = saved.getBoard();
			startingPlayer = saved.getPlayerToMove();
			totalTime.put(PlayerType.DOWN, saved.getTotalTimeDown());
			totalTime.put(PlayerType.UP, saved.getTotalTimeUp());
			usedTime.put(PlayerType.DOWN, saved.getUsedTimeDown());
			usedTime.put(PlayerType.UP, saved.getUsedTimeDown());
			turnIndex = saved.getTurnIndex();
		} catch (FileNotFoundException e) {
			e.printStackTrace();
			initNewGame();
		}
	}

	public void doGame() {
		boolean gameover = false;
		GameClient[] clients = GameClientFactory.getClients(initialBoard);
		GameClient playerDown = clients[0];
		System.out.println("Player Down Client: "
				+ playerDown.getClientDescription());
		GameClient playerUp = clients[1];
		System.out.println("Player Up Client: "
				+ playerUp.getClientDescription());

		GameClient gui = new MockClient(initialBoard, PlayerType.NONE);
		if (showGui && !(playerDown instanceof GuiGameClient)
				&& !(playerUp instanceof GuiGameClient)) {
			// only init gui if none of the existing clients is a gui client
			gui = new GuiGameClient(initialBoard, PlayerType.NONE);
		}

		// set to opponent because player is swapped at beginning of loop
		PlayerType playerToMove = startingPlayer.getOpponent();
		GameClient clientToMove;
		Board board = initialBoard;
		while (!gameover) {
			playerToMove = playerToMove.getOpponent();
			if (playerToMove.equals(PlayerType.DOWN)) {
				clientToMove = playerDown;
			} else {
				clientToMove = playerUp;
			}
			System.out.println("###### TURN " + turnIndex + " ###### Player "
					+ playerToMove + " moves on the following board");
			System.out.println(board);

			startTimeForPlayer(playerToMove);
			Ply ply = clientToMove.move(new Board(board), playerToMove,
					totalTime.get(playerToMove) - usedTime.get(playerToMove),
					turnIndex);
			stopTimeForPlayer(playerToMove);
			try {
				board.perform(ply);
				if (playerToMove.equals(startingPlayer.getOpponent())) {
					// second player has just moved
					turnIndex++;
					if (saveGameBetweenTurns)
						saveGame(board, playerToMove.getOpponent(), turnIndex);
				}
				// show Board on GUI
				gui.move(
						new RestrictiveBoard(board),
						PlayerType.NONE,
						totalTime.get(playerToMove)
								- usedTime.get(playerToMove), turnIndex);
			} catch (GameException e) {
				System.out.println("Illegal Ply by Player " + playerToMove);
				playerDown.announceWinner(playerToMove.getOpponent());
				playerUp.announceWinner(playerToMove.getOpponent());
				gui.announceWinner(playerToMove.getOpponent());
				gameover = true;
			}
			if (!board.whosTheWinner().equals(PlayerType.NONE)) {
				// someone has won
				playerDown.announceWinner(board.whosTheWinner());
				playerUp.announceWinner(board.whosTheWinner());
				gui.announceWinner(board.whosTheWinner());
				printTime(PlayerType.DOWN);
				printTime(PlayerType.UP);
				gameover = true;
			}
		}
	}

	private void startTimeForPlayer(PlayerType player) {
		tempTime.put(player, System.currentTimeMillis());
	}

	private void stopTimeForPlayer(PlayerType player) {
		long diff = System.currentTimeMillis() - tempTime.get(player);
		usedTime.put(player, usedTime.get(player) + diff);
		countPlies.put(player, countPlies.get(player) + 1);
	}

	public void printTime(PlayerType player) {
		System.out.println("total calculation time for player " + player
				+ " : " + usedTime.get(player) / 60000. + " minutes");
	}

	private static PlayerType getStartingPlayer() {
		String sStart = Config.getStringValue(Config.keyStartingPlayer,
				Config.valStartingPlayerUp);
		if (sStart.equals(Config.valStartingPlayerDown)) {
			return PlayerType.DOWN;
		} else if (sStart.equals(Config.valStartingPlayerUp)) {
			return PlayerType.UP;
		} else {
			System.out
					.println("WARNING: starting player could not be read from config file");
			return PlayerType.UP;
		}
	}

	private void saveGame(Board board, PlayerType playerToMove, int turnIndex) {
		String filename = "save/frogThrough-";
		SimpleDateFormat sdf = new SimpleDateFormat("YYYY-MM-dd-hh-mm-ss");
		filename += sdf.format(startTime);
		filename += "-turn-" + String.format("%02d", turnIndex);
		filename += ".save";
		try {
			SavedGame
					.store(new SavedGame(board, playerToMove, usedTime
							.get(PlayerType.UP), usedTime.get(PlayerType.DOWN),
							totalTime.get(PlayerType.UP), totalTime
									.get(PlayerType.DOWN), turnIndex), filename);
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
}
