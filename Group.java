import java.lang.reflect.Array;
import java.util.ArrayList;

public class Group extends Traceable{

    ArrayList<Traceable> children;
    Sphere boundSphere;

    public Group(ArrayList<Traceable> children, Sphere boundSphere){
        this.children = children;
        this.boundSphere = boundSphere;
    }

    public Group(ArrayList<Traceable> children){
        this.children = children;
        this.boundSphere = null;
    }

    public ArrayList<Intersection> local_intersect(Ray r){
        // consider the transformed ray

        Ray tr = r.transform(this.transform.invert());

        // if there's a bounding sphere, check if the ray intersects it
        if (this.boundSphere != null){
            ArrayList<Intersection> bsIntersections = this.boundSphere.intersections(tr);
            if (bsIntersections.size() == 0){ // if there's no intersection, return empty list
                return new ArrayList<Intersection>();
            } // else, do normal intersection test
        }

        ArrayList<Intersection> ans = new ArrayList<Intersection>();
        
        for (Traceable child: children){
            ArrayList<Intersection> childIntersections = child.intersections(tr);
            ans.addAll(childIntersections);
        }
        return ans;
    }

    public Vector local_normal_at(Point p, Intersection i){
        return i.object.normalAt(p, i);
    }

    public boolean includes(Traceable object) {
        // TODO: iterate over all children, see if any of them includes object
		return false;
	}

    public static void intersTestSuite(Group g){
        Ray straight = new Ray(new Point(0,0,2), new Vector(0,0,-1));
        Ray diagonalUp = new Ray(new Point(0,0,2), new Vector(0,1,-1));
        Ray diagonalDown = new Ray(new Point(0,0,2), new Vector(0,-1,-1));
        Ray fromBelow = new Ray(new Point(0,-4,0), new Vector(0,1,0));

        ArrayList<Intersection> ans1 = g.intersections(straight);
        System.out.println("Straight ray: " + straight);
        System.out.println(ans1);

        ArrayList<Intersection> ans2 = g.intersections(diagonalUp);
        System.out.println("Diagonal up ray: " + diagonalUp);
        System.out.println(ans2);

        ArrayList<Intersection> ans3 = g.intersections(diagonalDown);
        System.out.println("Diagonal down ray " + diagonalDown);
        System.out.println(ans3);

        ArrayList<Intersection> ans4 = g.intersections(fromBelow);
        System.out.println("From below ray " + fromBelow);
        System.out.println(ans4);

        // test local_normal_at
        for (Intersection i : ans4){
            Point p = fromBelow.position(i.t);
            System.out.println("Normal at " + i + ": " + g.normalAt(p, i));
        }
    }

    public static void boundTestSuite(Group g){
        Ray straight = new Ray(new Point(0,0,4), new Vector(0,0,-1));
        Ray diagonalUp = new Ray(new Point(0,0,4), new Vector(0,1,-1));

        ArrayList<Intersection> ans1 = g.intersections(straight);
        System.out.println("Straight ray: " + straight);
        System.out.println(ans1);

        ArrayList<Intersection> ans2 = g.intersections(diagonalUp);
        System.out.println("Very diagonal up ray: " + diagonalUp);
        System.out.println(ans2);

    }

    public static void testIntersections(){
        // sphere is transformed
        Sphere s = new Sphere();
        s.transform = Transformations.getTranslate(0, 2, 0);
        
        // cube isn't
        Cube c = new Cube();

        ArrayList<Traceable> children = new ArrayList<Traceable>();
        children.add(s); children.add(c);

        // make a group with these traceables
        Group g = new Group(children);

        // // test with untransformed group
        // System.out.println("Group at origin");
        // Group.intersTestSuite(g);

        // try again with transformed group
        g.transform = Transformations.getTranslate(0, -2, 0);
        System.out.println("Group at (0, -2, 0)");
        Group.intersTestSuite(g);
    }

    public static void testBoundSphere(){
        Sphere bs = new Sphere();
        bs.transform = Transformations.getScale(3, 3, 3);

        Cube c1 = new Cube();
        c1.transform = Transformations.getTranslate(0, 0.5, 0);
        Cube c2 = new Cube();
        c2.transform = Transformations.getTranslate(0, -0.5, 0);

        ArrayList<Traceable> children = new ArrayList<Traceable>();
        children.add(c1); children.add(c2);
        
        // untransformed group
        System.out.println("Untransformed group");
        Group g = new Group(children, bs);
        Group.boundTestSuite(g);

        // transformed group
        System.out.println("Transformed group");
        g.transform = Transformations.getScale(0.5, 0.5, 0.5);
        Group.boundTestSuite(g);
    }

    public static void main(String[] args){
        // Group.testBoundSphere();
        Group.testIntersections();
    }
}