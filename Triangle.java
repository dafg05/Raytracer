import java.util.ArrayList;

public class Triangle extends Traceable {

	// YOU NEED TO ADD U + V to Intersection
	
	Point a,b,c;
	Vector edge1, edge2, normal;
	private double zero_bound = 0.001;
	
	public Triangle(Point x, Point y, Point z) {
		super();
		this.a = x;
		this.b = y;
		this.c = z;
		
		edge1 = new Vector(Tuple.sub(y, x));
		edge2 = new Vector(Tuple.sub(z, x));

		normal = new Vector(Tuple.cross(edge1, edge2));

		
		
	}

	@Override
	public ArrayList<Intersection> local_intersect(Ray r) {
		ArrayList<Intersection> answer = 
				new ArrayList<Intersection>();
		r = r.transform(transform.invert());
		Vector cross = new Vector(Tuple.cross(r.direction,edge2));
		
		double det = Tuple.dot(cross, edge1);
		
		if (Math.abs(det) < zero_bound )
			return answer;
		
		double [][] lhsV = {
				{ a.t[0] - b.t[0], a.t[0] - c.t[0], r.direction.t[0]  },
				{ a.t[1] - b.t[1], a.t[1] - c.t[1], r.direction.t[1]},
				{ a.t[2] - b.t[2], a.t[2] - c.t[2], r.direction.t[2]}
		
		};
		
		double [] rhsV = {a.t[0] - r.origin.t[0],
				a.t[1] - r.origin.t[1],
				a.t[2] - r.origin.t[2]};
		
		Jama.Matrix lhs = new Jama.Matrix(lhsV);
		Jama.Matrix rhs = new Jama.Matrix(rhsV,3);
		Jama.Matrix answer2 = lhs.solve(rhs);
		
		double alpha = answer2.get(0,0);
		double beta  = answer2.get(1,0);
		double t  = answer2.get(1,0);
		
		if (alpha < 0 || alpha > 1)
			return answer;
		if (beta < 0 || beta > (1-alpha))
			return answer;
		
		answer.add( new Intersection(this,answer2.get(2, 0)));
		answer.get(0).u = answer2.get(0, 0);
		answer.get(0).v = answer2.get(1, 0);
		
		
		return answer;
	}
	@Override
	public Vector local_normal_at(Point p, Intersection dontUse) {
		return normal;
	}
	
	@Override
	public boolean includes(Traceable object) {
		
		return this == object;
	}
}
