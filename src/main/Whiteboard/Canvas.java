package Whiteboard;

import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
import java.awt.geom.Line2D;
import java.rmi.RemoteException;
import java.util.ArrayList;
import java.util.List;

public class Canvas extends JPanel implements MouseListener, MouseMotionListener {


    // list of line segments drawn locally
    private List<Line2D> lines = new ArrayList();
    // a preview, not finalized
    private Shape previewShape = null;

    // all shapes drawn
    private List<Shape> shapes = new ArrayList();

    private Point currentPoint = null;
    // active drawing mode.
    private DrawingMode mode = DrawingMode.FREE;

    private Color currColor = Color.BLACK;


    private WhiteboardFunctions remoteService;

    public Canvas(WhiteboardFunctions service) {
        this.remoteService = service;
        setBackground(Color.WHITE);
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
        lines.add(line);
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

    public synchronized void addShape(Shape s) {
        shapes.add(s);
        repaint();
    }

    // render the drawn shapes.
    @Override
    protected void paintComponent(Graphics g) {
        super.paintComponent(g);
        Graphics2D g2 = (Graphics2D) g;
        for (Line2D line : lines) {
            g2.draw(line);
        }
        if (previewShape != null) {
            g2.setColor(currColor);
            g2.draw(previewShape);
        }
        for (Shape s : shapes) {
            g2.draw(s);
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

    // switch
    public void setDrawingMode(DrawingMode mode) {
        this.mode = mode;
    }



    // toolbar settings
    private JPanel createToolbar() {
        JPanel toolbar = new JPanel(new FlowLayout(FlowLayout.LEFT));

        // Shapes Panel
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
            }

        });
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
