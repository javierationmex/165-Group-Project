package gameengine.player;

import graphicslib3D.Vector3D;
import net.java.games.input.Event;
import networking.Client;
import networking.packets.ingame.UpdateAvatarLocationInformationPacket;
import networking.packets.ingame.UpdateAvatarRotationInformationPacket;
import sage.input.action.AbstractInputAction;

import java.io.IOException;

/**
 * Created by Max on 4/1/2015.
 */
public class BaseAbstractInputAction extends AbstractInputAction {

    public void sendUpdateLocationPacket(Client client, float x, float y, float z){
        try {
            client.sendPacket(new UpdateAvatarLocationInformationPacket(client.getId(), x, y, z));
        } catch (IOException ev) {
            ev.printStackTrace();
        }
    }

    public void sendUpdateRotationPacket(Client client, float x, Vector3D axis){
        try {
            client.sendPacket(new UpdateAvatarRotationInformationPacket(client.getId(), x, axis));
        } catch (IOException ev) {
            ev.printStackTrace();
        }
    }

    @Override
    public void performAction(float v, Event event) {

    }
}
