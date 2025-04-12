package Whiteboard;

import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
import java.rmi.RemoteException;

public class Canvas extends JPanel implements WhiteboardFunctions,
        WindowListener,
        ActionListener,
        MouseListener,
        MouseMotionListener {

    public Canvas() {
        this.setLayout(new BorderLayout());
        JPanel toolbar = createToolbar();
        toolbar.setBackground(new Color(187,222,214,255));
        add(toolbar, BorderLayout.NORTH);
        this.setBackground(Color.WHITE);
    }


    @Override
    public void drawLine() throws RemoteException {

    }

    @Override
    public void actionPerformed(ActionEvent e) {

    }

    @Override
    public void mouseClicked(MouseEvent e) {

    }

    @Override
    public void mousePressed(MouseEvent e) {

    }

    @Override
    public void mouseReleased(MouseEvent e) {

    }

    @Override
    public void mouseEntered(MouseEvent e) {

    }

    @Override
    public void mouseExited(MouseEvent e) {

    }

    @Override
    public void mouseDragged(MouseEvent e) {

    }

    @Override
    public void mouseMoved(MouseEvent e) {

    }

    @Override
    public void windowOpened(WindowEvent e) {

    }

    @Override
    public void windowClosing(WindowEvent e) {

    }

    @Override
    public void windowClosed(WindowEvent e) {

    }

    @Override
    public void windowIconified(WindowEvent e) {

    }

    @Override
    public void windowDeiconified(WindowEvent e) {

    }

    @Override
    public void windowActivated(WindowEvent e) {

    }

    @Override
    public void windowDeactivated(WindowEvent e) {

    }


    private JPanel createToolbar() {
        JPanel toolbar = new JPanel(new FlowLayout(FlowLayout.LEFT));

        // Shapes Panel
        JPanel shapesPanel = new JPanel();
        shapesPanel.setBorder(BorderFactory.createTitledBorder("Shapes"));
        shapesPanel.setBackground(new Color(187,222,214,255));

        String[] shapes = {"Line", "Rect", "Oval", "Triangle"};
        JComboBox<String> shapeSelector = new JComboBox<>(shapes);
        shapesPanel.add(shapeSelector);

        // Tools Panel
        JPanel toolsPanel = new JPanel();
        toolsPanel.setBorder(BorderFactory.createTitledBorder("Tools"));
        toolsPanel.setBackground(new Color(187,222,214,255));

        JButton pencilBtn = new JButton("✏️");
        JButton eraserBtn = new JButton("🩹");
        toolsPanel.add(pencilBtn);
        toolsPanel.add(eraserBtn);

        // Color Panel
        JPanel colorPanel = new JPanel();
        colorPanel.setBorder(BorderFactory.createTitledBorder("Colors"));
        colorPanel.setBackground(new Color(187,222,214,255));
        Color[] palette = {Color.BLACK, Color.RED, Color.GREEN, Color.BLUE};
        for (Color c : palette) {
            JButton colorBtn = new JButton();
            colorBtn.setBackground(c);
            colorBtn.setPreferredSize(new Dimension(20, 20));
            colorBtn.setBorder(BorderFactory.createLineBorder(Color.GRAY));
            colorPanel.add(colorBtn);
        }

        // Add all panels to toolbar
        toolbar.add(shapesPanel);
        toolbar.add(Box.createRigidArea(new Dimension(15, 0)));
        toolbar.add(toolsPanel);
        toolbar.add(Box.createRigidArea(new Dimension(15, 0)));
        toolbar.add(colorPanel);

        return toolbar;
    }

}
