import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;


public abstract class Traceable {

	protected Matrices transform = Matrices.identity();
	protected Material material = new Material();

	public ArrayList<Intersection> intersections(Ray r) { 
		return this.local_intersect(r);
	};

	/* You need to write this - this should return 
	null if result is empty
	the Intersection with the smallest POSITIVE t value
	 */ 

	public static Intersection hit(ArrayList<Intersection> result) {
		double min = Double.POSITIVE_INFINITY;
		Intersection argmin = null;
	    for (Intersection inter: result){
			if (inter.t < min && inter.t > 0){
				min = inter.t;
				argmin = inter;
			}
		}
		return argmin;

	}


	//merges two Intersection Lists  
	public static ArrayList<Intersection> mergeInters(ArrayList<Intersection> rightxs,
			ArrayList<Intersection> leftxs) {
		ArrayList<Intersection> result;
		result = new ArrayList<Intersection>();
		result.addAll(rightxs);
		result.addAll(leftxs);

		return result;
	}

	public Vector normal_to_world(Vector normal) {	
		Matrices InverseTranspose = this.transform.invert().transpose();
		Vector worldNormal = new Vector(Matrices.apply(InverseTranspose, normal));
		return worldNormal;
	}

	public Point world_to_object(Point p) {	
		return new Point(Matrices.apply(this.transform.invert(), p));
	}

	public abstract ArrayList<Intersection> local_intersect(Ray r);




	public static void main(String[] args) {

		System.out.println("Testing normal to world");
		Traceable s = new Sphere();
		s.transform = Matrices.mult(Transformations.getScale(2, 2, 2), Transformations.getTranslate(5, 5, 5), Transformations.getRotZ(Math.PI));
		Vector n = s.local_normal_at(new Point(1,0,0), null);
		Vector result = s.normal_to_world(n);
		System.out.println("Result world normal: " + result);

		System.out.println("Testing world to object");
		s = new Sphere();
		s.transform = Matrices.mult(Transformations.getTranslate(0, 2, 0), Transformations.getScale(2, 2, 2));
		Point p = s.world_to_object(new Point(0, 4, 0));
		System.out.println("Result object coordinates: " + p);

		System.out.println("Testing normal at");
		s = new Sphere();
		s.transform = Matrices.mult(Transformations.getRotZ(Math.PI), Transformations.getTranslate(0, 2, 0), Transformations.getScale(2, 2, 2));
		Vector normal = s.normalAt(new Point(0, 4, 0), null);
		System.out.println("Result normal at: " + normal);
	}

	public final Vector normalAt(Point worldP, Intersection i) {
		Point localP = world_to_object(worldP);
		Vector localNormal = local_normal_at(localP, i);
		return normal_to_world(localNormal);
	}


	public abstract Vector local_normal_at(Point p, Intersection i);

	public abstract boolean includes(Traceable object);


}
