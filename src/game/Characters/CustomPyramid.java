package game.characters;

import networking.packets.IPacket;
import sage.scene.shape.Pyramid;

/**
 * Created by Max on 3/31/2015.
 */
public class CustomPyramid extends Pyramid implements IPacket {

    public CustomPyramid(String name) {
        super(name);
    }
}
