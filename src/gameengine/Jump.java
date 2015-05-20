package gameengine;

import game.SpaceRace;
import net.java.games.input.Event;
import sage.input.action.AbstractInputAction;

/**
 * Created by AKHMET on 4/20/2015.
 */
public class Jump extends AbstractInputAction {

    private SpaceRace game;

    public Jump(SpaceRace game) {
        this.game = game;
    }

    @Override
    public void performAction(float v, Event event) {
        game.jump();
    }
}
