package a3;



import swingmenus.mainmenucommands.SwitchToMultiPlayerCommand;
import swingmenus.mainmenucommands.SwitchToSinglePlayerCommand;

import javax.swing.*;
import java.awt.*;
import java.security.AccessController;
import java.security.PrivilegedAction;

/**
 * Created by Max on 3/22/2015.
 */
public class StartGame {
    private static JFrame frame;

    /**
     * Fix windows 8 warnings by defining a working plugin
     */
    static {

        AccessController.doPrivileged(new PrivilegedAction<Object>() {
            public Object run() {
                String os = System.getProperty("os.name", "").trim();
                if (os.startsWith("Windows 8")) {  // 8, 8.1 etc.

                    // disable default plugin lookup
                    System.setProperty("jinput.useDefaultPlugin", "false");

                    // set to same as windows 7 (tested for windows 8 and 8.1)
                    System.setProperty("net.java.games.input.plugins", "net.java.games.input.DirectAndRawInputEnvironmentPlugin");

                }
                return null;
            }
        });

    }

    private JPanel mainView;
    private JButton singlePlayerButton;
    private JButton multiPlayerButton;

    public StartGame() {
        mainView.setVisible(true);
        multiPlayerButton.setAction(new SwitchToMultiPlayerCommand(frame));
        singlePlayerButton.setAction(new SwitchToSinglePlayerCommand(frame));
    }

    public static void main(String[] args) {
        frame = new JFrame("Space Race");
        frame.setContentPane(new StartGame().mainView);
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.setResizable(false);
        frame.setSize(1000, 500);
        frame.setVisible(true);
    }

    {
// GUI initializer generated by IntelliJ IDEA GUI Designer
// >>> IMPORTANT!! <<<
// DO NOT EDIT OR ADD ANY CODE HERE!
        $$$setupUI$$$();
    }

    /**
     * Method generated by IntelliJ IDEA GUI Designer
     * >>> IMPORTANT!! <<<
     * DO NOT edit this method OR call it in your code!
     *
     * @noinspection ALL
     */
    private void $$$setupUI$$$() {
        mainView = new JPanel();
        mainView.setLayout(new BorderLayout(0, 0));
        final JLabel label1 = new JLabel();
        label1.setHorizontalAlignment(0);
        label1.setText("Space Race");
        mainView.add(label1, BorderLayout.CENTER);
        final JPanel panel1 = new JPanel();
        panel1.setLayout(new GridBagLayout());
        mainView.add(panel1, BorderLayout.SOUTH);
        singlePlayerButton = new JButton();
        singlePlayerButton.setText("Single Player");
        GridBagConstraints gbc;
        gbc = new GridBagConstraints();
        gbc.gridx = 0;
        gbc.gridy = 0;
        gbc.weightx = 1.0;
        gbc.weighty = 1.0;
        gbc.fill = GridBagConstraints.HORIZONTAL;
        panel1.add(singlePlayerButton, gbc);
        multiPlayerButton = new JButton();
        multiPlayerButton.setText("Multi Player");
        gbc = new GridBagConstraints();
        gbc.gridx = 1;
        gbc.gridy = 0;
        gbc.weightx = 1.0;
        gbc.weighty = 1.0;
        gbc.fill = GridBagConstraints.HORIZONTAL;
        panel1.add(multiPlayerButton, gbc);
    }

    /**
     * @noinspection ALL
     */
    public JComponent $$$getRootComponent$$$() {
        return mainView;
    }
}
