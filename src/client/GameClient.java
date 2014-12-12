package client;

import model.Board;
import model.PlayerType;
import model.Ply;
import config.Config;
import evaluate.EvaluationFunction;
import evaluate.EvaluationFunctionFactory;

public abstract class GameClient {

	protected final Board initialBoard;
	private final PlayerType player;
	protected EvaluationFunction evaluator;

	public static final int EXPECTED_NUMBER_OF_TURNS = (int) Config.readNumber(
			Config.keyEstimateTotalTurns, 14);
	public static final int LAST_OPENING_TURN = 2;
	public static final int LAST_MIDGAME_TURN = EXPECTED_NUMBER_OF_TURNS
			- EXPECTED_NUMBER_OF_TURNS / 3;
	public static final double[] timefraction = new double[] { 1, 1, 1, 2, 4,
			6, 8, 8, 6.5, 6.5, 5, 4 };
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
