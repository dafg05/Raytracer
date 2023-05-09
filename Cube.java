import java.util.ArrayList;
import java.lang.Math;

public class Cube extends Traceable {

	/* A Cube is cube centered at the origin, extending from -1 to +1 on
	 * each axis
	 * 
	 * Note:  there is a bug in local_intersect so it sometimes does not work
	 * correctly, but this should not give you a problem.
	 */

	public String toString() {
		return "Cube \n"+this.transform;
	}


	
	@Override
	public ArrayList<Intersection> local_intersect(Ray gray) {
			
		
		var ray = gray.transform(transform.invert());

		double[] rets = 
				check_axis(ray.origin.t[0], ray.direction.t[0]);
		double xtmin = rets[0];
		double xtmax = rets[1];

		rets = check_axis(ray.origin.t[1], ray.direction.t[1]);
		if (rets[0] > xtmin)
			xtmin = rets[0];
		if (rets[1] < xtmax)
			xtmax = rets[1];

		rets = check_axis(ray.origin.t[2], ray.direction.t[2]);
		if (rets[0] > xtmin)
			xtmin = rets[0];
		if (rets[1] < xtmax)
			xtmax = rets[1];

		ArrayList<Intersection> ans = new ArrayList<Intersection>();



		if (xtmin >= xtmax || xtmax == Double.POSITIVE_INFINITY) 
			return ans;

		ans.add(new Intersection(this, xtmin));
		ans.add(new	Intersection(this, xtmax));	

		return ans;
	}



	private double[] check_axis(double origin, double direction) {
		double tmin_numerator = (-1 - origin);
		double tmax_numerator = (1 - origin);
		double tmin;
		double tmax;
		if (Math.abs(direction) >= Aux.EPSILON) {
			tmin = tmin_numerator / direction;
			tmax = tmax_numerator / direction;
		}
		else {
				if (tmin_numerator >= 0)
				tmin =  Double.POSITIVE_INFINITY;
				else if (tmin_numerator <=0)
					tmin = Double.NEGATIVE_INFINITY;
				else tmin = 0;
				
				if (tmax_numerator >= 0)
					tmax =  Double.POSITIVE_INFINITY;
					else if (tmax_numerator <=0)
						tmax = Double.NEGATIVE_INFINITY;
					else tmax = 0;

		}

		if (tmin > tmax) {
			double temp = tmin;
			tmin = tmax;
			tmax = temp;
		}

		return new double[] {tmin, tmax};

	}



	@Override
	public Vector local_normal_at(Point point, Intersection dontUse) {
		int maxIndex = -1;
		double absMaxCoor = -1; // value of coordinate with max absolute value

		for (int i = 0; i < 3; i++){
			double absCoor = Math.abs(point.t[i]);
			if (absCoor >= absMaxCoor){
				absMaxCoor = absCoor;
				maxIndex = i;
			}
		}
		
		if (maxIndex == -1){
			System.out.println("Something went wrong in the calculation of absolute max coordinate");
			return null;
		}

		Vector normal;
		int sign = (point.t[maxIndex] >= 0)? 1 : -1; // sign of the value of the max coor
		
		switch (maxIndex) {
			case 0: 
				normal = new Vector(1 * sign, 0 , 0);
				break;
			case 1:
				normal = new Vector(0, 1 * sign, 0);
				break;
			case 2:
				normal = new Vector(0, 0, 1 * sign);
				break;
			default:
				normal = null;
		}
		
		if (normal == null){
			System.out.println("Something went wrong calculation of normal");
		}

		return normal;
	}

	public static void main(String[] args) {

		System.out.println("Testing local normal at for Cube:");

		Cube cube = new Cube();
		
		Point p1 = new Point(1, 0, 0);
		Vector n1 = cube.local_normal_at(p1, null);
		System.out.println("Point1: " + p1 + ", Normal: " + n1);

		Point p2 = new Point(0.333, 0.999, 0.8);
		Vector n2 = cube.local_normal_at(p2, null);
		System.out.println("Point2: " + p2 + ", Normal: " + n2);

		Point p3 = new Point(0.9, 0.7, 1.0);
		Vector n3 = cube.local_normal_at(p3, null);
		System.out.println("Point3: " + p3 + ", Normal: " + n3);

		Point p4 = new Point(-0.9, 0.7, -0.5);
		Vector n4 = cube.local_normal_at(p4, null);
		System.out.println("Point3: " + p4 + ", Normal: " + n4);

		Point p5 = new Point(-0.6, -0.98, -0.5);
		Vector n5 = cube.local_normal_at(p5, null);
		System.out.println("Point3: " + p5 + ", Normal: " + n5);

		Point p6 = new Point(0.8, 0.7, -0.99);
		Vector n6 = cube.local_normal_at(p6, null);
		System.out.println("Point3: " + p6 + ", Normal: " + n6);

	}



	@Override
	public boolean includes(Traceable object) {

		return this == object;
	}

}
