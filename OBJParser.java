import java.io.File;
import java.io.FileNotFoundException;
import java.util.ArrayList;
import java.util.Scanner;

public class OBJParser {

	String name;
	
	
	
	public OBJParser(String name) {
		super();
		this.name = name;
	}


	public ArrayList<Traceable> readFile() {
		
		ArrayList<Traceable> ts = new ArrayList<Traceable>();
		ArrayList<Point> points = new ArrayList<Point>();
		ArrayList<Vector> normals = new ArrayList<Vector>();

		points.add(new Point(0,0,0));
		normals.add(new Vector(0,0,0));
		
		double minx = Double.MAX_VALUE;
		double miny = Double.MAX_VALUE;;
		double minz = Double.MAX_VALUE;;
		double maxx = Double.MIN_VALUE;;
		double maxy = Double.MIN_VALUE;
		double maxz = Double.MIN_VALUE;

		
		try {
			File f = new File(name);
			Scanner s = new Scanner(f);
			
			while (s.hasNext() ) {
				String command = s.next();
				if (command.equals("v")) {
					points.add(new Point(s.nextDouble(),
							s.nextDouble(),
							s.nextDouble()));
					Point lastAdded = points.get(points.size()-1);
					if (lastAdded.t[0] > maxx)
						maxx = lastAdded.t[0];
					if (lastAdded.t[1] > maxy)
						maxy = lastAdded.t[1];
					if (lastAdded.t[2] > maxz)
						maxz = lastAdded.t[2];
					
					if (lastAdded.t[0] < minx)
						minx = lastAdded.t[0];
					if (lastAdded.t[1] < miny)
						miny = lastAdded.t[1];
					if (lastAdded.t[2] < minz)
						minz = lastAdded.t[2];
					
				}
				if (command.equals("vn")) {
					normals.add(new Vector(s.nextDouble(),
							s.nextDouble(),
							s.nextDouble()));
				}
				else if (command.equals("f")) {
					String line = s.nextLine();
					
					if (!line.contains("/")) {
						Scanner s1 = new Scanner(line);	
						
					Triangle tri = new Triangle(
							points.get(s1.nextInt()),
							points.get(s1.nextInt()),
							points.get(s1.nextInt()));
					ts.add(tri);
					}
					else {
						
						String[] parts = line.trim().split(" ");
						
						extract(ts,points,normals,parts[0],parts[1],parts[2]);

					}
					
				}
				else s.nextLine();
				
			}
			s.close();
		} catch (FileNotFoundException e) {
			// TODO Auto-generated catch block
			System.out.println(e);
			e.printStackTrace();
		}
		
		
		
		
		
		return ts;
	}
	
	
	private void extract(ArrayList<Traceable> ts, 
			ArrayList<Point> points,
			ArrayList<Vector> normals, 
			String x, String y, String z) {
		
			
		
			String[] xs = x.split("/");
			int p1 = Integer.parseInt(xs[0]);
			int t1 = Integer.parseInt(xs[1]);
			int n1 = Integer.parseInt(xs[2]);
			
			xs = y.split("/");
			int p2 = Integer.parseInt(xs[0]);
			int t2 = Integer.parseInt(xs[1]);
			int n2 = Integer.parseInt(xs[2]);
			
			xs = z.split("/");
			int p3 = Integer.parseInt(xs[0]);
			int t3 = Integer.parseInt(xs[1]);
			int n3 = Integer.parseInt(xs[2]);
			
			
			SmoothTriangle tri = new SmoothTriangle(
					points.get(p1),
					points.get(p2),
					points.get(p3),
					normals.get(n1),
					normals.get(n2),
					normals.get(n3)

					
					);
			
			
			
			ts.add(tri);
		
		
		
	}


	public static void main(String[] args) {
		OBJParser o1 = new OBJParser("teapot.obj");
		System.out.println(o1.readFile().size());
		
	}

}
