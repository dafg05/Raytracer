import java.util.ArrayList;
import java.io.FileDescriptor;
import java.lang.Math;

import javax.imageio.event.IIOWriteWarningListener;
import javax.imageio.stream.MemoryCacheImageOutputStream;

public class World {

	ArrayList<Traceable> objects = new ArrayList<Traceable>();
	ArrayList<LightSource> lightSources = new ArrayList<LightSource>();
	double perturbation = 0.001;
	double transparentOffset = 0.001;
	// MyColor ambientColor = new MyColor(0.2, 0.2, 0.2);
	double ambientFactor = 0.3;

	MyColor skyBlue = new MyColor(0.353, 0.808, 1.0);
	MyColor coral = new MyColor(0.918, 0.529, 0.365);
	MyColor olive = new MyColor(0.686, 0.780, 0.580);
	MyColor violet = new MyColor(0.233, 0.118, 0.912);
	MyColor warmWhite = new MyColor(0.98, 0.702, 0.747);

	public World() {
	}

	public void add(Traceable t) {
		objects.add(t);
	}

	public void addLight(LightSource l) {
		lightSources.add(l);
	}

	public Canvas render(String fileName, int hsize, int vsize, double size) {
		Canvas cav = new Canvas(hsize, vsize);
		cav.maxColorValue = 0;
		Point center = new Point(0, 0, 2);
		double sqrtRPP = 1; // sqrt(rays per pixel)
		for (int i = 0; i < hsize; i++) {
			for (int j = 0; j < vsize; j++) {

				// for each pixel, shoot sqrtRPP^2 rays and average the color
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

	private MyColor computeColor(Ray r, int reflectDepth) {
		/*
		 * Recursive method that calculates the color of the object that the ray hits.
		 */
		Intersection inters = Traceable.hit(intersectWorld(r)); // nearest intersection
		MyColor finalC = MyColor.Black;

		if (inters != null) {
			inters.setInterPoint(r);
			// Calculate the color of the object in relation to the light sources
			MyColor lightColor = MyColor.Black;
			for (LightSource ls : lightSources) {
				// calculate light intensity
				MyColor lightIntensity = ls.intensityAt(inters.interPoint, inters, this);
				// finalC = new MyColor(Tuple.add(finalC, lightIntensity));
				lightColor = new MyColor(Tuple.add(lightColor, lightIntensity));
			}
			MyColor ambColor = inters.object.material.getColor(0).scale(ambientFactor);

			lightColor = new MyColor(Tuple.add(lightColor, ambColor));

			// Calculate reflective color
			// check if intersection object is reflective. If it is, recursively call computeColor with a reflected ray
			MyColor reflectColor = MyColor.Black;
			double reflectFactor = 0;
			if (inters.object.material.reflective > 0 && reflectDepth < 3) {
				reflectFactor = inters.object.material.reflective;
				Ray reflectedRay = reflectRay(r, inters, inters.interPoint);
				reflectColor = this.computeColor(reflectedRay, reflectDepth + 1);
				// finalC = new MyColor(Tuple.add(reflectColor.scale(reflectionFactor), finalC.scale(1 - reflectionFactor)));
			}

			// Calculate transparent color
			// check if intersection object is transparent. If it is, recursively call computeColor with a ray through the object
			MyColor transpColor = MyColor.Black; // transparency color
			double transpFactor = 0;
			if (inters.object.material.transparency > 0){
				transpFactor = inters.object.material.transparency;
				// to avoid intersecting the same point, perturb the intersection point in the direction of the ray
				Point perturbedPoint = new Point(Tuple.add(inters.interPoint, r.direction.scale(transparentOffset)));
				Ray transpRay = new Ray(perturbedPoint, r.direction);
				transpColor = this.computeColor(transpRay, reflectDepth);
			}

			// Final Color = (1-r)(1-t)L + rR + tT
			// where r is reflectionFactor, t is transpFactor, L is lightColor, R is reflectColor, T is transpColor
			MyColor rR = reflectColor.scale(reflectFactor);
			MyColor tT = transpColor.scale(transpFactor);
			MyColor termC = lightColor.scale((1-reflectFactor)*(1-transpFactor));
			finalC = new MyColor(Tuple.add(termC, 
									Tuple.add(rR, tT)));
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
		/*
		 * Takes an arraylist of colors and returns the average color. Used for distributed ray tracing
		 */
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
		/* 
		 * MAIN METHOD!!!
		 */
		World w = new World();
		w.teapot();
		w.render("test001.ppm", 500,500, 10);
	}


/*
 * WORLD SETUPS!
 * 
 * 
 * 
 * 
 * WORLD SETUPS!
 */



	public void theWall() {
		// PointLight plight = new PointLight(new MyColor(1.0, 1.0, 1.0), new Point(-2.5, 5.0, 3.5));
		// addLight(plight);

		RectangularLight rlight = new RectangularLight(new MyColor(1.0, 1.0, 1.0), new Point(-2.5, 5.0, 3.5),
				new Vector(-0.4, 0.0, 0), new Vector(0, 0.4, 0), 4, 4);
		addLight(rlight);

		Cube mirror = new Cube();
		mirror.transform = Matrices.mult(
			Transformations.getTranslate(-3.4, 0.0, -2.5),
			Transformations.getScale(3.4, 6.6, 0.025));
		mirror.material.color = skyBlue;
		mirror.material.reflective = 0.5;

		add(mirror);

		Cube window = new Cube();
		window.transform = Matrices.mult(
			Transformations.getTranslate(3.4, 0.0, -2.5),
			Transformations.getScale(3.4, 6.6, 0.025));
		window.material.color = warmWhite;
		window.material.transparency = 0.8;

		add(window);

		Cube floor = new Cube();
		floor.transform = Matrices.mult(
			Transformations.getTranslate(0.0, -2.0, 0.0),
			Transformations.getScale(100.0, 0.1, 100.0));
		floor.material.color = coral;

		add(floor);

		// Ball in mirror
		Sphere ball = new Sphere();
		ball.transform = Matrices.mult(
				Transformations.getTranslate(-1.0, 1.0, 4.5),
				Transformations.getScale(2, 2, 2));
		ball.material = new Material();
		ball.material.color = olive;
		ball.material.diffuse = 0.7;
		ball.material.ambient = 0.6;

		add(ball);

		// TEAPOT
		OBJParser op = new OBJParser("teapot2.obj");
		ArrayList<Traceable> teapotTraceables = op.readFile();
		Matrices teapotTransf = Matrices.mult(
			Transformations.getTranslate(1.3,1.5,-5.0),
			Transformations.getRotY(Math.PI/6),
			Transformations.getRotX(-Math.PI/3),
			Transformations.getScale(2.5, 2.5 , 2.5 )
		);
		Material teapotMat = new Material();
		teapotMat.color = new MyColor(0.643, 0.729, 0.255);
		teapotMat.diffuse = 0.7;

		Group teapot = new Group(teapotTraceables, new Sphere(), teapotTransf, teapotMat); // use a unit sphere as bounding object
		add(teapot);
	}

	public void manyLights() {
		/*
		* Used for testing mulitple lightsources.
		*/
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
		/*
		* Show cases JER's teapot
		*/

		PointLight plight = new PointLight(new MyColor(1.0, 1.0, 1.0), new Point(2.0, 0.0, 2.0));
		addLight(plight);

		OBJParser op = new OBJParser("teapot2.obj");
		ArrayList<Traceable> teaPotTraceables = op.readFile();
		Matrices teapotTransf = Matrices.mult(
			Transformations.getTranslate(0,2.0,0.0),
			Transformations.getRotX(-Math.PI/6),
			Transformations.getRotY(Math.PI/12)
		);
		Material teapotMat = new Material();
		teapotMat.color = new MyColor(0.643, 0.729, 0.255);
		teapotMat.diffuse = 0.7;

		Group teapot = new Group(teaPotTraceables, new Sphere(), teapotTransf, teapotMat); // use a unit sphere as bounding object
		add(teapot);

		// A back wall to test shadows and reflections
		Cube back = new Cube();
		back.transform = Matrices.mult(
				Transformations.getTranslate(0.0, 2.0, -1.0),
				Transformations.getRotX(-Math.PI/12),
				Transformations.getScale(4.0, 4.0, 0.02));
		back.material = new Material();
		back.material.color = new MyColor(0.43, 0.51, 0.91);
		back.material.diffuse = 0.7;
		back.material.reflective = 0.6;
		add(back);
	}

	public void windows() {
		/*
		 * Test multiple transparent objects and groups without a specified material
		 */

		PointLight plight = new PointLight(new MyColor(1.0, 1.0, 1.0), new Point(-1.0, 3.0, 2.0));
		addLight(plight);

		Cube window1 = new Cube();
		window1.transform = Matrices.mult(
				Transformations.getScale(1.0, 1.0, 0.1));
		window1.material = new Material();
		window1.material.color = new MyColor(0.23, 0.11, 0.91);
		window1.material.diffuse = 0.7;
		window1.material.transparency = 0.8;

		Cube window2 = new Cube();
		window2.transform = Matrices.mult(
				Transformations.getTranslate(0.0, 0.0, -3.0),
				Transformations.getScale(1.0, 1.0, 0.1));
		window2.material = new Material();
		window2.material.color = new MyColor(0.91, 0.11, 0.23);
		window2.material.diffuse = 0.7;
		window2.material.transparency = 0.8;

		Sphere ball = new Sphere();
		ball.transform = Matrices.mult(
				Transformations.getTranslate(0.0, 0.0, -6.0),
				Transformations.getScale(0.75, 0.75, 0.75));
		ball.material = new Material();
		ball.material.color = new MyColor(0, 1, 1);

		ArrayList<Traceable> objects = new ArrayList<Traceable>();
		objects.add(window1); objects.add(window2); objects.add(ball);

		Group g = new Group(objects);
		g.setTransform(Matrices.mult(
			Transformations.getTranslate(0.0, 0.0, -1.0),
			Transformations.getRotY(-Math.PI/12)
		));
		add(g);
	}

	public void mirrors() {
		/*
		* Showcases reflections. There should be a recursive computeColor() call of depth 2
		*/

		PointLight light = new PointLight(new MyColor(1.0, 1.0, 1.0), new Point(3.0, 0.0, 2.0));
		addLight(light);

		// Our first mirror
		Cube mirror1 = new Cube();
		mirror1.transform = Matrices.mult(
			Transformations.getTranslate(0.0, 0.0, -2.0),
			Transformations.getRotY(Math.PI/4)
			);
		mirror1.material = new Material();
		mirror1.material.color = new MyColor(1.0, 1.0, 1.0);
		mirror1.material.diffuse = 0.7;
		mirror1.material.specular = 0.0;
		mirror1.material.ambient = 0.9;
		mirror1.material.reflective = 0.8;
		add(mirror1);

		// Our second mirror
		Cube mirror2 = new Cube();
		mirror2.transform = Matrices.mult(
			Transformations.getTranslate(4.0, 0.0, -2.0),
			Transformations.getRotY(-Math.PI/4)
			);
		mirror2.material = new Material();
		mirror2.material.color = new MyColor(1.0, 1.0, 1.0);
		mirror2.material.diffuse = 0.7;
		mirror2.material.specular = 0.0;
		mirror2.material.ambient = 0.9;
		mirror2.material.reflective = 0.8;
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
}