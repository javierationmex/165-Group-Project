// WHILE SPACE IS PRESSED reduce angular damp to drift
//add speed up



package game;

import com.bulletphysics.collision.broadphase.BroadphaseInterface;
import com.bulletphysics.collision.dispatch.CollisionConfiguration;
import com.bulletphysics.collision.dispatch.CollisionDispatcher;
import com.bulletphysics.dynamics.DynamicsWorld;
import com.bulletphysics.dynamics.RigidBody;
import com.bulletphysics.dynamics.constraintsolver.ConstraintSolver;
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
import sage.audio.*;
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
import trimesh.Pod;
import trimesh.Ship;

import javax.script.ScriptEngine;
import javax.script.ScriptEngineManager;
import javax.script.ScriptException;
import javax.swing.*;
import javax.vecmath.Vector3f;
import java.awt.*;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Random;
import java.util.UUID;

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
    private SkyBox skybox;
    private String oldRotation;
    private String oldTranslation;
    private String oldScale;
    private boolean canProcess;
    private IPhysicsEngine physicsEngine;
    private IPhysicsObject playerAvatarP, groundPlaneP, rightRailP, leftRailP, cube1P, pyramid1P;
    private Rectangle groundPlane;
    private boolean isPhysicsEnabled;
    private Cube rightRail, leftRail;
    private Pod NPC1;
    private Ship NPC2;
    private ChessPieceRock finish;


    private CollisionDispatcher collDispatcher;
    private BroadphaseInterface broadPhaseHandler;
    private ConstraintSolver solver;
    private CollisionConfiguration collConfig;
    private RigidBody physicsGround;
    private RigidBody physicsBall;
    private int maxProxies = 1024;
    private Vector3f worldAabbMin = new Vector3f(-10000, -10000, -10000);
    private Vector3f worldAabbMax = new Vector3f(10000, 10000, 10000);
    private DynamicsWorld physicsWorld;
    private IAudioManager audioMgr;
    private Sound windSound, npcSound, whooshSound;

    public MazeGame(Player player) {
        this.player = player;
        this.client = player.getClient();
        this.client.setMazeGame(this);
    }



    // other methods as in SAGE example.
