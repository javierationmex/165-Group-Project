package networking;

import networking.packets.GamePlayerInfoPacket;
import networking.packets.ServerPlayerInfoPacket;
import networking.packets.ingame.*;
import networking.packets.lobby.ChangeCharacterPacket;
import networking.packets.lobby.JoinPacket;
import networking.packets.lobby.StartGamePacket;
import sage.networking.server.GameConnectionServer;
import sage.networking.server.IClientInfo;
import swingmenus.multiplayer.data.PlayerInfo;

import java.io.IOException;
import java.net.InetAddress;
import java.util.ArrayList;
import java.util.UUID;

/**
 * Created by Max on 3/22/2015.
 */
public class Server extends GameConnectionServer<UUID> {

    private ArrayList<PlayerInfo> players;
    private boolean gameStarted = false;

    public Server(int localPort) throws IOException {
        super(localPort, ProtocolType.TCP);
        players = new ArrayList<PlayerInfo>();
    }

    @Override
    public void acceptClient(IClientInfo clientInfo, Object firstPacket) {
        if(!gameStarted){
            if (firstPacket instanceof JoinPacket) {
                this.addClient(clientInfo, ((JoinPacket) firstPacket).getClientID());
                this.addPlayer(new PlayerInfo(((JoinPacket) firstPacket).getClientID(), ((JoinPacket) firstPacket).getPlayerName()));
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
        }else{
            System.out.println("Sorry game has already started.");
        }
    }

    private void addPlayer(PlayerInfo player) {
        this.players.add(player);
    }

    @Override
    public void processPacket(Object packet, InetAddress senderIP, int senderPort) {
        if(packet instanceof ChangeCharacterPacket){
            UUID id = ((ChangeCharacterPacket) packet).getClientID();
            int characterID = ((ChangeCharacterPacket) packet).getNewCharacterID();
            boolean ready = ((ChangeCharacterPacket) packet).isReady();
            for(PlayerInfo p : players){
                if(p.getClientID().toString().equals(id.toString())){
                    p.setReady(ready);
                    p.setCharacterID(characterID);
                }
            }
            try {
                this.sendPacketToAll(new ServerPlayerInfoPacket(players));
            } catch (IOException e) {
                e.printStackTrace();
            }
        }

        if(packet instanceof StartGamePacket){
            gameStarted = true;
            try {
                sendPacketToAll((StartGamePacket) packet);
            } catch (IOException e) {
                e.printStackTrace();
            }
        }

        if(packet instanceof AddAvatarInformationPacket){
            UUID id = ((AddAvatarInformationPacket) packet).getClientID();
            for(PlayerInfo p : players){
                if(p.getClientID().toString().equals(id.toString())){
                    p.setAvatar(((AddAvatarInformationPacket) packet).getAvatar());
                }
            }
            try {
                this.sendPacketToAll(new GamePlayerInfoPacket(players));
            } catch (IOException e) {
                e.printStackTrace();
            }
        }

        if(packet instanceof UpdateAvatarInformationPacket){
            PlayerInfo player = null;
            UUID id = ((UpdateAvatarInformationPacket) packet).getClientID();
            for(PlayerInfo p : players){
                if(p.getClientID().toString().equals(id.toString())){
                    p.setAvatar(((UpdateAvatarInformationPacket) packet).getAvatar());
                    player = p;
                }
            }
            try {
                if(player != null)
                    this.sendPacketToAll(new UpdateGamePlayerInfoPacket(player));
            } catch (IOException e) {
                e.printStackTrace();
            }
        }

        if(packet instanceof LoadedPlayerPacket){
            boolean allReady = true;
            UUID id = ((LoadedPlayerPacket) packet).getId();
            for(PlayerInfo p : players){
                if(p.getClientID().toString().equals(id.toString())){
                    p.setLoaded(true);
                }
                if(!p.isLoaded()){
                    allReady = false;
                }
            }
            if(allReady){
                try {
                    sendPacketToAll(new AllReadyPacket());
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }

    }
}
