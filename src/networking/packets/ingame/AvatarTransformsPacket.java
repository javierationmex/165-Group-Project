package networking.packets.ingame;

import graphicslib3D.Matrix3D;
import networking.packets.IPacket;

import java.util.UUID;

/**
 * Created by Max on 5/14/2015.
 */
public class AvatarTransformsPacket implements IPacket {
    private UUID clientID;
    private Matrix3D localRotation, localTranslation;

    public AvatarTransformsPacket(UUID clientID, Matrix3D localRotation, Matrix3D localTranslation) {
        this.clientID = clientID;
        this.localRotation = localRotation;
        this.localTranslation = localTranslation;
    }

    public UUID getClientID() {
        return clientID;
    }

    public Matrix3D getLocalRotation() {
        return localRotation;
    }

    public Matrix3D getLocalTranslation() {
        return localTranslation;
    }
}
