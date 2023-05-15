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
		// inter.setInterPoint(r);
		
		if (inter != null && inter.t < 1){
			return true;
		}

		return false;
	}

	public MyColor shadowIntensity(Point worldPoint, Point lightPoint, World w){
		// fire a ray from point P to light source at lightPoint.
		Vector direction = new Vector(Tuple.sub(lightPoint, worldPoint));
		Ray r = new Ray(worldPoint, direction);

		ArrayList<Intersection> fwdIntersecs = Traceable.positveIntersections(w.intersectWorld(r));
		if (fwdIntersecs.size() == 0){
			return this.intensity;
		}
		
		// if there are any positive intersections, check if the intersection objects are transparent.
		// Scale the intensity by the transparency of the object.
		MyColor finalIntensity = this.intensity;
		for (Intersection i : fwdIntersecs){
			if (i.t < 1){ // if intersection object is between point and light source
				if (i.object.material.transparency <= 0){ // if object is opaque, point is completely shadowed.
					return MyColor.Black;
				}
				finalIntensity = finalIntensity.scale(i.object.material.transparency);
			}
			// if (i.object.material.transparency == 0 && i.t < 1){ // object is opaque and between the point and the light source
			// 	return MyColor.Black;
			// }
			// // if (i.t < 1){ // object is transparent and between the point and the light source
			// 	finalIntensity = finalIntensity.scale(i.object.material.transparency);
			// // }
		}
		return finalIntensity;
	}

public MyColor lighting(Point lightPoint, Point intersectionPoint, Vector unitNormal, Intersection inters, MyColor lightIntensity){
	MyColor lightColor = MyColor.Black;
	MyColor objectColor = inters.object.material.getColor(0.0);
	Vector lightDirection = new Vector(Tuple.sub(lightPoint, intersectionPoint)).normalize();
	double diffuseFactor = Tuple.dot(unitNormal, lightDirection) * inters.object.material.diffuse;
	if (diffuseFactor > 0){
		lightColor = new MyColor(Tuple.mult(objectColor.scale(diffuseFactor), lightIntensity));
	}
	return lightColor;
}




	
}