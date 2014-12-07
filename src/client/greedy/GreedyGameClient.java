package client.greedy;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

import model.Board;
import model.PlayerType;
import model.Ply;
import model.RatedBoardPieceCount;
import control.GameClient;
import evaluate.EvaluationFunction;
import evaluate.EvaluationFunctionPieceCount;

public class GreedyGameClient extends GameClient {

	private EvaluationFunction evaluator = new EvaluationFunctionPieceCount();
	private Random randall = new Random();

	public GreedyGameClient(Board initialBoard) {
		super(initialBoard);
	}

	@Override
	public Ply doMove(Board board, PlayerType forPlayer, long maxDuration) {
		board = new RatedBoardPieceCount(board);
		try {
			Thread.sleep(1000);
		} catch (InterruptedException e) {
			e.printStackTrace();
		}
		List<Ply> plies = board.getPossiblePlies(forPlayer);
		List<Ply> bestPlies = new ArrayList<Ply>();
		int bestrating = Integer.MIN_VALUE + 1;
		for (Ply ply : plies) {
			PlayerType captured = board.perform(ply);
			int rating = evaluator.evaluate(board, forPlayer, 0);
			if (rating > bestrating) {
				bestPlies.clear();
				bestrating = rating;
			}
			if (rating == bestrating) {
				bestPlies.add(ply);
			}
			board.undo(ply, captured);
		}
		// return any of the best plies
		return bestPlies.get(randall.nextInt(bestPlies.size()));
	}

	@Override
	public void announceWinner(PlayerType winner) {

	}

	@Override
	public String getClientDescription() {
		return "Greedy Client. Always captures. If not possible acts randomly. Has 1 second delay in moving";
	}

}
