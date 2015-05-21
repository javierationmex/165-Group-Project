package networking.packets.ingame;

import networking.packets.IPacket;

/**
 * Created by Max on 5/20/2015.
 */
public class HitCubePacket implements IPacket {
    private int ID;

    public HitCubePacket(int ID) {
        this.ID = ID;
    }

    public int getID() {
        return ID;
    }
}
