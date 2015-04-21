package networking.packets;

import swingmenus.multiplayer.data.PlayerInfo;

/**
 * Created by Max on 3/25/2015.
 */
public class GamePlayerInfoPacket implements IPacket {
    private PlayerInfo player;

    public GamePlayerInfoPacket(PlayerInfo player) {
        this.player = player;
    }

    public PlayerInfo getPlayer() {
        return player;
    }
}
