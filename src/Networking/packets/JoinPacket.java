package Networking.packets;

import game.Player;

import java.util.UUID;

/**
 * Created by Max on 3/23/2015.
 */
public class JoinPacket implements LobbyPacket{
    private boolean success = false;
    private UUID clientID;
    private String playerName;
    private Player player;

    public JoinPacket(UUID clientID, String playerName) {
        this.clientID = clientID;
        this.playerName = playerName;
    }

    public boolean isSuccess() {
        return success;
    }

    public void setSuccess(boolean success) {
        this.success = success;
    }

    public UUID getClientID() {
        return clientID;
    }

    public String getPlayerName() {
        return playerName;
    }
}
