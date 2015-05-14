package networking.packets.lobby;

import networking.packets.IPacket;

/**
 * Created by Max on 3/31/2015.
 */
public class StartGamePacket implements IPacket {
    int playerAmount;

    public StartGamePacket(int i) {
        playerAmount = i;
    }

    public int getPlayerAmount() {
        return playerAmount;
    }
}
