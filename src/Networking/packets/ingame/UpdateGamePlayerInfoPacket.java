package networking.packets.ingame;

import networking.packets.IPacket;
import swingmenus.multiplayer.data.PlayerInfo;

/**
 * Created by Max on 3/31/2015.
 */
public class UpdateGamePlayerInfoPacket implements IPacket {
    private PlayerInfo player;

    public UpdateGamePlayerInfoPacket(PlayerInfo player) {
        this.player = player;
    }

    public PlayerInfo getPlayer() {
        return player;
    }
}
