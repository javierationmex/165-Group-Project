package gameengine;

import game.MazeGame;
import net.java.games.input.Event;
import sage.input.action.AbstractInputAction;

/**
 * Created by Max on 4/20/2015.
 */
public class TogglePhysics extends AbstractInputAction {

    private MazeGame game;

    public TogglePhysics(MazeGame game) {
        this.game = game;
    }

    @Override
    public void performAction(float v, Event event) {
        game.togglePhysics(true);
    }
}
