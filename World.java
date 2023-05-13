import java.util.ArrayList;
import java.io.FileDescriptor;
import java.lang.Math;

import javax.imageio.event.IIOWriteWarningListener;
import javax.imageio.stream.MemoryCacheImageOutputStream;

public class World {

	// The list of Traceable Objects

	ArrayList<Traceable> objects = new ArrayList<Traceable>();
	ArrayList<LightSource> lightSources = new ArrayList<LightSource>();
	double perturbation = 0.001;
	double ambientCoefficient = 0.2;
	MyColor ambientColor = new MyColor(0.2, 0.2, 0.2);

	public World() {
	}

	public void add(Traceable t) {
		objects.add(t);
	}

	public void addLight(LightSource l) {
		lightSources.add(l);
	}

	public void setDefault() {

		Cube s1 = new Cube();
		Material material = new Material();
		material.color = new MyColor(0.8, 1.0, 0.6);
		material.diffuse = 0.7;
		material.specular = 0.2;
		s1.material = material;
		Cube s2 = new Cube();
		System.out.println(s2);
		s2.transform = Transformations.getTranslate(0, -8, -4);

		System.out.println(s2);

		objects.add(s1);
		objects.add(s2);

	}

	public Canvas render(String fileName, int hsize, int vsize, double size) {
		Canvas cav = new Canvas(hsize, vsize);
		cav.maxColorValue = 0;
		Point center = new Point(0, 0, 2);
		double sqrtRPP = 1; // sqrt(rays per pixel)
		for (int i = 0; i < hsize; i++) {
			for (int j = 0; j < vsize; j++) {

				// colors that we're averaging
				ArrayList<MyColor> colors = new ArrayList<MyColor>();
				for (int n = 0; n < sqrtRPP; n++) {
					for (int m = 0; m < sqrtRPP; m++) {
						double xDD = (((i + (n / sqrtRPP)) - (hsize / 2)) * size) / hsize;
						double yDD = -(((j + (m / sqrtRPP)) - (vsize / 2)) * size) / vsize;
						double zDD = -1 - center.t[2];
						Vector direction = new Vector(xDD, yDD, zDD);

						Ray r = new Ray(center, direction);
						colors.add(computeColor(r, 0));
					}
				}
				MyColor avgColor = averageColor(colors);

				// keep track of the larest color value. store it in cav.maxColorValue

				for (int k = 0; k < 3; k++) {
					if (avgColor.t[k] > cav.maxColorValue) {
						cav.maxColorValue = avgColor.t[k];
					}
				}

				cav.writeP(i, j, avgColor);
			}
		}
		cav.toPPM(fileName);
		System.out.println("Tracer world done");

		return cav;
	}

	private MyColor computeColor(Ray r, int depth) {
		Intersection inters = Traceable.hit(intersectWorld(r)); // nearest intersection
		MyColor finalC = MyColor.Black;
		if (inters != null) {
			inters.setInterPoint(r);
			for (LightSource ls : lightSources) {
				// calculate light intensity
				MyColor lightIntensity = ls.intensityAt(inters.interPoint, inters, this);
				finalC = new MyColor(Tuple.add(finalC, lightIntensity));
			}
			finalC = new MyColor(Tuple.add(finalC, this.ambientColor));
			// check if intersection object is reflective. If it is, recursively call
			// newComputeColor to get reflective color and combine that with regular color
			if (inters.object.material.reflective > 0 && depth < 3) {
				double reflectionFactor = inters.object.material.reflective;
				Ray reflectedRay = reflectRay(r, inters, inters.interPoint);
				MyColor reflectColor = this.computeColor(reflectedRay, depth + 1);
				finalC = new MyColor(Tuple.add(reflectColor.scale(reflectionFactor), finalC.scale(1 - reflectionFactor)));
			}
		}
		return finalC;
	}

	private Ray reflectRay(Ray r, Intersection inters, Point intersectionPoint) {
		// R = U - 2N (U dot N)
		Vector unitNormal = inters.object.normalAt(intersectionPoint, inters).normalize();
		double reflectionFactor = -2 * Tuple.dot(r.direction, unitNormal);
		Vector reflectionDir = new Vector(Tuple.add(r.direction, unitNormal.scale(reflectionFactor)));
		reflectionDir = reflectionDir.normalize();

		// perturb the intersection point in the direction of reflection vector to avoid self-intersection
		Point perturbedPoint = new Point(Tuple.add(intersectionPoint, reflectionDir.scale(perturbation)));
		return new Ray(perturbedPoint, reflectionDir);
	}

	private static MyColor averageColor(ArrayList<MyColor> colors) {
		double r = 0;
		double g = 0;
		double b = 0;
		for (MyColor c : colors) {
			r += c.t[0];
			g += c.t[1];
			b += c.t[2];
		}
		r = r / colors.size();
		g = g / colors.size();
		b = b / colors.size();
		return new MyColor(r, g, b);
	}

