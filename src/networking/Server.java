package networking;

import gameengine.NPC.NPCcontroller;
import graphicslib3D.Matrix3D;
import networking.packets.GamePlayerInfoPacket;
import networking.packets.ServerPlayerInfoPacket;
import networking.packets.ingame.AddAvatarInformationPacket;
import networking.packets.ingame.AllPlayerInfoPacket;
import networking.packets.ingame.UpdateAvatarInfoPacket;
import networking.packets.lobby.ChangeCharacterPacket;
import networking.packets.lobby.JoinPacket;
import networking.packets.lobby.StartGamePacket;
import sage.networking.server.GameConnectionServer;
import sage.networking.server.IClientInfo;
import swingmenus.multiplayer.data.PlayerInfo;
import swingmenus.multiplayer.data.SimplePlayerInfo;

import java.io.IOException;
import java.io.Serializable;
import java.net.InetAddress;
import java.util.ArrayList;
import java.util.Timer;
import java.util.TimerTask;
import java.util.UUID;

/**
 * Created by Max on 3/22/2015.
 */
public class Server extends GameConnectionServer<UUID> {

    private ArrayList<PlayerInfo> players;
    private NPCcontroller npcCtrl;
    private boolean gameStarted = false;
    private Timer timer, timer2;
    private UpdateNPCS updateNPCS;
    private SendPlayerInfo sendPlayerInfo;
    private boolean notSendingYet = true;


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
            createNPCs();
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
            updatePlayerAvatar((UpdateAvatarInfoPacket) packet);
        }
    }

    private void startSendingPlayerInfos() {
        if(timer2 == null) {
            sendPlayerInfo = new SendPlayerInfo(this);
            timer2 = new Timer();
            timer2.schedule(sendPlayerInfo, 0, 20);
        }
    }

    private void updatePlayerAvatar(UpdateAvatarInfoPacket packet) {
        UUID id = packet.getClientID();
        Matrix3D translation = new Matrix3D();
        translation.concatenate(packet.getTranslation());
        Matrix3D scale = new Matrix3D();
        scale.concatenate(packet.getScale());
        Matrix3D rotation = new Matrix3D();
        rotation.concatenate(packet.getRotation());

        for(PlayerInfo p : this.players){
            if(id.toString().equals(p.getClientID().toString())){
                p.setTranslation(translation);
                p.setScale(scale);
                p.setRotation(rotation);
            }
        }
        if(notSendingYet){
            notSendingYet = false;
            startSendingPlayerInfos();
        }
    }

    private void createNPCs() {
        npcCtrl = new NPCcontroller();
        npcCtrl.createNPCs();
        updateNPCS = new UpdateNPCS(this);
        timer = new Timer();
        timer.schedule(updateNPCS, 0, 500);

    }

    public void updateNPCs(){
        npcCtrl.updateNPCs();
        sendNPCinfo();
    }

    public void sendNPCinfo() {
        try {
            sendPacketToAll(npcCtrl.getNPCInfoPacket());
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private void sendPlayerInfo() {
        ArrayList<SimplePlayerInfo> simple = new ArrayList<SimplePlayerInfo>();
        for(PlayerInfo p : players){
            simple.add(p.getSimplePlayerInfo());
        }
        try {
            sendPacketToAll(new AllPlayerInfoPacket(simple));
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    class UpdateNPCS extends TimerTask {
        private Server server;
        public UpdateNPCS(Server server) {
            this.server = server;
        }

        /**
         * The action to be performed by this timer task.
         */
        @Override
        public void run() {
            server.updateNPCs();
        }
    }

    class SendPlayerInfo extends TimerTask {
        private Server server;
        public SendPlayerInfo(Server server) {
            this.server = server;
        }

        /**
         * The action to be performed by this timer task.
         */
        @Override
        public void run() {
            server.sendPlayerInfo();
        }
    }

}