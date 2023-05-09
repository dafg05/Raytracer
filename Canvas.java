import java.io.File;
import java.io.FileNotFoundException;
import java.io.PrintWriter;

public class Canvas {

	/* Make sure you understand all the methods you are given.
	 * 
	 * Then fill in the contents of toPPM.  The method should write the
	 * pixels array to a ppm file.  See the method for the details.
	 * 
	 */

	double maxColorValue;

	public Canvas() {
	}

	protected MyColor[][] pixels;

	public Canvas(int w, int h) {
		pixels = new MyColor[w][h];
		for (int i=0; i< w; i++)
			for (int j=0; j<h; j++)
				pixels[i][j] = new MyColor(0,0,0);
	}

	public void writeP(int i,int j,MyColor c) {
		// i denotes column number
		// j denotes row number
		pixels[i][j] = c;
	}


	public MyColor pixelAt(int i,int j) {
		return pixels[i][j];
	}

	public void toPPM(String name) {

		if (this.maxColorValue > 1){
			this.correctOverlightingByLog();
		}

		File f = new File(name);
		try {
			/* The first line of the file should be P3
			 * On the second line should the number of rows and the number of columns
			 * On the third line put the maximum integer for an RGB value 
			 * (which should be 255, at least for now)
			 * 
			 * After that the RGB values of each pixel.  
			 * 
			 * You need to convert the double values of the RGB to integer values
			 * The doubles should be between 0 and 1
			 */	

			PrintWriter p = new PrintWriter(f);
			// A printwriter has the print and println methods you usually use

			int w = this.pixels.length; // number of columns
			int h = this.pixels[0].length; // number of rows
			p.println("P3");
			p.println(w + " " + h); // other way: columns, then row (aka, width, then height)
			p.println(255);

			// for (int i = 0; i < w; i++){
			for (int j = 0; j < h; j++){
				for (int i = 0; i < w; i++){
				// for (int j = 0; j < h; j++){
					double[] t = pixelAt(i,j).getT();
					p.print(this.doubleRGBValueToInt(t[0]) + " ");
					p.print(this.doubleRGBValueToInt(t[1]) + " ");
					p.print(this.doubleRGBValueToInt(t[2]) + "\n");
				}
			}

			p.close();

		} catch (FileNotFoundException e) {
			e.printStackTrace();
			System.out.println(e);
			System.exit(17);
		}
	}


	private int doubleRGBValueToInt(double x){
		return (int) (x * 255);
	}

	private void correctOverlightingByLimit(){
		for (int i = 0; i < this.pixels.length; i++){
			for (int j = 0; j < this.pixels[0].length; j++){
				if (this.pixels[i][j].getT()[0] > 1){
					this.pixels[i][j].getT()[0] = 1;
				}
				if (this.pixels[i][j].getT()[1] > 1){
					this.pixels[i][j].getT()[1] = 1;
				}
				if (this.pixels[i][j].getT()[2] > 1){
					this.pixels[i][j].getT()[2] = 1;
				}
			}
		}
	}

	private void correctOverlightingByLog(){
		for (int i = 0; i < this.pixels.length; i++){
			for (int j = 0; j < this.pixels[0].length; j++){
				this.pixels[i][j].getT()[0] = funkyLog(this.pixels[i][j].getT()[0], this.maxColorValue);
				this.pixels[i][j].getT()[1] = funkyLog(this.pixels[i][j].getT()[1], this.maxColorValue);
				this.pixels[i][j].getT()[2] = funkyLog(this.pixels[i][j].getT()[2], this.maxColorValue);
			}
		}
	}

	private static double funkyLog(double x, double b){
		 /*
		  * A funny log function constructed in such a way that funkyLog(0) = 0 and funkyLog(b) = 1
		  */
		return Math.log(x + 1) / Math.log(b + 1);
	}
	
	public static void main(String args[]) {

		Canvas c = new Canvas(150,100);
		for (int i=0; i<50; i++) {
			for (int j=0;j<100; j++) {
				if (j < 50){
					c.writeP(i,j,new MyColor(1,0,0,1));
				}
				else {
					c.writeP(i,j,new MyColor(0,0,1,1));
				}	
			}
		}

		for (int i=50; i<100; i++){
			for (int j=0;j<100; j++){
				if (j < 50){
					c.writeP(i,j,new MyColor(0,0,1,1));
				}
				else {
					c.writeP(i,j,new MyColor(1,0,0,1));
				}
			}		
		}

		for (int i = 100; i < 150; i++) {
			for (int j=0;j<100; j++) {
				if (j < 50){
					c.writeP(i,j,new MyColor(1,0,0,1));
				}
				else {
					c.writeP(i,j,new MyColor(0,0,1,1));
				}
			}
		}

		c.toPPM("test.ppm");

	}

}
