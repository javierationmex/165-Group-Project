package game;

import game.characters.CustomCube;
import game.characters.CustomPyramid;
import gameengine.CameraController;
import gameengine.FullScreenDisplaySystem;
import gameengine.TogglePhysics;
import gameengine.player.MovePlayerBackwardAction;
import gameengine.player.MovePlayerForwardAction;
import gameengine.player.MovePlayerLeftAction;
import gameengine.player.MovePlayerRightAction;
import graphicslib3D.Matrix3D;
import graphicslib3D.Point3D;
import graphicslib3D.Vector3D;
import net.java.games.input.Component;
import net.java.games.input.Controller;
import networking.Client;
import networking.packets.ingame.AddAvatarInformationPacket;
import networking.packets.ingame.UpdateAvatarInfoPacket;
import sage.app.BaseGame;
import sage.camera.ICamera;
import sage.display.IDisplaySystem;
import sage.event.EventManager;
import sage.event.IEventManager;
import sage.input.IInputManager;
import sage.input.InputManager;
import sage.input.action.IAction;
import sage.input.action.QuitGameAction;
import sage.physics.IPhysicsEngine;
import sage.physics.IPhysicsObject;
import sage.physics.PhysicsEngineFactory;
import sage.renderer.IRenderer;
import sage.scene.SceneNode;
import sage.scene.SkyBox;
import sage.scene.shape.Cube;
import sage.scene.shape.Rectangle;
import sage.scene.state.RenderState;
import sage.scene.state.TextureState;
import sage.terrain.AbstractHeightMap;
import sage.terrain.ImageBasedHeightMap;
import sage.terrain.TerrainBlock;
import sage.texture.Texture;
import sage.texture.TextureManager;
import swingmenus.multiplayer.data.PlayerInfo;
import trimesh.ChessPieceRock;
import trimesh.Mushroom;
import trimesh.Pod;
import trimesh.Ship;

