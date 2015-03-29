package game;

import sage.app.BaseGame;

/**
 * Main Class for Game initialization etc here.
 */
public class MazeGame extends BaseGame {



    //Please separate all the initialization of stuff into different methods.
    //For example createPlayers(), createWalls(), etc... so that the initGame is very simple.


    @Override
    protected void createDisplay() {
        super.createDisplay();
        //TODO Create fullscreen mode, enable going in and out of full screen mode on button click (F11)
    }

    @Override
    protected void initGame() {
        super.initGame();
        //TODO create objects: walls, players, power up boosts, etc. Gotta create a recursive algorithm to auto generate a random maze.
        //TODO maybe have different colored sections of walls that players can walk through if they picked up a certain color boost.
    }

    @Override
    protected void update(float time) {
        super.update(time);
        //TODO override later
    }


    @Override
    protected void shutdown() {
        super.shutdown();
    }

    @Override
    protected void exit() {
        super.exit();
    }

    @Override
    protected void initSystem() {
        super.initSystem();
        //TODO override later
    }
}
