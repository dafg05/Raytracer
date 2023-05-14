# PROGRESS FOR FINAL PROJECT


I've improved my ray tracer in the following ways:

1. Multiple light sources

As mentioned before, the computeColor() method calculates intensity for each light source and add all these colors together. To handle overlighting, we keep track of the max color value in the canvas object. If, when writing to ppm, this value exceeds 1.0, then we iterate over all pixels, logarithmically scaling them so that the max value is 1.0 (see funkyLog() and correctOverlightingByLog() in Canvas.java).

2. Distributed rays when rendering

Shoot 2^2 rendering rays instead of 1 for each pixel. Technically, can use any int n^2, but 2^2 suffices for a nice blurry effect on objects that helps counteract aliasing.

3. Rectangular Lights

A grid of lights used for soft shadows. For each cell in the grid, use a random point within the grid for light calculations. Produces pretty nice soft shadows.

4. Reflection

A recursive implementation of reflections, in which if our camera rays intersect a reflective object, recursively call compute color with reflected rays. (Question: should faraway objects be of different colors?)

5. Triangles
 
Added files Triangle.java, SmoothTriangle.java, OBJParser.java. Used teapot as a setup in the teapot() method in World.java. Implemented local_normal_at for SmoothTriangle.java.

6. Groups

...


# TODO

* ~~Add references to traceables in a group that point back to their group~~
* ~~Test rotation of teapot~~
* ~~Possibly debug teapot~~
* ~~Refactor implementation so that the transforms of the children get multiplied with group transform (is this necessary?)~~
* Bounding traceables
* Naive transparency
* Refractive transparency

