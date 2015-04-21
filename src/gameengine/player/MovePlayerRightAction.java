package gameengine.player;

import graphicslib3D.Matrix3D;
import graphicslib3D.Point3D;
import graphicslib3D.Vector3D;
import net.java.games.input.Event;
import networking.Client;
import sage.scene.SceneNode;
import sage.terrain.TerrainBlock;

/**
 * Created by Max on 3/8/2015.
 */
public class MovePlayerRightAction extends BaseAbstractInputAction {

    private final Client client;
    private SceneNode avatar;
    private float speed = 0.01f;
    private TerrainBlock terrain;


    public MovePlayerRightAction(SceneNode n, TerrainBlock imageTerrain, Client client) {
        avatar = n;
        this.client = client;
        terrain = imageTerrain;
    }

    public void performAction(float time, Event e){
        Point3D avLoc = new Point3D(avatar.getLocalTranslation().getCol(3));
        float x = (float) avLoc.getX();
        float z = (float) avLoc.getZ();
        float terHeight = terrain.getHeight(x, z);
        Point3D terrainPoint = new Point3D(x, terHeight, z);
        Matrix3D rot = avatar.getLocalRotation();
        Vector3D dir = new Vector3D(1,0,0);
        if (avatar.getWorldBound().contains(terrainPoint)) dir = new Vector3D(-1, 0, 0);
        dir = dir.mult(rot);
        dir.scale((double)(speed * time));
        avatar.translate((float) dir.getX(), (float) dir.getY(), (float) dir.getZ());
    }

}