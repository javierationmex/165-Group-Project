package Networking.packets;

import java.util.UUID;

/**
 * Created by Max on 3/25/2015.
 */
public class ChangeCharacterPacket implements LobbyPacket {
    private int newCharacterID;
    private UUID clientID;

    public ChangeCharacterPacket(UUID clientID, int newCharacterID) {
        this.newCharacterID = newCharacterID;
        this.clientID = clientID;
    }

    public int getNewCharacterID() {
        return newCharacterID;
    }

    public UUID getClientID() {
        return clientID;
    }
}
