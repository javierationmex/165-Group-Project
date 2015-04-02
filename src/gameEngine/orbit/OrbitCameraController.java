package gameengine.orbit;

import gameengine.player.BaseAbstractInputAction;
import graphicslib3D.Point3D;
import graphicslib3D.Vector3D;
import net.java.games.input.Component;
import net.java.games.input.Event;
import networking.Client;
import sage.camera.ICamera;
import sage.input.IInputManager;
import sage.input.action.AbstractInputAction;
import sage.scene.SceneNode;
import sage.util.MathUtils;

/**
 * Created by Max on 3/8/2015.
 */
public class OrbitCameraController {

    private ICamera cam;
    private SceneNode target;
    private float cameraAzimuth;
    private float cameraElevation;
    private float cameraDistanceFromTarget;
    private Point3D targetPos;
    private Vector3D worldUpVec;
    private final Client client;

    public OrbitCameraController(ICamera cam, SceneNode target, IInputManager inputMgr, String controllerName, Client client) {
        this.cam = cam;
        this.target = target;
        worldUpVec = new Vector3D(0, 1, 0);
        cameraDistanceFromTarget = 5.0f;
        cameraAzimuth = 180;
        cameraElevation = 20.0f;
        update(0.0f);
        setupInput(inputMgr, controllerName);
        this.client = client;
    }
    public void update(float time)
    {
        updateTarget();
        updateCameraPosition();
        cam.lookAt(targetPos, worldUpVec);
    }
    private void updateTarget()
    {
        targetPos = new Point3D(target.getWorldTranslation().getCol(3));
    }

    private void updateCameraPosition()
    {
        double theta = cameraAzimuth;
        double phi = cameraElevation ;
        double r = cameraDistanceFromTarget;
        Point3D relativePosition = MathUtils.sphericalToCartesian(theta, phi, r);
        Point3D desiredCameraLoc = relativePosition.add(targetPos);
        cam.setLocation(desiredCameraLoc);
    }

    private void setupInput(IInputManager im, String cn)
    {
        im.associateAction(cn, Component.Identifier.Axis.RY, new OrbitAroundAction(), IInputManager.INPUT_ACTION_TYPE.REPEAT_WHILE_DOWN);
        im.associateAction(cn, Component.Identifier.Axis.RX, new OrbitUpDownAction(), IInputManager.INPUT_ACTION_TYPE.REPEAT_WHILE_DOWN);
        im.associateAction(cn, Component.Identifier.Button._6, new ZoomOutAction(), IInputManager.INPUT_ACTION_TYPE.REPEAT_WHILE_DOWN);
        im.associateAction(cn, Component.Identifier.Button._7, new ZoomInAction(), IInputManager.INPUT_ACTION_TYPE.REPEAT_WHILE_DOWN);
        im.associateAction(cn, Component.Identifier.Button._4, new RotateCameraAndShapeLeftAction(), IInputManager.INPUT_ACTION_TYPE.REPEAT_WHILE_DOWN);
        im.associateAction(cn, Component.Identifier.Button._5, new RotateCameraAndShapeRightAction(), IInputManager.INPUT_ACTION_TYPE.REPEAT_WHILE_DOWN);

        im.associateAction(cn, Component.Identifier.Key.LEFT, new RotateCameraAndShapeLeftAction(), IInputManager.INPUT_ACTION_TYPE.REPEAT_WHILE_DOWN);
        im.associateAction(cn, Component.Identifier.Key.RIGHT, new RotateCameraAndShapeRightAction(), IInputManager.INPUT_ACTION_TYPE.REPEAT_WHILE_DOWN);
        im.associateAction(cn, Component.Identifier.Key.UP, new OrbitUpAction(), IInputManager.INPUT_ACTION_TYPE.REPEAT_WHILE_DOWN);
        im.associateAction(cn, Component.Identifier.Key.DOWN, new OrbitDownAction(), IInputManager.INPUT_ACTION_TYPE.REPEAT_WHILE_DOWN);
        im.associateAction(cn, Component.Identifier.Key.END, new ZoomOutAction(), IInputManager.INPUT_ACTION_TYPE.REPEAT_WHILE_DOWN);
        im.associateAction(cn, Component.Identifier.Key.PAGEDOWN, new ZoomInAction(), IInputManager.INPUT_ACTION_TYPE.REPEAT_WHILE_DOWN);
        im.associateAction(cn, Component.Identifier.Key.HOME, new OrbitRightAction(), IInputManager.INPUT_ACTION_TYPE.REPEAT_WHILE_DOWN);
        im.associateAction(cn, Component.Identifier.Key.PAGEUP, new OrbitLeftAction(), IInputManager.INPUT_ACTION_TYPE.REPEAT_WHILE_DOWN);

    }

