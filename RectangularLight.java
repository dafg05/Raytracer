import java.util.ArrayList;


// A rectangular light source. Extends LightSource.

public class RectangularLight extends LightSource {

    Vector widthVector;
    Vector heightVector;
    int widthSamples;
    int heightSamples;

    public String toString() {
        return "Rectangular light at " + this.position + " with intensity " + this.intensity;
    }

    @Override 
    public MyColor intensityAt(Point point, Intersection inters, World w) {
        // compute normal for the given given point, note that it doesn't change when 
        // calculating color of different cells
        Vector unitNormal = inters.object.normalAt(point, inters).normalize();
        Vector scaledNormal = unitNormal.scale(w.perturbation);

        // Sum intensities(aka color when considering lighting effects) of all samples (cells), store in totalIntensity
        MyColor totalIntensity = MyColor.Black;
        // iterate over all cells in the rectangular light source
        for (int i = 0; i < this.widthSamples; i++) {
            for (int j = 0; j < this.heightSamples; j++) {
                // cell coordinates, aka sample point
                Point samplePoint = this.getSamplePoint(i, j);
                // use a perturbed point for shadow calculations
                Point perturbedPoint = new Point(Tuple.add(point, scaledNormal));

                MyColor shadowIntensity = this.shadowIntensity(perturbedPoint, samplePoint, w);
		        if (!shadowIntensity.equals(MyColor.Black)){ // skip lighting calculations if point is shadowed.
                    // calculate color at samplePoint using lighting effects
                    totalIntensity = new MyColor(Tuple.add(this.lighting(samplePoint, point, unitNormal, inters, shadowIntensity), totalIntensity));
		        }
            }
        }
        // actual intensity will be the average of all sample intensities
        double averagingFactor = 1.0 / (this.heightSamples * this.widthSamples);
        MyColor actualIntensity = totalIntensity.scale(averagingFactor);
        return actualIntensity;
    }

    public MyColor getIntensity() {
		return intensity;
	}

    public void setIntensity(MyColor intensity) {
		this.intensity = intensity;
	}

    /* Note:  */
    public RectangularLight(MyColor intensity, Point position, Vector widthVector, Vector heightVector, int widthSamples, int heightSamples) {
        super();
        this.intensity = intensity;
        this.position = position;
        this.widthVector = widthVector;
        this.heightVector = heightVector;
        this.widthSamples = widthSamples;
        this.heightSamples = heightSamples;
    }

    private Point getSamplePoint(int i, int j) {
        return new Point(Tuple.add(this.position, Tuple.add(this.widthVector.scale((i + Math.random())/this.widthSamples), this.heightVector.scale((j + Math.random())/this.heightSamples))));
    }
}