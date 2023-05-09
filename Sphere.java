import java.util.ArrayList;
import java.lang.Math;


public class Sphere extends Traceable {
	
	protected double radius = 1;
	
	public Sphere() {
		radius = 1;
	}
	
	public ArrayList<Intersection> local_intersect(Ray r1) {
		/* transform the ray by inverse of the Traceable transform before 
		 * intersecting See the beginning of the method in Cube for help
		 * */
		
		// transformed r1
		Ray tR1 = r1.transform(this.transform.invert());
		
		/* Calculate a, b and c as in class.  Be careful about using the ray origin, 
		 * which is a point - don't accidentally get an extra 1 from the w coordinate
		 * 
		 */
		
		// Vector originV = new Vector(tR1.origin.t[0], tR1.origin.t[1], tR1.origin.t[2]);
		Vector originV = new Vector(tR1.origin);
		// System.out.println(originV);

		double a = Tuple.dot(tR1.direction, tR1.direction);
		double b = 2 * Tuple.dot(originV, tR1.direction);
		double c = Tuple.dot(originV, originV) - 1;
		
		double discriminant = b*b -4*a*c;

		
		ArrayList<Intersection> ans = new ArrayList<Intersection>();

		if (discriminant == 0){
			double t = -b/(2*a);
			ans.add(new Intersection(this, t));
		}
		else if (discriminant > 0){ 
			double t1 = (-b + Math.sqrt(discriminant)) / (2 * a);
			double t2 = (-b - Math.sqrt(discriminant)) / (2 * a);
			ans.add(new Intersection(this, t1));
			ans.add(new Intersection(this, t2));
		}
		
		return ans;
	}
	
	@Override
	public Vector local_normal_at(Point p, Intersection dontUse) {
		
		// Assumes point is in sphere, in object coordinates.

		Vector normal = new Vector(p);
		normal = normal.normalize();
		return normal;
	}
	
	public static void main(String[] args) {

		ArrayList<Ray> rays = new ArrayList<Ray>();
		rays.add(new Ray(new Point(0,0,-5),new Vector(0,0,1)));
		rays.add(new Ray(new Point(0,-5,0),new Vector(0,1,0)));
		rays.add(new Ray(new Point(-5, 0 , 0), new Vector(1, 0, 0)));

		for (Ray r : rays){

			System.out.println("////////////|||||||||||||");
			
			System.out.println("Ray: " + r);
			Traceable s = new Sphere();
			System.out.println("Sphere transformation: ");
			System.out.println(s.transform);

			show(r,s);
					
			s.transform = Transformations.getScale(2, 2, 2);
			System.out.println("Sphere transformation: ");
			System.out.println(s.transform);;

			show(r,s);
			
			s.transform = Transformations.getTranslate(5, 5, 5);
			System.out.println("Sphere transformation: ");
			System.out.println(s.transform);

			show(r,s);

			s.transform = Transformations.getTranslate(2, 2, 2);
			s.transform = Matrices.mult(s.transform, Transformations.getScale(4, 4, 4));
			System.out.println("Sphere transformation: ");
			System.out.println(s.transform);
			show(r,s);

			System.out.println("////////////|||||||||||||");
		}	
	}

	private static void show(Ray r, Traceable s) {		
		ArrayList<Intersection> ans = s.intersections(r);
		if (ans.size() == 0)
			System.out.println("No Intersections");
		else 
			for (Intersection inters : ans) {
				System.out.println("t: " + inters.t + "; intersection point: " + r.position(inters.t));
			}
		System.out.println("___________");
	}

	@Override
	public boolean includes(Traceable object) {
		return false;
	}
	
	
	
	
	
	
	
}