	public ArrayList<Intersection> intersectWorld(Ray r) {

		ArrayList<Intersection> result = new ArrayList<Intersection>();

		for (Traceable o : objects) {

			ArrayList<Intersection> inters = o.intersections(r);
			result = Traceable.mergeInters(inters, result);

		}

		return result;
	}

	public static void main(String[] args) {
		World w = new World();
		// w.mirrors();
		w.teapot();
		w.render("test102.ppm", 800, 800, 15);
	}

	public void mySetup() {
		PointLight plight = new PointLight(new MyColor(1.0, 1.0, 1.0), new Point(-1.0, -1.0, 2.0));
		addLight(plight);

		RectangularLight rlight = new RectangularLight(new MyColor(1.0, 1.0, 1.0), new Point(1.0, 1.0, 2.0),
				new Vector(-0.25, 0.0, 0), new Vector(0, -0.25, 0), 6, 6);
		addLight(rlight);

		Cube back = new Cube();
		back.transform = Matrices.mult(
				Transformations.getTranslate(0.0, 0.0, -2.0),
				Transformations.getScale(2.5, 2.5, 0.25));
		back.material = new Material();
		back.material.color = new MyColor(1, 0, 1);
		back.material.diffuse = 0.7;
		back.material.specular = 0.3;
		back.material.ambient = 0.9;

		add(back);

		Sphere smallBall = new Sphere();
		smallBall.transform = Matrices.mult(
				Transformations.getTranslate(0.0, 0.0, 0.0),
				Transformations.getScale(0.25, 0.25, 0.1));
		smallBall.material = new Material();
		smallBall.material.color = new MyColor(0, 1.0, 0.5);
		smallBall.material.diffuse = 0.7;
		smallBall.material.specular = 0.3;
		smallBall.material.ambient = 0.9;

		add(smallBall);

		Sphere ball = new Sphere();
		ball.transform = Matrices.mult(
				Transformations.getTranslate(2.0, 1.0, -1.0),
				Transformations.getScale(1, 1, 1));
		ball.material = new Material();
		ball.material.color = new MyColor(0, 1, 1);
		ball.material.diffuse = 0.7;
		ball.material.specular = 0.4;
		ball.material.ambient = 0.6;

		add(ball);
	}

	public void teapot() {
		OBJParser op = new OBJParser("teapot2.obj");
		ArrayList<Traceable> teaPot = op.readFile();
		for (Traceable tr : teaPot){
			add(tr);
		}
		PointLight plight = new PointLight(new MyColor(1.0, 1.0, 1.0), new Point(0.0, 0.0, 3.0));
		addLight(plight);

	}

	public void mirrors() {
		// PointLight light = new PointLight(new MyColor(1.0, 1.0, 1.0), new Point(0.0, 0.0, 2.0));
		// addLight(light);

		RectangularLight rlight = new RectangularLight(new MyColor(1.0, 1.0, 1.0), new Point(-0.5, -0.5, 2.0),
				new Vector(1.0, 0.0, 0), new Vector(0, 1.0, 0), 4, 4);
		addLight(rlight);

		Cube mirror1 = new Cube();
		mirror1.transform = Matrices.mult(
			Transformations.getTranslate(0.0, 0.0, -1.0),
			Transformations.getRotY(Math.PI/4),
			Transformations.getScale(1.0, 1.0, 1.0)
			);
		mirror1.material = new Material();
		mirror1.material.color = new MyColor(1.0, 1.0, 1.0);
		mirror1.material.diffuse = 0.7;
		mirror1.material.specular = 0.0;
		mirror1.material.ambient = 0.9;
		mirror1.material.reflective = 0.4;
		add(mirror1);

		Cube mirror2 = new Cube();
		mirror2.transform = Matrices.mult(
			Transformations.getTranslate(4.0, 0.0, -1.0),
			Transformations.getRotY(-Math.PI/4),
			Transformations.getScale(1.0, 1.0, 1.0)
			);
		mirror2.material = new Material();
		mirror2.material.color = new MyColor(1.0, 1.0, 1.0);
		mirror2.material.diffuse = 0.7;
		mirror2.material.specular = 0.0;
		mirror2.material.ambient = 0.9;
		mirror2.material.reflective = 0.4;
		add(mirror2);


		Sphere ball = new Sphere();
		ball.transform = Matrices.mult(
			Transformations.getTranslate(3.0, 0.0, 4.0),
			Transformations.getScale(0.5, 0.5, 0.5)
			);
		ball.material = new Material();
		ball.material.color = new MyColor(0, 1, 1);
		ball.material.diffuse = 0.7;
		ball.material.specular = 0.4;
		ball.material.ambient = 0.6;

		add(ball);
	
	}

	public void testReflection(){
		Ray og = new Ray(new Point(0,0,2), new Vector(0, 0, -1));
		this.computeColor(og, 0);
	}
}