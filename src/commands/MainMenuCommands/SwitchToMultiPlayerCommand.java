package commands.mainMenuCommands;

import swingMenus.Multiplayer.MultiplayerLobby;

import javax.swing.*;
import java.awt.event.ActionEvent;

/**
 * Created by Max on 3/22/2015.
 */
public class SwitchToMultiPlayerCommand extends AbstractAction{

    private JFrame frame;

    public SwitchToMultiPlayerCommand(JFrame frame){
        super("Multiplayer");
        this.frame = frame;
    }

    @Override
    public void actionPerformed(ActionEvent e) {
        frame.setContentPane(new MultiplayerLobby(frame).getMainView());
        frame.setVisible(true);
    }
}
