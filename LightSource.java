import java.util.ArrayList;

public abstract class LightSource {
	
	protected MyColor intensity;
	protected Point position;
		
	public abstract MyColor intensityAt(
			Point point, 
			Intersection inters,
			World w);

	public static boolean isShadowed(Point worldPoint,  Point lightPoint ,World w) {
		// fire a ray from point P to light source at lightPoint. If there's an intersection, our point is shadowed.

		// from point to light source
		Vector direction = new Vector(Tuple.sub(lightPoint, worldPoint));
		
		Ray r = new Ray(worldPoint, direction);
		// ArrayList<Intersection> intersections = w.intersectWorld(r);
		Intersection inter = Traceable.hit(w.intersectWorld(r));
		
		if (inter != null){
			return true;
		}

		return false;
	}

public MyColor lighting(Point lightPoint, Point intersectionPoint, Vector unitNormal, Intersection inters){
	// TODO: test function with dummy normal and intersection

	MyColor lightColor = MyColor.Black;
	MyColor objectColor = inters.object.material.getColor(0.0);
	Vector lightDirection = new Vector(Tuple.sub(lightPoint, intersectionPoint)).normalize();
	double diffuseFactor = Tuple.dot(unitNormal, lightDirection) * inters.object.material.diffuse;
	if (diffuseFactor > 0){
		lightColor = new MyColor(Tuple.mult(objectColor.scale(diffuseFactor), this.intensity));
	}
	return lightColor;
}




	
}