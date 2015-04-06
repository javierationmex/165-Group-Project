package swingforms;

import javax.swing.*;
import javax.swing.event.ListSelectionEvent;
import javax.swing.event.ListSelectionListener;

/**
 * Created by arash on 4/5/2015.
 */
public class ControllerSelection {
    private JList list1;

    public ControllerSelection() {
        list1.addListSelectionListener(new lis);
        list1.addListSelectionListener(new ListSelectionListener() {
            @Override
            public void valueChanged(ListSelectionEvent e) {

            }
        });
    }
}
