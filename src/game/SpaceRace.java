//AWESOME NAMELESS GAME



package game;

import com.bulletphysics.collision.broadphase.BroadphaseInterface;
import com.bulletphysics.collision.dispatch.CollisionConfiguration;
import com.bulletphysics.collision.dispatch.CollisionDispatcher;
import com.bulletphysics.dynamics.DynamicsWorld;
import com.bulletphysics.dynamics.RigidBody;
import com.bulletphysics.dynamics.constraintsolver.ConstraintSolver;
import game.characters.CustomCube;
import gameengine.CameraController;
import gameengine.FullScreenDisplaySystem;
import gameengine.Jump;
import gameengine.NPC.NPC;
import gameengine.TogglePhysics;
import gameengine.player.*;
import graphicslib3D.Matrix3D;
import graphicslib3D.Point3D;
import graphicslib3D.Vector3D;
import net.java.games.input.Component;
import net.java.games.input.Controller;
import networking.Client;
import networking.packets.EndScore;
import networking.packets.ingame.*;
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
import sage.model.loader.ogreXML.OgreXMLParser;
import sage.physics.IPhysicsEngine;
import sage.physics.IPhysicsObject;
import sage.physics.PhysicsEngineFactory;
import sage.renderer.IRenderer;
import sage.scene.*;
import sage.scene.bounding.BoundingSphere;
import sage.scene.shape.Cube;
import sage.scene.shape.Rectangle;
import sage.scene.state.RenderState;
import sage.scene.state.TextureState;
import sage.terrain.AbstractHeightMap;
import sage.terrain.TerrainBlock;
import sage.texture.Texture;
import sage.texture.TextureManager;
import swingmenus.multiplayer.data.PlayerInfo;
import swingmenus.multiplayer.data.SimplePlayerInfo;
import trimesh.*;

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

public class SpaceRace extends BaseGame {

    ICamera camera1;
    TextureState shipTextureState;
    private Player player;
    private Client client;
    private IDisplaySystem display;
    private IEventManager eventManager;
    private IInputManager inputMgr;
    private IRenderer renderer;
    private CameraController cam1Controller;
    private SceneNode playerAvatar;
    private ArrayList<PlayerInfo> playersInfo;
    private ArrayList<NPC> npcs;
    private ArrayList<NPCGhost> npcsGhosts;
    private float time = 0;
    private int score = 0;
    private SceneNode rootNode;
    private ScriptEngine engine;
    private TerrainBlock imageTerrain;
    private SkyBox skybox;
    private String oldRotation;
    private String oldTranslation;
    private String oldScale;
    private boolean canProcess;
    private IPhysicsEngine physicsEngine;
    private IPhysicsObject playerAvatarP, groundPlaneP, rightRailP, leftRailP, NPC1P, NPC2P, NPC3P, NPC4P;
    private Rectangle[] groundPlane;
    private boolean isPhysicsEnabled;
    private Cube rightRail, leftRail;
    private Cube finish;
    private Pod NPC1;
    private Ship NPC2;
    private Arc170 NPC3;
    private Viper NPC4;
    private CustomCube[] cube;
    private CenterCity[] city;
    private RotationController rotate;
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
    private Group model;
    private Model3DTriMesh myObject;
    private boolean gameover = false;

    private Random rand = new Random();
    private IAudioManager audioMgr;
    private Sound windSound, npcSound, whooshSound, floop;
    private HUDString scoreString1, speedString, avatarName;
    private int cubeCount;
    private float[] cubeLocations;
    //private Sound[] whooshSound;
    private int finishline = 21000;

    public SpaceRace(Player player) {
        this.player = player;
        this.client = player.getClient();
        this.client.setSpaceRace(this);
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
        IDisplaySystem displaySystem = new FullScreenDisplaySystem(400, 400, 24, 20, false, "sage.renderer.jogl.JOGLRenderer");
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
        initScripting();
        initGameObjects();
        initPhysicsSystem();
        createSagePhysicsWorld();

        setControls();
        initAudio();
        display.setTitle("Space Race");
        isPhysicsEnabled = true;
    }

