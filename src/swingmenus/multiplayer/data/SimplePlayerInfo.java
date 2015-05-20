package swingmenus.multiplayer.data;

import graphicslib3D.Matrix3D;
import networking.packets.IPacket;

import java.util.UUID;

/**
 * Created by Max on 5/19/2015.
 */
public class SimplePlayerInfo implements IPacket {
    private UUID clientID;
    private Matrix3D scale, rotation, translation;

    public SimplePlayerInfo(UUID clientID, Matrix3D scale, Matrix3D rotation, Matrix3D translation) {
        this.clientID = clientID;
        this.scale = scale;
        this.rotation = rotation;
        this.translation = translation;
    }

    public UUID getClientID() {
        return clientID;
    }

    public Matrix3D getScale() {
        return scale;
    }

    public Matrix3D getRotation() {
        return rotation;
    }

    public Matrix3D getTranslation() {
        return translation;
    }
}
