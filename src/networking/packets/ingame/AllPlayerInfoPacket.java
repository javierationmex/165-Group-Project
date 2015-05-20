package networking.packets.ingame;

import networking.packets.IPacket;
import swingmenus.multiplayer.data.SimplePlayerInfo;

import java.util.ArrayList;

/**
 * Created by Max on 5/19/2015.
 */
public class AllPlayerInfoPacket implements IPacket {

    private ArrayList<SimplePlayerInfo> simple;

    public AllPlayerInfoPacket(ArrayList<SimplePlayerInfo> simple) {
        this.simple = simple;
    }

    public ArrayList<SimplePlayerInfo> getSimple() {
        return simple;
    }
}
