package knightthrough.client.alphabeta;

import java.util.List;
import java.util.Random;

import knightthrough.model.Board;
import knightthrough.model.PlayerType;
import knightthrough.model.Ply;
import knightthrough.client.GameClient;
import knightthrough.evaluate.EvaluationFunction;

/**
 * @author Fabian Braun
 *
 */
public class AlphaBetaClient1 extends GameClient {
	public static final int depth = 5;

	Random r = new Random();

	public AlphaBetaClient1(Board initialBoard, PlayerType player) {
		super(initialBoard, player);
	}

	@Override
	public Ply doMove(Board board, PlayerType forPlayer, long maxDuration) {
		board = evaluator.convertBoard(board);
		List<Ply> plies = board.getPossiblePlies(forPlayer);
		int bestrating = -EvaluationFunction.infty;
		Ply bestPly = plies.get(0);
		for (Ply ply : plies) {
			PlayerType captured = board.perform(ply);
			int rating = -alphabeta(board, depth - 1,
					-EvaluationFunction.infty, EvaluationFunction.infty,
					forPlayer.getOpponent());
			if (rating > bestrating) {
				bestPly = ply;
				bestrating = rating;
			}
			board.undo(ply, captured);
		}
		return bestPly;
	}

	private int alphabeta(Board b, int depth, int alpha, int beta, PlayerType p) {
		if (!PlayerType.NONE.equals(b.whosTheWinner()) || depth <= 0) {
			return evaluator.evaluate(b, p) * 1000 + r.nextInt(5) - 2 + depth;
		}
		int score = -EvaluationFunction.infty;
		List<Ply> plies = b.getPossiblePlies(p);
		for (Ply ply : plies) {
			PlayerType captured = b.perform(ply);
			int value = -alphabeta(b, depth - 1, -beta, -alpha, p.getOpponent());
			b.undo(ply, captured);
			if (value > score) {
				score = value;
			}
			if (score > alpha) {
				alpha = score;
			}
			if (score >= beta) {
				break; // prune
			}
		}
		return score;
	}

	@Override
	public void announceWinner(PlayerType winner) {
		// fine, I don't care...

	}

	@Override
	public String getClientDescription() {
		return "Good old AlphaBeta without enhancements. Reaches depth 5. Uses "
				+ evaluator.getClass().getSimpleName() + "";
	}
}
