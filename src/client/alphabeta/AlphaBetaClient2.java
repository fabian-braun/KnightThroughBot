package client.alphabeta;

import java.util.Collections;
import java.util.List;
import java.util.concurrent.Callable;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.TimeoutException;

import model.Board;
import model.PlayerType;
import model.Ply;
import client.GameClient;
import evaluate.EvaluationFunction;

/**
 * @author Fabian Braun
 *
 */
public class AlphaBetaClient2 extends GameClient {

	ExecutorService executor = Executors.newSingleThreadExecutor();

	long nodeCount = 0;

	private Future<Ply> futureBestPly;

	public AlphaBetaClient2(Board initialBoard, PlayerType player) {
		super(initialBoard, player);
	}

	@Override
	public Ply doMove(Board board, final PlayerType forPlayer, long duration) {
		System.out.println("time for next move (seconds): " + duration / 1000);
		long timeToFinish = System.currentTimeMillis() + duration;
		final Board boardF = evaluator.convertBoard(board);
		Ply bestPly = alphabeta(boardF, forPlayer, 1);
		for (int depth = 4; depth < 10; depth++) {
			if (bestPly.getEvaluationValue() > EvaluationFunction.infty - 30) {
				// bestPly is winning move
				// don't evaluate further
				System.out.println("winning move found: " + bestPly);
				break;
			}
			long remaining = timeToFinish - System.currentTimeMillis();
			final int depthF = depth;
			Callable<Ply> callable = new Callable<Ply>() {
				@Override
				public Ply call() throws Exception {
					return alphabeta(boardF, forPlayer, depthF);
				}
			};
			futureBestPly = executor.submit(callable);
			try {
				bestPly = futureBestPly.get(remaining, TimeUnit.MILLISECONDS);
			} catch (ExecutionException e) {
				System.out.println("Execution Exception... this is not good");
				e.printStackTrace();
			} catch (TimeoutException e) {
				futureBestPly.cancel(true);
				System.out.println("Time was consumed during depth " + depth);
				break;
			} catch (InterruptedException e) {
				System.out
						.println("InterruptedException... don't have a problem with that");
			}
		}
		System.out.println("Play " + bestPly + " rated as "
				+ bestPly.getEvaluationValue());
		return bestPly;
	}

	private Ply alphabeta(Board board, PlayerType forPlayer, int depth) {
		nodeCount = 0;
		List<Ply> plies = board.getPossiblePlies(forPlayer);
		int bestrating = -EvaluationFunction.infty;
		Ply bestPly = plies.get(0);
		for (Ply ply : plies) {
			PlayerType captured = board.perform(ply);
			int rating = -alphabeta(board, depth - 1,
					-EvaluationFunction.infty, EvaluationFunction.infty,
					forPlayer.getOpponent());
			board.undo(ply, captured);
			if (rating > bestrating) {
				bestPly = ply;
				bestrating = rating;
			}
		}
		bestPly.setEvaluationValue(bestrating);
		System.out.println("Evaluated nodes on depth " + depth + " : "
				+ nodeCount);
		return bestPly;
	}

	private int alphabeta(Board b, int depth, int alpha, int beta, PlayerType p) {
		if (Thread.currentThread().isInterrupted()) {
			return EvaluationFunction.infty + 15;
		}
		if (!PlayerType.NONE.equals(b.whosTheWinner()) || depth <= 0) {
			nodeCount++;
			return evaluator.evaluate(b, p) + depth;
		}
		int score = -EvaluationFunction.infty;
		List<Ply> plies = b.getPossiblePlies(p);
		// move ordering
		if (depth > 1) {
			sortPlies(plies, b, p);
		}
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
		nodeCount++;
		return score;
	}

	private void sortPlies(List<Ply> plies, Board b, PlayerType player) {
		for (Ply ply : plies) {
			PlayerType captured = b.perform(ply);
			int rating = captured.equals(player.getOpponent()) ? 1 : 0;
			b.undo(ply, captured);
			ply.setEvaluationValue(rating);
		}
		Collections.sort(plies);
	}

	@Override
	public void announceWinner(PlayerType winner) {
		futureBestPly.cancel(true);
		executor.shutdown();
	}

	@Override
	public String getClientDescription() {
		return "AlphaBeta Fast. Uses iterative Deepening, Move Ordering: capture moves first, "
				+ evaluator.getClass().getSimpleName() + "";
	}

}
