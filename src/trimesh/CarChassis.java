package trimesh;

import graphicslib3D.Vector3D;
import sage.scene.Group;
import sage.scene.SceneNode;

import java.util.Iterator;

/**
 * Created by arash on 2/26/2015.
 */
public class CarChassis extends Group {
    public CarChassis() {
        setIsTransformSpaceParent(true);
        this.setName("CarChassis");

        Wheel frontRightWheel = new Wheel();
        // Matrix3D ls = new Matrix3D();
        //ls.setToIdentity();
        //ls.scale(0.11f, 0.11f, 0.11f);
        frontRightWheel.scale(0.11f, 0.11f, 0.11f);
        frontRightWheel.rotate(90, new Vector3D(1, 0, 0));
        frontRightWheel.translate(2, -1, 1.1f);


        Wheel frontLeftWheel = new Wheel();
        frontLeftWheel.scale(0.11f, 0.11f, 0.11f);
        frontLeftWheel.rotate(90, new Vector3D(1, 0, 0));
        frontLeftWheel.translate(2, -1, -1.1f);


        Wheel BackRightWheel = new Wheel();
        BackRightWheel.scale(0.11f, 0.11f, 0.11f);
        BackRightWheel.rotate(90, new Vector3D(1, 0, 0));
        BackRightWheel.translate(-1, -1, 1.1f);


        Wheel BackLeftWheel = new Wheel();
        BackLeftWheel.scale(0.11f, 0.11f, 0.11f);
        BackLeftWheel.rotate(90, new Vector3D(1, 0, 0));
        BackLeftWheel.translate(-1, -1, -1.1f);


        this.addChild(frontRightWheel);
        this.addChild(frontLeftWheel);
        this.addChild(BackRightWheel);
        this.addChild(BackLeftWheel);


    }

    public void rotateWheels() {
        Iterator it = this.getChildren();
        while (it.hasNext()) {
            SceneNode s = (SceneNode) it.next();
            if (s.getName().equalsIgnoreCase("Wheel")) s.rotate(10, new Vector3D(0, 1, 0));
        }
    }
}
