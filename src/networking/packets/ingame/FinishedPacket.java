package networking.packets.ingame;

import networking.packets.IPacket;

import java.util.UUID;

/**
 * Created by Max on 5/20/2015.
 */
public class FinishedPacket implements IPacket {
    private int score;
    private UUID clientID;
    private String playerName;

    public FinishedPacket(int score, UUID clientID, String playerName) {
        this.score = score;
        this.clientID = clientID;
        this.playerName = playerName;
    }

    public int getScore() {
        return score;
    }

    public UUID getClientID() {
        return clientID;
    }

    public String getPlayerName() {
        return playerName;
    }
}

