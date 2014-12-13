package client.alphabeta.v7;

import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Random;
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
import model.Position;
import transpositiontable.Entry;
import transpositiontable.EntryType;
import transpositiontable.TTable;
import client.GameClient;
import evaluate.EvaluationFunction;

/**
 * 
 * @author Fabian
 *
 */
public class AlphaBetaClient7 extends GameClient {

	ExecutorService executor = Executors.newSingleThreadExecutor();

	public static final int TT_SIZE = 10000000;

	private Map<Long, Entry> tTable = new HashMap<Long, Entry>(TT_SIZE);
	private Map<Long, Entry> tTablePrevious = new HashMap<Long, Entry>(TT_SIZE);

	Random random = new Random();

	private static final int startDepth = 5;

	long nodeCount = 0;

	long preDuration = 1;
	long prepreDuration = 1;

	private Future<Ply> futureBestPly;

	private long remaining = 1;

	public AlphaBetaClient7(Board initialBoard, PlayerType player) {
		super(initialBoard, player);
	}

	@Override
	public Ply doMove(Board board, final PlayerType forPlayer, long duration) {
		System.out.println("time for next move (seconds): " + duration / 1000);
		long timeToFinish = System.currentTimeMillis() + duration;
		final Board boardF = evaluator.convertBoard(board);
		Ply bestPly = alphabeta(boardF, forPlayer, 1);
		for (int depth = startDepth; depth < 15; depth++) {
			if (bestPly.getEvaluationValue() > EvaluationFunction.infty - 50) {
				// bestPly is winning move
				// don't evaluate further
				System.out.println("winning move found: " + bestPly);
				break;
			}
			long lastRemaining = remaining;
			remaining = timeToFinish - System.currentTimeMillis();
			prepreDuration = preDuration;
			preDuration = lastRemaining - remaining;
			if (preDuration < 1)
				preDuration = 1;

			if (depth > startDepth + 2
					&& remaining < preDuration * preDuration / prepreDuration)
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
		tTablePrevious = tTable;
		tTable = new HashMap<Long, Entry>(TT_SIZE);
		List<Ply> plies;
		Position threateningPieceAt = board.getThreateningPiece(forPlayer
				.getOpponent());
		if (threateningPieceAt != null) {
			List<Ply> capturePlies = board.getPossiblePliesTo(forPlayer,
					threateningPieceAt);
			if (capturePlies.size() == 1) {
				// capturing is the only option
				System.out.println("Forced capture move");
				return capturePlies.get(0);
			} else if (capturePlies.size() > 1) {
				System.out.println("Only evaluate " + capturePlies.size()
						+ " capture moves");
				plies = capturePlies;
			} else {
				// we lost anyway
				// could also do random move here
				plies = board.getPossiblePlies(forPlayer);
			}
		} else {
			plies = board.getPossiblePlies(forPlayer);
			plies = sortPlies(plies, board, forPlayer);
		}
		int bestrating = -EvaluationFunction.infty;
		Ply bestPly = plies.get(0);
		for (Ply ply : plies) {
			PlayerType captured = board.perform(ply);
			int rating = -alphabetaRec(board, depth - 1,
					-EvaluationFunction.infty, -bestrating + 1,
					forPlayer.getOpponent());
			board.undo(ply, captured);
			if (rating > bestrating) {
				bestPly = ply;
				bestrating = rating;
			} else if (rating == bestrating && random.nextBoolean()) {
				// random behavior
				bestPly = ply;
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
			if (alpha >= beta)
				return saved.getValue();
		}
		if (depth <= 0 || !PlayerType.NONE.equals(b.whosTheWinner())) {
			nodeCount++;
			return evaluator.evaluate(b, p) + depth;
		}

		// forward pruning
		Position threateningPieceAt = b.getThreateningPiece(p.getOpponent());
		List<Ply> plies = null;
		if (threateningPieceAt != null) {
			plies = b.getPossiblePliesTo(p, threateningPieceAt);
			if (plies.isEmpty()) {
				// we lost
				nodeCount++;
				return -EvaluationFunction.infty - depth;
			}
		}
		// opponent has piece advantage, only evaluate capture moves
		if ((plies == null)
				&& b.getCountFor(p) < b.getCountFor(p.getOpponent())) {
			plies = b.getCapturePlies(p);
		}
		if (plies == null || plies.isEmpty()) {
			plies = b.getPossiblePlies(p);
		}
		// move ordering
		if (depth > 1) {
			plies = sortPlies(plies, b, p);
		}

		int bestValue = -EvaluationFunction.infty;
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
		// Replacement scheme: Deep-New
		if (saved == null || depth >= saved.getDepth()) {
			Entry tTableEntry;
			if (bestValue <= alphaOrig) {
				tTableEntry = new Entry(bestValue, EntryType.UPPERBOUND, depth);
			} else if (bestValue >= beta) {
				tTableEntry = new Entry(bestValue, EntryType.LOWERBOUND, depth);
			} else {
				tTableEntry = new Entry(bestValue, EntryType.EXACT, depth);
			}
			tTable.put(b.getZobrist(), tTableEntry);
		}
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
	private List<Ply> sortPlies(List<Ply> plies, final Board b,
			PlayerType player) {
		LinkedList<Ply> sorted = new LinkedList<Ply>();
		for (Ply ply : plies) {
			long zobrist = b.getZobrist();
			zobrist ^= TTable.code(ply.from.y, ply.from.x, player);
			if (!PlayerType.NONE.equals(b.getPlayerType(ply.to)))
				zobrist ^= TTable
						.code(ply.to.y, ply.to.x, player.getOpponent());
			zobrist ^= TTable.code(ply.to.y, ply.to.x, player);
			Entry entryPly = tTablePrevious.get(zobrist);
			if (entryPly == null || !entryPly.getType().equals(EntryType.EXACT))
				if (!PlayerType.NONE.equals(b.getPlayerType(ply.to))) {
					// this is a capture move
					sorted.addFirst(ply);
				} else {
					sorted.addLast(ply);
				}
			else if (entryPly.getValue() > 500) {
				sorted.addFirst(ply);
			} else if (entryPly.getValue() > 0) {
				sorted.add(sorted.size() / 3 + sorted.size() > 1 ? 1 : 0, ply);
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
		return "AlphaBeta v7. Uses iterative Deepening, TT, TT-based Move Ordering, Greedy Forward Pruning, "
				+ evaluator.getClass().getSimpleName()
				+ " - this is outperformed by AlphaBeta6 in the end game";
	}

}
