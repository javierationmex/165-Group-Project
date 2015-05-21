package networking.packets.ingame;

import networking.packets.IPacket;

/**
 * Created by Max on 5/20/2015.
 */
public class CubeLocationsPacket implements IPacket {
    private float[] cubeLocations;

    public CubeLocationsPacket(float[] cubeLocations) {
        this.cubeLocations = cubeLocations;
    }

    public float[] getCubeLocations() {
        return cubeLocations;
    }
}
