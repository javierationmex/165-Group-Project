package Networking;

import Networking.packets.IPacket;
import Networking.packets.JoinPacket;
import game.Player;
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

    public Client(InetAddress remoteAddr, int remotePort, ProtocolType protocolType, Player player) throws IOException {
        super(remoteAddr, remotePort, protocolType);
        this.id = UUID.randomUUID();
        this.ghostAvatars = new ArrayList<TriMesh>();
        this.player = player;
        this.player.setPlayerUUID(this.id);
    }

    @Override
    protected void processPacket(Object packet) {
        System.out.println("In Client Processing Packets");
        if(packet instanceof JoinPacket){
            if(((JoinPacket) packet).isSuccess()){
                System.out.println("Joined the game.");
                player.setPlayerInLobby(true);
            }else{
                System.out.println("failed");
            }
        }
    }

    public void sendJoinPacket(){
        try {
            sendPacket(new JoinPacket(this.id, player.getPlayerName()));
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
