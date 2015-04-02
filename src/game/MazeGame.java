package game;

import game.characters.CustomCube;
import game.characters.CustomPyramid;
import gameengine.FullScreenDisplaySystem;
import gameengine.orbit.OrbitCameraController;
import gameengine.player.MovePlayerBackwardAction;
import gameengine.player.MovePlayerForwardAction;
import gameengine.player.MovePlayerLeftAction;
import gameengine.player.MovePlayerRightAction;
import graphicslib3D.Point3D;
import graphicslib3D.Vector3D;
import net.java.games.input.Component;
import net.java.games.input.Controller;
import networking.Client;
import networking.packets.ingame.AddAvatarInformationPacket;
import networking.packets.ingame.UpdateAvatarLocationInformationPacket;
import networking.packets.ingame.UpdateAvatarRotationInformationPacket;
import sage.app.BaseGame;
import sage.camera.ICamera;
import sage.display.IDisplaySystem;
import sage.event.EventManager;
import sage.event.IEventManager;
import sage.input.IInputManager;
import sage.input.InputManager;
import sage.input.action.IAction;
import sage.input.action.QuitGameAction;
import sage.renderer.IRenderer;
import sage.scene.SceneNode;
import sage.scene.shape.Line;
import sage.scene.shape.Rectangle;
import swingmenus.multiplayer.data.PlayerInfo;

import java.awt.*;
import java.io.IOException;
import java.util.ArrayList;
import java.util.UUID;

/**
 * Main Class for Game initialization etc here.
 */
public class MazeGame extends BaseGame {
    ICamera camera1;
    private Player player;
    private Client client;
    private IDisplaySystem display;
    private IEventManager eventManager;
    private IInputManager inputMgr;
    private IRenderer renderer;
    private OrbitCameraController cam1Controller;
    private SceneNode playerAvatar;
    private ArrayList<PlayerInfo> playersInfo;
    private float time = 0;

    private String oldRotation;
    private String oldTranslation;
    private String oldScale;
    private boolean canProcess;

    public MazeGame(Player player) {
        this.player = player;
        this.client = player.getClient();
        this.client.setMazeGame(this);
    }

    //Please separate all the initialization of stuff into different methods.
    //For example createPlayers(), createWalls(), etc... so that the initGame is very simple.


    @Override
    protected void initGame() {
        eventManager = EventManager.getInstance();
        inputMgr = getInputManager();
        renderer = display.getRenderer();
        initGameObjects();
        drawAxis();
        drawPlane();
        setControls();
        display.setTitle("Treasure Hunt 2015");


        //TODO create objects: walls, players, power up boosts, etc. Gotta create a recursive algorithm to auto generate a random maze.
        //TODO maybe have different colored sections of walls that players can walk through if they picked up a certain color boost.
    }

    private void setControls() {
        String keyboardName = inputMgr.getKeyboardName();
        ArrayList<Controller> controllers = inputMgr.getControllers();
        String controllerName = null;
        if (controllers.size() > 2)
            controllerName = controllers.get(3).getName();
        IAction quitGame = new QuitGameAction(this);


        cam1Controller = new OrbitCameraController(camera1, playerAvatar, inputMgr, keyboardName, client);

        inputMgr.associateAction(
                keyboardName, Component.Identifier.Key.ESCAPE,
                quitGame, IInputManager.INPUT_ACTION_TYPE.ON_PRESS_ONLY);

        inputMgr.associateAction(
                keyboardName, Component.Identifier.Key.W,
                new MovePlayerForwardAction(playerAvatar, client), IInputManager.INPUT_ACTION_TYPE.REPEAT_WHILE_DOWN);
        inputMgr.associateAction(
                keyboardName, Component.Identifier.Key.S,
                new MovePlayerBackwardAction(playerAvatar, client), IInputManager.INPUT_ACTION_TYPE.REPEAT_WHILE_DOWN);
        inputMgr.associateAction(
                keyboardName, Component.Identifier.Key.A,
                new MovePlayerLeftAction(playerAvatar, client), IInputManager.INPUT_ACTION_TYPE.REPEAT_WHILE_DOWN);
        inputMgr.associateAction(
                keyboardName, Component.Identifier.Key.D,
                new MovePlayerRightAction(playerAvatar, client), IInputManager.INPUT_ACTION_TYPE.REPEAT_WHILE_DOWN);
    }

    private void drawPlane() {
        Rectangle plane = new Rectangle("plane");
        plane.rotate(90, new Vector3D(1, 0, 0));
        plane.scale(400, 400, 400);
        plane.setColor(Color.GRAY);
        addGameWorldObject(plane);
    }

    private void initGameObjects() {
        IDisplaySystem display = getDisplaySystem();
        display.setTitle("Treasure Hunt 2015");

        addPlayer();
    }

    private void addPlayer() {
        if(player.getCharacterID() == 1){
            playerAvatar = new CustomPyramid("PLAYER1");
        }else if(player.getCharacterID() == 0){
            playerAvatar = new CustomCube("PLAYER1");
        }
        playerAvatar.translate(0, 1, 50);
        playerAvatar.rotate(180, new Vector3D(0, 1, 0));

        updateOldPosition();

        try {
            client.sendPacket(new AddAvatarInformationPacket(client.getId(), playerAvatar));
        } catch (IOException e) {
            e.printStackTrace();
        }
        addGameWorldObject(playerAvatar);
        camera1 = display.getRenderer().getCamera();
        camera1.setLocation(new Point3D(0, 1, 50));
    }