    private void initGameObjects() {
        IDisplaySystem display = getDisplaySystem();
        display.setTitle("Space Race");

        //Animated Object
        OgreXMLParser loader = new OgreXMLParser();
        try
        {
            model = loader.loadModel("." + File.separator + "materials" + File.separator + "Sphere.010.mesh.xml",
                    "." + File.separator + "materials" + File.separator + "Material.001.material",
                    "." + File.separator + "materials" + File.separator +"Sphere.010.skeleton.xml");
            model.updateGeometricState(0, true);
            java.util.Iterator<SceneNode> modelIterator = model.iterator();
            myObject = (Model3DTriMesh) modelIterator.next();

        }
        catch (Exception e)
        {
            e.printStackTrace();
            System.exit(1);
        }
        myObject.scale(1.5f, 1.5f, 1.5f);
        myObject.translate(1f,1f, 1f);

        String shipDir = "." + File.separator + "materials" + File.separator;
        String shipTextureFilename = "oracleTexturingFlipped3.png";
        String shipTextureFilePath = shipDir + shipTextureFilename;

        Texture shipTexture = TextureManager.loadTexture2D(shipTextureFilePath);
        shipTexture.setApplyMode(Texture.ApplyMode.Replace);
        shipTextureState = (TextureState) display.getRenderer().createRenderState(RenderState.RenderStateType.Texture);
        shipTextureState.setTexture(shipTexture, 0);
        shipTextureState.setEnabled(true);
        myObject.setRenderState(shipTextureState);
        //myObject.rotate(180, new Vector3D(0,1,0));
        //myObject.translate(0f, 5f, 0f);

        //myObject.setTexture(shipTexture);
        myObject.updateRenderStates();

        myObject.startAnimation("my_animation");



        addPlayer();
        initTerrain();
        drawSkyBox();

        //inviisible rails
        rightRail = new Cube();
        rightRail.scale(2, 10, 4000);
        rightRail.translate(50, 0, 5000);


        leftRail = new Cube();
        leftRail.scale(2, 10, 4000);
        leftRail.translate(-50, 0, 5000);


        //NPC model adding
        NPC1 = new Pod();
        NPC1.translate(-10, 5, -800);
        NPC1.scale(1.5f, 1.5f, 1.5f);
        NPC1.updateGeometricState(0, true);
        addGameWorldObject(NPC1);

        NPC2 = new Ship();
        NPC2.translate(-5, 5, -800);
        NPC2.scale(1.5f, 1.5f, 1.5f);
        NPC2.updateGeometricState(0, true);
        addGameWorldObject(NPC2);

        NPC3 = new Arc170();
        NPC3.translate(5, 5, -800);
        NPC3.scale(1.5f, 1.5f, 1.5f);
        NPC3.updateGeometricState(0, true);
        addGameWorldObject(NPC3);

        NPC4 = new Viper();
        NPC4.translate(10, 5, -800);
        NPC4.scale(1.5f, 1.5f, 1.5f);
        NPC4.updateGeometricState(0, true);
        addGameWorldObject(NPC4);

        String materialDir = "." + File.separator + "materials" + File.separator;
        String finishtextureFilename = "finish.jpg";
        String finishtextureFilePath = materialDir + finishtextureFilename;
        Texture finishtexture = TextureManager.loadTexture2D(finishtextureFilePath);
        finish = new Cube("finish");

        finish.translate(0, 40, finishline);
        finish.rotate(180, new Vector3D(0, 1, 0));
        finish.rotate(180, new Vector3D(0, 0, 1));
        finish.scale(80, 40, 1);
        finish.setTexture(finishtexture);
        addGameWorldObject(finish);


        city = new CenterCity[3];
        for (int i = 0; i < 3; i++) {
            city[i] = new CenterCity();
            addGameWorldObject(city[i]);
            city[i].translate(235, 0, i * 1000);
            city[i].scale(1.5f, 1.0f, 1.2f);
        }


        rotate = new RotationController(50, new Vector3D(1, 1, 1));

        cube = new CustomCube[cubeCount];

        while(cubeLocations == null){
            client.processPackets();
        }
        //String materialDir = "." + File.separator + "materials" + File.separator;
        String textureFilename = "elem.jpg";
        String textureFilePath = materialDir + textureFilename;
        Texture texture = TextureManager.loadTexture2D(textureFilePath);
        int j = 0;
        for (int i = 0; i < cubeCount; i++) {
            cube[i] = new CustomCube("bug", i);
            cube[i].translate(cubeLocations[j], cubeLocations[j + 1], cubeLocations[j + 2]);
            j += 3;
            cube[i].scale(5, 5, 5);
            cube[i].rotate(45, new Vector3D(1, 1, 1));
            //cube[i].addController(rotate);
            addGameWorldObject(cube[i]);
            cube[i].setTexture(texture);


        }
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
        float mass = 100.0f;

        BoundingSphere playerBoundingBox = (BoundingSphere) playerAvatar.getWorldBound();
        playerAvatarP = physicsEngine.addCapsuleObject(physicsEngine.nextUID(), mass, playerAvatar.getLocalTranslation().getValues(), playerBoundingBox.getRadius(), playerBoundingBox.getRadius());
        playerAvatar.setPhysicsObject(playerAvatarP);
        playerAvatarP.setBounciness(1.1f);
        playerAvatarP.setSleepThresholds(0.5f, 0.5f);
        playerAvatarP.setFriction(0.5f);
        playerAvatarP.setDamping(0.99f, 0.9f);
        float[] f = {0, 0, 100};
        playerAvatarP.setLinearVelocity(f);


        float[] rightRailSize = {10, 500, 20000};
        rightRailP = physicsEngine.addBoxObject(physicsEngine.nextUID(), 0, rightRail.getLocalTranslation().getValues(), rightRailSize);
        rightRailP.setBounciness(0.1f);
        rightRailP.getTransform()[6] = 50;
        rightRail.setPhysicsObject(rightRailP);


        float[] leftRailSize = {10, 500, 20000};
        leftRailP = physicsEngine.addBoxObject(physicsEngine.nextUID(), 0, leftRail.getLocalTranslation().getValues(), leftRailSize);
        leftRailP.setBounciness(0.1f);
        leftRail.setPhysicsObject(leftRailP);


        BoundingSphere NPC1BoundingBox = (BoundingSphere) NPC1.getWorldBound();

        NPC1P = physicsEngine.addCapsuleObject(physicsEngine.nextUID(), mass, NPC1.getLocalTranslation().getValues(), 3f, 3f);
        NPC1P.setBounciness(1.1f);
        NPC1P.setDamping(0.1f, 0.1f);
        NPC1.setPhysicsObject(NPC1P);

        NPC2P = physicsEngine.addCapsuleObject(physicsEngine.nextUID(), mass, NPC2.getLocalTranslation().getValues(), 3f, 3f);
        NPC2P.setBounciness(1.1f);
        NPC2P.setDamping(0.1f, 0.1f);
        NPC2.setPhysicsObject(NPC2P);

        NPC3P = physicsEngine.addCapsuleObject(physicsEngine.nextUID(), mass, NPC3.getLocalTranslation().getValues(), 3f, 3f);
        NPC3P.setBounciness(1.1f);
        NPC3P.setDamping(0.1f, 0.1f);
        NPC3.setPhysicsObject(NPC3P);

        NPC4P = physicsEngine.addCapsuleObject(physicsEngine.nextUID(), mass, NPC4.getLocalTranslation().getValues(), 3f, 3f);
        NPC4P.setBounciness(1.1f);
        NPC4P.setDamping(0.1f, 0.1f);
        NPC4.setPhysicsObject(NPC4P);


        // add the ground groundPlane physics
        float up[] = {0,1,0}; // {0,1,0} is flat
        groundPlaneP = physicsEngine.addStaticPlaneObject(physicsEngine.nextUID(), groundPlane[0].getWorldTranslation().getValues(), up, 0.0f);
        groundPlaneP.setBounciness(0.1f);
        groundPlaneP.setFriction(0);
        //groundPlane.setPhysicsObject(groundPlaneP);


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
        //rootNode = (SceneNode) engine.get("rootNode");
        cubeCount = Integer.decode(engine.get("cubeCount").toString());

        try {
            client.sendPacket(new CubeCountPacket(cubeCount));
        } catch (IOException e) {
            e.printStackTrace();
        }
        //addGameWorldObject(rootNode);
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
    //========================================================================================INPUT SECTION
    //=====================================================================================================
    private void setControls() {
        String keyboardName = JOptionPane.showInputDialog(null, "Pick a keyboard", "Input", JOptionPane.QUESTION_MESSAGE, null, inputMgr.getControllers().toArray(), "keyboard").toString();
        //String keyboardName = inputMgr.getKeyboardName();
        ArrayList<Controller> controllers = inputMgr.getControllers();
        //String controllerName;
        //if (controllers.size() > 2)  controllerName = controllers.get(3).getName();
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
                new MovePlayerLeftAction(playerAvatar, imageTerrain, client, playerAvatarP), IInputManager.INPUT_ACTION_TYPE.REPEAT_WHILE_DOWN);
        inputMgr.associateAction(
                keyboardName, Component.Identifier.Key.D,
                new MovePlayerRightAction(playerAvatar, imageTerrain, client, playerAvatarP), IInputManager.INPUT_ACTION_TYPE.REPEAT_WHILE_DOWN);
        inputMgr.associateAction(
                keyboardName, Component.Identifier.Key.F2,
                new TogglePhysics(this), IInputManager.INPUT_ACTION_TYPE.ON_PRESS_AND_RELEASE);
        inputMgr.associateAction(
                keyboardName, Component.Identifier.Key.SPACE, new Jump(this), IInputManager.INPUT_ACTION_TYPE.ON_PRESS_ONLY);

        if (keyboardName.equals("Controller (XBOX 360 For Windows)")){

//            inputMgr.associateAction(keyboardName, Component.Identifier.Axis.RX, new OrbitAroundHorizAction(), IInputManager.INPUT_ACTION_TYPE.REPEAT_WHILE_DOWN);
//            inputMgr.associateAction(keyboardName, Component.Identifier.Axis.RY, new OrbitAroundVertAction(), IInputManager.INPUT_ACTION_TYPE.REPEAT_WHILE_DOWN);
//            inputMgr.associateAction(keyboardName, Component.Identifier.Axis.Z, new Zoom(), IInputManager.INPUT_ACTION_TYPE.REPEAT_WHILE_DOWN);
//            im.associateAction(cn, Component.Identifier.Button._9, new OrbitResetAction(), IInputManager.INPUT_ACTION_TYPE.ON_PRESS_ONLY);
//            inputMgr.associateAction(keyboardName, Component.Identifier.Button._4, new TurnLeftAction(), IInputManager.INPUT_ACTION_TYPE.REPEAT_WHILE_DOWN);
//            inputMgr.associateAction(keyboardName, Component.Identifier.Button._5, new TurnRightAction(), IInputManager.INPUT_ACTION_TYPE.REPEAT_WHILE_DOWN);


            inputMgr.associateAction(keyboardName,
                    Component.Identifier.Axis.X,
                    new MoveXAxis(playerAvatar, playerAvatarP), IInputManager.INPUT_ACTION_TYPE.REPEAT_WHILE_DOWN);
            inputMgr.associateAction(keyboardName,
                    Component.Identifier.Axis.Y,
                    new MoveYAxis(playerAvatar, playerAvatarP), IInputManager.INPUT_ACTION_TYPE.REPEAT_WHILE_DOWN);
            inputMgr.associateAction(
                    keyboardName, Component.Identifier.Button._0, new Jump(this), IInputManager.INPUT_ACTION_TYPE.ON_PRESS_ONLY);
        }

    }

