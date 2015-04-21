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
public class MovePlayerForwardAction extends BaseAbstractInputAction {

    private final Client client;
    private SceneNode avatar;
    private float speed = 0.01f;
    private TerrainBlock terrain;

    public MovePlayerForwardAction(SceneNode n, TerrainBlock imageTerrain, Client client) {
        avatar = n;
        this.client = client;
        terrain = imageTerrain;
    }

    public void performAction(float time, Event e){
        Matrix3D rot = avatar.getLocalRotation();
        Vector3D dir = new Vector3D(0, 0, -1);
        dir = dir.mult(rot);
        dir.scale((double) (speed * time));
        avatar.translate((float) dir.getX(), (float) dir.getY(), (float) dir.getZ());
        sendUpdateLocationPacket(client, avatar.getLocalTranslation(), avatar.getLocalScale(), avatar.getLocalRotation());

        Point3D avLoc = new Point3D(avatar.getLocalTranslation().getCol(3));
        float x = (float) avLoc.getX();
        float z = (float) avLoc.getZ();
        float terHeight = terrain.getHeight(x, z);
        Point3D terrainPoint = new Point3D(x, terHeight - 5, z);


        if (avatar.getWorldBound().contains(terrainPoint)) {
            avatar.translate(-(float) dir.getX(), (float) dir.getY(), -(float) dir.getZ());
        }


    }

}