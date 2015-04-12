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
import sage.model.loader.OBJLoader;
import sage.renderer.IRenderer;
import sage.scene.SceneNode;
import sage.scene.SkyBox;
import sage.scene.TriMesh;
import sage.texture.Texture;
import sage.texture.TextureManager;
import swingmenus.multiplayer.data.PlayerInfo;

import javax.script.ScriptEngine;
import javax.script.ScriptEngineManager;
import javax.script.ScriptException;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.UUID;

//import objects.mushroom.*;


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

    private SceneNode rootNode;
    private ScriptEngine engine;

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
        initScripting();
        setControls();
        display.setTitle("Maze Game");


        //TODO create objects: walls, players, power up boosts, etc. Gotta create a recursive algorithm to auto generate a random maze.
        //TODO maybe have different colored sections of walls that players can walk through if they picked up a certain color boost.
    }

    private void initScripting() {
        ScriptEngineManager factory = new ScriptEngineManager();
        String scriptDir = "." + File.separator + "src" + File.separator + "scripts" + File.separator;
        String CreateObjectsScriptFileName = "CreateObjects.js";
        String CreateObjectsScriptPath = scriptDir + CreateObjectsScriptFileName;

        /*// get a list of the script engines on this platform
        java.util.List<ScriptEngineFactory> list = factory.getEngineFactories();
        //List<ScriptEngineFactory> list = factory.getEngineFactories();
        System.out.println("Script Engine Factories found:");
        for (ScriptEngineFactory f : list) {
            System.out.println(" Name = " + f.getEngineName()
                    + " language = " + f.getLanguageName()
                    + " extensions = " + f.getExtensions());
        }*/

        // get the JavaScript engine
        engine = factory.getEngineByName("js");
        // run the script
        executeScript(engine, CreateObjectsScriptPath);

        rootNode = (SceneNode) engine.get("rootNode");
        addGameWorldObject(rootNode);
    }

    private void executeScript(ScriptEngine engine, String scriptFileName) {
        try
        { FileReader fileReader = new FileReader(scriptFileName);
            engine.eval(fileReader); //execute the script statements in the file
            fileReader.close();
        }
        catch (FileNotFoundException e1)
        { System.out.println(scriptFileName + " not found " + e1); }
        catch (IOException e2)
        { System.out.println("IO problem with " + scriptFileName + e2); }
        catch (ScriptException e3)
        { System.out.println("ScriptException in " + scriptFileName + e3); }
        catch (NullPointerException e4)
        { System.out.println ("Null ptr exception in " + scriptFileName + e4); }
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

    private void initGameObjects() {
        IDisplaySystem display = getDisplaySystem();
        display.setTitle("Treasure Hunt 2015");
        //drawSkyBox();
        addPlayer();

        {
            OBJLoader loader = new OBJLoader();
            String mushroomDir = "." + File.separator + "materials" + File.separator;
            String mushroomFilename = "mushroom.obj";
            String mushroomFilePath = mushroomDir + mushroomFilename;
            TriMesh mushroom = loader.loadModel(mushroomFilePath);
            mushroom.updateLocalBound();
            addGameWorldObject(mushroom);
            mushroom.scale(10, 10, 10);
            mushroom.translate(-20, 2, 0);

            String mushroomTextureFilename = "red-mushroom-texture.png";
            String mushroomTextureFilePath = mushroomDir + mushroomTextureFilename;
            Texture mushroomTexture = TextureManager.loadTexture2D(mushroomTextureFilePath);
            mushroom.setTexture(mushroomTexture);
        }
        {
            OBJLoader loader = new OBJLoader();
            String chesspieceDir = "." + File.separator + "materials" + File.separator;
            String chesspieceFilename = "chesspiece.obj";
            String chesspieceFilePath = chesspieceDir + chesspieceFilename;
            TriMesh chesspiece = loader.loadModel(chesspieceFilePath);
            chesspiece.updateLocalBound();
            addGameWorldObject(chesspiece);
            chesspiece.scale(1, 1, 1);
            chesspiece.translate(20, 2, 0);

            String chesspieceTextureFilename = "chess-texture.jpg";
            String chesspieceTextureFilePath = chesspieceDir + chesspieceTextureFilename;
            Texture chesspieceTexture = TextureManager.loadTexture2D(chesspieceTextureFilePath);
            chesspiece.setTexture(chesspieceTexture);
        }


    }

    private void drawSkyBox() {
        SkyBox skybox = new SkyBox("skybox", 500, 500, 500);

        String textureDir = "." + File.separator + "textures" + File.separator + "canyon" + File.separator;
        String topFilename = "top.jpg";
        String topFilePath = textureDir + topFilename;

        String frontFilename = "front.jpg";
        String frontFilePath = textureDir + frontFilename;

        String leftFilename = "left.jpg";
        String leftFilePath = textureDir + leftFilename;

        String rightFilename = "right.jpg";
        String rightFilePath = textureDir + rightFilename;

        String backFilename = "back.jpg";
        String backFilePath = textureDir + backFilename;

        Texture top = TextureManager.loadTexture2D(topFilePath);
        skybox.setTexture(SkyBox.Face.Up, top);
        skybox.setTexture(SkyBox.Face.Down, top);
        Texture front = TextureManager.loadTexture2D(frontFilePath);
        skybox.setTexture(SkyBox.Face.North, front);
        Texture back = TextureManager.loadTexture2D(backFilePath);
        skybox.setTexture(SkyBox.Face.South, back);
        Texture left = TextureManager.loadTexture2D(leftFilePath);
        skybox.setTexture(SkyBox.Face.West, left);
        Texture right = TextureManager.loadTexture2D(rightFilePath);
        skybox.setTexture(SkyBox.Face.East, right);

        skybox.translate(0, 100, 0);


        addGameWorldObject(skybox);

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
        camera1.setPerspectiveFrustum(60, 1, 1, 1000);
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

    public boolean isCanProcess() {
        return canProcess;
    }

    public void setCanProcess(boolean canProcess) {
        this.canProcess = canProcess;
    }

}
