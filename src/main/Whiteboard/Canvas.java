package Whiteboard;

import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
import java.awt.geom.Line2D;
import java.rmi.RemoteException;
import java.util.ArrayList;
import java.util.List;

public class Canvas extends JPanel implements MouseListener, MouseMotionListener {


    List<ShapeCustom> shapes = new ArrayList<>();
    // a preview, not finalized
    private Shape previewShape = null;


    private Point currentPoint = null;
    // active drawing mode.
    private DrawingMode mode = DrawingMode.FREE;

    private Color currColor = Color.BLACK;
    private Color backgroundColor = Color.WHITE;


    private WhiteboardFunctions remoteService;

    public Canvas(WhiteboardFunctions service) {
        this.remoteService = service;
        setBackground(backgroundColor);
        addMouseListener(this);
        addMouseMotionListener(this);
        this.add(createToolbar());
    }


    // helper functions
    // record start point
    public void setCurrentPoint(Point p) {
        this.currentPoint = p;
    }
    // get current point
    public Point getCurrentPoint() {
        return currentPoint;
    }

    // add a line locally and send the drawing command remotely
    public synchronized void addLineSegment(Point start, Point end) {
        // add the line locally
        Line2D line = new Line2D.Float(start, end);
        shapes.add(new ShapeCustom(new Line2D.Float(start, end), currColor));
        repaint();


        DrawingInfo info = new DrawingInfo(start.x, start.y, end.x, end.y, currColor);
        try {
            remoteService.FreeDraw(info);
        } catch (RemoteException ex) {
            ex.printStackTrace();
        }
    }

    public synchronized void addLine(Point start, Point end) {
        DrawingInfo info = new DrawingInfo(start.x, start.y, end.x, end.y, this.currColor);
        try {
            this.remoteService.DrawLine(info);
        } catch (RemoteException ex) {
            ex.printStackTrace();
        }
    }

    public synchronized void addRectangle(Point start, Point end) {
        DrawingInfo info = new DrawingInfo(start.x, start.y, end.x, end.y, this.currColor);
        try {
            this.remoteService.DrawRectangle(info);
        } catch (RemoteException ex) {
            ex.printStackTrace();
        }
    }

    public synchronized void addOval(Point start, Point end) {
        DrawingInfo info = new DrawingInfo(start.x, start.y, end.x, end.y, this.currColor);
        try {
            this.remoteService.DrawOval(info);
        } catch (RemoteException ex) {
            ex.printStackTrace();
        }
    }

    public synchronized void addTriangle(Point start, Point end) {
        DrawingInfo info = new DrawingInfo(start.x, start.y, end.x, end.y, this.currColor);
        try {
            this.remoteService.DrawTriangle(info);
        } catch (RemoteException ex) {
            ex.printStackTrace();
        }
    }

    public synchronized void addShape(Shape s) {
        shapes.add(new ShapeCustom(s, currColor));
        repaint();
    }

    // render the drawn shapes.
    @Override
    protected void paintComponent(Graphics g) {
        super.paintComponent(g);
        Graphics2D g2 = (Graphics2D) g;
        for (ShapeCustom shape : shapes) {
            g2.setColor(shape.getColor());
            g2.draw(shape.getShape());
        }
        if (previewShape != null) {
            g2.setColor(currColor);
            g2.draw(previewShape);
        }

    }

    public void setPreviewShape(Shape s) {
        this.previewShape = s;
        repaint();
    }

    public void clearPreviewShape() {
        this.previewShape = null;
        repaint();
    }



    // mouse events
    @Override
    public void mousePressed(MouseEvent e) {
        mode.mousePressed(e, this);
    }

    @Override
    public void mouseDragged(MouseEvent e) {
        mode.mouseDragged(e, this);
    }

    @Override
    public void mouseReleased(MouseEvent e) {
        mode.mouseReleased(e, this);
    }

    // unused
    @Override public void mouseClicked(MouseEvent e) {

    }
    @Override public void mouseEntered(MouseEvent e) {}
    @Override public void mouseExited(MouseEvent e) {}
    @Override public void mouseMoved(MouseEvent e) {}

    // switch current mode
    public void setDrawingMode(DrawingMode mode) {
        this.mode = mode;
    }

    // set fore colour
    public void setForeColor(Color c) {
        this.currColor = c;
    }



    // toolbar settings
    private JPanel createToolbar() {
        JPanel toolbar = new JPanel(new FlowLayout(FlowLayout.LEFT));

        // shapes Panel
        JPanel shapesPanel = new JPanel();
        shapesPanel.setBorder(BorderFactory.createTitledBorder("Shapes"));
        shapesPanel.setBackground(new Color(187,222,214,255));

        String[] shapes = {"Free", "Line", "Rectangle", "Oval", "Triangle"};
        JComboBox<String> shapeSelector = new JComboBox<>(shapes);
        shapeSelector.addActionListener(e -> {
            String selected = (String) shapeSelector.getSelectedItem();
            if (selected.equals("Line")) {
                this.setDrawingMode(DrawingMode.LINE);
            } else if(selected.equals("Rectangle")) {
                this.setDrawingMode(DrawingMode.RECTANGLE);
            } else if(selected.equals("Free")) {
                this.setDrawingMode(DrawingMode.FREE);
            } else if(selected.equals("Oval")) {
                this.setDrawingMode(DrawingMode.OVAL);
            } else if(selected.equals("Triangle")) {
                this.setDrawingMode(DrawingMode.TRIANGLE);
            }

        });
        shapesPanel.add(shapeSelector);


        // tools Panel
        JPanel toolsPanel = new JPanel();
        toolsPanel.setBorder(BorderFactory.createTitledBorder("Tools"));
        toolsPanel.setBackground(new Color(187,222,214,255));

        // processing image
        Image pencil = new ImageIcon("src/main/Whiteboard/resources/pencil.png").getImage();
        pencil = pencil.getScaledInstance(24, 24, Image.SCALE_SMOOTH);
        Image rubber = new ImageIcon("src/main/Whiteboard/resources/rubber.png").getImage();
        rubber = rubber.getScaledInstance(24, 24, Image.SCALE_SMOOTH);

        JButton pencilBtn = new JButton(new ImageIcon(pencil));
        JButton eraserBtn = new JButton(new ImageIcon(rubber));
        eraserBtn.addActionListener(e -> {
            this.setDrawingMode(DrawingMode.FREE);
            this.setForeColor(backgroundColor);
        });
        toolsPanel.add(pencilBtn);
        toolsPanel.add(eraserBtn);

        // color Panel
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

        // add all panels to toolbar
        toolbar.add(shapesPanel);
        toolbar.add(Box.createRigidArea(new Dimension(15, 0)));
        toolbar.add(toolsPanel);
        toolbar.add(Box.createRigidArea(new Dimension(15, 0)));
        toolbar.add(colorPanel);
        return toolbar;
    }

}
