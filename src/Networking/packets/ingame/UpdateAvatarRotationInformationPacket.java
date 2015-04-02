package networking.packets.ingame;

import graphicslib3D.Vector3D;
import networking.packets.IPacket;
import sage.scene.SceneNode;

import java.util.UUID;


/**
 * Created by Max on 3/31/2015.
 */
public class UpdateAvatarRotationInformationPacket implements IPacket {
    private final Vector3D axis;
    private float x;
    private UUID clientID;

    public UpdateAvatarRotationInformationPacket(UUID clientID, float x, Vector3D axis) {
        this.clientID = clientID;
        this.x = x;
        this.axis = axis;
    }

    public UUID getClientID() {
        return clientID;
    }

    public float getX() {
        return x;
    }

    public Vector3D getAxis() {
        return axis;
    }
}
