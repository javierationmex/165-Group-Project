package gameengine.player;

import graphicslib3D.Matrix3D;
import graphicslib3D.Vector3D;
import net.java.games.input.Event;
import networking.Client;
import sage.physics.IPhysicsObject;
import sage.scene.SceneNode;
import sage.terrain.TerrainBlock;


public class MovePlayerLeftAction extends BaseAbstractInputAction {

    private final Client client;
    private SceneNode avatar;
    private IPhysicsObject playerAvatarP;
    private float speed = 0.05f;
    private TerrainBlock terrain;


    public MovePlayerLeftAction(SceneNode n, TerrainBlock imageTerrain, Client client, IPhysicsObject playerAvatarP) {
        avatar = n;
        this.client = client;
        terrain = imageTerrain;
        this.playerAvatarP = playerAvatarP;
    }

    public void performAction(float time, Event e){


        Matrix3D rot = avatar.getLocalRotation();
        Vector3D dir = new Vector3D(-1,0,0);
        dir = dir.mult(rot);
        dir.scale((double) (speed * time));


        float[] f = playerAvatarP.getLinearVelocity();
        f[0] += (float) dir.getX() * 20;
        f[2] += (float) dir.getZ() * 20;
        playerAvatarP.setLinearVelocity(f);
    }

}