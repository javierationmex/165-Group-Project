package gameengine.player;

import graphicslib3D.Matrix3D;
import net.java.games.input.Event;
import networking.Client;
import networking.packets.ingame.UpdateAvatarInfoPacket;
import sage.input.action.AbstractInputAction;

import java.io.IOException;

/**
 * Created by Max on 4/1/2015.
 */
public class BaseAbstractInputAction extends AbstractInputAction {

    public void sendUpdateLocationPacket(Client client, Matrix3D translate, Matrix3D scale, Matrix3D rotation){
        try {
            client.sendPacket(new UpdateAvatarInfoPacket(client.getId(), translate, scale, rotation));
        } catch (IOException ev) {
            ev.printStackTrace();
        }
    }

    @Override
    public void performAction(float v, Event event) {

    }
}
