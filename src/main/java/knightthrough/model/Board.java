package knightthrough.model;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Set;

import knightthrough.transpositiontable.Zobrist;

/**
 * Basic representation of a KnightThrough board. Allows illegal moves.
 * Optimized to produce high performance.
 * 
 * @author Fabian Braun
 *
 */
public class Board implements Serializable {

	private static final long serialVersionUID = -8288026676803633407L;
	protected PlayerType[][] board;
	protected final int ySize;
	protected final int xSize;
	private long zobristHash;
	private static HashMap<Position, List<Position>> legalTargetsDown;
	private static HashMap<Position, List<Position>> legalTargetsUp;

	static {
		// static initialization of legal targets to increase performance
		legalTargetsDown = new HashMap<Position, List<Position>>();
		legalTargetsUp = new HashMap<Position, List<Position>>();
		for (int y = 0; y < 8; y++) {
			for (int x = 0; x < 8; x++) {
				legalTargetsUp.put(new Position(y, x),
						getLegalPositions(y, x, 8, 8, PlayerType.UP));
				legalTargetsDown.put(new Position(y, x),
						getLegalPositions(y, x, 8, 8, PlayerType.DOWN));
			}
		}
	}

	/**
	 * returns all positions that can be reached by a piece directly. Does not
	 * take into account other pieces on the board. For faster access use
	 * lookupLegalPositions.
	 * 
	 * @param y
	 * @param x
	 * @param ySize
	 * @param xSize
	 * @param player
	 * @return
	 */
	private static List<Position> getLegalPositions(int y, int x, int ySize,
			int xSize, PlayerType player) {
		List<Position> positions = getAdjacentPositions(y, x, player);
		for (int i = positions.size() - 1; i >= 0; i--) {
			if (positions.get(i).x < 0 || positions.get(i).x > xSize - 1
					|| positions.get(i).y < 0 || positions.get(i).y > ySize - 1) {
				positions.remove(i);
			}
		}
		return positions;
	}

	private static List<Position> getAdjacentPositions(int y, int x,
			PlayerType player) {
		List<Position> positions = new LinkedList<Position>();
		if (player == PlayerType.UP) {
			positions.add(new Position(y - 1, x + 2));
			positions.add(new Position(y - 1, x - 2));
			positions.add(new Position(y - 2, x + 1));
			positions.add(new Position(y - 2, x - 1));
		} else if (player == PlayerType.DOWN) {
			positions.add(new Position(y + 2, x + 1));
			positions.add(new Position(y + 2, x - 1));
			positions.add(new Position(y + 1, x + 2));
			positions.add(new Position(y + 1, x - 2));
		}
		return positions;
	}

	/**
	 * returns all positions that can be reached by a piece directly. Does not
	 * take into account other pieces on the board.
	 * 
	 * @param y
	 * @param x
	 * @param ySize
	 * @param xSize
	 * @param player
	 * @return
	 */
	public static List<Position> lookupLegalPositions(Position from,
			PlayerType forPlayer) {
		// return a copy, as the returned List must never be changed!
		switch (forPlayer) {
		case DOWN:
			return new ArrayList<Position>(legalTargetsDown.get(from));
		case UP:
			return new ArrayList<Position>(legalTargetsUp.get(from));
		default:
			return new ArrayList<Position>();
		}
	}

	/**
	 * returns all legal positions for a player's piece which are not occupied
	 * by this player.
	 * 
	 * @param y
	 * @param x
	 * @param forPlayer
	 * @return
	 */
	protected List<Position> getLegalUnoccupiedPositions(int y, int x,
			PlayerType forPlayer) {
		List<Position> positions = lookupLegalPositions(new Position(y, x),
				forPlayer);
		for (int i = positions.size() - 1; i >= 0; i--) {
			if (board[positions.get(i).y][positions.get(i).x] == forPlayer) {
				positions.remove(i);
			}
		}
		return positions;
	}

	/**
	 * Copy Constructor
	 * 
	 * @param copy
	 */
	public Board(Board copy) {
		this.zobristHash = copy.zobristHash;
		this.ySize = copy.ySize;
		this.xSize = copy.xSize;
		board = new PlayerType[ySize][xSize];
		for (int y = 0; y < ySize; y++) {
			for (int x = 0; x < xSize; x++) {
				set(y, x, copy.board[y][x]);
			}
		}
	}

