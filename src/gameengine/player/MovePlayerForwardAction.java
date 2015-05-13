package gameengine.player;

import graphicslib3D.Matrix3D;
import graphicslib3D.Vector3D;
import net.java.games.input.Event;
import networking.Client;
import sage.physics.IPhysicsObject;
import sage.scene.SceneNode;
import sage.terrain.TerrainBlock;

/**
 * Created by Max on 3/8/2015.
 * but modified mostly by Arash! :P
 */
public class MovePlayerForwardAction extends BaseAbstractInputAction {

    private final Client client;
    private SceneNode avatar;
    private IPhysicsObject playerAvatarP;
    private float speed = 0.05f;
    private TerrainBlock terrain;

    public MovePlayerForwardAction(SceneNode n, TerrainBlock imageTerrain, Client client, IPhysicsObject playerAvatarP) {
        avatar = n;
        this.client = client;
        this.playerAvatarP = playerAvatarP;
        terrain = imageTerrain;
    }

    public void performAction(float time, Event e){

        Matrix3D rot = avatar.getLocalRotation();
        Vector3D dir = new Vector3D(0, 0, -1);
        dir = dir.mult(rot);
        dir.scale((double) (speed * time));

        Vector3D projectedDirection = new Vector3D(0, 0, -1);
//        projectedDirection = projectedDirection.mult(rot);
//        //projectedDirection.scale(2);
//        projectedDirection.add(avatar.getWorldTranslation().getCol(3));
//
//        float projectedx = (float) projectedDirection.getX();
//        float projectedy = (float) projectedDirection.getY();
//        float projectedz = (float) projectedDirection.getZ();
//        float terrainHeight = terrain.getHeight(projectedx, projectedz);
        //if (projectedy <= terrainHeight) {
            //dir=new Vector3D(0, 0, 0);

            // playerAvatarP.setDamping(40f,0f);
            //avatar.translate((float) dir.getX(), (float) dir.getY(), (float) dir.getZ());
        //}

        playerAvatarP.setLinearVelocity(new float[]{(float) dir.getX() * 100, 0, (float) dir.getZ() * 100});
        //playerAvatarP.setFriction(100000);
        //playerAvatarP.setSleepThresholds(1,1);
//        //CHECKING HEIGHTS
//        Point3D avLoc = new Point3D(avatar.getLocalTranslation().getCol(3));
//        float x = (float) avLoc.getX();
//        float y = (float) avLoc.getY();
//        float z = (float) avLoc.getZ();
//
//        float newx1 = x + 1;
//        float newy1 = y;
//        float newz1 = z + 1;
//        Point3D newloc1 = new Point3D(newx1, newy1, newz1);
//        float newterHeight1 = terrain.getHeightFromWorld(newloc1);
//
//        float newx2 = x - 1;
//        float newy2 = y;
//        float newz2 = z - 1;
//        Point3D newloc2 = new Point3D(newx2, newy2, newz2);
//        float newterHeight2 = terrain.getHeightFromWorld(newloc2);
//
//
//        float newx3 = x + 1;
//        float newy3 = y;
//        float newz3 = z - 1;
//        Point3D newloc3 = new Point3D(newx3, newy3, newz3);
//        float newterHeight3 = terrain.getHeightFromWorld(newloc3);
//
//        float newx4 = x - 1;
//        float newy4 = y;
//        float newz4 = z + 1;
//        Point3D newloc4 = new Point3D(newx4, newy4, newz4);
//        float newterHeight4 = terrain.getHeightFromWorld(newloc4);
//
//        if (y > newterHeight1 && y > newterHeight2 && y > newterHeight3 && y > newterHeight4  ) {
//           // System.out.println("collision");


        //dir.scale(100);
        //float[] pdirection = {(float) dir.getX(), (float) dir.getY(), (float) dir.getZ()};
        //playerAvatarP.setFriction(100f);
        //double[] ptdirection = { dir.getX(),  dir.getY(),  dir.getZ(),0};
        //playerAvatarP.setTransform(avatar.getWorldTransform().getValues());
        // playerAvatarP.setLinearVelocity(pdirection);
//        }



    }

}