package Networking.packets;

import java.util.UUID;

/**
 * Created by Max on 3/23/2015.
 */
public class LeavePacket implements IPacket{
    private UUID ghostID;

    public LeavePacket(UUID ghostID) {
        this.ghostID = ghostID;
    }

    public UUID getGhostID() {
        return ghostID;
    }
}
