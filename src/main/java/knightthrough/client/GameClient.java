package knightthrough.client;

import knightthrough.model.Board;
import knightthrough.model.PlayerType;
import knightthrough.model.Ply;
import knightthrough.config.Config;
import knightthrough.evaluate.EvaluationFunction;
import knightthrough.evaluate.EvaluationFunctionFactory;

/**
 * @author Fabian Braun
 *
 */
public abstract class GameClient {

	protected final Board initialBoard;
	private final PlayerType player;
	protected EvaluationFunction evaluator;

	private static final double[] timefraction = new double[] { 0.1, 1, 2, 3,
			6, 9, 9.5, 10, 10.5 };
	static {
		// normalize time
		double sum = 0;
		for (double d : timefraction) {
			sum += d;
		}
		for (int i = 0; i < timefraction.length; i++) {
			timefraction[i] = timefraction[i] / sum;
		}
	}

	public GameClient(Board initialBoard, PlayerType player) {
		this.initialBoard = initialBoard;
		this.player = player;
		String key = "";
		if (player.equals(PlayerType.DOWN)) {
			key = Config.keyEvaluationFunctionPlayerDown;
		} else {
			key = Config.keyEvaluationFunctionPlayerUp;
		}
		evaluator = EvaluationFunctionFactory.mapStringToEval(Config
				.getStringValue(key, Config.valEvaluationFunctionDevelopment));
	}

	public Ply move(final Board board, PlayerType forPlayer, long maxDuration,
			int turnIndex) {
		long durationForNextPly = getDurationForNextPly(maxDuration, turnIndex);
		Ply next = doMove(board, player, durationForNextPly);
		turnIndex++;
		return next;
	}

	private long getDurationForNextPly(long totalTimeLeft, int turnIndex) {
		if (turnIndex < timefraction.length) {
			return (long) (totalTimeLeft * timefraction[turnIndex]);
		} else {
			// we exceeded estimated number of moves already. Assume 3 plies
			// left
			return totalTimeLeft / 3;
		}
	}

	// duration = millisec
	protected abstract Ply doMove(Board board, PlayerType forPlayer,
			long duration);

	public abstract void announceWinner(PlayerType winner);

	public abstract String getClientDescription();
}
