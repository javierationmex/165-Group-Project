package trimesh;

import graphicslib3D.Point3D;
import sage.scene.Group;
import sage.scene.SceneNode;
import sage.scene.TriMesh;
import sage.scene.shape.Polygon3D;

import java.awt.*;
import java.nio.FloatBuffer;
import java.nio.IntBuffer;
import java.util.Iterator;


public class Car extends Group {
    public Car() {

        this.setName("Car");
        setIsTransformSpaceParent(true);


        TriMeshCar c = new TriMeshCar();
        this.addChild(c);
        //CarChassis ch = new CarChassis();
        //this.addChild(ch);

        //this.setWorldTranslation(tm);


        sage.scene.shape.Polygon3D frontwindow = new Polygon3D();
        frontwindow.getVertices().add(new Point3D(2.05f, 0.2f, 0.9f));
        frontwindow.getVertices().add(new Point3D(1.15f, 0.9f, 0.7f));
        frontwindow.getVertices().add(new Point3D(1.15f, 0.9f, -0.7f));
        frontwindow.getVertices().add(new Point3D(2.05, 0.2f, -0.9f));
        this.addChild(frontwindow);
        frontwindow.setColor(Color.BLACK);

        sage.scene.shape.Polygon3D backwindow = new Polygon3D();
        backwindow.getVertices().add(new Point3D(-1.555f, 0.2f, 0.9f));
        backwindow.getVertices().add(new Point3D(-1.11f, 0.9f, 0.7f));
        backwindow.getVertices().add(new Point3D(-1.11f, 0.9f, -0.7f));
        backwindow.getVertices().add(new Point3D(-1.555f, 0.2f, -0.9f));
        this.addChild(backwindow);
        backwindow.setColor(Color.BLACK);

        sage.scene.shape.Polygon3D rightsidewindow = new Polygon3D();
        rightsidewindow.getVertices().add(new Point3D(-1.2f, 0.3f, 1.01f));
        rightsidewindow.getVertices().add(new Point3D(-0.9f, 0.9f, 0.85f));
        rightsidewindow.getVertices().add(new Point3D(0.9, 0.9f, 0.85f));
        rightsidewindow.getVertices().add(new Point3D(1.5f, 0.3f, 1.01f));
        this.addChild(rightsidewindow);
        rightsidewindow.setColor(Color.BLACK);

        sage.scene.shape.Polygon3D leftsidewindow = new Polygon3D();
        leftsidewindow.getVertices().add(new Point3D(-1.2f, 0.3f, -1.01f));
        leftsidewindow.getVertices().add(new Point3D(-0.9f, 0.9f, -0.85f));
        leftsidewindow.getVertices().add(new Point3D(0.9, 0.9f, -0.85f));
        leftsidewindow.getVertices().add(new Point3D(1.5f, 0.3f, -1.01f));
        this.addChild(leftsidewindow);
        leftsidewindow.setColor(Color.BLACK);


    }

    public void rotateWheels() {
        Iterator it = this.getChildren();
        while (it.hasNext()) {
            SceneNode s = (SceneNode) it.next();
            if (s.getName().equalsIgnoreCase("CarChassis")) ((CarChassis) s).rotateWheels();
        }
        updateTransforms(1);
    }

    private class TriMeshCar extends TriMesh {
        public TriMeshCar() {

            float[] vrts = new float[]{
                    -2, -1, 1, -2, 0, 1, -1.5f, 0.3f, 1, -1, 1, 0.8f, 1, 1, 0.8f, 2, 0.2f, 1, 3, 0, 1, 3, -1, 1,
                    -2, -1, -1, -2, 0, -1, -1.5f, 0.3f, -1, -1, 1, -0.8f, 1, 1, -0.8f, 2, 0.2f, -1, 3, 0, -1, 3, -1, -1
            };

            float[] cl = new float[]{
                    0.5f, 0.5f, 0.5f, 1, 0.5f, 0.5f, 0.5f, 1, 1, 1, 1, 1, 1, 1, 0, 1, 1, 1, 0, 1, 1, 1, 1, 1, 1, 1, 1, 1, 0.5f, 0.5f, 0.5f, 1,
                    0.5f, 0.5f, 0.5f, 1, 0.5f, 0.5f, 0.5f, 1, 1, 1, 1, 1, 1, 1, 0, 1, 1, 1, 0, 1, 1, 1, 1, 1, 1, 1, 1, 1, 0.5f, 0.5f, 0.5f, 1,
            };

            int[] triangles = new int[]{
                    0, 1, 2, 2, 3, 4, 2, 4, 5, 0, 2, 5, 0, 5, 6, 0, 7, 6,
                    0, 8, 9, 0, 1, 9, 1, 9, 10, 1, 2, 10, 2, 10, 11, 2, 3, 11, 3, 11, 12, 3, 4, 12, 4, 12, 13, 4, 5, 13, 5, 13, 14, 5, 6, 14, 6, 14, 15, 6, 7, 15, 0, 7, 15, 0, 8, 15,
                    8, 9, 10, 10, 11, 12, 10, 12, 13, 8, 10, 13, 8, 13, 14, 8, 14, 15
            };

            FloatBuffer vertBuf = com.jogamp.common.nio.Buffers.newDirectFloatBuffer(vrts);
            FloatBuffer colorBuf = com.jogamp.common.nio.Buffers.newDirectFloatBuffer(cl);
            IntBuffer triangleBuf = com.jogamp.common.nio.Buffers.newDirectIntBuffer(triangles);
            this.setVertexBuffer(vertBuf);
            this.setColorBuffer(colorBuf);
            this.setIndexBuffer(triangleBuf);
        }
    }
}
