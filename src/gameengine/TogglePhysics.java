package gameengine;

import game.SpaceRace;
import net.java.games.input.Event;
import sage.input.action.AbstractInputAction;

/**
 * Created by Max on 4/20/2015.
 */
public class TogglePhysics extends AbstractInputAction {

    private SpaceRace game;

    public TogglePhysics(SpaceRace game) {
        this.game = game;
    }

    @Override
    public void performAction(float v, Event event) {
        game.togglePhysics(true);
    }
}
