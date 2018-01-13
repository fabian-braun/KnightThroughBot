package knightthrough.error;

/**
 * @author Fabian Braun
 *
 */
public class GameException extends RuntimeException {
	private static final long serialVersionUID = 4257530574318031501L;

	public GameException() {
		super();
	}

	public GameException(String message) {
		super(message);
	}
}
