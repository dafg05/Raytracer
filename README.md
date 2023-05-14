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

(TODO: talk more) implemented local_normal_at for smooth_triangle, got teapot (...) working with ray tracer

6. Groups

Implemented Group.java... Tested...