	/**
	 * See {@link BoardFactory} for initialization of {@link Board}. This
	 * Constructor creates an 8x8 board with a single piece for each player in
	 * the corner of the board.
	 * 
	 * @param ySize
	 * @param xSize
	 */
	public Board(int ySize, int xSize) {
		this.ySize = ySize;
		this.xSize = xSize;
		board = new PlayerType[ySize][xSize];
		for (int i = 0; i < board.length; i++) {
			Arrays.fill(board[i], PlayerType.NONE);
		}
		set(0, 0, PlayerType.DOWN);
		set(ySize - 1, xSize - 1, PlayerType.UP);
	}

	/**
	 * sets a position on the {@link Board} to the specified {@link PlayerType}.
	 * The zobrist hash of this {@link Board} is updated accordingly. Never
	 * directly modify the internal board array as the zobrist hash is getting
	 * incorrect in this case.
	 * 
	 * @param y
	 * @param x
	 * @param type
	 */
	public void set(int y, int x, PlayerType type) {
		if (!PlayerType.NONE.equals(board[y][x]))
			zobristHash = zobristHash ^ Zobrist.code(y, x, board[y][x]);
		board[y][x] = type;
		if (!type.equals(PlayerType.NONE))
			zobristHash = zobristHash ^ Zobrist.code(y, x, type);
	}

	/**
	 * @param forPlayer
	 * @return all possible plies that a player can perform in the current
	 *         position
	 */
	public List<Ply> getPossiblePlies(PlayerType forPlayer) {
		List<Ply> possiblePlies = new ArrayList<Ply>();
		for (int y = 0; y < ySize; y++) {
			for (int x = 0; x < xSize; x++) {
				if (board[y][x] == forPlayer) {
					Position from = new Position(y, x);
					List<Position> legalTargets = getLegalUnoccupiedPositions(
							from.y, from.x, forPlayer);
					for (Position to : legalTargets) {
						possiblePlies.add(new Ply(from, to));
					}
				}
			}
		}
		return possiblePlies;
	}

	/**
	 * @param at
	 * @return
	 * @throws IndexOutOfBoundsException
	 *             if y or x is not in range
	 */
	public final PlayerType getPlayerType(Position at) {
		return getPlayerType(at.y, at.x);
	}

	/**
	 * @param y
	 * @param x
	 * @return
	 * @throws IndexOutOfBoundsException
	 *             if y or x is not in range
	 */
	public final PlayerType getPlayerType(int y, int x) {
		return board[y][x];
	}

	/**
	 * if y or x are out of range, {@link PlayerType} NONE is returned.
	 * 
	 * @param y
	 * @param x
	 * @return
	 */
	public final PlayerType getPlayerTypeSave(int y, int x) {
		if (y >= ySize || y < 0 || x >= xSize || x < 0) {
			return PlayerType.NONE;
		}
		return board[y][x];
	}

	/**
	 * @param forPlayer
	 * @param from
	 * @return all possible plies that a player can perform in the current
	 *         position with a specific piece.
	 */
	public List<Ply> getPossiblePlies(PlayerType forPlayer, Position from) {
		if (!getPlayerType(from).equals(forPlayer)) {
			return new ArrayList<Ply>();
		}
		List<Position> legalTargets = getLegalUnoccupiedPositions(from.y,
				from.x, forPlayer);
		List<Ply> possiblePlies = new ArrayList<Ply>();
		for (Position to : legalTargets) {
			possiblePlies.add(new Ply(from, to));
		}
		return possiblePlies;
	}

	/**
	 * @param forPlayer
	 * @param target
	 * @return all possible plies that a player can perform in the current
	 *         position to a specific target.
	 */
	public List<Ply> getPossiblePliesTo(PlayerType forPlayer, Position target) {
		// legal origins are legal target from opponents view
		List<Position> legalOrigins = lookupLegalPositions(target,
				forPlayer.getOpponent());
		List<Ply> possiblePlies = new ArrayList<Ply>();
		for (Position from : legalOrigins) {
			if (board[from.y][from.x].equals(forPlayer))
				possiblePlies.add(new Ply(from, target));
		}
		return possiblePlies;
	}

	@Override
	public String toString() {
		StringBuilder sb = new StringBuilder();
		sb.append("   ");
		for (int i = 0; i < board.length; i++) {
			sb.append(" " + i + " ");
		}
		sb.append("\n");
		for (int y = 0; y < ySize; y++) {
			sb.append(" " + y + " ");
			for (int x = 0; x < ySize; x++) {
				switch (board[y][x]) {
				case DOWN:
					sb.append("[v]");
					break;
				case NONE:
					sb.append("[ ]");
					break;
				case UP:
					sb.append("[^]");
					break;
				default:
					break;

				}
			}
			sb.append(" " + y + " \n");
		}
		sb.append("   ");
		for (int i = 0; i < board.length; i++) {
			sb.append(" " + i + " ");
		}
		return sb.toString();
	}

