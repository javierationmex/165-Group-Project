package networking.packets.ingame;

import networking.packets.EndScore;
import networking.packets.IPacket;

import java.util.ArrayList;

/**
 * Created by Max on 5/20/2015.
 */
public class AllFinishedPacket implements IPacket {
    ArrayList<EndScore> endScores;

    public AllFinishedPacket(ArrayList<EndScore> endScores) {
        this.endScores = endScores;
    }

    public ArrayList<EndScore> getEndScores() {
        return endScores;
    }
}
