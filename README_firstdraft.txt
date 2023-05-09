For the first draft, I've improved my ray tracer in the following ways:

1. More modular code

The render() method in World.java now calls a computeColor() method with a ray and intersection as parameter. Within that computeColor() method, intensityAt() from LightSource.java is called for each light source, and within the intensity at all a lighting() function is called for each non-shadowed point.

2. Multiple light sources

As mentioned before, the computeColor() method calculates intensity for each light source and add all these colors together. To handle overlighting, we keep track of the max color value in the canvas object. If, when writing to ppm, this value exceeds 1.0, then we iterate over all pixels, logarithmically scaling them so that the max value is 1.0 (see funkyLog() and correctOverlightingByLog() in Canvas.java).

3. Distributed rays when rendering

Shoot 2^2 rendering rays instead of 1 for each pixel. Technically, can use any int n^2, but 2^2 suffices for a nice blurry effect on objects that helps counteract aliasing.

4. Rectangular Lights

A grid of lights used for soft shadows. For each cell in the grid, use a random point within the grid for light calculations. Produces pretty nice soft shadows.
