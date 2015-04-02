package networking;

import game.MazeGame;
import game.Player;
import networking.packets.GamePlayerInfoPacket;
import networking.packets.ServerPlayerInfoPacket;
import networking.packets.ingame.UpdateAvatarLocationInformationPacket;
import networking.packets.ingame.UpdateAvatarRotationInformationPacket;
import networking.packets.lobby.JoinPacket;
import networking.packets.lobby.StartGamePacket;
import sage.networking.client.GameConnectionClient;
import sage.scene.TriMesh;

import java.io.IOException;
import java.net.InetAddress;
import java.util.ArrayList;
import java.util.UUID;

/**
 * Created by Max on 3/23/2015.
 */
public class Client extends GameConnectionClient {

    private UUID id;
    private ArrayList<TriMesh> ghostAvatars;
    private Player player;
    private MazeGame mazeGame;

    public Client(InetAddress remoteAddr, int remotePort, ProtocolType protocolType, Player player) throws IOException {
        super(remoteAddr, remotePort, protocolType);
        this.id = UUID.randomUUID();
        this.ghostAvatars = new ArrayList<TriMesh>();
        this.player = player;
        this.player.setPlayerUUID(this.id);
    }

    @Override
    protected void processPacket(Object packet) {
        if (packet instanceof JoinPacket) {
            if (((JoinPacket) packet).isSuccess()) {
                System.out.println("Joined the game.");
            } else {
                System.out.println("failed");
            }
        }
        if(packet instanceof ServerPlayerInfoPacket){
            System.out.println("Updating Character Selection Screen.");
            player.updateCharacterSelectionScreen(((ServerPlayerInfoPacket) packet).getPlayers());
        }

        if(packet instanceof StartGamePacket){
            player.startGame();
        }

        if(packet instanceof GamePlayerInfoPacket){
            mazeGame.addGhostAvatar(((GamePlayerInfoPacket) packet).getPlayer());
        }

        if(packet instanceof UpdateAvatarLocationInformationPacket){
            mazeGame.updateGhostAvatar(((UpdateAvatarLocationInformationPacket) packet));
        }

        if(packet instanceof UpdateAvatarRotationInformationPacket){
            mazeGame.updateGhostAvatar(((UpdateAvatarRotationInformationPacket) packet));
        }
    }

    public void sendJoinPacket() {
        try {
            sendPacket(new JoinPacket(this.id, player.getPlayerName()));
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public MazeGame getMazeGame() {
        return mazeGame;
    }

    public void setMazeGame(MazeGame mazeGame) {
        this.mazeGame = mazeGame;
    }

    public UUID getId() {
        return id;
    }
}