    public void jump() {

        float[] f = playerAvatarP.getLinearVelocity();
        f[1] = f[1] + 100;
        playerAvatarP.setLinearVelocity(f);
    }
    //=====================================================================================================
    //============================================================================================ TERRAIN SECTION
    //=====================================================================================================
    private void initTerrain() {
//        // create height map and terrain block
//        String heightDir = "." + File.separator + "materials" + File.separator;
//        String heightFilename = "road.jpg";
//        String heightFilePath = heightDir + heightFilename;
//        ImageBasedHeightMap myHeightMap = new ImageBasedHeightMap(heightFilePath);
//        imageTerrain = createTerBlock(myHeightMap);
//
//        // create texture and texture state to color the terrain
//        TextureState grassState;
//        String heighttextureDir = "." + File.separator + "materials" + File.separator;
//        String heighttextureFilename = "green.jpg";
//        String heighttextureFilePath = heighttextureDir + heighttextureFilename;
//        Texture grassTexture = TextureManager.loadTexture2D(heighttextureFilePath);
//        grassTexture.setApplyMode(sage.texture.Texture.ApplyMode.Replace);
//        grassState = (TextureState) display.getRenderer().createRenderState(RenderState.RenderStateType.Texture);
//        grassState.setTexture(grassTexture, 0);
//        grassState.setEnabled(true);
//        // apply the texture to the terrain
//        imageTerrain.setRenderState(grassState);
//        imageTerrain.scale(0.4f, 1, 10);
//        imageTerrain.translate(-50, -2, -1000);
        //addGameWorldObject(imageTerrain);

        //Floor groundPlane

        String groundextureDir = "." + File.separator + "materials" + File.separator;
        String groundexturefilename = "Asphalt.jpg";
        String groundexturefilepath = groundextureDir + groundexturefilename;
        Texture groundexture = TextureManager.loadTexture2D(groundexturefilepath);
        Vector3D vec = new Vector3D(1, 0, 0);
        groundPlane = new Rectangle[30];
        for (int i = 0; i < 30; i++) {
            groundPlane[i] = new Rectangle("ground", 300, 1000);
            groundPlane[i].rotate(90, vec);
            groundPlane[i].setColor(Color.GRAY);
            groundPlane[i].setTexture(groundexture);
            groundPlane[i].translate(0, 0, i * 1000);
            addGameWorldObject(groundPlane[i]);
        }

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
        return new TerrainBlock(name, terrainSize, terrainScale, heightMap.getHeightData(), terrainOrigin);

    }

