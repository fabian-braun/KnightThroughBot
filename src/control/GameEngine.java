package control;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.ObjectOutputStream;
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
import config.Config;
import error.GameException;

public class GameEngine {

	private Board initialBoard;
	private Date startTime = new Date();
	private Map<PlayerType, Long> usedTime = new HashMap<PlayerType, Long>();
	private Map<PlayerType, Long> totalTime = new HashMap<PlayerType, Long>();
	private Map<PlayerType, Long> tempTime = new HashMap<PlayerType, Long>();
	private Map<PlayerType, Long> countPlies = new HashMap<PlayerType, Long>();
	private int turnIndex = 0;
	PlayerType startingPlayer = PlayerType.UP;
	private boolean saveGameBetweenTurns = false;
	private boolean showGui = false;

	public GameEngine() {
		String savedGameFilePath = Config.getStringValue(
				Config.keySavedGameFilePath, "");
		if (savedGameFilePath.isEmpty()) {
			initialBoard = BoardFactory.createStandardBoard();
			// set to opponent because player is swapped at beginning of loop
			startingPlayer = getStartingPlayer().getOpponent();
			totalTime.put(PlayerType.DOWN, 60 * 1000 * Config.readNumber(
					Config.keyTotalTimePlayerDown, 15));
			totalTime.put(PlayerType.UP, 60 * 1000 * Config.readNumber(
					Config.keyTotalTimePlayerUp, 15));
		} else {
			SavedGame saved = BoardFactory.readFromFile(savedGameFilePath);
			initialBoard = saved.getBoard();
			// set to opponent because player is swapped at beginning of loop
			startingPlayer = saved.getPlayerToMove().getOpponent();
			totalTime.put(PlayerType.DOWN, saved.getRemainingTimeDown());
			totalTime.put(PlayerType.UP, saved.getRemainingTimeUp());
		}
		tempTime.put(PlayerType.DOWN, 0l);
		tempTime.put(PlayerType.UP, 0l);
		usedTime.put(PlayerType.DOWN, 0l);
		usedTime.put(PlayerType.UP, 0l);
		countPlies.put(PlayerType.DOWN, 0l);
		countPlies.put(PlayerType.UP, 0l);
		saveGameBetweenTurns = Config
				.getBooleanValue(Config.keySaveGame, false);
		showGui = Config.getBooleanValue(Config.keyShowGui, true);
	}

	public void doGame() {
		boolean gameover = false;
		GameClient[] clients = GameClientFactory.getClients(initialBoard,
				showGui);
		GameClient gui = clients[0];
		GameClient playerDown = clients[1];
		System.out.println("Player Down Client: "
				+ playerDown.getClientDescription());
		GameClient playerUp = clients[2];
		System.out.println("Player Up Client: "
				+ playerUp.getClientDescription());
		PlayerType playerToMove = startingPlayer;
		GameClient clientToMove;
		Board board = initialBoard;
		while (!gameover) {
			playerToMove = playerToMove.getOpponent();
			if (playerToMove.equals(PlayerType.DOWN)) {
				clientToMove = playerDown;
			} else {
				clientToMove = playerUp;
			}
			System.out.println("########## Player " + playerToMove
					+ " moves on the following board");
			System.out.println(board);

			startTimeForPlayer(playerToMove);
			Ply ply = clientToMove.move(new Board(board), playerToMove,
					totalTime.get(playerToMove) - usedTime.get(playerToMove));
			stopTimeForPlayer(playerToMove);

			try {
				board.perform(ply);
				if (saveGameBetweenTurns)
					saveGame(board, playerToMove, totalTime.get(playerToMove)
							- usedTime.get(playerToMove),
							totalTime.get(playerToMove.getOpponent())
									- usedTime.get(playerToMove.getOpponent()),
							turnIndex);
				if (playerToMove.equals(startingPlayer.getOpponent()))
					turnIndex++;
				// show Board on GUI
				gui.move(
						new RestrictiveBoard(board),
						PlayerType.NONE,
						totalTime.get(playerToMove)
								- usedTime.get(playerToMove));
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
		System.out.println("needed to perform " + countPlies.get(player)
				+ " plies");
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

	private void saveGame(Board board, PlayerType playerToMove,
			long remainingTimeUp, long remainingTimeDown, int turnIndex) {
		SavedGame save = new SavedGame(board, playerToMove, remainingTimeUp,
				remainingTimeDown);
		String filename = "save/frogThrough-";
		SimpleDateFormat sdf = new SimpleDateFormat("YYYY-MM-dd-hh-mm-ss");
		filename += sdf.format(startTime);
		filename += "-turn-" + turnIndex;
		filename += ".save";
		File f = new File(filename);
		FileOutputStream fos = null;
		ObjectOutputStream oos = null;
		try {
			f.createNewFile();
			fos = new FileOutputStream(f);
			oos = new ObjectOutputStream(fos);
			oos.writeObject(save);
			oos.close();
			fos.close();
		} catch (IOException e) {
			e.printStackTrace();
			// silently close streams
			if (oos != null)
				try {
					oos.close();
				} catch (Exception e2) {
				}
			if (fos != null) {
				try {
					fos.close();
				} catch (Exception e2) {
				}
			}
		}
	}
}
