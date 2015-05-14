package networking;

import game.characters.CustomCube;
import game.characters.CustomPyramid;
import graphicslib3D.Matrix3D;
import graphicslib3D.Vector3D;
import networking.packets.GamePlayerInfoPacket;
import networking.packets.ServerPlayerInfoPacket;
import networking.packets.ingame.AddAvatarInformationPacket;
import networking.packets.ingame.AvatarTransformsPacket;
import networking.packets.ingame.GetAvatarInformationPacket;
import networking.packets.ingame.UpdateAvatarInfoPacket;
import networking.packets.lobby.ChangeCharacterPacket;
import networking.packets.lobby.JoinPacket;
import networking.packets.lobby.StartGamePacket;
import sage.networking.server.GameConnectionServer;
import sage.networking.server.IClientInfo;
import sage.physics.IPhysicsEngine;
import sage.physics.IPhysicsObject;
import sage.physics.PhysicsEngineFactory;
import sage.scene.shape.Rectangle;
import swingmenus.multiplayer.data.PlayerInfo;
import trimesh.ChessPieceRock;
import trimesh.Pod;
import trimesh.Ship;

import java.io.IOException;
import java.io.Serializable;
import java.net.InetAddress;
import java.util.ArrayList;
import java.util.UUID;

/**
 * Created by Max on 3/22/2015.
 */
public class Server extends GameConnectionServer<UUID> {

    private IPhysicsEngine physicsEngine;

    private ArrayList<PlayerInfo> players;
    private boolean gameStarted = false;
    private int totalPlayerAmount, currentPlayerAmount = 0;

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
            totalPlayerAmount = ((StartGamePacket) packet).getPlayerAmount();
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
                    createAvatar(player);
                    currentPlayerAmount++;
                }
            }
            if(currentPlayerAmount == totalPlayerAmount){
                initPhysicsSystem();
            }
            /*try {
                this.sendPacketToAll(new GamePlayerInfoPacket(player));
            } catch (IOException e) {
                e.printStackTrace();
            }*/
        }

        if (packet instanceof UpdateAvatarInfoPacket) {
            try {
                this.sendPacketToAll((Serializable) packet);
            } catch (IOException e) {
                e.printStackTrace();
            }
        }

        if (packet instanceof GetAvatarInformationPacket) {
            UUID clientID = ((GetAvatarInformationPacket) packet).getClientID();
            AvatarTransformsPacket a = new AvatarTransformsPacket(clientID, getAvatarRotation(clientID), getAvatarTranslation(clientID));
            try {
                this.sendPacket(a, clientID);
            } catch (IOException e) {
                e.printStackTrace();
            }
        }


    }

    private Matrix3D getAvatarTranslation(UUID clientID) {
        for(PlayerInfo p : players){
            if(p.getClientID() == clientID){
                return p.getAvatar().getLocalTranslation();
            }
        }
        return null;
    }

    private Matrix3D getAvatarRotation(UUID clientID) {
        for(PlayerInfo p : players){
            if(p.getClientID() == clientID){
                return p.getAvatar().getLocalRotation();
            }
        }
        return null;
    }

    private void createAvatar(PlayerInfo player) {
        if(player.getCharacterID() == 1){
            player.setAvatar(new CustomPyramid("PLAYER1"));
        }else if(player.getCharacterID() == 0){
            player.setAvatar(new CustomCube("PLAYER1"));
        }else if(player.getCharacterID() == 2){
            player.setAvatar(new Ship().getChild());
        }else if(player.getCharacterID() == 3){
            player.setAvatar(new ChessPieceRock().getChild());
        }else if(player.getCharacterID() == 4){
            player.setAvatar(new Pod().getChild());
        }

        player.getAvatar().rotate(180, new Vector3D(0, 1, 0));
        player.getAvatar().translate(0, 5, 0);
    }

    protected void initPhysicsSystem()
    {
        String engine = "sage.physics.JBullet.JBulletPhysicsEngine";
        physicsEngine = PhysicsEngineFactory.createPhysicsEngine(engine);
        physicsEngine.initSystem();
        float[] gravity = {0, -98f, 0};
        physicsEngine.setGravity(gravity);
        createSagePhysicsWorld();
    }

    private void createSagePhysicsWorld() {

        IPhysicsObject groundPlaneP;
        float mass = 0.01f;

        for(PlayerInfo p: players){
            IPhysicsObject playerAvatarP;
            float[] avatarsize = {1, 1, 1};
            playerAvatarP = physicsEngine.addCapsuleObject(physicsEngine.nextUID(), mass, p.getAvatar().getWorldTransform().getValues(), 1, 1);
            p.getAvatar().setPhysicsObject(playerAvatarP);
            playerAvatarP.setBounciness(0.5f);

            playerAvatarP.setSleepThresholds(0.5f, 0.5f);
            playerAvatarP.setDamping(0.99f, 0.0f);
            playerAvatarP.setFriction(0);
        }

        // add the ground groundPlane physics
        Rectangle groundPlane = new Rectangle("ground", 5000, 5000);
        Vector3D vec = new Vector3D(1, 0, 0);
        groundPlane.rotate(90, vec);
        groundPlane.scale(5, 1, 1);

        float up[] = {0,1,0}; // {0,1,0} is flat
        groundPlaneP =
                physicsEngine.addStaticPlaneObject(physicsEngine.nextUID(),
                        groundPlane.getLocalTranslation().getValues(), up, 0f);
        groundPlaneP.setBounciness(0.0f);
        groundPlane.setPhysicsObject(groundPlaneP);

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