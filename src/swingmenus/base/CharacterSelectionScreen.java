package swingmenus.base;


import game.Player;
import swingmenus.multiplayer.data.PlayerInfo;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.ItemEvent;
import java.awt.event.ItemListener;
import java.util.ArrayList;
import java.util.Vector;

/**
 * Created by Max on 3/30/2015.
 */
public class CharacterSelectionScreen {
    private final JFrame frame;
    private JPanel mainView;
    private JLabel title;
    private JButton readyButton;
    private JButton startGameButton;
    private JPanel characterListPanel;
    private JPanel otherPlayersPanel;
    private Player player;
    private DefaultComboBoxModel model;
    private JComboBox characterSelectionComboBox;
    private JCheckBox readyCheckBox;
    private Vector characterOptions; // change to characters we make in the future
    private ArrayList<PlayerInfo> players;

    public CharacterSelectionScreen(final Player player, final JFrame frame) {
        this.player = player;
        this.frame = frame;
        characterOptions = new Vector();
        characterOptions.add("Cube");
        characterOptions.add("Pyramid");
        characterOptions.add("Ship");
        characterOptions.add("Rook");
        model = new DefaultComboBoxModel(characterOptions);
        this.player.setCharacterSelectionScreen(this);
        $$$setupUI$$$();


        readyButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                characterSelectionComboBox.setEnabled(false);
                readyCheckBox.setSelected(true);
                readyButton.setEnabled(false);
                player.setReady(true);
                player.sendUpdatePacket();
            }
        });

        characterSelectionComboBox.addItemListener(new ItemListener() {

            /**
             * Invoked when an item has been selected or deselected by the user.
             * The code written for this method performs the operations
             * that need to occur when an item is selected (or deselected).
             *
             * @param e
             */
            @Override
            public void itemStateChanged(ItemEvent e) {
                if (e.getStateChange() == ItemEvent.SELECTED) {
                    JComboBox comboBox = (JComboBox) e.getSource();
                    player.setCharacterID(comboBox.getSelectedIndex());
                    player.sendUpdatePacket();
                }
            }
        });

        if (player.isHost()) {
            startGameButton.setEnabled(true);
        }
        startGameButton.addActionListener(new ActionListener() {
            public boolean canStartGame;

            /**
             * Invoked when an action occurs.
             *
             * @param e
             */
            @Override
            public void actionPerformed(ActionEvent e) {
                this.canStartGame = true;
                for (PlayerInfo p : players) {
                    if (!p.isReady()) {
                        this.canStartGame = false;
                    }
                }
                player.sendStartGamePacket();
            }
        });
    }

    public JPanel getMainView() {
        return mainView;
    }


    private void createPlayerFields() {
        characterListPanel = new JPanel(new GridLayout());
        otherPlayersPanel = new JPanel();
        JPanel playerPanel = new JPanel();
        JLabel playerNameLabel = new JLabel(player.getPlayerName());
        JLabel characterLabel = new JLabel("Character:");
        characterSelectionComboBox = new JComboBox(model);
        readyCheckBox = new JCheckBox("Ready", false);
        readyCheckBox.setEnabled(false);
        playerPanel.add(playerNameLabel);
        playerPanel.add(characterLabel);
        playerPanel.add(characterSelectionComboBox);
        playerPanel.add(readyCheckBox);
        characterListPanel.add(playerPanel);
        characterListPanel.add(otherPlayersPanel);
    }

    public void updatePlayerList(ArrayList<PlayerInfo> players) {
        this.players = players;
        characterListPanel.remove(otherPlayersPanel);
        otherPlayersPanel = new JPanel();
        int lastRow = 0;
        if (players != null) {
            for (PlayerInfo p : players) {
                if (!p.getClientID().toString().equals(player.getPlayerUUID().toString())) {
                    JPanel newPanel = new JPanel();
                    newPanel.add(new JLabel(p.getPlayerName()));
                    newPanel.add(new JLabel("Character:"));
                    newPanel.add(createDisabledComboBox(p.getCharacterID()));
                    newPanel.add(createDisabledCheckBox(p.isReady()));
                    otherPlayersPanel.add(newPanel);
                }
            }
        }
        characterListPanel.add(otherPlayersPanel);
        characterListPanel.setVisible(true);
        characterListPanel.revalidate();
        characterListPanel.repaint();
    }

    private JComboBox createDisabledComboBox(int selection) {
        JComboBox characterSelectionComboBoxs = new JComboBox(new DefaultComboBoxModel(characterOptions));
        characterSelectionComboBoxs.setEnabled(false);
        characterSelectionComboBoxs.setSelectedIndex(selection);
        return characterSelectionComboBoxs;
    }

    private JCheckBox createDisabledCheckBox(boolean ready) {
        JCheckBox readyCheckBoxs = new JCheckBox("Ready", ready);
        readyCheckBoxs.setEnabled(false);
        return readyCheckBoxs;
    }

    private void createUIComponents() {
        createPlayerFields();
    }

    /**
     * Method generated by IntelliJ IDEA GUI Designer
     * >>> IMPORTANT!! <<<
     * DO NOT edit this method OR call it in your code!
     *
     * @noinspection ALL
     */
    private void $$$setupUI$$$() {
        createUIComponents();
        mainView = new JPanel();
        mainView.setLayout(new BorderLayout(0, 0));
        title = new JLabel();
        title.setHorizontalAlignment(0);
        title.setHorizontalTextPosition(0);
        title.setText("Pick your Characters and Game Settings");
        mainView.add(title, BorderLayout.NORTH);
        mainView.add(characterListPanel, BorderLayout.CENTER);
        final JPanel panel1 = new JPanel();
        panel1.setLayout(new FlowLayout(FlowLayout.CENTER, 5, 5));
        mainView.add(panel1, BorderLayout.SOUTH);
        readyButton = new JButton();
        readyButton.setHorizontalAlignment(10);
        readyButton.setText("Ready");
        panel1.add(readyButton);
        startGameButton = new JButton();
        startGameButton.setEnabled(false);
        startGameButton.setText("Start Game");
        panel1.add(startGameButton);
    }

    /**
     * @noinspection ALL
     */
    public JComponent $$$getRootComponent$$$() {
        return mainView;
    }
}
