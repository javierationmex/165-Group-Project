package game.characters;

import networking.packets.IPacket;
import sage.scene.shape.Cube;

/**
 * Created by Max on 3/31/2015.
 */
public class CustomCube extends Cube implements IPacket {
    public CustomCube(String name) {
        super(name);
    }
}