    //=====================================================================================================
    //============================================================================================ SKY BOX
    //=====================================================================================================
    private void drawSkyBox() {
        skybox = new SkyBox("skybox", 2000, 2000, 2000);

        String textureDir = "." + File.separator + "materials" + File.separator + "city" + File.separator;
        String topFilename = "top.jpg";
        String topFilePath = textureDir + topFilename;

        String bottomFilename = "bottom.jpg";
        String bottomFilePath = textureDir + bottomFilename;

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
        Texture bottom = TextureManager.loadTexture2D(bottomFilePath);
        skybox.setTexture(SkyBox.Face.Down, bottom);
        Texture front = TextureManager.loadTexture2D(frontFilePath);
        skybox.setTexture(SkyBox.Face.North, front);
        Texture back = TextureManager.loadTexture2D(backFilePath);
        skybox.setTexture(SkyBox.Face.South, back);
        Texture left = TextureManager.loadTexture2D(leftFilePath);
        skybox.setTexture(SkyBox.Face.West, left);
        Texture right = TextureManager.loadTexture2D(rightFilePath);
        skybox.setTexture(SkyBox.Face.East, right);

        //skybox.translate(0, 500, 0);

        skybox.setZBufferStateEnabled(false);
        // skybox.rotate(-45,new Vector3D(0,1,0));
        addGameWorldObject(skybox);

    }

