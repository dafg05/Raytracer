# FINAL PROJECT: RAY TRACER

## Features I added for my final iteration

### 1. Multiple light sources

The computeColor() method in World.java calculates intensity for each light source and adds all these colors together. To handle overlighting (aka, RGB values over 1.0), I keep track of the max color value by storing it in the canvas object. If, when writing to ppm, this max value exceeds 1.0, then we iterate over all pixels, logarithmically scaling them so that the max value is 1.0 (see funkyLog() and correctOverlightingByLog() in Canvas.java for details on the log function chosen).

Files: *World.java* in computeColor(), *Canvas.java* in correctOverlightingByLog().

**Seen in:**  
* **manyLights.ppm - with a point light and a rectangular light.**

### 2. Distributed rays when rendering

For each pixel, shoot a ray at square grid that subdivides the area of the pixel; average the colors returned. The number of rays shot is (sqrtRPP)^2; this variable is found in World.java, and for every rendered image turned in, sqrtRPP <- 2. I found using this value results in a nice anti-aliasing effect without being too computationally expensive.

Files: *World.java* in render().

**Seen in every rendered image. Focus on the edges of objects, especially spheres, to notice the effect.**

### 3. Rectangular Lights

A grid of lights used to produce soft shadows. To intialize, uses two vectors (width and height Vectors) to determine the orientation and scale of the grid; uses widthSamples (number of columns in grid) and heightSamples (number of rows in grid) to determine how many cells will be in the rectangle. The actual sample point is computed randomly within each cell.

Files: *World.java* in computeColor() and setup methods. *RectangularLight.java* for implementation. In addition, added lighting() to *LightSource.java*, changed *intensityAt()*.

**Seen in:**
* **theWall.ppm**
* **manyLights.ppm - compares soft shadows to hard shadows.**

### 4. Reflection

If an object is reflective, I calculate the reflected ray with a formula that I found online: 

R = U - 2N (U dot N)

where R is the reflected ray, U is the incident ray, N is the normal.

To do this, turned computeColor() method into a recursive method with reflectionDepth as a parameter, maxing out at 3. Note: when shooting a reflected ray, I perturbed its origin in its own direction to avoid self reflection. 

Files: *World.java* in computeColor(), reflectRay().

**Seen in:**
* **theWall.ppm: the left part of the wall is a mirror – aka a reflected cube –.**
* **mirrors.ppm: two reflective cubes rotated 45 degrees to show off a sphere not visible to the camera.**
* **teapot.ppm: back wall is a slightly reflective surface.**

### 5. Transparency

Transparency without refraction: if a ray intersects a transparent object, recursively shoot another ray in the same direction, with the ray's origin slightly offset from the intersection point in the ray's direction. Works with multiple transparent objects.

Note that shadows are affected by transparent objects, so I stopped using the isShadowed() method in *LightSource.java* in favor of a new method called *shadowIntensity*, which scales the light intensity by the transparency of every transparent object intersected. 

Also note that computeColor() can handle both reflection and transparent colors with the following formula:

Final Color = (1-r)(1-t)L + rR + tT
where r is reflectionFactor, t is transpFactor, L is lightColor, R is reflectColor, T is transpColor

Files: *World.java* in computeColor(), *LightSource.java* in shadowIntensity(), adjustment to intensityAt() for *PointLight.java* and *RectangularLight.java*.

**Seen in:**
* **theWall.ppm: the right wall is transparent and reveals JER's teapot. Notice how this right wall blocks the ray between the teapot and the light source, but because it's transparent, it lets some light through.**
* **windows.ppm: showcases stacked transparent object. Notice the light shadow on the sphere, this situation resembles that of the teapot in theWall.ppm, which works due to the shadowIntensity() method.**

### 6. Triangles
 
Added files Triangle.java, SmoothTriangle.java, OBJParser.java from class. Implemented local_normal_at for SmoothTriangle.java.

**Seen in:**
* **theWall.ppm: JER's triangle teapot behind a transparent cube.**
* **teapot.ppm: teapot coupled with a reflective wall, shows a shadow.**

### 7. Groups

A Traceable that holds other Traceables. If its transform is changed, then this transform is applied to every single one of its children. If initialized with a material, sets the same material for all of its children (as I did with the triangles of the teapot). Note that both the transform and the material are parent variables that can only be set with dedicated setter methods: this is to ensure that a Group's fields reflect that of its children. Can add a bounding traceable to it, whose transform is also affected by the group's transform.

Files: The entirety of *Group.java*, which also contains tests for local_intersect(), local_normal_at(), and, when coupled with print statements in local_intersect(), bounding objects.

**Seen in:**
* **Everytime a teapot is present, where I initialized a group with a specific material**
* **windows.ppm: the windows and the sphere form a group, which I used to rotate all of the them at the same time.**

### 8. Bounding traceables

For a Group object, can add any traceable as a bounding object to speed up intersection calculations. Note that because the traceable is user specified, the initialdimensions of this object is not automatically calculated and must be specified by the programmer. Once specified, the bounding object's transform varies with that of its Group.

Files: **Group.java*

**Seen in:**
* **For every teapot used, a unit bounding sphere is used.**

## How to use

In main method of World.java, call one of the setup methods listed at the bottom of the file.

Default settings:

* setup = teapot() ; change in `main()` of *World.java*, by deleting the line that says `w.teapot()` and replacing with another setup method; e.g. `w.mirrors()`
* `sqrtRPP = 1` ; change at `render()` in *World.java*.
* `hsize = 500` and `wsize = 500`; change `render() ` parameters of `main()` of World.java.
* filename = "test001.ppm" ; change `render() ` filename parameter of `main()` of World.java.
