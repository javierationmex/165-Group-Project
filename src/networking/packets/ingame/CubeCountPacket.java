package networking.packets.ingame;

import networking.packets.IPacket;

/**
 * Created by Max on 5/20/2015.
 */
public class CubeCountPacket implements IPacket {
    private int cubeCount;

    public CubeCountPacket(int cubeCount) {
        this.cubeCount = cubeCount;
    }

    public int getCubeCount() {
        return cubeCount;
    }
}
