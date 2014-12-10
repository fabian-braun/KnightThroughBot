package client.alphabeta.v3;

import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
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
import transpositiontable.Entry;
import transpositiontable.EntryType;
import client.GameClient;
import evaluate.EvaluationFunction;

/**
 * 
 * @author Fabian
 *
 */
public class AlphaBetaClient3 extends GameClient {

	ExecutorService executor = Executors.newSingleThreadExecutor();

	private Map<Long, Entry> tTable = new HashMap<Long, Entry>(1000000);

	long nodeCount = 0;

	private Future<Ply> futureBestPly;

	public AlphaBetaClient3(Board initialBoard, PlayerType player) {
		super(initialBoard, player);
	}

	@Override
	public Ply doMove(Board board, final PlayerType forPlayer, long duration) {
		System.out.println("time for next move (seconds): " + duration / 1000);
		long timeToFinish = System.currentTimeMillis() + duration;
		final Board boardF = evaluator.convertBoard(board);
		Ply bestPly = alphabeta(boardF, forPlayer, 1);
		for (int depth = 4; depth < 15; depth++) {
			if (bestPly.getEvaluationValue() > EvaluationFunction.infty - 30) {
				// bestPly is winning move
				// don't evaluate further
				System.out.println("winning move found: " + bestPly);
				break;
			}
			long remaining = timeToFinish - System.currentTimeMillis();
			if (remaining < duration / 2)
				break; // won't finish anyway
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
				System.out.println("Time was consumed during depth " + depth);
				System.out.println("calculation was cancelled:"
						+ futureBestPly.cancel(true));
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
		tTable.clear();
		List<Ply> plies = board.getPossiblePlies(forPlayer);
		int bestrating = -EvaluationFunction.infty;
		Ply bestPly = plies.get(0);
		for (Ply ply : plies) {
			PlayerType captured = board.perform(ply);
			int rating = -alphabetaRec(board, depth - 1,
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
				+ nodeCount + "; TTable size : " + tTable.size());
		return bestPly;
	}

	private int alphabetaRec(Board b, int depth, int alpha, int beta,
			PlayerType p) {
		if (Thread.currentThread().isInterrupted()) {
			return EvaluationFunction.infty + 15;
		}
		int alphaOrig = alpha;
		Entry saved = tTable.get(b.getZobrist());
		if (saved != null && saved.getDepth() >= depth) {
			switch (saved.getType()) {
			case EXACT:
				return saved.getValue();
			case LOWERBOUND:
				alpha = alpha > saved.getValue() ? alpha : saved.getValue();
				break;
			case UPPERBOUND:
				beta = beta < saved.getValue() ? beta : saved.getValue();
				break;
			default:
				break;
			}
			if (alpha > beta)
				return saved.getValue();
		}
		if (depth <= 0 || !PlayerType.NONE.equals(b.whosTheWinner())) {
			nodeCount++;
			return evaluator.evaluate(b, p, 30 - depth);
		}
		int bestValue = -EvaluationFunction.infty;
		List<Ply> plies = b.getPossiblePlies(p);
		// move ordering
		if (depth > 1) {
			plies = sortPlies(plies, b, p);
		}
		for (Ply ply : plies) {
			PlayerType captured = b.perform(ply);
			int value = -alphabetaRec(b, depth - 1, -beta, -alpha,
					p.getOpponent());
			b.undo(ply, captured);
			if (value > bestValue) {
				bestValue = value;
			}
			if (bestValue > alpha) {
				alpha = bestValue;
			}
			if (alpha >= beta) {
				break; // prune
			}
		}
		Entry tTableEntry;
		if (bestValue <= alphaOrig) {
			tTableEntry = new Entry(bestValue, EntryType.UPPERBOUND, depth);
		} else if (bestValue >= beta) {
			tTableEntry = new Entry(bestValue, EntryType.LOWERBOUND, depth);
		} else {
			tTableEntry = new Entry(bestValue, EntryType.EXACT, depth);
		}
		tTable.put(b.getZobrist(), tTableEntry);
		nodeCount++;
		return bestValue;
	}

	/**
	 * puts all plies in a new list. Capture-plies are put in the beginning and
	 * others in the end.
	 * 
	 * @param plies
	 * @param b
	 * @param player
	 * @return new sorted List
	 */
	private List<Ply> sortPlies(List<Ply> plies, Board b, PlayerType player) {
		LinkedList<Ply> sorted = new LinkedList<Ply>();
		for (Ply ply : plies) {
			if (!PlayerType.NONE.equals(b.getPlayerType(ply.to))) {
				// this is a capture move
				sorted.addFirst(ply);
			} else {
				sorted.addLast(ply);
			}
		}
		return sorted;
	}

	@Override
	public void announceWinner(PlayerType winner) {
		futureBestPly.cancel(true);
		executor.shutdown();
	}

	@Override
	public String getClientDescription() {
		return "AlphaBeta v3. Uses iterative Deepening, TT, Move Ordering, "
				+ evaluator.getClass().getSimpleName() + "";
	}

}
