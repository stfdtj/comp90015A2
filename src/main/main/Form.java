package main;

import javax.swing.*;
import java.awt.*;

public class Form {
    private final JDialog dialog;
    private final JPanel form;
    private final JPanel buttonBar;
    private final GridBagConstraints gbc;
    private int row = 0;
    private int result = JOptionPane.CANCEL_OPTION;

    public Form(Window owner, String title) {
        dialog = new JDialog(owner, title, Dialog.ModalityType.APPLICATION_MODAL);
        form   = new JPanel(new GridBagLayout());
        buttonBar = new JPanel(new FlowLayout(FlowLayout.RIGHT));

        // shared constraints for form rows
        gbc = new GridBagConstraints();
        gbc.insets = new Insets(5,5,5,5);
        gbc.anchor = GridBagConstraints.WEST;
        gbc.fill   = GridBagConstraints.HORIZONTAL;
        gbc.weightx= 1.0;

        dialog.getContentPane().setLayout(new BorderLayout(10,10));
        dialog.getContentPane().add(form, BorderLayout.CENTER);
        dialog.getContentPane().add(buttonBar, BorderLayout.SOUTH);
    }


    public Form addRow(String labelText, JComponent comp) {
        // label in column 0
        gbc.gridx = 0; gbc.gridy = row;
        gbc.weightx = 0;
        form.add(new JLabel(labelText), gbc);

        // component in column 1
        gbc.gridx = 1; gbc.weightx = 1;
        form.add(comp, gbc);

        row++;
        return this;
    }


    public Form addButton(String text, int returnValue) {
        JButton btn = new JButton(text);
        btn.addActionListener(e -> {
            result = returnValue;
            dialog.setVisible(false);
        });
        buttonBar.add(btn);
        return this;
    }

    public int showDialog() {
        dialog.pack();
        dialog.setLocationRelativeTo(dialog.getOwner());
        dialog.setVisible(true);
        return result;
    }

}

