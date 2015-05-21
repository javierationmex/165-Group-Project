package game.characters;

import networking.packets.IPacket;
import sage.scene.shape.Cube;

/**
 * Created by Max on 3/31/2015.
 */
public class CustomCube extends Cube implements IPacket {
    private int ID;
    public CustomCube(String name, int ID) {
        super(name);
        this.ID = ID;
    }

    public int getID() {
        return ID;
    }
}
