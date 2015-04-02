package gameengine.player;

import graphicslib3D.Matrix3D;
import graphicslib3D.Vector3D;
import net.java.games.input.Event;
import networking.Client;
import sage.scene.SceneNode;

/**
 * Created by Max on 3/8/2015.
 */
public class MovePlayerRightAction extends BaseAbstractInputAction {

    private SceneNode avatar;
    private float speed = 0.01f;
    private final Client client;


    public MovePlayerRightAction(SceneNode n, Client client){
        avatar = n;
        this.client = client;
    }

    public void performAction(float time, Event e){
        Matrix3D rot = avatar.getLocalRotation();
        Vector3D dir = new Vector3D(1,0,0);
        dir = dir.mult(rot);
        dir.scale((double)(speed * time));
        avatar.translate((float) dir.getX(), (float) dir.getY(), (float) dir.getZ());
        sendUpdateLocationPacket(client, (float) dir.getX(), (float) dir.getY(), (float) dir.getZ());
    }

}