// for example, to create the graphics objects and scene.


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
        while (!displaySystem.isCreated()) {
            try {
                Thread.sleep(10);
            } catch (InterruptedException e) {
                throw new RuntimeException("Display creation interrupted");
            }
            count++;
            System.out.print("+");
            if (count % 80 == 0) {
                System.out.println();
            }
            if (count > 2000) // 20 seconds (approx.)
            {
                throw new RuntimeException("Unable to create display");
            }
        }
        System.out.println();
        return displaySystem;
    }
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
        initAudio();
        display.setTitle("Maze Game");
        isPhysicsEnabled = true;
    }

    private void initGameObjects() {
        IDisplaySystem display = getDisplaySystem();
        display.setTitle("Treasure Hunt 2015");

        addPlayer();
        initTerrain();
        drawSkyBox();

/*
        Moved to script
        Mushroom s = new Mushroom();
        addGameWorldObject(s);
        s.translate(200, 6, 200);
        s.scale(20, 50, 20);
        */

        rightRail = new Cube();
        rightRail.scale(2, 2, 4000);
        rightRail.translate(50, 0, 0);
        addGameWorldObject(rightRail);


        leftRail = new Cube();
        leftRail.scale(2, 2, 4000);
        leftRail.translate(-50, 0, 0);
        addGameWorldObject(leftRail);

        NPC1 = new Pod();
        NPC1.translate(150, 1, 2000);
        addGameWorldObject(NPC1);
        NPC2 = new Ship();
        NPC2.translate(-150, 1, 2000);
        addGameWorldObject(NPC2);

        finish = new ChessPieceRock();
        finish.translate(0, 0, 3000);
        finish.scale(5, 5, 5);
        addGameWorldObject(finish);


    }

    //=====================================================================================================
    //===================================================================================PHYSICS SECTION
    //=====================================================================================================
    protected void initPhysicsSystem()
    {
        String engine = "sage.physics.JBullet.JBulletPhysicsEngine";
        physicsEngine = PhysicsEngineFactory.createPhysicsEngine(engine);
        physicsEngine.initSystem();
        float[] gravity = {0, -98f, 0};
        physicsEngine.setGravity(gravity);
    }

    //PHYSICS
    private void createSagePhysicsWorld() {
        float mass = 0.01f;

        float[] avatarsize = {1, 1, 1};
        playerAvatarP = physicsEngine.addCapsuleObject(physicsEngine.nextUID(), mass, playerAvatar.getWorldTransform().getValues(), 1, 1);
        playerAvatar.setPhysicsObject(playerAvatarP);
        playerAvatarP.setBounciness(0.5f);

        playerAvatarP.setSleepThresholds(0.5f, 0.5f);
        //playerAvatarP.setDamping(0.999999f, 0f);
        //playerAvatarP.setFriction(0.5f);

        //playerAvatarP.setSleepThresholds(0.5f, 0.5f);
        playerAvatarP.setDamping(0.99f, 0.0f);
        playerAvatarP.setFriction(0.1f);

//        float rightRailSize[] = {2, 2, 4000};
//        rightRailP = physicsEngine.addStaticPlaneObject(physicsEngine.nextUID(), 1000, rightRail.getWorldTransform().getValues(), rightRailSize);
//        rightRailP.setBounciness(0.9f);
//        rightRailP.getTransform()[6]=50;
//        rightRail.setPhysicsObject(rightRailP);
//
//        float leftRailSize[] = {2, 2, 4000};
//        leftRailP = physicsEngine.addStaticPlaneObject(physicsEngine.nextUID(), 1000, leftRail.getWorldTransform().getValues(), leftRailSize);
//        leftRailP.setBounciness(0.9f);
//        leftRail.setPhysicsObject(leftRailP);


        float cube1Size[] = {1, 1, 1};
        cube1P = physicsEngine.addCylinderObject(physicsEngine.nextUID(), mass, NPC1.getWorldTransform().getValues(), cube1Size);
        cube1P.setBounciness(5.5f);
        cube1P.setDamping(0.1f, 0.1f);
        NPC1.setPhysicsObject(cube1P);
        //cube1P.setLinearVelocity(new float[]{200f, 0f, 0f});




        float cube2Size[] = {1, 1, 1};
        pyramid1P = physicsEngine.addCapsuleObject(physicsEngine.nextUID(), mass, NPC2.getWorldTransform().getValues(), 1f, 1f);
        pyramid1P.setBounciness(5.5f);
        pyramid1P.setDamping(0.1f, 0.1f);
        NPC2.setPhysicsObject(pyramid1P);
        //pyramid1P.setLinearVelocity(new float[]{-200f, 0f, 0f});
        // add the ground groundPlane physics
        float up[] = {0,1,0}; // {0,1,0} is flat
        groundPlaneP = physicsEngine.addStaticPlaneObject(physicsEngine.nextUID(), groundPlane.getLocalTranslation().getValues(), up, 0.0f);
        groundPlaneP.setBounciness(0.7f);
        groundPlane.setPhysicsObject(groundPlaneP);

    }


    //==========================================================================================SCRIPTING SECTION
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

    //=====================================================================================================
    //INPUT SECTION
    //=====================================================================================================
    private void setControls() {
        String keyboardName = JOptionPane.showInputDialog(null, "Pick a keyboard", "Input", JOptionPane.QUESTION_MESSAGE, null, inputMgr.getControllers().toArray(), "keyboard").toString();
        //String keyboardName = inputMgr.getKeyboardName();
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
                new MovePlayerForwardAction(playerAvatar, imageTerrain, client, playerAvatarP), IInputManager.INPUT_ACTION_TYPE.REPEAT_WHILE_DOWN);
        inputMgr.associateAction(
                keyboardName, Component.Identifier.Key.S,
                new MovePlayerBackwardAction(playerAvatar, imageTerrain, client, playerAvatarP), IInputManager.INPUT_ACTION_TYPE.REPEAT_WHILE_DOWN);
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

    //=====================================================================================================
    //============================================================================================ TERRAIN SECTION
    //=====================================================================================================
    private void initTerrain() {
//        // create height map and terrain block
        String heightDir = "." + File.separator + "materials" + File.separator;
        String heightFilename = "t.jpg";
        String heightFilePath = heightDir + heightFilename;
        ImageBasedHeightMap myHeightMap = new ImageBasedHeightMap(heightFilePath);
        imageTerrain = createTerBlock(myHeightMap);

        // create texture and texture state to color the terrain
        TextureState grassState;
        String heighttextureDir = "." + File.separator + "materials" + File.separator;
        String heighttextureFilename = "green.jpg";
        String heighttextureFilePath = heighttextureDir + heighttextureFilename;
        Texture grassTexture = TextureManager.loadTexture2D(heighttextureFilePath);
        grassTexture.setApplyMode(sage.texture.Texture.ApplyMode.Replace);
        grassState = (TextureState) display.getRenderer().createRenderState(RenderState.RenderStateType.Texture);
        grassState.setTexture(grassTexture, 0);
        grassState.setEnabled(true);
        // apply the texture to the terrain
        imageTerrain.setRenderState(grassState);

        imageTerrain.translate(-imageTerrain.getSize() / 2, -7, -imageTerrain.getSize() / 2);
        //imageTerrain.scale(3, 1, 3);
        //addGameWorldObject(imageTerrain);

        //Floor groundPlane

        groundPlane = new Rectangle("ground", 500, 10000);
        Vector3D vec = new Vector3D(1, 0, 0);
        groundPlane.rotate(90, vec);
        //groundPlane.scale(1, 1, 10);
        //groundPlane.scale(1, 1, 1);
        //groundPlane.translate(0, 0, 0);
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
        skybox = new SkyBox("skybox", 1000, 1000, 1000);

        String textureDir = "." + File.separator + "materials" + File.separator + "dunes" + File.separator;
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

        skybox.setZBufferStateEnabled(false);

        addGameWorldObject(skybox);

    }

    private void addPlayer() {
        if(player.getCharacterID() == 1){

            playerAvatar = new CustomPyramid("PLAYER1");
        }else if(player.getCharacterID() == 0){
            playerAvatar = new CustomCube("PLAYER1");
        }else if(player.getCharacterID() == 2){
            playerAvatar = new Ship().getChild();
        }else if(player.getCharacterID() == 3){
            playerAvatar = new ChessPieceRock().getChild();
        }else if(player.getCharacterID() == 4){
            playerAvatar = new Pod().getChild();
        }

        //set the character ID here and catch it in addGhostAvatar();

        //playerAvatar.scale(0.2f, 0.2f, 0.2f);
        playerAvatar.rotate(180, new Vector3D(0, 1, 0));
        playerAvatar.translate(0, 5, 0);
        //playerAvatar.setShowBound(true);


        updateOldPosition();

        try {
            client.sendPacket(new AddAvatarInformationPacket(client.getId(), player.getCharacterID()));
        } catch (IOException e) {
            e.printStackTrace();
        }
        addGameWorldObject(playerAvatar);
        camera1 = display.getRenderer().getCamera();
        camera1.setPerspectiveFrustum(60, 1, 1, 5000);
        camera1.setLocation(new Point3D(0, 1, 50));
    }


    //-------------------------------------------------------------------------------------------------------
    // ----------------------------------------------------------------------UPDATE SECTION -----------------
    //-------------------------------------------------------------------------------------------------------

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
        super.update(time);
        if(playerChanged()){
            try {
                client.sendPacket(new UpdateAvatarInfoPacket(client.getId(), playerAvatar.getLocalTranslation(), playerAvatar.getLocalScale(), playerAvatar.getLocalRotation()));
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        //--------------------------------------------------------------SKYBOX FOLLOWING THE AVATAR
        skybox.setLocalTranslation(playerAvatar.getLocalTranslation());


        // -------------------------------------------------------------APPLIES FRICTION WHEN IN IMAGETERRAIN
//        Point3D avLoc = new Point3D(playerAvatar.getLocalTranslation().getCol(3));
//        float terHeight = imageTerrain.getHeightFromWorld(avLoc);
//        if (avLoc.getY() < terHeight) {
//            playerAvatarP.setFriction(0.9f);
//            //JOptionPane.showMessageDialog(null,"COllision");
//        } else {
//            playerAvatarP.setFriction(0);
//        }

        //playerAvatar.getLocalTranslation().setElementAt(1, 3, desiredHeight);

        if (isPhysicsEnabled) {

            Random rand = new Random();
            float ranfdomFloat = rand.nextFloat();
            SceneNode particle = NPC1;

            float[] swarmBehaviour = new float[3];
            {
                Point3D finishPoint = new Point3D(finish.getLocalTranslation().getCol(3));
                Point3D startPoint = new Point3D(particle.getLocalTranslation().getCol(3));
                swarmBehaviour[0] = (float) ((finishPoint.getX() - startPoint.getX()) * 0.1);
                swarmBehaviour[1] = 0;
                swarmBehaviour[2] = (float) ((finishPoint.getZ() - startPoint.getZ()) * 0.1);
            }


            float[] particleBehaviour = new float[3];
            {
                Point3D finishPoint = new Point3D(playerAvatar.getLocalTranslation().getCol(3));
                Point3D startPoint = new Point3D(particle.getLocalTranslation().getCol(3));
                particleBehaviour[0] = (float) ((finishPoint.getX() - startPoint.getX()) * 0.1);
                particleBehaviour[1] = 0;
                particleBehaviour[2] = (float) ((finishPoint.getZ() - startPoint.getZ()) * 0.1);
            }


            float[] behaviour = new float[3];
            behaviour[0] = (swarmBehaviour[0] + particleBehaviour[0]) * rand.nextFloat();
            behaviour[1] = 0;
            behaviour[2] = (swarmBehaviour[2] + particleBehaviour[2]) * rand.nextFloat();

            //float halfrand = (rand.nextInt(50 - 0) + 0)/100;

            cube1P.setLinearVelocity(behaviour);


            pyramid1P.setLinearVelocity(behaviour);

            //playerAvatarP.setTransform(playerAvatar.getWorldTransform().getValues());
            Matrix3D mat;
            Vector3D translateVec, rotateVec;
            physicsEngine.update(100.0f);
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


        npcSound.setLocation(new Point3D(NPC1.getLocalTranslation().getCol(3)));
        windSound.setLocation(new Point3D(playerAvatar.getLocalTranslation().getCol(3)));
        windSound.setVolume((int) (playerAvatarP.getAngularVelocity()[2] * 0.5));
        setEarParameters();
    }

    //-------------------------------------------------------------------------------------------------------
    // ----------------------------------------------------------------------SOUND SECTION -----------------
    //-------------------------------------------------------------------------------------------------------

    public void initAudio() {
        AudioResource resource1, resource2, resource3;
        audioMgr = AudioManagerFactory.createAudioManager("sage.audio.joal.JOALAudioManager");
        if (!audioMgr.initialize()) {
            System.out.println("Audio Manager failed to initialize!");
            return;
        }
        String soundDir = "." + File.separator + "materials" + File.separator + "sounds" + File.separator;
        String windFilename = "Wind.wav";
        String windFilePath = soundDir + windFilename;
        String strongwindFilename = "StrongWind.wav";
        String strongwindFilePath = soundDir + strongwindFilename;
        String whooshFilename = "Whoosh.wav";
        String whooshFilePath = soundDir + whooshFilename;
        String shipFilename = "ship1.wav";
        String shipFilePath = soundDir + shipFilename;

        resource1 = audioMgr.createAudioResource(shipFilePath, AudioResourceType.AUDIO_SAMPLE);
        resource2 = audioMgr.createAudioResource(whooshFilePath, AudioResourceType.AUDIO_SAMPLE);
        resource3 = audioMgr.createAudioResource(strongwindFilePath, AudioResourceType.AUDIO_SAMPLE);
        npcSound = new Sound(resource1, SoundType.SOUND_EFFECT, 100, true);
        whooshSound = new Sound(resource2, SoundType.SOUND_EFFECT, 100, true);
        windSound = new Sound(resource3, SoundType.SOUND_EFFECT, 50, true);

        npcSound.initialize(audioMgr);
        windSound.initialize(audioMgr);
        whooshSound.initialize(audioMgr);
        npcSound.setMaxDistance(200);
        npcSound.setMinDistance(50.0f);
        npcSound.setRollOff(5.0f);
        windSound.setMaxDistance(200);
        windSound.setMinDistance(50.0f);
        windSound.setRollOff(5.0f);
        whooshSound.setMaxDistance(200);
        whooshSound.setMinDistance(50.0f);
        whooshSound.setRollOff(5.0f);

        npcSound.setLocation(new Point3D(NPC1.getLocalTranslation().getCol(3)));
        windSound.setLocation(new Point3D(playerAvatar.getLocalTranslation().getCol(3)));
        whooshSound.setLocation(new Point3D(finish.getLocalTranslation().getCol(3)));
        setEarParameters();
        npcSound.play();
        windSound.play();
        whooshSound.play();

    }

    public void setEarParameters() {
        Matrix3D avDir = (Matrix3D) (playerAvatar.getWorldRotation().clone());
        //float camAz = camera1.get.getAzimuth();
        avDir.rotateY(180.0f);
        Vector3D camDir = new Vector3D(0, 0, 1);
        camDir = camDir.mult(avDir);
        audioMgr.getEar().setLocation(camera1.getLocation());
        audioMgr.getEar().setOrientation(camDir, new Vector3D(0, 1, 0));
    }

    //-------------------------------------------------------------------------------------------------------
    // ----------------------------------------------------------------------player? SECTION -----------------
    //-------------------------------------------------------------------------------------------------------

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

    private void avatarCollisionCorrection() {

        // if (avLoc.getY() < terHeight) {
        // System.out.println("collision");
        //  Z Z Z
        //  7 8 1   x
        //  6 X 2   x
        //  5 4 3   x
        //
        //1 x++ ,y ,z++
        //2 x++ ,y ,z
        //3 x++ ,y ,z--
        //4 x   ,y ,z--
        //5 x-- ,y ,z--
        //6 x-- ,y ,z
        //7 x-- ,y ,z++
        //8 x   ,y ,z++


        Point3D avLoc = new Point3D(playerAvatar.getLocalTranslation().getCol(3));
        float x = (float) avLoc.getX();
        float y = (float) avLoc.getY();
        float z = (float) avLoc.getZ();

        int o = 2;
        float[] newx = {0, x + o, x + o, x + o, x, x - o, x - o, x - o, x};
        float[] newy = {0, y, y, y, y, y, y, y, y};
        float[] newz = {0, z + o, z, z - o, z - o, z - o, z, z + o, z + o};


        Point3D[] newloc = new Point3D[9];
        float[] newterHeight = new float[9];
        boolean[] colidesWithTerrian = new boolean[9];
        for (int i = 0; i < 9; i++) {
            newloc[i] = new Point3D(newx[i], newy[i], newz[i]);
            newterHeight[i] = imageTerrain.getHeightFromWorld(newloc[i]);
            colidesWithTerrian[i] = newterHeight[i] >= newy[i];
        }
//        System.out.println();
//        System.out.print(colidesWithTerrian[7]);
//        System.out.print(colidesWithTerrian[8]);
//        System.out.println(colidesWithTerrian[1]);
//        System.out.print(colidesWithTerrian[6]);
//        System.out.print(colidesWithTerrian[0]);
//        System.out.println(colidesWithTerrian[2]);
//        System.out.print(colidesWithTerrian[5]);
//        System.out.print(colidesWithTerrian[4]);
//        System.out.println(colidesWithTerrian[3]);

        boolean collition = false;
        for (int i = 1; i < 9; i++) if (colidesWithTerrian[i]) collition = true;

        o = o * 3;
        float[] futurex = {0, x + o, x + o, x + o, x, x - o, x - o, x - o, x};
        float[] futurey = {0, y, y, y, y, y, y, y, y};
        float[] futurez = {0, z + o, z, z - o, z - o, z - o, z, z + o, z + o};

        if (collition) {
            if (colidesWithTerrian[1] || colidesWithTerrian[2] || colidesWithTerrian[3]) {
                playerAvatar.getLocalTranslation().setElementAt(0, 3, futurex[7]);
                playerAvatar.getLocalTranslation().setElementAt(2, 3, futurez[7]);
            }
            if (colidesWithTerrian[5] || colidesWithTerrian[6] || colidesWithTerrian[7]) {
                playerAvatar.getLocalTranslation().setElementAt(0, 3, futurex[1]);
                playerAvatar.getLocalTranslation().setElementAt(2, 3, futurez[1]);
            }
            if (colidesWithTerrian[8]) {
                playerAvatar.getLocalTranslation().setElementAt(0, 3, futurex[4]);
                playerAvatar.getLocalTranslation().setElementAt(2, 3, futurez[4]);
            }
            if (colidesWithTerrian[4]) {
                playerAvatar.getLocalTranslation().setElementAt(0, 3, futurex[8]);
                playerAvatar.getLocalTranslation().setElementAt(2, 3, futurez[8]);
            }

        }


    }

    private boolean collidesWithTerrain(Point3D p) {
        boolean collides = true;

        float x = (float) p.getX();
        float y = (float) p.getY();
        float z = (float) p.getZ();
        float[] newx = {0, x + 1, x + 1, x + 1, x, x - 1, x - 1, x - 1, x};
        float[] newy = {0, y, y, y, y, y, y, y, y};
        float[] newz = {0, z + 1, z, z - 1, z - 1, z - 1, z, z + 1, z + 1};

        Point3D[] newloc = new Point3D[9];
        float[] newterHeight = new float[9];
        // boolean[] colidesWithTerrian = new boolean[9];
        for (int i = 0; i < 9; i++) {
            newloc[i] = new Point3D(newx[i], newy[i], newz[i]);
            newterHeight[i] = imageTerrain.getHeightFromWorld(newloc[i]);
            if (newterHeight[i] >= newy[i]) collides = true;
        }

        return collides;
    }
}
