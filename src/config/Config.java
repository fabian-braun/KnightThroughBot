package config;

import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.util.Properties;

public class Config {

	private static final String CONFIG_FILE_BIN = ClassLoader
			.getSystemClassLoader().getResource(".").getPath()
			+ "config.properties";
	private static final String CONFIG_FILE_PRJ = "resources/config.properties";
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
	public static final String valClientTypeAlphabetaStd = "ALPHABETA_STANDARD";
	public static final String valClientTypeAlphabetaFast = "ALPHABETA_FAST";
	public static final String valClientTypeAlphabeta3 = "ALPHABETA_3";
	public static final String valClientTypeAlphabeta4 = "ALPHABETA_4";

	public static final String keyTotalTimePlayerUp = "TOTAL_TIME_PLAYER_UP";
	public static final String keyTotalTimePlayerDown = "TOTAL_TIME_PLAYER_DOWN";

	public static final String keySaveGame = "SAVE_GAME";
	public static final String keySavedGameFilePath = "INIT_GAME_FROM_FILE";

	public static final String keyShowGui = "SHOW_GUI";

	/**
	 * returns the value for the given key from the property file. If value
	 * cannot be obtained from the property file, the reason is logged, and the
	 * defaultvalue is applied.
	 * 
	 * @param key
	 * @param defaultValue
	 * @return
	 */
	public static boolean getBooleanValue(String key, boolean defaultValue) {
		// read configuration
		Properties prop = new Properties();
		InputStream input = null;
		boolean value = defaultValue;
		try {
			input = getConfigFileHandle();
			prop.load(input);
			String svalue = prop.getProperty(key);
			if (svalue == null) {
			} else if (svalue.matches(valTrue)) {
				value = true;
			} else if (svalue.matches(valFalse)) {
				value = false;
			}
		} catch (IOException ex) {
			System.err.println("config file cannot be read. Default value '"
					+ defaultValue + "' assumed for property '" + key + "'");
		} finally {
			if (input != null) {
				try {
					input.close();
				} catch (IOException e) {
				}
			}
		}
		return value;
	}

	public static long readNumber(String key, long defaultValue) {
		// read configuration
		Properties prop = new Properties();
		InputStream input = null;
		long value = defaultValue;
		try {
			input = getConfigFileHandle();
			prop.load(input);
			String svalue = prop.getProperty(key);
			value = Long.parseLong(svalue);
		} catch (IOException ex) {
			System.err.println("config file cannot be read. Default value '"
					+ defaultValue + "' assumed for property '" + key + "'");
		} catch (NumberFormatException e) {
			System.err.println("value for property " + key
					+ " is not numeric. Default value '" + defaultValue
					+ "' assumed");
		} finally {
			if (input != null) {
				try {
					input.close();
				} catch (IOException e) {
				}
			}
		}
		return value;
	}

	/**
	 * returns the value for the given key from the property file. If value
	 * cannot be obtained from the property file, the reason is logged, and the
	 * defaultvalue is applied.
	 * 
	 * @param key
	 * @param defaultValue
	 * @return
	 */
	public static String getStringValue(String key, String defaultValue) {
		// read configuration
		Properties prop = new Properties();
		InputStream input = null;
		String value = defaultValue;
		try {
			input = getConfigFileHandle();
			prop.load(input);
			value = prop.getProperty(key);
		} catch (IOException ex) {
			System.err.println("config file cannot be read. Default value '"
					+ defaultValue + "' assumed for property '" + key + "'");
		} finally {
			if (input != null) {
				try {
					input.close();
				} catch (IOException e) {
				}
			}
		}
		return value;
	}

	private static FileInputStream getConfigFileHandle()
			throws FileNotFoundException {
		try {
			FileInputStream input = new FileInputStream(CONFIG_FILE_BIN);
			return input;
		} catch (FileNotFoundException e) {
		}
		try {
			FileInputStream input = new FileInputStream(CONFIG_FILE_PRJ);
			return input;
		} catch (FileNotFoundException e) {
			System.out.println("config file " + CONFIG_FILE_BIN
					+ " cannot be read.");
			System.out.println("config file " + CONFIG_FILE_PRJ
					+ " cannot be read.");
			throw e;
		}

	}

}
