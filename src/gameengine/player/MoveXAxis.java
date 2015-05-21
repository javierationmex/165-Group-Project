package gameengine.player;

import graphicslib3D.Matrix3D;
import graphicslib3D.Vector3D;
import net.java.games.input.Event;
import sage.input.action.AbstractInputAction;
import sage.physics.IPhysicsObject;
import sage.scene.SceneNode;

/**
 * Created by Javier G on 2/24/2015.
 */
public class MoveXAxis extends AbstractInputAction
{
    private float speed = 0.05f;
    private SceneNode avatar;
    private IPhysicsObject playerAvatarP;
    public MoveXAxis(SceneNode c, IPhysicsObject playerAvatarP)
    {
        avatar = c;
        this.playerAvatarP = playerAvatarP;
    }

    @Override
    public void performAction(float time, Event e) {

        if (e.getValue() < -0.4)
        {
            Matrix3D rot = avatar.getLocalRotation();
            Vector3D dir = new Vector3D(-1,0,0);
            dir = dir.mult(rot);
            dir.scale((double) (speed * time));


            float[] f = playerAvatarP.getLinearVelocity();
            f[0] += (float) dir.getX() * 10;
            f[2] += (float) dir.getZ() * 10;
            if (f[0] < 1000)
                if (f[2] < 1000){
                    playerAvatarP.setLinearVelocity(f);
                }

        }
        else if (e.getValue() > 0.4)
        {
            Matrix3D rot = avatar.getLocalRotation();
            Vector3D dir = new Vector3D(1,0,0);
            dir = dir.mult(rot);
            dir.scale((double) (speed * time));


            float[] f = playerAvatarP.getLinearVelocity();
            f[0] += (float) dir.getX() * 10;
            f[2] += (float) dir.getZ() * 10;
            if (f[0] < 1000)
                if (f[2] < 1000){
                    playerAvatarP.setLinearVelocity(f);
                }

        }
    }
}
