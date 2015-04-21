package networking.packets.ingame;

import networking.packets.IPacket;
import sage.scene.SceneNode;

import java.util.UUID;

/**
 * Created by Max on 3/31/2015.
 */
public class AddAvatarInformationPacket implements IPacket {
    private UUID clientID;
    private int avatarID;

    public AddAvatarInformationPacket(UUID clientID, int avatarID) {
        this.clientID = clientID;
        this.avatarID = avatarID;
    }

    public UUID getClientID() {
        return clientID;
    }

    public int getAvatarID() {
        return avatarID;
    }
}
