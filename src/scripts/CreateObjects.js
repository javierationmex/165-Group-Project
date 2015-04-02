var JavaPackages = new JavaImporter(
 Packages.java.awt.Color,
 Packages.sage.scene.Group,
 Packages.sage.scene.shape.Sphere,
 Packages.sage.scene.shape.Pyramid,
 Packages.sage.scene.shape.Cylinder,
 Packages.sage.scene.shape.Line,
 Packages.sage.scene.shape.Rectangle,
 Packages.gameengine.shapes.Trapesoid,
 Packages.graphicslib3D.Vector3D,
 Packages.graphicslib3D.Point3D);

with (JavaPackages)
{
var rootNode = new Group();

//Sphere
var t1 = new Sphere(2,100,100,java.awt.Color.green);
t1.translate(5,3,4);
rootNode.addChild(t1);

//Cylinder
var c1 = new Cylinder(5,3,100,100);
c1.translate(1,5,10);
rootNode.addChild(c1);

//Trapesoid
var t1 = new Trapesoid();
t1.translate(-5,2,5);
rootNode.addChild(t1);

//Floor plane
var plane = new Rectangle();
var vec = new Vector3D(1, 0, 0);
plane.rotate(90, vec);
plane.scale(400, 400, 400);
plane.setColor(Color.GRAY);
rootNode.addChild(plane);

//Axis
var xaxis = new Line(new Point3D(-1000, 0, 0), new Point3D(1000, 0, 0), Color.RED, 2);
rootNode.addChild(xaxis);
var yaxis = new Line(new Point3D(0, -1000, 0), new Point3D(0, 1000, 0), Color.GREEN, 2);
rootNode.addChild(yaxis);
var zaxis = new Line(new Point3D(0, 0, -1000), new Point3D(0, 0, 1000), Color.BLUE, 2);
rootNode.addChild(zaxis);
}