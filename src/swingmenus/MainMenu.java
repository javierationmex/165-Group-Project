package swingmenus;


import swingmenus.mainmenucommands.SwitchToMultiPlayerCommand;
import swingmenus.mainmenucommands.SwitchToSinglePlayerCommand;

import javax.swing.*;

/**
 * Created by Max on 3/22/2015.
 */
public class MainMenu extends JPanel {

    private JFrame frame;

    public MainMenu(JFrame frame) {
        this.frame = frame;
        this.setSize(1000, 500);
        this.add(new JLabel("Space Race"));

        JButton singleplayerButton = new JButton("Single player");
        singleplayerButton.setAction(new SwitchToSinglePlayerCommand(frame));
        JButton multiplayerButton = new JButton("Multiplayer");
        multiplayerButton.setAction(new SwitchToMultiPlayerCommand(frame));

        this.add(singleplayerButton);
        this.add(multiplayerButton);
    }

}
