package client;

import model.Board;
import model.PlayerType;
import client.alphabeta.standard.AlphaBetaClient;
import client.alphabeta.v3.AlphaBetaClient3;
import client.alphabeta.v4.AlphaBetaClient4;
import client.alphabeta.v5.AlphaBetaClient5;
import client.alphabeta.v6.AlphaBetaClient6;
import client.alphabeta.v7.AlphaBetaClient7;
import client.greedy.GreedyGameClient;
import client.gui.GuiGameClient;
import client.random.RandomClient;
import config.Config;

public class GameClientFactory {

	public static GameClient[] getClients(Board initialBoard) {
		GameClient[] clients = new GameClient[2];
		String clientTypeDown = Config.getStringValue(
				Config.keyClientTypePlayerDown, Config.valClientTypeRandom);
		String clientTypeUp = Config.getStringValue(
				Config.keyClientTypePlayerUp, Config.valClientTypeGui);
		boolean guiRequired = false;
		if (Config.valClientTypeGui.equals(clientTypeDown)
				|| Config.valClientTypeGui.equals(clientTypeUp)) {
			guiRequired = true;
		}
		GameClient gui = null;
		if (guiRequired) {
			gui = new GuiGameClient(initialBoard, PlayerType.NONE);
		}
		clients[0] = mapStringToClient(clientTypeDown, gui, PlayerType.DOWN,
				initialBoard);
		clients[1] = mapStringToClient(clientTypeUp, gui, PlayerType.UP,
				initialBoard);
		return clients;
	}

	private static GameClient mapStringToClient(String clientType,
			GameClient gui, PlayerType player, Board initialBoard) {
		if (Config.valClientTypeGui.equals(clientType)) {
			return gui;
		} else {
			if (Config.valClientTypeAlphabetaStd.equals(clientType)) {
				return new AlphaBetaClient(initialBoard, player);
			} else if (Config.valClientTypeAlphabetaFast.equals(clientType)) {
				return new client.alphabeta.fast.AlphaBetaClientFast(
						initialBoard, player);
			} else if (Config.valClientTypeGreedy.equals(clientType)) {
				return new GreedyGameClient(initialBoard, player);
			} else if (Config.valClientTypeAlphabeta3.equals(clientType)) {
				return new AlphaBetaClient3(initialBoard, player);
			} else if (Config.valClientTypeAlphabeta4.equals(clientType)) {
				return new AlphaBetaClient4(initialBoard, player);
			} else if (Config.valClientTypeAlphabeta5.equals(clientType)) {
				return new AlphaBetaClient5(initialBoard, player);
			} else if (Config.valClientTypeAlphabeta6.equals(clientType)) {
				return new AlphaBetaClient6(initialBoard, player);
			} else if (Config.valClientTypeAlphabeta7.equals(clientType)) {
				return new AlphaBetaClient7(initialBoard, player);
			} else {
				return new RandomClient(initialBoard, player);
			}
		}
	}
}
