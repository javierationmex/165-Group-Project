package commands.mainmenucommands;

import game.Player;
import networking.Server;
import swingmenus.base.CharacterSelectionScreen;
import swingmenus.multiplayer.data.MultiplayerLobbyData;

import javax.swing.*;
import java.awt.event.ActionEvent;
import java.io.IOException;
import java.net.InetAddress;
import java.net.UnknownHostException;

/**
 * Created by Max on 3/22/2015.
 */
public class HostGameCommand extends AbstractAction {

    private JFrame frame;
    private boolean isHostingGame = false;
    private Server server;
    private MultiplayerLobbyData data;
    private Player host;

    public HostGameCommand(JFrame frame, MultiplayerLobbyData data) {
        super("Host Game");
        this.frame = frame;
        this.data = data;
    }

    @Override
    public void actionPerformed(ActionEvent e) {
        if (!isHostingGame) {
            try {
                server = new Server(51234);
                System.out.println(server.getLocalInetAddress().toString());
            } catch (IOException e1) {
                e1.printStackTrace();
            }
            host = new Player(data.getPlayerName());
            host.setFrame(frame);
            data.addPlayer(host);
            host.setHost(true);

            try {
                host.createLinkedClient(InetAddress.getByName(data.getServerIPAddress()), data.getServerPortNumber());
            } catch (UnknownHostException e1) {
                e1.printStackTrace();
            }

            host.setCharacterSelectionScreen(new CharacterSelectionScreen(host, frame));

            frame.setContentPane(host.getCharacterSelectionScreen().getMainView());
            frame.setVisible(true);
        } else {
            System.out.println("Already hosting a game.");
        }
    }
}
