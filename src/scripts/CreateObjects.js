var JavaPackages = new JavaImporter(
 Packages.java.awt.Color,
 Packages.sage.scene.Group,
 Packages.sage.scene.shape.Sphere,
 Packages.sage.scene.shape.Pyramid,
 Packages.sage.scene.shape.Cylinder,
 Packages.sage.scene.shape.Line,
 Packages.sage.scene.shape.Rectangle,
 Packages.gameengine.shapes.Trapesoid,
 Packages.trimesh.Mushroom,
 Packages.trimesh.ChessPieceRock,
 Packages.graphicslib3D.Vector3D,
 Packages.graphicslib3D.Point3D);

with (JavaPackages)
{
var rootNode = new Group();
var cubeCount = 100;

////Sphere
//var t1 = new Sphere(2,50,50,java.awt.Color.green);
//t1.translate(5,3,4);
//rootNode.addChild(t1);
//
////Cylinder
//var c1 = new Cylinder(5,3,50,50);
//c1.translate(1,5,10);
//rootNode.addChild(c1);

////Trapesoid
//var t1 = new Trapesoid();
//t1.translate(-5,2,5);
//rootNode.addChild(t1);
//
//var s = new Mushroom("bad");
//s.translate(0, 0, 200);
//s.scale(10, 40, 10);
//rootNode.addChild(s);
//
//var s = new Mushroom("bad");
//s.translate(10, 0, 600);
//s.scale(10, 40, 10);
//rootNode.addChild(s);
//
//
//    var s = new ChessPieceRock("bad");
//    s.translate(-10, 0, 500);
//    s.scale(10, 10, 10);
//    rootNode.addChild(s);
//
//   var s = new ChessPieceRock("bad");
//    s.translate(-20, 0, 1000);
//    s.scale(10, 10, 10);
//    rootNode.addChild(s);
//
//   var s = new ChessPieceRock("bad");
//    s.translate(0, 0, 2000);
//    s.scale(10, 10, 10);
//    rootNode.addChild(s);
//
//   var s = new ChessPieceRock("bad");
//    s.translate(-1, 0, 2300);
//    s.scale(10, 10, 10);
//    rootNode.addChild(s);
//
//   var s = new ChessPieceRock("bad");
//    s.translate(30, 0, 4000);
//    s.scale(10, 10, 10);
//    rootNode.addChild(s);
//
//   var s = new ChessPieceRock("bad");
//    s.translate(0, 0, 5000);
//    s.scale(10, 10, 10);
//    rootNode.addChild(s);
//
//   var s = new ChessPieceRock("bad");
//    s.translate(20, 0, 5500);
//    s.scale(10, 10, 10);
//    rootNode.addChild(s);
//
//   var s = new ChessPieceRock("bad");
//    s.translate(0, 0, 7000);
//    s.scale(10, 10, 10);
//    rootNode.addChild(s);

//Floor plane
//var plane = new Rectangle();
//var vec = new Vector3D(1, 0, 0);
//plane.rotate(90, vec);
//plane.scale(1000, 1000, 1);
//plane.setColor(Color.GRAY);
//rootNode.addChild(plane);

//Axis
//var xaxis = new Line(new Point3D(-500, 0, 0), new Point3D(500, 0, 0), Color.RED, 2);
//rootNode.addChild(xaxis);
//var yaxis = new Line(new Point3D(0, -500, 0), new Point3D(0, 500, 0), Color.GREEN, 2);
//rootNode.addChild(yaxis);
//var zaxis = new Line(new Point3D(0, 0, -500), new Point3D(0, 0, 500), Color.BLUE, 2);
//rootNode.addChild(zaxis);
}