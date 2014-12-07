package model;

import java.util.List;

import error.GameException;

public class RestrictiveBoard extends Board {

	/**
	 * 
	 */
	private static final long serialVersionUID = 7348372821276241915L;

	public RestrictiveBoard(Board copy) {
		super(copy);
	}

	@Override
	public void set(int y, int x, PlayerType type) {
		// do range validation, which is omitted in super type
		if (y > ySize - 1 || y < 0 || x > xSize - 1 || x < 0) {
			throw new GameException("[" + y + ";" + x + "] out of range ["
					+ ySize + ";" + xSize + "]");
		}
		super.set(y, x, type);
	}

	@Override
	public PlayerType perform(Ply ply) {
		List<Ply> possibles = getPossiblePlies(board[ply.from.y][ply.from.x],
				ply.from);
		if (possibles.contains(ply)) {
			return super.perform(ply);
		} else {
			throw new GameException("Ply " + ply + " is illegal on Board \n"
					+ this);
		}
	}

}
