package networking.packets.lobby;

import networking.packets.IPacket;

import java.util.UUID;

/**
 * Created by Max on 3/25/2015.
 */
public class ChangeCharacterPacket implements IPacket {
    private int newCharacterID;
    private UUID clientID;
    private boolean ready;

    public ChangeCharacterPacket(UUID clientID) {
        this.clientID = clientID;
    }

    public int getNewCharacterID() {
        return newCharacterID;
    }

    public void setNewCharacterID(int newCharacterID) {
        this.newCharacterID = newCharacterID;
    }

    public UUID getClientID() {
        return clientID;
    }

    public void setClientID(UUID clientID) {
        this.clientID = clientID;
    }

    public boolean isReady() {
        return ready;
    }

    public void setReady(boolean ready) {
        this.ready = ready;
    }
}
