package gameengine.player;

import graphicslib3D.Matrix3D;
import graphicslib3D.Vector3D;
import net.java.games.input.Event;
import networking.Client;
import sage.physics.IPhysicsObject;
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
    private IPhysicsObject playerAvatarP;

    public MovePlayerBackwardAction(SceneNode n, TerrainBlock imageTerrain, Client client, IPhysicsObject playerAvatarP) {
        avatar = n;
        this.client = client;
        this.playerAvatarP = playerAvatarP;
        terrain = imageTerrain;
    }

    public void performAction(float time, Event e){


        Matrix3D rot = avatar.getLocalRotation();
        Vector3D dir = new Vector3D(0, 0, 1);
        dir = dir.mult(rot);
        dir.scale((double) (speed * time));

        float[] f = playerAvatarP.getLinearVelocity();
        f[0] = 0;
        f[2] = 0;
        playerAvatarP.setLinearVelocity(f);
    }

}