package swingMenus.Multiplayer.data;

import Networking.packets.IPacket;

import java.util.UUID;

/**
 * Created by Max on 3/24/2015.
 */
public class PlayerInfo implements IPacket{
    private UUID clientID;
    private String playerName;
    private int characterID = 0;

    public PlayerInfo(UUID clientID, String playerName) {
        this.clientID = clientID;
        this.playerName = playerName;
    }

    public void setCharacterID(int characterID) {
        this.characterID = characterID;
    }

    public UUID getClientID() {

        return clientID;
    }

    public String getPlayerName() {
        return playerName;
    }

    public int getCharacterID() {
        return characterID;
    }
}
