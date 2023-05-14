public class SmoothTriangle extends Triangle {

	Vector vx, vy, vz;
	
	public SmoothTriangle(Point x, Point y, Point z, Vector v1, Vector v2, Vector v3) {
		super(x, y, z);
		vx = v1;
		vy = v2;
		vz = v3;
	}

	@Override
	public Vector local_normal_at(Point p, Intersection hit) {
		double u = hit.u;
		double v = hit.v;
		double w = 1-u-v;
		Vector smoothNormal = new Vector(
			Tuple.add(
				Tuple.add(
					vx.scale(w),
					vy.scale(u)
				),
				vz.scale(v)
			)
		);
		return smoothNormal.normalize();
	}
	
	
	
}
