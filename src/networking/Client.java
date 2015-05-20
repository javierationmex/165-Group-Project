package networking;

import game.SpaceRace;
import game.Player;
import networking.packets.GamePlayerInfoPacket;
import networking.packets.ServerPlayerInfoPacket;
import networking.packets.ingame.AllPlayerInfoPacket;
import networking.packets.ingame.NPCPacket;
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
    private SpaceRace spaceRace;

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
            spaceRace.addGhostAvatar(((GamePlayerInfoPacket) packet).getPlayer());
        }

        if(packet instanceof AllPlayerInfoPacket){
            spaceRace.updateGhostAvatars(((AllPlayerInfoPacket) packet));
        }

        if(packet instanceof NPCPacket){
            spaceRace.updateNPCGhosts((NPCPacket) packet);
        }
    }

    public void sendJoinPacket() {
        try {
            sendPacket(new JoinPacket(this.id, player.getPlayerName()));
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public SpaceRace getSpaceRace() {
        return spaceRace;
    }

    public void setSpaceRace(SpaceRace spaceRace) {
        this.spaceRace = spaceRace;
    }

    public UUID getId() {
        return id;
    }
}
