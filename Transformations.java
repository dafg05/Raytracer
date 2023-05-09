import java.util.Random;

public class Transformations {

	/* In order to cement your understanding of Transformations, 
	 * fill in the methods with missing insides - the ones that currently
	 * return null
	 */
	
	public static Matrices getTranslate(double x, double y, double z) {
		return new Matrices(
				new double[][]{
					{1,0,0,x},
					{0, 1,0,y},
					{0,0,1,z},
					{0,0,0,1}});
	}
	
	public static Matrices getScale(double sx, double sy, double sz) {
		return new Matrices(
				new double[][]{
					{sx,0,0,0},
					{0,sy,0,0},
					{0,0,sz,0},
					{0,0,0,1}});
	}
	
	public static Matrices getRotZ(double x) {
		return new Matrices(
				new double[][]{
					{Math.cos(x),-Math.sin(x),0,0},
					{Math.sin(x), Math.cos(x),0,0},
					{0,0,1,0},
					{0,0,0,1}});
	}
	
	public static Matrices getRotY(double x) {
		return new Matrices(
				new double[][]{
					{Math.cos(x),0,Math.sin(x),0},
					{0, 1,0,0},
					{-Math.sin(x),0,Math.cos(x),0},
					{0,0,0,1}});
	}
	
	public static Matrices getRotX(double x) {
		return new Matrices(
				new double[][]{
					{1, 0, 0, 0},
					{0, Math.cos(x),-Math.sin(x),0},
					{0, Math.sin(x), Math.cos(x),0},
					{0, 0, 0, 1}
				});
	}
	
	public static void main(String[] args) {
		Random r = new Random();
		System.out.println("My tests");
		
		scaleTest(r);
		noCommuteTest(r);
		customTest(r);
	}

	private static void scaleTest(Random r){
		System.out.println("////////////////////////");
		System.out.println("Testing commutativity of scaling matrices");
		
		Matrices ms1 = Transformations.getScale(randInt(1, 11, r), randInt(1, 11, r), randInt(1, 11, r));
		Matrices ms2 = Transformations.getScale(randInt(1, 11, r), randInt(1, 11, r), randInt(1, 11, r));

		System.out.println("ms1 (scaling matrix 1):");
		System.out.println(ms1.toString());
		System.out.println("ms2 (scaling matrix 2):");
		System.out.println(ms2.toString());
		
		Matrices sproduct1 = Matrices.mult(ms1, ms2);
		Matrices sproduct2 = Matrices.mult(ms2, ms1);
		
		System.out.println("ms1 times ms2");
		System.out.println(sproduct1.toString());
		System.out.println("ms2 times ms1");
		System.out.println(sproduct2.toString());

		if (sproduct1.equals(sproduct2)) System.out.println("Commutativity of scaling works. SUCCESS");
		else System.out.println("Commutativity FAILED");
		System.out.println("////////////////////////");
	}

	private static void noCommuteTest(Random r){
		System.out.println("////////////////////////");
		System.out.println("Two matrices that don't commute");
		Matrices ms = Transformations.getScale(randInt(1, 11, r), randInt(1, 11, r), randInt(1, 11, r));
		Matrices mr = Transformations.getRotX(45);

		System.out.println("ms (scaling matrix):");
		System.out.println(ms.toString());
		System.out.println("mr (rotation matrix):");
		System.out.println(mr.toString());
		
		Matrices rsproduct1 = Matrices.mult(ms, mr);
		Matrices rsproduct2 = Matrices.mult(mr, ms);

		System.out.println("ms1 times mr1");
		System.out.println(rsproduct1);
		System.out.println("mr1 times ms1");
		System.out.println(rsproduct2);
		
		if (rsproduct1.equals(rsproduct2)) System.out.println("Non-commutative matrices commuted. FAILED");
		else System.out.println("Non-commutative matrices's products are different. SUCCESS");
		System.out.println("////////////////////////");
	}

	private static void customTest(Random r){
		System.out.println("////////////////////////");
		System.out.println("Custom test");

		System.out.println("My claim is scaling and translation are commutative, and matrices that represent this combined transformation are of the form:");

		System.out.println("sx  0  0  tx");
		System.out.println("0  sy  0  ty");
		System.out.println("0  0  sz  tz");
		System.out.println("0  0  0  1");

		System.out.println("This test verifies this empirically.");

		Matrices ms = Transformations.getScale(randInt(1, 11, r), randInt(1, 11, r), randInt(1, 11, r));

		Matrices mt = Transformations.getTranslate(randInt(1, 11, r), randInt(1, 11, r), randInt(1, 11, r));

		System.out.println("ms:");
		System.out.println(ms.toString());
		System.out.println("mt:");
		System.out.println(mt.toString());

		Matrices soproduct1 = Matrices.mult(ms, mt);
		Matrices soproduct2 = Matrices.mult(mt, ms);

		System.out.println("The matrix products will either verify or disprove my claim:");

		System.out.println("ms times mt");
		System.out.println(soproduct1);
		System.out.println("mt times ms");
		System.out.println(soproduct2);

		System.out.println("////////////////////////");
	}

	public static int randInt(int start, int stop, Random r) {
		return (int)(r.nextFloat() * (stop-start) + start);
	}

}