    private void addPlayer() {
        if(player.getCharacterID() == 0){
            playerAvatar = new Arc170();
            player.setAvatarName("Arc170");
        }/*else if(player.getCharacterID() == 0){
            playerAvatar = new Viper();
            player.setAvatarName("Viper");
        }*/else if(player.getCharacterID() == 1){
            playerAvatar = new Ship().getChild();
            player.setAvatarName("SpaceShip");
        }else if(player.getCharacterID() == 2){
            playerAvatar = new Pod().getChild();
            player.setAvatarName("SpacePod");
        }else if(player.getCharacterID() == 3){
            playerAvatar = myObject;
            player.setAvatarName("SpaceShip Animated");
        }

        //set the character ID here and catch it in addGhostAvatar();

        //playerAvatar.scale(0.2f, 0.2f, 0.2f);
        playerAvatar.rotate(180, new Vector3D(0, 1, 0));
        playerAvatar.translate(0, 3, 0);
        //playerAvatar.setShowBound(true);
        playerAvatar.updateGeometricState(0, true);

        updateOldPosition();

        try {
            client.sendPacket(new AddAvatarInformationPacket(client.getId(), player.getCharacterID()));
        } catch (IOException e) {
            e.printStackTrace();
        }
        addGameWorldObject(playerAvatar);
        camera1 = display.getRenderer().getCamera();
        camera1.setPerspectiveFrustum(90, 1, 1, 10000);
        camera1.setLocation(new Point3D(0, 1, 50));

        avatarName = new HUDString("Model: " + player.getAvatarName());
        avatarName.setName("Player1ID");
        avatarName.setLocation(0.47, 0.97);
        avatarName.setRenderMode(sage.scene.SceneNode.RENDER_MODE.ORTHO);
        avatarName.setColor(Color.white);
        avatarName.setCullMode(sage.scene.SceneNode.CULL_MODE.NEVER);
        camera1.addToHUD(avatarName);

        scoreString1 = new HUDString("");
        scoreString1.setName("Player1ID");
        scoreString1.setLocation(0.47, 0.95);
        scoreString1.setRenderMode(sage.scene.SceneNode.RENDER_MODE.ORTHO);
        scoreString1.setColor(Color.white);
        scoreString1.setCullMode(sage.scene.SceneNode.CULL_MODE.NEVER);
        camera1.addToHUD(scoreString1);

        speedString = new HUDString("");
        speedString.setName("Speed");
        speedString.setLocation(0.47, 0.93);
        speedString.setRenderMode(sage.scene.SceneNode.RENDER_MODE.ORTHO);
        speedString.setColor(Color.white);
        speedString.setCullMode(sage.scene.SceneNode.CULL_MODE.NEVER);
        camera1.addToHUD(speedString);

    }


    //-------------------------------------------------------------------------------------------------------
    // ----------------------------------------------------------------------UPDATE SECTION -----------------
    //-------------------------------------------------------------------------------------------------------

    private void updateOldPosition() {


        oldRotation = playerAvatar.getWorldRotation().toString();
        oldTranslation = playerAvatar.getWorldTranslation().toString();
        oldScale = playerAvatar.getWorldScale().toString();
        playerAvatar.setWorldRotation(playerAvatar.getWorldRotation());
        playerAvatar.setWorldTranslation(playerAvatar.getWorldTranslation());
        playerAvatar.setWorldScale(playerAvatar.getWorldScale());
    }

    private void updateHUD() {
        float[] speed = playerAvatarP.getLinearVelocity();
        scoreString1.setText("Score: " + score);
        speedString.setText("Speed: " + ((int) speed[2] / 10) + " mph");
    }

