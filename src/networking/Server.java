package networking;

import networking.packets.GamePlayerInfoPacket;
import networking.packets.ServerPlayerInfoPacket;
import networking.packets.ingame.AddAvatarInformationPacket;
import networking.packets.ingame.UpdateAvatarInfoPacket;
import networking.packets.lobby.ChangeCharacterPacket;
import networking.packets.lobby.JoinPacket;
import networking.packets.lobby.StartGamePacket;
import sage.networking.server.GameConnectionServer;
import sage.networking.server.IClientInfo;
import swingmenus.multiplayer.data.PlayerInfo;

import java.io.IOException;
import java.io.Serializable;
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
        if (!gameStarted) {
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
        } else {
            System.out.println("Sorry game has already started.");
        }
    }

    private void addPlayer(PlayerInfo player) {
        this.players.add(player);
    }

    @Override
    public void processPacket(Object packet, InetAddress senderIP, int senderPort) {
        if (packet instanceof ChangeCharacterPacket) {
            UUID id = ((ChangeCharacterPacket) packet).getClientID();
            int characterID = ((ChangeCharacterPacket) packet).getNewCharacterID();
            boolean ready = ((ChangeCharacterPacket) packet).isReady();
            for (PlayerInfo p : players) {
                if (p.getClientID().toString().equals(id.toString())) {
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

        if (packet instanceof StartGamePacket) {
            gameStarted = true;
            try {
                sendPacketToAll((StartGamePacket) packet);
            } catch (IOException e) {
                e.printStackTrace();
            }
        }

        if (packet instanceof AddAvatarInformationPacket) {
            UUID id = ((AddAvatarInformationPacket) packet).getClientID();
            PlayerInfo player = null;
            for (PlayerInfo p : players) {
                if (p.getClientID().toString().equals(id.toString())) {
                    p.setCharacterID(((AddAvatarInformationPacket) packet).getAvatarID());
                    player = p;
                }
            }
            try {
                this.sendPacketToAll(new GamePlayerInfoPacket(player));
            } catch (IOException e) {
                e.printStackTrace();
            }
        }

        if (packet instanceof UpdateAvatarInfoPacket) {
            try {
                this.sendPacketToAll((Serializable) packet);
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }


    public void sendNPCinfo() {// informs clients of new NPC positions
//        for(int i=0; i<npcCtrl.getNumOfNPCs(); i++) {
//            try {
//                this.sendPacketToAll();
//            } catch (IOException e) {
//                e.printStackTrace();
//            }
//            try {
//                String message = new String("mnpc," + Integer.toString(i));
//                message += "," + (npcCtrl.getNPC(i)).getX();
//                message += "," + (npcCtrl.getNPC(i)).getY();
//                message += "," + (npcCtrl.getNPC(i)).getZ();
//                this.sendPacketToAll(message);
//                // also additional cases for receiving messages about NPCs, such as:
//                if (messageTokens[0].compareTo("needNPC") == 0) {
//                    ...}
//                if (messageTokens[0].compareTo("collide") == 0) {
//                    ...}
//
//            }catch(){}
//        }
    }

}