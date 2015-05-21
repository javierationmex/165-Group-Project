package networking.packets;

/**
 * Created by Max on 5/20/2015.
 */
public class EndScore implements IPacket {
    private String playerName;
    private int score;

    public EndScore(String playerName, int score) {
        this.playerName = playerName;
        this.score = score;
    }

    public String getPlayerName() {
        return playerName;
    }

    public int getScore() {
        return score;
    }
}
