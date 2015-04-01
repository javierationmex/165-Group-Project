package commands.mainmenucommands;


import game.Player;
import sage.networking.IGameConnection;
import swingmenus.base.CharacterSelectionScreen;
import swingmenus.multiplayer.data.MultiplayerLobbyData;

import javax.swing.*;
import java.awt.event.ActionEvent;
import java.net.InetAddress;
import java.net.UnknownHostException;

/**
 * Created by Max on 3/22/2015.
 */
public class JoinGameCommand extends AbstractAction {

    private JFrame frame;
    private String serverAddress = "127.0.0.1";
    private int serverPort = 5123;
    private IGameConnection.ProtocolType protocol = IGameConnection.ProtocolType.TCP;
    private MultiplayerLobbyData data;
    private Player player;

    public JoinGameCommand(JFrame frame, MultiplayerLobbyData data) {
        super("Join Game");
        this.data = data;
        this.frame = frame;
    }

    @Override
    public void actionPerformed(ActionEvent e) {
        this.updateData();
        System.out.println(serverAddress);
        System.out.println(serverPort);

        player = new Player(data.getPlayerName());
        player.setFrame(frame);
        data.addPlayer(player);
        player.setHost(false);

        try {
            player.createLinkedClient(InetAddress.getByName(data.getServerIPAddress()), data.getServerPortNumber());
        } catch (UnknownHostException e1) {
            e1.printStackTrace();
        }

        player.setCharacterSelectionScreen(new CharacterSelectionScreen(player, frame));

        frame.setContentPane(player.getCharacterSelectionScreen().getMainView());
        frame.setVisible(true);

    }

    private void updateData() {
        this.serverAddress = data.getServerIPAddress();
        this.serverPort = data.getServerPortNumber();
    }
}
