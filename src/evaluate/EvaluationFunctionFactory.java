package evaluate;

import config.Config;
import error.GameException;

public class EvaluationFunctionFactory {

	public static EvaluationFunction mapStringToEval(String functionType) {
		if (Config.valEvaluationFunctionDevelopment.equals(functionType)) {
			return new EvaluationFunctionDevelopment();
		} else if (Config.valEvaluationFunctionLeaderPosition
				.equals(functionType)) {
			return new EvaluationFunctionLeaderPosition();
		} else if (Config.valEvaluationFunctionPieceCount.equals(functionType)) {
			return new EvaluationFunctionPieceCount();
		} else if (Config.valEvaluationFunctionPieceCountAdvanced
				.equals(functionType)) {
			return new EvaluationFunctionPieceCountAdvanced();
		} else if (Config.valEvaluationFunctionZickZack.equals(functionType)) {
			return new EvaluationFunctionZickZack();
		} else if (Config.valEvaluationFunctionZickZackDevelopment
				.equals(functionType)) {
			return new EvaluationFunctionZickZackDevelopment();
		} else {
			throw new GameException("the specified functionType "
					+ functionType + " does not exist");
		}
	}

}
