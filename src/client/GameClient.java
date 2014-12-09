package client;

import model.Board;
import model.PlayerType;
import model.Ply;

public abstract class GameClient {

	protected final Board initialBoard;

	public static final int expectedNumberOfTurns = 14;
	public static final int openingEnds = 2;
	public static final int midGameEnds = expectedNumberOfTurns
			- expectedNumberOfTurns / 3;

	public GameClient(Board initialBoard) {
		this.initialBoard = initialBoard;
	}

	public Ply move(final Board board, final PlayerType forPlayer,
			long maxDuration, int turnIndex) {
		long durationForNextPly = getDurationForNextPly(maxDuration, turnIndex);
		if (openingEnds < turnIndex && turnIndex <= midGameEnds) {
			durationForNextPly = getDurationAlternating(durationForNextPly,
					turnIndex);
		}
		Ply next = doMove(board, forPlayer, durationForNextPly);
		turnIndex++;
		return next;
	}

	private long getDurationForNextPly(long totalTimeLeft, int turnIndex) {
		if (turnIndex <= openingEnds) {
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
		if (turnIndex < expectedNumberOfTurns - (expectedNumberOfTurns / 4)) {
			// assume equal distribution
			return expectedNumberOfTurns - turnIndex;
		} else if (turnIndex < expectedNumberOfTurns
				- (expectedNumberOfTurns / 6)) {
			// 75% to 83%
			return expectedNumberOfTurns / 4;
		}
		return expectedNumberOfTurns / 6;
	}

	// duration = millisec
	protected abstract Ply doMove(Board board, PlayerType forPlayer,
			long duration);

	public abstract void announceWinner(PlayerType winner);

	public abstract String getClientDescription();
}
