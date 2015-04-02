package networking.packets.ingame;

import networking.packets.IPacket;
import sage.scene.SceneNode;

import java.util.UUID;


/**
 * Created by Max on 3/31/2015.
 */
public class UpdateAvatarLocationInformationPacket implements IPacket {
    private float x;
    private float y;
    private float z;
    private UUID clientID;

    public UpdateAvatarLocationInformationPacket(UUID clientID, float x, float y, float z) {
        this.clientID = clientID;
        this.x = x;
        this.y = y;
        this.z = z;
    }

    public UUID getClientID() {
        return clientID;
    }

    public float getX() {
        return x;
    }

    public float getY() {
        return y;
    }

    public float getZ() {
        return z;
    }
}
