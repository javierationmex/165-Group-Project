package gameengine.player;

import net.java.games.input.Component;
import net.java.games.input.Event;
import networking.Client;
import sage.input.action.AbstractInputAction;
import sage.scene.SceneNode;
import sage.terrain.TerrainBlock;


/**
 * Created by Max on 2/24/2015.
 */
public class DetermineActionForControllerPlayerAction extends AbstractInputAction {

    private final Client client;
    private SceneNode player;
    private Component.Identifier.Axis axis;
    private TerrainBlock terrain;

    public DetermineActionForControllerPlayerAction(SceneNode player, Component.Identifier.Axis axis, TerrainBlock imageTerrain, Client client)
    {
        this.player = player;
        this.axis = axis;
        this.client = client;
        terrain = imageTerrain;
    }
    @Override
    public void performAction(float v, Event event) {
        if(axis.equals(Component.Identifier.Axis.Y)){
            if(event.getValue() > 0.2){
                new MovePlayerLeftAction(player, terrain, client).performAction(v, event);
            }else if (event.getValue() < -0.2){
                new MovePlayerRightAction(player, terrain, client).performAction(v, event);
            }
        }else if (axis.equals(Component.Identifier.Axis.X)){
            if(event.getValue() > 0.2){
                new MovePlayerBackwardAction(player, terrain, client).performAction(v, event);
            }else if (event.getValue() < -0.2){
                new MovePlayerForwardAction(player, terrain, client).performAction(v, event);
            }
        }
    }
}
