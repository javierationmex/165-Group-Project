package game;

import Networking.Client;
import sage.networking.IGameConnection;
import swingMenus.Base.BaseCharacterSelectionMenu;

import java.io.IOException;
import java.net.InetAddress;
import java.util.UUID;

/**
 * Created by Max on 3/23/2015.
 */
public class Player {
    private String playerName;
    private UUID playerUUID;
    private int characterID;
    private Client client;
    private boolean playerInLobby = false;
    private boolean isHost;
    private BaseCharacterSelectionMenu baseCharacterSelectionMenu;

    public Player(String playerName) {
        this.playerName = playerName;
    }

    public String getPlayerName() {
        return playerName;
    }

    public UUID getPlayerUUID() {
        return playerUUID;
    }

    public void setPlayerUUID(UUID playerUUID) {
        this.playerUUID = playerUUID;
    }

    public void createLinkedClient(InetAddress remoteAddr, int remotePort){
        if(remoteAddr != null){
            try {
                client = new Client(remoteAddr,remotePort, IGameConnection.ProtocolType.TCP, this);
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        if(client != null){
            client.sendJoinPacket();
        }
    }

    public boolean isPlayerInLobby() {
        return playerInLobby;
    }

    public void setPlayerInLobby(boolean playerInLobby) {
        this.playerInLobby = playerInLobby;
    }

    public boolean isHost() {
        return isHost;
    }

    public void setHost(boolean isHost) {
        this.isHost = isHost;
    }

    public boolean processPackets(){
        client.processPackets();
        return playerInLobby;
    }

    public BaseCharacterSelectionMenu getBaseCharacterSelectionMenu() {
        return baseCharacterSelectionMenu;
    }

    public void setBaseCharacterSelectionMenu(BaseCharacterSelectionMenu baseCharacterSelectionMenu) {
        this.baseCharacterSelectionMenu = baseCharacterSelectionMenu;
    }

    public Client getClient() {
        return client;
    }


}
