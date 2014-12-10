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
		if (LAST_OPENING_TURN < turnIndex && turnIndex <= LAST_MIDGAME_TURN) {
			durationForNextPly = getDurationAlternating(durationForNextPly,
					turnIndex);
		}
		Ply next = doMove(board, player, durationForNextPly);
		turnIndex++;
		return next;
	}

	private long getDurationForNextPly(long totalTimeLeft, int turnIndex) {
		if (turnIndex <= LAST_OPENING_TURN) {
			// first turns use few time
			return (totalTimeLeft / 4) / turnsToWin(turnIndex);
		}
		return totalTimeLeft / turnsToWin(turnIndex);
	}

	private long getDurationAlternating(long normalDuration, int turnIndex) {
		if (turnIndex % 2 > 0) {
			return normalDuration + normalDuration / 2;
		} else {
			// return normalDuration - normalDuration / 2;
			return normalDuration;
		}
	}

	private int turnsToWin(int turnIndex) {
		// first 75% of the game
		if (turnIndex < EXPECTED_NUMBER_OF_TURNS
				- (EXPECTED_NUMBER_OF_TURNS / 4)) {
			// assume equal distribution
			return EXPECTED_NUMBER_OF_TURNS - turnIndex;
		} else if (turnIndex < EXPECTED_NUMBER_OF_TURNS
				- (EXPECTED_NUMBER_OF_TURNS / 6)) {
			// 75% to 83%
			return EXPECTED_NUMBER_OF_TURNS / 4;
		}
		return EXPECTED_NUMBER_OF_TURNS / 6;
	}

	// duration = millisec
	protected abstract Ply doMove(Board board, PlayerType forPlayer,
			long duration);

	public abstract void announceWinner(PlayerType winner);

	public abstract String getClientDescription();
}
