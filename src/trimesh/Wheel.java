package trimesh;

import graphicslib3D.Vector3D;
import sage.scene.Group;

/**
 * Created by arash on 2/26/2015.
 */
public class Wheel extends Group {
    public Wheel() {
        setIsTransformSpaceParent(true);
        this.setName("Wheel");


        Wedge wedge1 = new Wedge();
        wedge1.setName("wedge" + 0);
        wedge1.rotate(0, new Vector3D(0, 1, 0));
        this.addChild(wedge1);

        //this.setLocalBound(new BoundingSphere(new Point3D(0, 0, 0), 1));

        //System.out.println(this.getWorldBound().toString());

        //RotationController rc = new RotationController();
        //rc.addControlledNode(this);
        //this.addController(rc);

        //
        //updateLocalBound();
        //updateWorldBound();
    }
}
