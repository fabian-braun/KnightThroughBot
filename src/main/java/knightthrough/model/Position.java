package knightthrough.model;

import java.io.Serializable;

/**
 * @author Fabian Braun
 *
 */
public class Position implements Serializable {
	private static final long serialVersionUID = 775450809456043342L;
	public final int y;
	public final int x;
	public static final char[] xIndexRef = new char[] { 'A', 'B', 'C', 'D',
			'E', 'F', 'G', 'H', 'I', 'J', 'K', 'L' };

	public Position(int y, int x) {
		this.y = y;
		this.x = x;
	}

	@Override
	public String toString() {
		return "[" + y + ";" + x + "]";
	}

	public String toCoordinateString() {
		return "[" + (8 - y) + ";" + xIndexRef[x] + "]";
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + x;
		result = prime * result + y;
		return result;
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;
		Position other = (Position) obj;
		if (x != other.x)
			return false;
		if (y != other.y)
			return false;
		return true;
	}

}
