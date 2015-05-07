package gameengine.player;

import graphicslib3D.Matrix3D;
import graphicslib3D.Vector3D;
import net.java.games.input.Event;
import networking.Client;
import sage.scene.SceneNode;
import sage.terrain.TerrainBlock;


/**
 * Created by Max on 3/8/2015.
 */
public class MovePlayerBackwardAction extends BaseAbstractInputAction {

    private final Client client;
    private SceneNode avatar;
    private float speed = 0.05f;
    private TerrainBlock terrain;

    public MovePlayerBackwardAction(SceneNode n, TerrainBlock imageTerrain, Client client) {
        avatar = n;
        this.client = client;
        terrain = imageTerrain;
    }

    public void performAction(float time, Event e){


        Matrix3D rot = avatar.getLocalRotation();
        Vector3D dir = new Vector3D(0, 0, 1);
        dir = dir.mult(rot);
        dir.scale((double) (speed * time));
        Vector3D projectedDirection = new Vector3D(0, 0, 1);
        projectedDirection = projectedDirection.mult(rot);
        projectedDirection.scale(2);
        projectedDirection.add(avatar.getWorldTranslation().getCol(3));

        float projectedx = (float) projectedDirection.getX();
        float projectedy = (float) projectedDirection.getX();
        float projectedz = (float) projectedDirection.getX();
        float terrainHeight = terrain.getHeight(projectedx, projectedz);
        if (projectedy > terrainHeight) {
            avatar.translate((float) dir.getX(), (float) dir.getY(), (float) dir.getZ());
        }
    }

}