	/**
	 * performs the ply on the Board. Does not check for consistency. If it is a
	 * capture ply the captured piece is returned.
	 * 
	 * @param ply
	 * @return
	 */
	public PlayerType perform(Ply ply) {
		PlayerType movingPiece = board[ply.from.y][ply.from.x];
		PlayerType capturedPiece = board[ply.to.y][ply.to.x];
		// set target position
		set(ply.to.y, ply.to.x, movingPiece);
		// set origin to empty
		set(ply.from.y, ply.from.x, PlayerType.NONE);
		return capturedPiece;
	}

	/**
	 * Undoes the ply on the Board. Does not check for consistency. If the ply
	 * to undo was a capture-ply, the piece which had been captured must be
	 * handed over to this method.
	 * 
	 * @param ply
	 * @return
	 */
	public void undo(Ply ply, PlayerType recoverCapturedPiece) {
		// set origin position
		set(ply.from.y, ply.from.x, board[ply.to.y][ply.to.x]);
		// set target to previously captured piece (may also be NONE)
		set(ply.to.y, ply.to.x, recoverCapturedPiece);
	}

	public int getRowCount() {
		return ySize;
	}

	public int getColCount() {
		return xSize;
	}

	public PlayerType whosTheWinner() {
		for (int x = 0; x < xSize; x++) {
			if (board[0][x].equals(PlayerType.UP)) {
				return PlayerType.UP;
			}
			if (board[ySize - 1][x].equals(PlayerType.DOWN)) {
				return PlayerType.DOWN;
			}
		}
		return PlayerType.NONE;
	}

	/**
	 * this is slow. For faster count method use {@link RatedBoardPieceCount}
	 * 
	 * @param player
	 * @return
	 */
	public int getCountFor(PlayerType player) {
		int count = 0;
		for (int y = 0; y < ySize; y++) {
			for (int x = 0; x < xSize; x++) {
				if (board[y][x].equals(player)) {
					count++;
				}
			}
		}
		return count;
	}

	/**
	 * returns the first piece encountered, which can win in one move for the
	 * specified player.
	 * 
	 * @param player
	 * @return
	 */
	public Position getThreateningPiece(PlayerType player) {
		int yStart;
		if (player.equals(PlayerType.UP))
			yStart = 1;
		else
			yStart = 5;

		for (int y = yStart; y < yStart + 2; y++) {
			for (int x = 0; x < board[y].length; x++) {
				if (board[y][x].equals(player)) {
					return new Position(y, x);
				}
			}
		}
		return null;
	}

	/**
	 * returns the first piece encountered of this player, which is located in
	 * the opponent's half.
	 * 
	 * @param player
	 * @return
	 */
	public Position getPieceInOppHalf(PlayerType player) {
		int yStart;
		if (player.equals(PlayerType.UP))
			yStart = 1;
		else
			yStart = 4;

		for (int y = yStart; y < yStart + 3; y++) {
			for (int x = 0; x < board[y].length; x++) {
				if (board[y][x].equals(player)) {
					return new Position(y, x);
				}
			}
		}
		return null;
	}

	/**
	 * @param player
	 * @return all plies for the specified player which capture a piece of the
	 *         opponent.
	 */
	public List<Ply> getCapturePlies(PlayerType player) {
		List<Ply> allPlies = getPossiblePlies(player);
		List<Ply> capturePlies = new LinkedList<Ply>();
		for (Ply ply : allPlies) {
			if (board[ply.to.y][ply.to.x].equals(player.getOpponent())) {
				capturePlies.add(ply);
			}
		}
		return capturePlies;
	}

	/**
	 * @return zobrist hash of the current board position
	 */
	public long getZobrist() {
		return zobristHash;
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;
		Board other = (Board) obj;
		if (!Arrays.deepEquals(board, other.board))
			return false;
		return true;
	}

	/**
	 * @param other
	 * @return all positions which have a different {@link PlayerType} on this
	 *         and the other {@link Board}
	 */
	public Set<Position> findDifferencesTo(Board other) {
		if (other == null) {
			return new HashSet<Position>();
		}
		if (other.ySize != this.ySize || other.xSize != this.xSize) {
			System.out
					.println("Boards cannot be compared, as they have different dimensions");
			return new HashSet<Position>();
		}
		Set<Position> differences = new HashSet<Position>();
		for (int y = 0; y < ySize; y++) {
			for (int x = 0; x < xSize; x++) {
				if (board[y][x] != other.board[y][x]) {
					differences.add(new Position(y, x));
				}
			}
		}
		return differences;
	}

}
