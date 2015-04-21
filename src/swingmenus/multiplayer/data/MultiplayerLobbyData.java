package swingmenus.multiplayer.data;

import game.Player;
import swingmenus.multiplayer.MultiplayerLobby;

import java.util.ArrayList;

public class MultiplayerLobbyData {
    private final MultiplayerLobby lobby;
    private String serverIPAddress;
    private int serverPortNumber;
    private String playerName = "Default Name";
    private ArrayList<Player> players;

    public MultiplayerLobbyData(MultiplayerLobby lobby) {
        this.lobby = lobby;
        this.players = new ArrayList<Player>();
    }

    public String getServerIPAddress() {
        return serverIPAddress;
    }

    public void setServerIPAddress(final String serverIPAddress) {
        this.serverIPAddress = serverIPAddress;
        this.lobby.setUseDefaultCheckBoxChecked(false);
    }

    public int getServerPortNumber() {
        return serverPortNumber;
    }

    public void setServerPortNumber(final int serverPortNumber) {
        this.serverPortNumber = serverPortNumber;
        this.lobby.setUseDefaultCheckBoxChecked(false);
    }

    public String getPlayerName() {
        return playerName;
    }

    public void setPlayerName(final String playerName) {
        this.playerName = playerName;
    }

    public void addPlayer(Player player) {
        this.players.add(player);
    }

    public void removePlayer(Player player) {
        this.players.remove(player);
    }
}