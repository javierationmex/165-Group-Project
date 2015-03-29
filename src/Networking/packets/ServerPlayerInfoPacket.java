package Networking.packets;

import swingMenus.Multiplayer.data.PlayerInfo;

import java.util.ArrayList;

/**
 * Created by Max on 3/25/2015.
 */
public class ServerPlayerInfoPacket implements LobbyPacket {
    private ArrayList<PlayerInfo> players;

    public ServerPlayerInfoPacket(ArrayList<PlayerInfo> players) {
        this.players = players;
    }

    public ArrayList<PlayerInfo> getPlayers() {
        return players;
    }
}