    //=====================================================================================================
    //============================================================================================ UPDATE SECTION
    //=====================================================================================================
    @Override
    protected void update(float time) {
        updateHUD();
        this.time += time;
        cam1Controller.update(this.time);
        super.update(time);
        myObject.updateAnimation(time);
        if(playerChanged()){
            try {
                client.sendPacket(new UpdateAvatarInfoPacket(client.getId(), playerAvatar.getLocalTranslation(), playerAvatar.getLocalScale(), playerAvatar.getLocalRotation()));
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        //--------------------------------------------------------------SKYBOX FOLLOWING THE AVATAR


        Matrix3D wt = (playerAvatar.getLocalTranslation());
        skybox.setLocalTranslation(wt);

//        Matrix3D wr = (skybox.getLocalRotation());
//        wr.rotate(1,new Vector3D(0,1,0));
//        skybox.setLocalRotation(wt);


        //skybox.setLocalTranslation(playerAvatar.getLocalTranslation());
        // -------------------------------------------------------------APPLIES FRICTION WHEN IN IMAGETERRAIN

//        Point3D avLoc = new Point3D(playerAvatar.getWorldTranslation().getCol(3));
//
//        float terHeight = imageTerrain.getHeightFromWorld(avLoc);
//        if (avLoc.getY()-4 <= terHeight) {
//            playerAvatarP.setFriction(9999.9f);
//            JOptionPane.showMessageDialog(null,"COllision");
//        } else {
//            playerAvatarP.setFriction(0);
//        }

        //playerAvatar.getLocalTranslation().setElementAt(1, 3, desiredHeight);



            SceneNode particle = NPC1;

            float[] swarmBehaviour = new float[3];
            {
                Point3D finishPoint = new Point3D(finish.getLocalTranslation().getCol(3));
                Point3D startPoint = new Point3D(particle.getLocalTranslation().getCol(3));
                Vector3D swarmVector = new Vector3D((finishPoint.getX() - startPoint.getX()), (finishPoint.getY() - startPoint.getY()), (finishPoint.getZ() - startPoint.getZ()));
                swarmVector.normalize();
                swarmBehaviour[0] = (float) (swarmVector.getX() * 0.3);
                swarmBehaviour[1] = (float) (swarmVector.getY() * 0.3);
                swarmBehaviour[2] = (float) (swarmVector.getZ() * 0.3);
            }


            float[] particleBehaviour = new float[3];
            {
                Point3D finishPoint = new Point3D(playerAvatar.getLocalTranslation().getCol(3));
                Point3D startPoint = new Point3D(particle.getLocalTranslation().getCol(3));
                Vector3D particleVector = new Vector3D((finishPoint.getX() - startPoint.getX()), (finishPoint.getY() - startPoint.getY()), (finishPoint.getZ() - startPoint.getZ()));
                particleVector.normalize();
                particleBehaviour[0] = (float) (particleVector.getX() * 0.5);
                particleBehaviour[1] = (float) (particleVector.getY() * 0.5);
                particleBehaviour[2] = (float) (particleVector.getZ() * 0.5);
            }


            float[] behaviour = new float[3];


            behaviour[0] = (swarmBehaviour[0] + particleBehaviour[0]) * rand.nextFloat();
            behaviour[1] = (swarmBehaviour[1] + particleBehaviour[1]) * rand.nextFloat();
            behaviour[2] = (swarmBehaviour[2] + particleBehaviour[2]) * rand.nextFloat();
        NPC1P.setLinearVelocity(behaviour);


            behaviour[0] = (swarmBehaviour[0] + particleBehaviour[0]) * rand.nextFloat();
            behaviour[1] = (swarmBehaviour[1] + particleBehaviour[1]) * rand.nextFloat();
            behaviour[2] = (swarmBehaviour[2] + particleBehaviour[2]) * rand.nextFloat();
        NPC2P.setLinearVelocity(behaviour);

        behaviour[0] = (swarmBehaviour[0] + particleBehaviour[0]) * rand.nextFloat();
        behaviour[1] = (swarmBehaviour[1] + particleBehaviour[1]) * rand.nextFloat();
        behaviour[2] = (swarmBehaviour[2] + particleBehaviour[2]) * rand.nextFloat();
        NPC3P.setLinearVelocity(behaviour);


        behaviour[0] = (swarmBehaviour[0] + particleBehaviour[0]) * rand.nextFloat();
        behaviour[1] = (swarmBehaviour[1] + particleBehaviour[1]) * rand.nextFloat();
        behaviour[2] = (swarmBehaviour[2] + particleBehaviour[2]) * rand.nextFloat();
        NPC4P.setLinearVelocity(behaviour);


        if (NPC1.getWorldTranslation().getCol(3).getZ() > finishline) NPC1P.setLinearVelocity(new float[]{0, 200, 0});
        if (NPC2.getWorldTranslation().getCol(3).getZ() > finishline) NPC1P.setLinearVelocity(new float[]{0, 200, 0});
        if (NPC3.getWorldTranslation().getCol(3).getZ() > finishline) NPC1P.setLinearVelocity(new float[]{0, 200, 0});
        if (NPC4.getWorldTranslation().getCol(3).getZ() > finishline) NPC1P.setLinearVelocity(new float[]{0, 200, 0});
        //playerAvatarP.setTransform(playerAvatar.getLocalTransform().getValues());


        //for (SceneNode s : getGameWorld()){

//            }




        if(client != null){
            client.processPackets();
        }


        npcSound.setLocation(new Point3D(NPC1.getLocalTranslation().getCol(3)));
        windSound.setLocation(new Point3D(playerAvatar.getLocalTranslation().getCol(3)));
        windSound.setVolume((int) (playerAvatarP.getLinearVelocity()[2] * 0.05));
        setEarParameters();


        //SHIFTING CITY
        Point3D avLoc = new Point3D(playerAvatar.getWorldTranslation().getCol(3));
        for (int i = 1; i < 20; i++) {
            if ((avLoc.getZ() > i * 1000) && (avLoc.getZ() < (i + 1) * 1000) && (city[0].getChild().getWorldTranslation().getCol(3).getZ() < i * 1000))
                city[0].translate(0, 0, 3000);
            if ((avLoc.getZ() > (i + 1) * 1000) && (avLoc.getZ() < (i + 2) * 1000) && (city[1].getChild().getWorldTranslation().getCol(3).getZ() < (i + 1) * 1000))
                city[1].translate(0, 0, 3000);
            if ((avLoc.getZ() > (i + 2) * 1000) && (avLoc.getZ() < (i + 3) * 1000) && (city[2].getChild().getWorldTranslation().getCol(3).getZ() < (i + 2) * 1000))
                city[2].translate(0, 0, 3000);

        }
        if (avLoc.getY() > 10) {
            float[] f = playerAvatarP.getLinearVelocity();
            f[1] -= 50;
            playerAvatarP.setLinearVelocity(f);
        }

        Matrix3D mat;
        Vector3D translateVec, rotateVec;
        for (SceneNode s : getGameWorld()) {

            if (s.getPhysicsObject() != null) {
                mat = new Matrix3D(s.getPhysicsObject().getTransform());
                translateVec = mat.getCol(3);
                //rotateVec = mat.getCol(2);
                s.getLocalTranslation().setCol(3, translateVec);
                //s.getLocalRotation().setCol(3,rotateVec);
            }

            if (s.getName().equalsIgnoreCase("bug")) {
                //s.rotate(20, new Vector3D(rand.nextInt(1), rand.nextInt(1), rand.nextInt(1)));
                s.rotate(20, new Vector3D(1, 1, -1));
                //s.scale(0, 0, 0.005f);
            }
            if (s.getWorldBound() != null) {
                if (s.getWorldBound().intersects(playerAvatar.getWorldBound()) && s.getName().equalsIgnoreCase("bug")) {
                    whooshSound.setLocation(avLoc);
                    whooshSound.play();
                    score += 10;
                    System.out.println("COLLISION SCORE" + score);
                    float[] f = playerAvatarP.getLinearVelocity();
                    f[0] = f[0] * 1.2f;
                    f[2] = f[2] * 1.2f;
                    playerAvatarP.setLinearVelocity(f);
                    try {
                        client.sendPacket(new HitCubePacket(((CustomCube) s).getID()));
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                    removeGameWorldObject(s);

                    break;
                }

            }
        }

        if (!gameover & finish.getWorldBound().intersects(playerAvatar.getWorldBound())) gameOver();

        // update the HUD
//        scoreString.setText("Score = " + score);
//        time += elapsedTimeMS;
//        DecimalFormat df = new DecimalFormat("0.0"); timeString.setText("Time = " + df.format(time/1000));
//

        super.update(time);
        physicsEngine.update(time);

//        if (playerAvatar.getWorldBound().intersects(finish.getWorldBound())) {
//            finish.translate(0, 0, 100);
//            playerAvatar.scale(10, 10, 10);
//
//            JOptionPane.showMessageDialog(null, "YOU WIN!!!!!!");
//        }
    }

    //-------------------------------------------------------------------------------------------------------
    // ----------------------------------------------------------------------SOUND SECTION -----------------
    //-------------------------------------------------------------------------------------------------------

    public void gameOver() {
        gameover = true;
        float[] f = {0, 100, 0};
        playerAvatarP.setLinearVelocity(f);
        playerAvatarP.setDamping(0, 0);
        f = new float[]{0, 3000, 0};
        physicsEngine.setGravity(f);

        //playerAvatar.scale(10,10,10);
        removeGameWorldObject(finish);
        try {
            client.sendPacket(new FinishedPacket(score, client.getId(), player.getPlayerName()));
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void allFinishedGame(AllFinishedPacket packet){
        StringBuilder stringBuilder = new StringBuilder();
        ArrayList<EndScore> endScores = packet.getEndScores();
        stringBuilder.append("\t\tFinal Score:\nPlayer Name          Score\n");
        for(int i = 0; i<endScores.size(); i++){
            EndScore e = endScores.get(i);
            stringBuilder.append((i+1)+") "+e.getPlayerName()+"      "+e.getScore()+"\n");
        }
        JOptionPane.showMessageDialog(null, stringBuilder.toString());
    }

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
        String whooshFilename = "s.wav";
        String whooshFilePath = soundDir + whooshFilename;
        String shipFilename = "ship1.wav";
        String shipFilePath = soundDir + shipFilename;
        String floopFilename = "floop.wav";
        String floopFilePath = soundDir + floopFilename;
        resource1 = audioMgr.createAudioResource(shipFilePath, AudioResourceType.AUDIO_SAMPLE);
        resource2 = audioMgr.createAudioResource(whooshFilePath, AudioResourceType.AUDIO_SAMPLE);
        resource3 = audioMgr.createAudioResource(strongwindFilePath, AudioResourceType.AUDIO_SAMPLE);
        npcSound = new Sound(resource1, SoundType.SOUND_EFFECT, 10, true);
        whooshSound = new Sound(resource2, SoundType.SOUND_EFFECT, 100, false);
        windSound = new Sound(resource3, SoundType.SOUND_EFFECT, 100, true);


//        whooshSound = new Sound[40];
//        for(int i =0;i<40;i++){
//        whooshSound[i] = new Sound(resource2, SoundType.SOUND_EFFECT, 100, false);
//            whooshSound[i].initialize(audioMgr);
//            whooshSound[i].setMaxDistance(200);
//            whooshSound[i].setMinDistance(50.0f);
//            whooshSound[i].setRollOff(5.0f);
//            whooshSound[i].setLocation(new Point3D(cube[i].getLocalTranslation().getCol(3)));
//        }


        npcSound.initialize(audioMgr);
        windSound.initialize(audioMgr);
        whooshSound.initialize(audioMgr);


        npcSound.setMaxDistance(200);
        npcSound.setMinDistance(50.0f);
        npcSound.setRollOff(5.0f);
        windSound.setMaxDistance(200);
        windSound.setMinDistance(50.0f);
        windSound.setRollOff(5.0f);
        whooshSound.setMaxDistance(10);
        whooshSound.setMinDistance(10.0f);
        whooshSound.setRollOff(1.0f);


        npcSound.setLocation(new Point3D(NPC1.getLocalTranslation().getCol(3)));
        windSound.setLocation(new Point3D(playerAvatar.getLocalTranslation().getCol(3)));
        whooshSound.setLocation(new Point3D(playerAvatar.getLocalTranslation().getCol(3)));

        setEarParameters();
        npcSound.play();
        windSound.play();
        //whooshSound.play();


    }

    public void setEarParameters() {
        Matrix3D avDir = (Matrix3D) (playerAvatar.getLocalRotation().clone());
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
        if (!oldRotation.equals(playerAvatar.getWorldRotation().toString()) ||
                !oldTranslation.equals(playerAvatar.getWorldTranslation().toString()) ||
                !oldScale.equals(playerAvatar.getWorldScale().toString())) {
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


    public void updateGhostAvatars(AllPlayerInfoPacket packet) {
        ArrayList<SimplePlayerInfo> simplePlayerInfos = packet.getSimple();
        for(SimplePlayerInfo s : simplePlayerInfos){
            UUID id = s.getClientID();
            if(s.getTranslation() != null && s.getScale() != null && s.getRotation() != null){
                Matrix3D translation = new Matrix3D();
                translation.concatenate(s.getTranslation());
                Matrix3D scale = new Matrix3D();
                scale.concatenate(s.getScale());
                Matrix3D rotation = new Matrix3D();
                rotation.concatenate(s.getRotation());
                if (!id.toString().equals(this.player.getPlayerUUID().toString())){
                    for(PlayerInfo p : this.playersInfo){
                        if(id.toString().equals(p.getClientID().toString())){
                            p.getAvatar().setLocalScale(scale);
                            p.getAvatar().setLocalRotation(rotation);
                            p.getAvatar().setLocalTranslation(translation);
                        }
                    }
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
            /*if(player.getCharacterID() == 0) {
                player.setAvatar(new Viper());
            }else*/ if(player.getCharacterID() == 0){
                player.setAvatar(new Arc170());
            }else if (player.getCharacterID() == 1){
                player.setAvatar(new Ship().getChild());
            }else if(player.getCharacterID() == 2) {
                player.setAvatar(new Pod().getChild());
            }else if(player.getCharacterID() == 3) {
                player.setAvatar(myObject);
            }


            player.getAvatar().translate(0, 5, -1000);
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


    public void updateNPCGhosts(NPCPacket packet) {
        if(this.getGameWorld() != null){
            if(this.npcs == null){
                this.npcs = packet.getNpcs();
                createNPCGhosts();
            }
            ArrayList<NPC> tempNPCs = packet.getNpcs();
            for(NPCGhost n : npcsGhosts){
                for(NPC n1 : tempNPCs){
                    if(n.getID() == n1.getID()){
                        n.updateNPCGhost(n1);
                    }
                }
            }
        }
    }

    private void createNPCGhosts() {
        npcsGhosts = new ArrayList<NPCGhost>();
        for(NPC npc : npcs){
            SceneNode npcGhost = null;
            //Add necessary NPC types to this.
            if(npc.getType().equals("SpaceShip")){
                npcGhost = new Ship().getChild();
            }else if(npc.getType().equals("SpacePod")){
                npcGhost = new Pod().getChild();
            }else if(npc.getType().equals("Rook")){
                npcGhost = new ChessPieceRock("avatar").getChild();
            }else if(npc.getType().equals("Cube")){
                npcGhost = new Cube("Cube");
            }else{
                try {
                    throw new Exception(String.format("Can't find this (%s) in the MazeGame createNPCGhosts().  Make sure to add it to the if statement.", npc.getType()));
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
            if(npcGhost != null){
                npcGhost.setLocalRotation(npc.getRotation());
                npcGhost.setLocalTranslation(npc.getTranslation());
                npcGhost.setLocalScale(npc.getScale());
                npcsGhosts.add(new NPCGhost(npcGhost, npc.getID(), npc.getLocation(), npc.getType()));
                addGameWorldObject(npcGhost);
            }
        }
    }

    public void setCubeLocations(float[] cubeLocations) {
        this.cubeLocations = cubeLocations;
    }

    public void removeCube(int id) {
        for (int i = 0; i < 100; i++) {
            if(cube[i].getID() == id){
                removeGameWorldObject(cube[i]);
            }
        }
    }
}
class NPCGhost{
    private SceneNode avatar;
    private int ID;
    private Point3D location;
    private String type;

    public NPCGhost(SceneNode avatar, int ID, Point3D location, String type) {
        this.avatar = avatar;
        this.ID = ID;
        this.location = location;
        this.type = type;
    }

    public void updateNPCGhost(NPC npc){
        this.avatar.setLocalTranslation(npc.getTranslation());
        this.avatar.setLocalRotation(npc.getRotation());
        this.avatar.setLocalScale(npc.getScale());
        //this.location = npc.getLocation();
    }

    public SceneNode getAvatar() {
        return avatar;
    }

    public int getID() {
        return ID;
    }

    public Point3D getLocation() {
        return location;
    }

    public String getType() {
        return type;
    }


}
