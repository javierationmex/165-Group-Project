package networking.packets.ingame;

import networking.packets.IPacket;

import java.util.UUID;

/**
 * Created by Max on 5/14/2015.
 */
public class GetAvatarInformationPacket implements IPacket{
    private UUID clientID;

    public GetAvatarInformationPacket(UUID clientID) {
        this.clientID = clientID;
    }

    public UUID getClientID() {
        return clientID;
    }
}
