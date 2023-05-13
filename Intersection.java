
public class Intersection implements Comparable<Intersection>{

	protected Traceable object;
	protected double t;
	protected double u; // for barycentric coordinates
	protected double v; // for barycentric coordinates
	protected Point interPoint;

	public Intersection(Traceable object, double t) {
		this.object = object;
		this.t = t;
	}

	public String toString() {
		return ""+object+"  "+t;
	}
	
	public void setInterPoint(Ray r) {
		this.interPoint = r.position(t);
	}

	public Point getInterPoint() {
		return interPoint;
	}

	@Override
	public int compareTo(Intersection o) {
		if (t < o.t)
			return -1;
		else if (t > o.t)
			return 1;
		else return 0;

	}
}
