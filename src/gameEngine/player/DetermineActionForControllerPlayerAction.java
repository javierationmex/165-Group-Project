package gameengine.player;

import net.java.games.input.Component;
import net.java.games.input.Event;
import sage.input.action.AbstractInputAction;
import sage.scene.SceneNode;


/**
 * Created by Max on 2/24/2015.
 */
public class DetermineActionForControllerPlayerAction extends AbstractInputAction {

    private SceneNode player;
    private Component.Identifier.Axis axis;

    public DetermineActionForControllerPlayerAction(SceneNode player, Component.Identifier.Axis axis)
    {
        this.player = player;
        this.axis = axis;
    }
    @Override
    public void performAction(float v, Event event) {
        if(axis.equals(Component.Identifier.Axis.Y)){
            if(event.getValue() > 0.2){
                new MovePlayerLeftAction(player).performAction(v, event);
            }else if (event.getValue() < -0.2){
                new MovePlayerRightAction(player).performAction(v, event);
            }
        }else if (axis.equals(Component.Identifier.Axis.X)){
            if(event.getValue() > 0.2){
                new MovePlayerBackwardAction(player).performAction(v, event);
            }else if (event.getValue() < -0.2){
                new MovePlayerForwardAction(player).performAction(v, event);
            }
        }
    }
}
