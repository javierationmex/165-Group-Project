package Networking;

import Networking.packets.JoinPacket;
import Networking.packets.ServerPlayerInfoPacket;
import sage.networking.server.GameConnectionServer;
import sage.networking.server.IClientInfo;
import swingMenus.Multiplayer.data.PlayerInfo;

import java.io.IOException;
import java.net.InetAddress;
import java.util.ArrayList;
import java.util.UUID;

/**
 * Created by Max on 3/22/2015.
 */
public class Server extends GameConnectionServer<UUID> {

    private ArrayList<PlayerInfo> players;

    public Server(int localPort) throws IOException{
        super(localPort, ProtocolType.TCP);
        players = new ArrayList<PlayerInfo>();
    }

    @Override
    public void acceptClient(IClientInfo clientInfo, Object firstPacket) {
        if(firstPacket instanceof JoinPacket){
            this.addClient(clientInfo, ((JoinPacket) firstPacket).getClientID());
            this.addPlayer(new PlayerInfo(((JoinPacket) firstPacket).getClientID(),((JoinPacket) firstPacket).getPlayerName()));
            ((JoinPacket) firstPacket).setSuccess(true);
            System.out.println("Server accepted Client.");
            try {
                this.sendPacket((JoinPacket) firstPacket, ((JoinPacket) firstPacket).getClientID());
                this.sendPacketToAll(new ServerPlayerInfoPacket(players));
                System.out.println("Server sent updated PlayerInfo to all Clients.");
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    private void addPlayer(PlayerInfo player){
        this.players.add(player);
    }

    @Override
    public void processPacket(Object object, InetAddress senderIP, int senderPort) {
        System.out.println("In Server Processing Packets");

    }
}
