package networking.packets.ingame;

import networking.packets.IPacket;
import sage.scene.SceneNode;

import java.util.UUID;

/**
 * Created by Max on 3/31/2015.
 */
public class AddAvatarInformationPacket implements IPacket {
    private UUID clientID;
    private SceneNode avatar;

    public AddAvatarInformationPacket(UUID clientID, SceneNode avatar) {
        this.clientID = clientID;
        this.avatar = avatar;
    }

    public UUID getClientID() {
        return clientID;
    }

    public SceneNode getAvatar() {
        return avatar;
    }
}
