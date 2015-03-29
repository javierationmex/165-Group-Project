package commands.mainMenuCommands;


import sage.networking.IGameConnection;
import swingMenus.Multiplayer.data.MultiplayerLobbyData;

import javax.swing.*;
import java.awt.event.ActionEvent;

/**
 * Created by Max on 3/22/2015.
 */
public class JoinGameCommand extends AbstractAction{

    private JFrame frame;
    private String serverAddress = "127.0.0.1";
    private int serverPort = 5123;
    private IGameConnection.ProtocolType protocol = IGameConnection.ProtocolType.TCP;
    private MultiplayerLobbyData data;

    public JoinGameCommand(JFrame frame, MultiplayerLobbyData data){
        super("Join Game");
        this.data = data;
        this.frame = frame;
    }

    @Override
    public void actionPerformed(ActionEvent e) {
        this.updateData();
        System.out.println(serverAddress);
        System.out.println(serverPort);

    }

    private void updateData() {
        this.serverAddress = data.getServerIPAddress();
        this.serverPort = data.getServerPortNumber();
    }
}
