package networking.packets.ingame;

        import graphicslib3D.Matrix3D;
        import networking.packets.IPacket;

        import java.util.UUID;


/**
 * Created by Max on 3/31/2015.
 */
public class UpdateAvatarInfoPacket implements IPacket {
    private Matrix3D translation;
    private Matrix3D scale;
    private Matrix3D rotation;
    private UUID clientID;

    public UpdateAvatarInfoPacket(UUID clientID, Matrix3D translation, Matrix3D scale, Matrix3D rotation) {
        this.clientID = clientID;
        this.translation = translation;
        this.scale = scale;
        this.rotation = rotation;
    }

    public UUID getClientID() {
        return clientID;
    }

    public Matrix3D getTranslation() {
        return translation;
    }

    public Matrix3D getScale() {
        return scale;
    }

    public Matrix3D getRotation() {
        return rotation;
    }
}
