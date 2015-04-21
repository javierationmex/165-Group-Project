package networking.packets.ingame;

import networking.packets.IPacket;

/**
 * Created by Max on 3/31/2015.
 */
public class AllReadyPacket implements IPacket {
    boolean ready;

    public AllReadyPacket(boolean ready) {
        this.ready = ready;
    }

    public boolean isReady() {
        return ready;
    }
}
