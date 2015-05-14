package gameengine.NPC;

import graphicslib3D.Point3D;

/**
 * Created by arash on 5/13/2015.
 */
public class NPC {
    Point3D location;

    public double getX() {
        return location.getX();
    }

    public double getY() {
        return location.getY();
    }

    public double getZ() {
        return location.getZ();
    }

    public void updateLocation(Point3D p) {
        location = p;
    }
}
