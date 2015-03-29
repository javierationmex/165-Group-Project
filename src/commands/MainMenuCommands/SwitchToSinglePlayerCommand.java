package commands.mainMenuCommands;

import swingMenus.Base.BaseCharacterSelectionMenu;

import javax.swing.*;
import java.awt.event.ActionEvent;

/**
 * Created by Max on 3/22/2015.
 */
public class SwitchToSinglePlayerCommand extends AbstractAction{

    private JFrame frame;

    public SwitchToSinglePlayerCommand(JFrame frame){
        super("Singleplayer");
        this.frame = frame;
    }

    @Override
    public void actionPerformed(ActionEvent e) {
        /*frame.setContentPane(new BaseCharacterSelectionMenu().getMainView());
        frame.setVisible(true);*/
    }
}
