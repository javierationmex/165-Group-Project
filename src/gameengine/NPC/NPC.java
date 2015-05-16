package gameengine.NPC;

import graphicslib3D.Matrix3D;
import graphicslib3D.Point3D;

import java.io.Serializable;

/**
 * Created by arash on 5/13/2015.
 */
public class NPC implements Serializable{
    private Point3D location;
    private String type;
    private int ID;
    private Matrix3D translation, rotation, scale;

    public NPC(String type, int ID, Matrix3D translation, Matrix3D rotation, Matrix3D scale) {
        this.type = type;
        this.ID = ID;
        this.translation = translation;
        this.rotation = rotation;
        this.scale = scale;
    }

    public double getX() {
        return location.getX();
    }

    public double getY() {
        return location.getY();
    }

    public double getZ() {
        return location.getZ();
    }

    public Point3D getLocation() {
        return location;
    }

    public String getType() {
        return type;
    }

    public int getID() {
        return ID;
    }

    public Matrix3D getTranslation() {
        return translation;
    }

    public Matrix3D getRotation() {
        return rotation;
    }

    public Matrix3D getScale() {
        return scale;
    }

    public void updateLocation() {
        //update NPC location here
    }


}
