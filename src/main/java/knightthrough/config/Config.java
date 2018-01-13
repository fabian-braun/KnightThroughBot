package knightthrough.config;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.Properties;

import knightthrough.error.GameException;

/**
 * @author Fabian Braun
 *
 */
public class Config {

	public static final String valTrue = "TRUE|True|true|1";
	public static final String valFalse = "FALSE|False|false|0";

	public static final String keyStartingPlayer = "STARTING_PLAYER";
	public static final String valStartingPlayerUp = "PLAYER_UP";
	public static final String valStartingPlayerDown = "PLAYER_DOWN";

	public static final String keyClientTypePlayerUp = "PLAYER_UP";
	public static final String keyClientTypePlayerDown = "PLAYER_DOWN";
	public static final String valClientTypeGui = "GUI";
	public static final String valClientTypeRandom = "RANDOM";
	public static final String valClientTypeGreedy = "GREEDY";
	public static final String valClientTypeAlphabeta1 = "ALPHABETA_1";
	public static final String valClientTypeAlphabeta2 = "ALPHABETA_2";
	public static final String valClientTypeAlphabeta3 = "ALPHABETA_3";
	public static final String valClientTypeAlphabeta4 = "ALPHABETA_4";
	public static final String valClientTypeAlphabeta5 = "ALPHABETA_5";
	public static final String valClientTypeAlphabeta6 = "ALPHABETA_6";
	public static final String valClientTypeAlphabeta7 = "ALPHABETA_7";

	public static final String keyEvaluationFunctionPlayerUp = "EVALUATION_PLAYER_UP";
	public static final String keyEvaluationFunctionPlayerDown = "EVALUATION_PLAYER_DOWN";
	public static final String valEvaluationFunctionDevelopment = "DEVELOPMENT";
	public static final String valEvaluationFunctionLeaderPosition = "LEADER_POS";
	public static final String valEvaluationFunctionPieceCount = "PIECE_COUNT";
	public static final String valEvaluationFunctionStructure = "STRUCTURE";
	public static final String valEvaluationFunctionZickZack = "ZICKZACK";
	public static final String valEvaluationFunctionZickZackDevelopment = "ZICKZACKDEVELOPMENT";

	public static final String keyTotalTimePlayerUp = "TOTAL_TIME_PLAYER_UP";
	public static final String keyTotalTimePlayerDown = "TOTAL_TIME_PLAYER_DOWN";

	public static final String keySaveGame = "SAVE_GAME";
	public static final String keySavedGameFilePath = "INIT_GAME_FROM_FILE";

	public static final String keyShowGui = "SHOW_GUI";

	private static Properties configuration = initConfiguration();

	private static Properties initConfiguration() {
		Properties p = new Properties();
		try (InputStream is = Thread.currentThread().getContextClassLoader().getResourceAsStream("config.properties")) {
			p.load(is);
		} catch (IOException e) {
			throw new GameException("default configuration file couldn't be read from classpath", e);
		}
		String pwd = System.getProperty("user.dir");
		File fsConfigFile = new File(pwd, "config.properties");
		if (fsConfigFile.exists()) {
			try (FileInputStream fis = new FileInputStream(fsConfigFile)) {
				p = new Properties();
				p.load(fis);
			} catch (IOException e) {
				System.out.println(
						"Couldn't read config file [" + fsConfigFile.getAbsolutePath() + "]. " + e.getMessage());
			}
		} else {
			System.out
					.println("Couldn't find config file at [" + fsConfigFile.getAbsolutePath() + "]. Using defaults.");
		}
		return p;
	}

	/**
	 * returns the value for the given key from the property file. If value cannot
	 * be obtained from the property file, the reason is logged, and the
	 * defaultvalue is applied.
	 * 
	 * @param key
	 * @param defaultValue
	 * @return
	 */
	public static boolean getBooleanValue(String key, boolean defaultValue) {
		// read configuration
		String svalue = configuration.getProperty(key);
		if (svalue == null) {
			return defaultValue;
		} else {
			return svalue.matches(valTrue);
		}
	}

	public static long readNumber(String key, long defaultValue) {
		long value = defaultValue;
		try {
			String svalue = configuration.getProperty(key);
			value = Long.parseLong(svalue);
		} catch (NumberFormatException e) {
			System.err.println(
					"value for property " + key + " is not numeric. Default value '" + defaultValue + "' assumed");
		}
		return value;
	}

	/**
	 * returns the value for the given key from the property file. If value cannot
	 * be obtained from the property file, the reason is logged, and the
	 * defaultvalue is applied.
	 * 
	 * @param key
	 * @param defaultValue
	 * @return
	 */
	public static String getStringValue(String key, String defaultValue) {
		String svalue = configuration.getProperty(key);
		if (svalue == null) {
			return defaultValue;
		} else {
			return svalue;
		}
	}

}