    private class OrbitAroundAction extends AbstractInputAction
    {
        public void performAction(float time, Event evt)
        {
            float rotAmount;
            if (evt.getValue() < -0.3){
                rotAmount=-0.1f;
            }
            else{
                if (evt.getValue() > 0.3){
                    rotAmount=0.1f;
                }
                else{
                    rotAmount=0.0f;
                }
            }
            cameraAzimuth += rotAmount ;
            cameraAzimuth = cameraAzimuth % 360 ;
        }
    }

    private class OrbitLeftAction extends AbstractInputAction
    {
        public void performAction(float time, Event evt)
        {
            cameraAzimuth -= 0.1f ;
            cameraAzimuth = cameraAzimuth % 360 ;
        }
    }

    private class OrbitRightAction extends AbstractInputAction
    {
        public void performAction(float time, Event evt)
        {
            cameraAzimuth += 0.1f ;
            cameraAzimuth = cameraAzimuth % 360 ;
        }
    }

    private class OrbitUpDownAction extends AbstractInputAction
    {
        public void performAction(float time, Event evt)
        {
            float rotAmount;
            if (evt.getValue() < -0.3){
                rotAmount=-0.1f;
            }
            else{
                if (evt.getValue() > 0.3){
                    rotAmount=0.1f;
                }
                else{
                    rotAmount=0.0f;
                }
            }
            cameraElevation += rotAmount ;
            cameraElevation = cameraElevation % 360 ;
            if(cameraElevation > 80){
                cameraElevation = 80;
            }
            if(cameraElevation < 0){
                cameraElevation = 0;
            }
        }
    }

    private class OrbitUpAction extends AbstractInputAction
    {
        public void performAction(float time, Event evt)
        {
            cameraElevation += 0.1f ;
            cameraElevation = cameraElevation % 360 ;
            if(cameraElevation > 80){
                cameraElevation = 80;
            }
        }
    }

    private class OrbitDownAction extends AbstractInputAction
    {
        public void performAction(float time, Event evt)
        {
            cameraElevation -= 0.1f ;
            cameraElevation = cameraElevation % 360 ;
            if(cameraElevation < 0){
                cameraElevation = 0;
            }
        }
    }

    private class ZoomInAction extends AbstractInputAction
    {
        public void performAction(float time, Event evt)
        {
            cameraDistanceFromTarget -= 0.1f;
            if(cameraDistanceFromTarget < 3.0){
                cameraDistanceFromTarget = (float)3.0;
            }
        }
    }

    private class ZoomOutAction extends AbstractInputAction
    {
        public void performAction(float time, Event evt)
        {
            cameraDistanceFromTarget += 0.1f;
            if(cameraDistanceFromTarget > 60.0){
                cameraDistanceFromTarget = (float)60.0;
            }
        }
    }

    private class RotateCameraAndShapeLeftAction extends BaseAbstractInputAction
    {
        public void performAction(float time, Event evt)
        {
            cameraAzimuth += 1.3f ;
            cameraAzimuth = cameraAzimuth % 360 ;
            target.rotate((float)1.3, new Vector3D(0,1,0));
            sendUpdateRotationPacket(client, (float) 1.3, new Vector3D(0, 1, 0));
        }
    }

    private class RotateCameraAndShapeRightAction extends BaseAbstractInputAction
    {
        public void performAction(float time, Event evt)
        {
            cameraAzimuth -= 1.3f ;
            cameraAzimuth = cameraAzimuth % 360 ;
            target.rotate((float)-1.3, new Vector3D(0,1,0));
            sendUpdateRotationPacket(client, (float)-1.3, new Vector3D(0,1,0));
        }
    }
}