    private void updateOldPosition() {
        oldRotation = playerAvatar.getLocalRotation().toString();
        oldTranslation = playerAvatar.getLocalTranslation().toString();
        oldScale = playerAvatar.getLocalScale().toString();
    }

    @Override
    protected void update(float time) {
        this.time += time;
        cam1Controller.update(this.time);

        //checkIfToUpdatePlayer();

        if(client != null){
            client.processPackets();
        }
        super.update(time);

        //TODO override later
    }

    private void checkIfToUpdatePlayer() {
        if(oldRotation.equals(playerAvatar.getLocalRotation().toString())){
            updateOldPosition();
            /*try {
                client.sendPacket(new UpdateAvatarRotationPacket(client.getId(), playerAvatar.getLocalRotation()));
            } catch (IOException e) {
                e.printStackTrace();
            }*/
        }
        if(oldRotation.equals(playerAvatar.getLocalScale().toString())){
            updateOldPosition();
            /*try {
                client.sendPacket(new UpdateAvatarScalePacket(client.getId(), playerAvatar.getLocalScale()));
            } catch (IOException e) {
                e.printStackTrace();
            }*/
        }
        if(oldRotation.equals(playerAvatar.getLocalTranslation().toString())){
            updateOldPosition();
            /*try {
                client.sendPacket(new UpdateAvatarTranslationPacket(client.getId(), playerAvatar.getLocalTranslation()));
            } catch (IOException e) {
                e.printStackTrace();
            }*/
        }
    }


    @Override
    protected void shutdown() {
        super.shutdown();
    }

    @Override
    protected void exit() {
        super.exit();
    }

    @Override
    protected void initSystem() {
        display = createDisplaySystem();
        setDisplaySystem(display);
        IInputManager inputManager = new InputManager();
        setInputManager(inputManager);
        ArrayList<SceneNode> gameWorld = new ArrayList<SceneNode>();
        setGameWorld(gameWorld);
    }

    private IDisplaySystem createDisplaySystem() {
        IDisplaySystem displaySystem = new FullScreenDisplaySystem(700, 300, 24, 20, false, "sage.renderer.jogl.JOGLRenderer");
        System.out.print("\nWaiting for display creation...");
        int count = 0;
        while (!displaySystem.isCreated())
        {
            try
            { Thread.sleep(10); }
            catch (InterruptedException e)
            { throw new RuntimeException("Display creation interrupted"); }
            count++;
            System.out.print("+");
            if (count % 80 == 0) { System.out.println(); }
            if (count > 2000) // 20 seconds (approx.)
            { throw new RuntimeException("Unable to create display");
            }
        }
        System.out.println();
        return displaySystem ;
    }

    private void drawAxis() {
        Line xaxis = new Line(new Point3D(-1000, 0, 0), new Point3D(1000, 0, 0), Color.RED, 2);
        addGameWorldObject(xaxis);

        Line yaxis = new Line(new Point3D(0, -1000, 0), new Point3D(0, 1000, 0), Color.GREEN, 2);
        addGameWorldObject(yaxis);

        Line zaxis = new Line(new Point3D(0, 0, -1000), new Point3D(0, 0, 1000), Color.BLUE, 2);
        addGameWorldObject(zaxis);
    }

/*    public void updateGhostAvatars(ArrayList<PlayerInfo> players) {
        this.playersInfo = players;
        for(PlayerInfo p : players){
            if (!p.getClientID().toString().equals(player.getPlayerUUID().toString())) {
                if(p.getAvatar() != null){
                    removeGameWorldObject(p.getAvatar());
                    addGameWorldObject(p.getAvatar());
                }
            }
        }
    }*/

    public void updateGhostAvatar(UpdateAvatarLocationInformationPacket packet) {
        UUID id = packet.getClientID();
        float x = packet.getX();
        float y = packet.getY();
        float z = packet.getZ();
        if (!id.toString().equals(this.player.getPlayerUUID().toString())){
            for(PlayerInfo p : this.playersInfo){
                if(id.toString().equals(p.getClientID().toString())){
                    p.getAvatar().translate(x, y, z);
                }
            }
        }
    }

    public void updateGhostAvatar(UpdateAvatarRotationInformationPacket packet) {
        UUID id = packet.getClientID();
        float x = packet.getX();
        Vector3D axis = packet.getAxis();
        if (!id.toString().equals(this.player.getPlayerUUID().toString())){
            for(PlayerInfo p : this.playersInfo){
                if(id.toString().equals(p.getClientID().toString())){
                    p.getAvatar().rotate(x, axis);
                }
            }
        }
    }

    public void addGhostAvatar(PlayerInfo player) {
        if(this.playersInfo == null){
            this.playersInfo = new ArrayList<PlayerInfo>();
        }
        this.playersInfo.add(player);
        if (!player.getClientID().toString().equals(this.player.getPlayerUUID().toString())){
            player.getAvatar().translate(0, 1, 50);
            player.getAvatar().rotate(180, new Vector3D(0, 1, 0));
            addGameWorldObject(player.getAvatar());
        }
    }

    public void setCanProcess(boolean canProcess) {
        this.canProcess = canProcess;
    }

    public boolean isCanProcess() {
        return canProcess;
    }

}
