package swingmenus.multiplayer.data;

import networking.packets.IPacket;
import sage.scene.SceneNode;

import java.util.UUID;

/**
 * Created by Max on 3/24/2015.
 */
public class PlayerInfo implements IPacket {
    private UUID clientID;
    private String playerName;
    private int characterID = 0;
    private boolean ready;
    private SceneNode avatar;
    private boolean loaded;

    public PlayerInfo(UUID clientID, String playerName) {
        this.clientID = clientID;
        this.playerName = playerName;
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

    public void setCharacterID(int characterID) {
        this.characterID = characterID;
    }

    public boolean isReady() {
        return ready;
    }

    public void setReady(boolean ready) {
        this.ready = ready;
    }

    public SceneNode getAvatar() {
        return avatar;
    }

    public void setAvatar(SceneNode avatar) {
        this.avatar = avatar;
    }



    public boolean isLoaded() {
        return loaded;
    }

    public void setLoaded(boolean loaded) {
        this.loaded = loaded;
    }
}
