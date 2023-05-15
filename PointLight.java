import java.util.ArrayList;

public class PointLight extends LightSource{

	public String toString() {
		return "Point light at "+this.position+" with intensity "+this.intensity;
	}
	
	@Override
	public MyColor intensityAt(Point point, Intersection inters, World w) {
		MyColor actualIntensity = MyColor.Black;
		
		Vector unitNormal = inters.object.normalAt(point, inters).normalize();
        Vector scaledNormal = unitNormal.scale(w.perturbation);
		// use a perturbed point for shadow calculations
		Point perturbedPoint = new Point(Tuple.add(point, scaledNormal));
		// // skip all lighting calculations if the point is shadowed
		// if (!LightSource.isShadowed(perturbedPoint, this.position, w)) {
		// 	// calculate color at samplePoint using lighting effects
		// 	actualIntensity = this.lighting(this.position, point, unitNormal, inters, this.intensity);
		// }

		MyColor shadowIntensity = this.shadowIntensity(perturbedPoint, this.position, w);
		if (shadowIntensity.equals(MyColor.Black)){ // skip lighting calculations if point is shadowed.
			return MyColor.Black;
		}
		actualIntensity = this.lighting(this.position, point, unitNormal, inters, shadowIntensity);

		return actualIntensity;
	}

	
	public MyColor getIntensity() {
		return intensity;
	}

	public void setIntensity(MyColor intensity) {
		this.intensity = intensity;
	}



	public Point getPosition() {
		return position;
	}



	public void setPosition(Point position) {
		this.position = position;
	}



	public PointLight(MyColor intensity, Point position) {
		super();
		this.intensity = intensity;
		this.position = position;
	}
	
	
	public PointLight(double r, double g, double b, double x, double y, double z) {
		super();
		this.intensity = new MyColor(r,g,b);
		this.position = new Point(x,y,z);
	}

	public static void main(String[] args) {

	}

}
