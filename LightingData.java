public class LightingData {
    Intersection inters;
    Point intersectionPoint;
    Point perturbedPoint;
    Vector normal;
}



// let's seee
// for an area light:

// calculating the color of a point:

// From world:
// If we intersect an object, then get the object color.
// Calculate the light intensity at the point.
// If the light intensity is zero, then add ambient light and call it a day.
// Else, compute lighting for the point. 
// Done