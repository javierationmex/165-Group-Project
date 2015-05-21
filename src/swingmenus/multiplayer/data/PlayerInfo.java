package swingmenus.multiplayer.data;

import graphicslib3D.Matrix3D;
import networking.packets.IPacket;
import sage.scene.SceneNode;

import java.util.UUID;

/**
 * Created by Max on 3/24/2015.
 */
public class PlayerInfo implements IPacket {
    private UUID clientID;
    private String playerName;
    private int characterID = 0, score;
    private boolean ready;
    private SceneNode avatar;
    private boolean loaded, finished = false;
    private Matrix3D scale, rotation, translation;

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

    public Matrix3D getScale() {
        return scale;
    }

    public void setScale(Matrix3D scale) {
        this.scale = scale;
    }

    public Matrix3D getRotation() {
        return rotation;
    }

    public void setRotation(Matrix3D rotation) {
        this.rotation = rotation;
    }

    public Matrix3D getTranslation() {
        return translation;
    }

    public void setTranslation(Matrix3D translation) {
        this.translation = translation;
    }

    public boolean isLoaded() {
        return loaded;
    }

    public void setLoaded(boolean loaded) {
        this.loaded = loaded;
    }

    public SimplePlayerInfo getSimplePlayerInfo(){
        return new SimplePlayerInfo(clientID, scale,rotation,translation);
    }

    public boolean isFinished() {
        return finished;
    }

    public void setFinished(boolean finished) {
        this.finished = finished;
    }

    public int getScore() {
        return score;
    }

    public void setScore(int score) {
        this.score = score;
    }
}
