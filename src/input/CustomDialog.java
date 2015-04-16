package input;

import javax.swing.*;
import java.awt.*;

public class CustomDialog extends JDialog {

    private javax.swing.JLabel jLabel1;
    private javax.swing.JButton jbtOK;
    private javax.swing.JTextField jtfInput;
    private String inputString = "";

    public CustomDialog(JFrame frame, boolean modal) {
        super(frame, modal);
        initComponents();
        pack();
        setLocationRelativeTo(frame);
        setVisible(true);
    }

    private void initComponents() {

        jbtOK = new javax.swing.JButton();
        jtfInput = new javax.swing.JTextField();
        jLabel1 = new javax.swing.JLabel();

        setLayout(new GridLayout(0, 2));

        jbtOK.setText("OK");
        jbtOK.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jbtOKActionPerformed(evt);
            }
        });
        add(jbtOK);
        //new org.netbeans.lib.awtextra.AbsoluteConstraints(160, 190, -1,-1));

        jtfInput.addKeyListener(new java.awt.event.KeyAdapter() {
            public void keyReleased(java.awt.event.KeyEvent evt) {
                jtfInputKeyReleased(evt);
            }
        });
        // add(jtfInput, new org.netbeans.lib.awtextra.AbsoluteConstraints(180, 120, 160, 20));

        jLabel1.setText("Input some text here:");
        add(jLabel1);
        //new org.netbeans.lib.awtextra.AbsoluteConstraints(70, 120, -1, -1));
    }

    private void jbtOKActionPerformed(java.awt.event.ActionEvent evt) {
        this.dispose();
    }

    private void jtfInputKeyReleased(java.awt.event.KeyEvent evt) {
        inputString += evt.getKeyChar();
    }

    public String getInputText() {
        return inputString;
    }

}  