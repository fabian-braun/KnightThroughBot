package model;

public class Ply implements Comparable<Ply> {

	public final Position from;
	public final Position to;
	private int evaluationValue = 0;

	public Ply(Position from, Position to) {
		this.from = from;
		this.to = to;
	}

	public void setEvaluationValue(int evaluationValue) {
		this.evaluationValue = evaluationValue;
	}

	public int getEvaluationValue() {
		return evaluationValue;
	}

	public Position getOrigin() {
		return from;
	}

	public Position getTarget() {
		return to;
	}

	@Override
	public String toString() {
		return from.toCoordinateString() + " --> " + to.toCoordinateString();
	}

	@Override
	public boolean equals(Object obj) {
		return obj != null && obj instanceof Ply
				&& ((Ply) obj).from.equals(from) && ((Ply) obj).to.equals(to);
	}

	@Override
	public int compareTo(Ply o) {
		return Integer.compare(o.evaluationValue, evaluationValue);
	}
}
