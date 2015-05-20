package networking.packets.ingame;

import gameengine.NPC.NPC;
import networking.packets.IPacket;

import java.util.ArrayList;

/**
 * Created by Max on 5/16/2015.
 */
public class NPCPacket implements IPacket {
    ArrayList<NPC> npcs;

    public NPCPacket(ArrayList<NPC> npcs) {
        this.npcs = npcs;
    }

    public ArrayList<NPC> getNpcs() {
        return npcs;
    }
}
