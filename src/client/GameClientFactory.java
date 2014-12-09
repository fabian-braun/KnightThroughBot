package client;

import model.Board;
import client.alphabeta.standard.AlphaBetaClient;
import client.alphabeta.v3.AlphaBetaClient3;
import client.alphabeta.v4.AlphaBetaClient4;
import client.greedy.GreedyGameClient;
import client.gui.GuiGameClient;
import client.gui.MockClient;
import client.random.RandomClient;
import config.Config;

public class GameClientFactory {

	public static GameClient[] getClients(Board initialBoard, boolean showGui) {
		GameClient[] clients = new GameClient[3];
		String clientType1 = Config.getStringValue(
				Config.keyClientTypePlayerDown, Config.valClientTypeRandom);
		String clientType2 = Config.getStringValue(
				Config.keyClientTypePlayerUp, Config.valClientTypeGui);
		if (Config.valClientTypeGui.equals(clientType1)
				|| Config.valClientTypeGui.equals(clientType2)) {
			showGui = true;
		}
		GameClient gui = null;
		if (showGui) {
			gui = new GuiGameClient(initialBoard);
		} else {
			gui = new MockClient(initialBoard);
		}
		clients[0] = gui;
		clients[1] = mapStringToClient(clientType1, gui, initialBoard);
		clients[2] = mapStringToClient(clientType2, gui, initialBoard);
		return clients;
	}

	private static GameClient mapStringToClient(String clientType,
			GameClient gui, Board initialBoard) {
		if (Config.valClientTypeGui.equals(clientType)) {
			return gui;
		} else {
			if (Config.valClientTypeAlphabetaStd.equals(clientType)) {
				return new AlphaBetaClient(initialBoard);
			} else if (Config.valClientTypeAlphabetaFast.equals(clientType)) {
				return new client.alphabeta.fast.AlphaBetaClientFast(
						initialBoard);
			} else if (Config.valClientTypeGreedy.equals(clientType)) {
				return new GreedyGameClient(initialBoard);
			} else if (Config.valClientTypeAlphabeta3.equals(clientType)) {
				return new AlphaBetaClient3(initialBoard);
			} else if (Config.valClientTypeAlphabeta4.equals(clientType)) {
				return new AlphaBetaClient4(initialBoard);
			} else {
				return new RandomClient(initialBoard);
			}
		}
	}
}
