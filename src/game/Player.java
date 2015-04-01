package game;

import networking.Client;
import networking.packets.lobby.ChangeCharacterPacket;
import networking.packets.lobby.StartGamePacket;
import sage.networking.IGameConnection;
import swingmenus.base.CharacterSelectionScreen;
import swingmenus.multiplayer.data.PlayerInfo;

import javax.swing.*;
import java.io.IOException;
import java.net.InetAddress;
import java.util.ArrayList;
import java.util.Timer;
import java.util.TimerTask;
import java.util.UUID;

/**
 * Created by Max on 3/23/2015.
 */
public class Player {
    private String playerName;
    private UUID playerUUID;
    private int characterID = 0;
    private boolean ready = false;
    private Client client;
    private boolean isHost;
    private CharacterSelectionScreen characterSelectionScreen;
    private Timer timer;
    private ProcessPackets processPackets;
    private JFrame frame;
    private boolean inGame = false;
    private MazeGame game;

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

    public void createLinkedClient(InetAddress remoteAddr, int remotePort) {
        if (remoteAddr != null) {
            try {
                client = new Client(remoteAddr, remotePort, IGameConnection.ProtocolType.TCP, this);
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        if (client != null) {
            client.sendJoinPacket();
            processPackets = new ProcessPackets(this);
            timer = new Timer();
            timer.schedule(processPackets, 0, 100);
        }
    }

    public boolean isHost() {
        return isHost;
    }

    public void setHost(boolean isHost) {
        this.isHost = isHost;
    }

    public void processPackets() {
        if(!inGame){
            client.processPackets();
        }
    }

    public CharacterSelectionScreen getCharacterSelectionScreen() {
        return characterSelectionScreen;
    }

    public void setCharacterSelectionScreen(CharacterSelectionScreen characterSelectionScreen) {
        this.characterSelectionScreen = characterSelectionScreen;
    }

    public Client getClient() {
        return client;
    }

    public void updateCharacterSelectionScreen(ArrayList<PlayerInfo> players){
        this.characterSelectionScreen.updatePlayerList(players);
    }

    public void sendUpdatePacket() {
        ChangeCharacterPacket changeCharacterPacket = new ChangeCharacterPacket(this.playerUUID);
        changeCharacterPacket.setNewCharacterID(characterID);
        changeCharacterPacket.setReady(ready);
        try {
            client.sendPacket(changeCharacterPacket);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public boolean isReady() {
        return ready;
    }

    public void setReady(boolean ready) {
        this.ready = ready;
    }

    public int getCharacterID() {
        return characterID;
    }

    public void setCharacterID(int characterID) {
        this.characterID = characterID;
    }

    public void sendStartGamePacket() {
        try {
            client.sendPacket(new StartGamePacket());
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void startGame() {
        frame.setVisible(false);
        timer.cancel();
        game = new MazeGame(this);
        inGame = true;
    }

    public void setFrame(JFrame frame) {
        this.frame = frame;
    }

    public MazeGame getGame() {
        return game;
    }

    public boolean isInGame() {
        return inGame;
    }
}

class ProcessPackets extends TimerTask {
    private Player player;
    public ProcessPackets(Player player) {
        this.player = player;
    }

    /**
     * The action to be performed by this timer task.
     */
    @Override
    public void run() {
        player.processPackets();
        if(player.isInGame()){
            player.getGame().start();
        }
    }

}
