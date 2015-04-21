package gameengine.player;

import net.java.games.input.Component;
import net.java.games.input.Event;
import networking.Client;
import sage.input.action.AbstractInputAction;
import sage.scene.SceneNode;


/**
 * Created by Max on 2/24/2015.
 */
public class DetermineActionForControllerPlayerAction extends AbstractInputAction {

    private SceneNode player;
    private Component.Identifier.Axis axis;
    private final Client client;

    public DetermineActionForControllerPlayerAction(SceneNode player, Component.Identifier.Axis axis, Client client)
    {
        this.player = player;
        this.axis = axis;
        this.client = client;
    }
    @Override
    public void performAction(float v, Event event) {
        if(axis.equals(Component.Identifier.Axis.Y)){
            if(event.getValue() > 0.2){
                new MovePlayerLeftAction(player, client).performAction(v, event);
            }else if (event.getValue() < -0.2){
                new MovePlayerRightAction(player, client).performAction(v, event);
            }
        }else if (axis.equals(Component.Identifier.Axis.X)){
            if(event.getValue() > 0.2){
                new MovePlayerBackwardAction(player, client).performAction(v, event);
            }else if (event.getValue() < -0.2){
                new MovePlayerForwardAction(player, client).performAction(v, event);
            }
        }
    }
}
