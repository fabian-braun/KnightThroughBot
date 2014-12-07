package transpositiontable;

public class Entry {
	private int value;
	private EntryType type;
	private int depth;

	public Entry(int value, EntryType type, int depth) {
		super();
		this.value = value;
		this.type = type;
		this.depth = depth;
	}

	public int getValue() {
		return value;
	}

	public EntryType getType() {
		return type;
	}

	public int getDepth() {
		return depth;
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + depth;
		result = prime * result + ((type == null) ? 0 : type.hashCode());
		result = prime * result + value;
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
		Entry other = (Entry) obj;
		if (depth != other.depth)
			return false;
		if (type != other.type)
			return false;
		if (value != other.value)
			return false;
		return true;
	}

}
