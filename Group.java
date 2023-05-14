import java.lang.reflect.Array;
import java.util.ArrayList;

public class Group extends Traceable{

    private ArrayList<Traceable> children;
    private Sphere boundSphere;
    private Matrices transform; // override Traceable's transform to make it private

    public Group(ArrayList<Traceable> children){
        this(children, null);
    }

    public Group(ArrayList<Traceable> children, Sphere boundSphere){
        this(children, boundSphere, Matrices.identity());
    }

    public Group(ArrayList<Traceable> children, Sphere boundSphere, Matrices transform){
        this.transform = transform;
        this.setChildren(children);
        if (boundSphere != null){
            this.setBoundSphere(boundSphere);
        }
    };

    public ArrayList<Intersection> local_intersect(Ray r){
        // NOTE: since we assume that all objects' transforms have been updated,
        // we don't need to transform the ray here.

        // if there's a bounding sphere, check if the ray intersects it
        if (this.boundSphere != null){
            ArrayList<Intersection> bsIntersections = this.boundSphere.intersections(r);
            if (bsIntersections.size() == 0){ // if there's no intersection, return empty list
                return new ArrayList<Intersection>();
            } // else, do normal intersection test
        }

        ArrayList<Intersection> ans = new ArrayList<Intersection>();

        for (Traceable child: children){
            ArrayList<Intersection> childIntersections = child.intersections(r);
            ans.addAll(childIntersections);
        }
        return ans;
    }

    public void setChildren(ArrayList<Traceable> children){
        // Set the children of this group, and update their transforms.
        this.children = children;
        for (Traceable child : this.children){
            child.transform = Matrices.mult(this.transform, child.transform);
            child.parentGroup = this;
        }
    }

    public void setBoundSphere(Sphere boundSphere){
        // Set the bounding sphere of this group, and update its transform.
        this.boundSphere = boundSphere;
        this.boundSphere.transform = Matrices.mult(this.transform, this.boundSphere.transform);
    }

    public void setTransform(Matrices m){
        // Set the transform of this group, and update the transforms of its children and bounding sphere.
        this.transform = m;
        this.setChildren(this.children);
        if (this.boundSphere != null){
            this.setBoundSphere(this.boundSphere);
        }
    }

    public Vector local_normal_at(Point p, Intersection i){
        // TODO: deal with individual object transforms

        return i.object.normalAt(p, i);
    }

    public boolean includes(Traceable object) {
        // NOT IMPLEMENTED!! 
		return false;
	}

    public static void main(String[] args){
        Group.testBoundSphere();
        // Group.testIntersections();
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

        // test with untransformed group
        System.out.println("Group at origin");
        Group.intersTestSuite(g);

        // try again with transformed group
        g.setTransform(Transformations.getTranslate(0, -2, 0));
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
        g.setTransform(Transformations.getScale(0.5, 0.5, 0.5));
        Group.boundTestSuite(g);
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
            System.out.println("inters point: " + p);
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


}