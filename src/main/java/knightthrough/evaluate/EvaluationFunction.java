package knightthrough.evaluate;

import knightthrough.model.Board;
import knightthrough.model.PlayerType;

/**
 * @author Fabian Braun
 *
 */
public interface EvaluationFunction {

	// infinity
	public static final int infty = 1000000;

	public int evaluate(Board b, PlayerType p);

	public abstract Board convertBoard(Board board);
}