import javax.script.ScriptEngine;
import javax.script.ScriptEngineManager;
import javax.script.ScriptException;
import java.awt.*;
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
    private CameraController cam1Controller;
    private SceneNode playerAvatar;
    private ArrayList<PlayerInfo> playersInfo;
    private float time = 0;

    private SceneNode rootNode;
    private ScriptEngine engine;
    private TerrainBlock imageTerrain;
    private String oldRotation;
    private String oldTranslation;
    private String oldScale;
    private boolean canProcess;
    private IPhysicsEngine physicsEngine;
    private IPhysicsObject playerAvatarP, groundPlaneP, cubeP;
    private Rectangle groundPlane;
    private boolean isPhysicsEnabled;
    private Cube cube;

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
        initPhysicsSystem();
        createSagePhysicsWorld();

        initScripting();
        setControls();
        display.setTitle("Maze Game");

        isPhysicsEnabled = false;



    }

    protected void initPhysicsSystem()
    {
        String engine = "sage.physics.JBullet.JBulletPhysicsEngine";
        physicsEngine = PhysicsEngineFactory.createPhysicsEngine(engine);
        physicsEngine.initSystem();
        float[] gravity = {0, -15f, 0};
        physicsEngine.setGravity(gravity);
    }

    private void createSagePhysicsWorld() { // add the ball physics
        float mass = 1.0f;

        if(player.getCharacterID() == 3){
            //radius, ???, radius*height
            float[] halfExtents = {5, 0, 5};
            playerAvatarP = physicsEngine.addCylinderObject(physicsEngine.nextUID(),
                    mass, playerAvatar.getWorldTransform().getValues(),halfExtents);
            playerAvatar.setPhysicsObject(playerAvatarP);
            playerAvatarP.setBounciness(1.0f);
        }
        float cubeSize[] = {1,1,1};
        cubeP = physicsEngine.addBoxObject(physicsEngine.nextUID(), 0.5f, cube.getWorldTransform().getValues(), cubeSize);
        cubeP.setBounciness(1.0f);
        cube.setPhysicsObject(cubeP);

        // add the ground groundPlane physics
        float up[] = {0,1,0}; // {0,1,0} is flat
        groundPlaneP =
                physicsEngine.addStaticPlaneObject(physicsEngine.nextUID(),
                        groundPlane.getLocalTranslation().getValues(), up, 0.0f);
        groundPlaneP.setBounciness(0f);
        groundPlane.setPhysicsObject(groundPlaneP);

    }

    private void initScripting() {
        ScriptEngineManager factory = new ScriptEngineManager();

        // Set up script general pathing
        String scriptDir = "." + File.separator + "src" + File.separator + "scripts" + File.separator;
        String CreateObjectsScriptFileName = "CreateObjects.js";
        String CreateObjectsScriptPath = scriptDir + CreateObjectsScriptFileName;

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


        cam1Controller = new CameraController(camera1, playerAvatar, inputMgr, keyboardName);

        inputMgr.associateAction(
                keyboardName, Component.Identifier.Key.ESCAPE,
                quitGame, IInputManager.INPUT_ACTION_TYPE.ON_PRESS_ONLY);

        inputMgr.associateAction(
                keyboardName, Component.Identifier.Key.W,
                new MovePlayerForwardAction(playerAvatar, imageTerrain, client), IInputManager.INPUT_ACTION_TYPE.REPEAT_WHILE_DOWN);
        inputMgr.associateAction(
                keyboardName, Component.Identifier.Key.S,
                new MovePlayerBackwardAction(playerAvatar, imageTerrain, client), IInputManager.INPUT_ACTION_TYPE.REPEAT_WHILE_DOWN);
        inputMgr.associateAction(
                keyboardName, Component.Identifier.Key.A,
                new MovePlayerLeftAction(playerAvatar, imageTerrain, client), IInputManager.INPUT_ACTION_TYPE.REPEAT_WHILE_DOWN);
        inputMgr.associateAction(
                keyboardName, Component.Identifier.Key.D,
                new MovePlayerRightAction(playerAvatar, imageTerrain, client), IInputManager.INPUT_ACTION_TYPE.REPEAT_WHILE_DOWN);
        inputMgr.associateAction(
                keyboardName, Component.Identifier.Key.F2,
                new TogglePhysics(this), IInputManager.INPUT_ACTION_TYPE.ON_PRESS_AND_RELEASE);
    }

    private void initGameObjects() {
        IDisplaySystem display = getDisplaySystem();
        display.setTitle("Treasure Hunt 2015");
        //drawSkyBox();
        addPlayer();
        initTerrain();

/*
        Moved to script
        Mushroom s = new Mushroom();
        addGameWorldObject(s);
        s.translate(200, 6, 200);
        s.scale(20, 50, 20);
        */
        cube = new Cube();
        cube.translate(0, 20, 55);
        cube.setWorldTranslation(cube.getLocalTranslation());
        cube.setShowBound(true);

        addGameWorldObject(cube);
    }

    private void initTerrain() {
        // create height map and terrain block
        String heightDir = "." + File.separator + "materials" + File.separator;
        String heightFilename = "maze2.jpg";
        String heightFilePath = heightDir + heightFilename;
        ImageBasedHeightMap myHeightMap = new ImageBasedHeightMap(heightFilePath);
        imageTerrain = createTerBlock(myHeightMap);

        // create texture and texture state to color the terrain
        TextureState grassState;
        String heighttextureDir = "." + File.separator + "materials" + File.separator;
        String heighttextureFilename = "green-leather-texture.jpg";
        String heighttextureFilePath = heighttextureDir + heighttextureFilename;
        Texture grassTexture = TextureManager.loadTexture2D(heighttextureFilePath);
        grassTexture.setApplyMode(sage.texture.Texture.ApplyMode.Replace);
        grassState = (TextureState) display.getRenderer().createRenderState(RenderState.RenderStateType.Texture);
        grassState.setTexture(grassTexture, 0);
        grassState.setEnabled(true);
        // apply the texture to the terrain
        imageTerrain.setRenderState(grassState);
        imageTerrain.translate(0,-5,0);
        addGameWorldObject(imageTerrain);
        //Floor groundPlane

        groundPlane = new Rectangle();
        Vector3D vec = new Vector3D(1, 0, 0);
        groundPlane.rotate(90, vec);
        groundPlane.scale(1000, 1000, 1);
        groundPlane.setColor(Color.GRAY);
        String planetextureDir = "." + File.separator + "materials" + File.separator;
        String planetexturefilename = "sand.jpg";
        String planetexturefilepath = planetextureDir + planetexturefilename;
        Texture planetexture = TextureManager.loadTexture2D(planetexturefilepath);
        groundPlane.setTexture(planetexture);
        addGameWorldObject(groundPlane);

    }

    private TerrainBlock createTerBlock(AbstractHeightMap heightMap) {
        float heightScale = 0.05f;
        Vector3D terrainScale = new Vector3D(1, heightScale, 1);
        // use the size of the height map as the size of the terrain
        int terrainSize = heightMap.getSize();
        // specify terrain origin so heightmap (0,0) is at world origin
        float cornerHeight = heightMap.getTrueHeightAtPoint(0, 0) * heightScale;
        Point3D terrainOrigin = new Point3D(0, -cornerHeight, 0);
        // create a terrain block using the height map
        String name = "Terrain:" + heightMap.getClass().getSimpleName();
        TerrainBlock tb = new TerrainBlock(name, terrainSize, terrainScale, heightMap.getHeightData(), terrainOrigin);
        return tb;
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
        }else if(player.getCharacterID() == 2){
            playerAvatar = new Ship().getChild();
            playerAvatar.rotate(180, new Vector3D(0, 1, 0));
        }else if(player.getCharacterID() == 3){
            playerAvatar = new ChessPieceRock().getChild();
        }else if(player.getCharacterID() == 4){
            playerAvatar = new Pod().getChild();
            playerAvatar.rotate(180, new Vector3D(0, 1, 0));
        }

        //set the character ID here and catch it in addGhostAvatar();

        playerAvatar.scale(0.2f, 0.2f, 0.2f);
        playerAvatar.translate(0, 10, 50);
        //playerAvatar.setShowBound(true);


        updateOldPosition();

        try {
            client.sendPacket(new AddAvatarInformationPacket(client.getId(), player.getCharacterID()));
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
        playerAvatar.setWorldRotation(playerAvatar.getLocalRotation());
        playerAvatar.setWorldTranslation(playerAvatar.getLocalTranslation());
        playerAvatar.setWorldScale(playerAvatar.getLocalScale());
    }

    @Override
    protected void update(float time) {
        this.time += time;
        cam1Controller.update(this.time);

        if(playerChanged()){
            try {
                client.sendPacket(new UpdateAvatarInfoPacket(client.getId(), playerAvatar.getLocalTranslation(), playerAvatar.getLocalScale(), playerAvatar.getLocalRotation()));
            } catch (IOException e) {
                e.printStackTrace();
            }
        }

        if (isPhysicsEnabled) {
            Matrix3D mat;
            Vector3D translateVec, rotateVec;
            physicsEngine.update(20.0f);
            for (SceneNode s : getGameWorld()){
                if (s.getPhysicsObject() != null){
                    mat = new Matrix3D(s.getPhysicsObject().getTransform());
                    translateVec = mat.getCol(3);
                    //rotateVec = mat.getCol(2);
                    s.getLocalTranslation().setCol(3,translateVec);
                    //s.getLocalRotation().setCol(3,rotateVec);
                }
            }
        }


        if(client != null){
            client.processPackets();
        }

        super.update(time);

        //TODO override later
    }

    private boolean playerChanged() {
        if(!oldRotation.equals(playerAvatar.getLocalRotation().toString()) ||
           !oldTranslation.equals(playerAvatar.getLocalTranslation().toString()) ||
           !oldScale.equals(playerAvatar.getLocalScale().toString())){
            updateOldPosition();
            return true;
        }
        return false;
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

    public void updateGhostAvatar(UpdateAvatarInfoPacket packet) {

        UUID id = packet.getClientID();
        Matrix3D translation = new Matrix3D();
        translation.concatenate(packet.getTranslation());
        Matrix3D scale = new Matrix3D();
        scale.concatenate(packet.getScale());
        Matrix3D rotation = new Matrix3D();
        rotation.concatenate(packet.getRotation());
        if (!id.toString().equals(this.player.getPlayerUUID().toString())){
            for(PlayerInfo p : this.playersInfo){
                if(id.toString().equals(p.getClientID().toString())){
                    p.getAvatar().setLocalTranslation(translation);
                    p.getAvatar().setLocalScale(scale);
                    p.getAvatar().setLocalRotation(rotation);
                }
            }
        }
    }

    public void addGhostAvatar(PlayerInfo player) {
        if(this.playersInfo == null){
            this.playersInfo = new ArrayList<PlayerInfo>();
        }
        this.playersInfo.add(player);
        if (!player.getClientID().toString().equals(this.player.getPlayerUUID().toString())) {

            //Add avatar adding here
            if(player.getCharacterID() == 0) {
                player.setAvatar(new CustomCube("PLAYER1"));
            }else if(player.getCharacterID() == 1){
                player.setAvatar(new CustomPyramid("PLAYER1"));
            }else if (player.getCharacterID() == 2){
                player.setAvatar(new Ship().getChild());
            }else if(player.getCharacterID() == 3) {
                player.setAvatar(new ChessPieceRock().getChild());
            }else if(player.getCharacterID() == 4) {
                player.setAvatar(new Pod().getChild());
            }
                //radius, ???, radius*height
                float[] halfExtents = {10, 10, 10};
                IPhysicsObject playerP = physicsEngine.addCylinderObject(physicsEngine.nextUID(),
                        1.0f, player.getAvatar().getWorldTransform().getValues(), halfExtents);
                player.getAvatar().setPhysicsObject(playerP);
                playerP.setBounciness(1.0f);

            player.getAvatar().translate(0, 5, 50);
            player.getAvatar().rotate(180, new Vector3D(0, 1, 0));
            addGameWorldObject(player.getAvatar());
        }
    }

    public void togglePhysics(boolean yesno){
        isPhysicsEnabled = yesno;
    }

    public boolean isCanProcess() {
        return canProcess;
    }

    public void setCanProcess(boolean canProcess) {
        this.canProcess = canProcess;
    }

}
