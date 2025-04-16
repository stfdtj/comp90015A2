package Whiteboard;

import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
import java.awt.geom.Ellipse2D;
import java.awt.geom.Line2D;
import java.awt.geom.Rectangle2D;
import java.rmi.RemoteException;
import java.util.ArrayList;
import java.util.List;

public class Canvas extends JPanel implements MouseListener, MouseMotionListener {

    Image pencil = new ImageIcon("src/main/Whiteboard/resources/pencil.png").getImage();
    Image rubber = new ImageIcon("src/main/Whiteboard/resources/rubber.png").getImage();
    Image text = new ImageIcon("src/main/Whiteboard/resources/text.png").getImage();
    private boolean identiy;

    private JSlider slider;

    List<ShapeCustom> shapes = new ArrayList<>();
    // a preview, not finalized
    private Shape previewShape = null;


    private Point currentPoint = null;
    // active drawing mode.
    private DrawingMode mode = DrawingMode.FREE;

    private Color currColor = Color.BLACK;
    private Color backgroundColor = Color.WHITE;
    private float thickness = 3;



    private WhiteboardFunctions remoteService;

    public Canvas(WhiteboardFunctions service, boolean identity) {
        this.remoteService = service;
        this.identiy = identity;
        this.setLayout(new BorderLayout());
        setBackground(backgroundColor);
        addMouseListener(this);
        addMouseMotionListener(this);
        

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
        Shape line = CreateShape("Line", start, end);
        shapes.add(new ShapeCustom(line, currColor, mode, thickness));
        repaint();

        DrawingInfo info = new DrawingInfo(start, end, currColor, "Free", mode, thickness);
        if (identiy) {
            try {
                remoteService.BroadcastDrawing(info);
                System.out.println("broadcasting " + info);
            } catch (RemoteException ex) {
                ex.printStackTrace();
            }
        }

    }

    public synchronized void addLine(Point start, Point end) {
        Shape line = CreateShape("Line", start, end);
        shapes.add(new ShapeCustom(line, currColor, mode, thickness));
        DrawingInfo info = new DrawingInfo(start, end, this.currColor, "Line", mode, thickness);
        if (identiy) {
            try {
                remoteService.BroadcastDrawing(info);
                System.out.println("broadcasting " + info);
            } catch (RemoteException ex) {
                ex.printStackTrace();
            }
        }

    }

    public synchronized void addRectangle(Point start, Point end) {
        Shape rect = CreateShape("Rectangle", start, end);
        shapes.add(new ShapeCustom(rect, currColor, mode, thickness));
        DrawingInfo info = new DrawingInfo(start, end, this.currColor, "Rectangle", mode, thickness);
        if (identiy) {
            try {
                remoteService.BroadcastDrawing(info);
                System.out.println("broadcasting " + info);
            } catch (RemoteException ex) {
                ex.printStackTrace();
            }
        }
    }

    public synchronized void addOval(Point start, Point end) {
        Shape oval = CreateShape("Oval", start, end);
        shapes.add(new ShapeCustom(oval, currColor, mode, thickness));
        DrawingInfo info = new DrawingInfo(start, end, this.currColor, "Oval", mode, thickness);
        if (identiy) {
            try {
                remoteService.BroadcastDrawing(info);
                System.out.println("broadcasting " + info);
            } catch (RemoteException ex) {
                ex.printStackTrace();
            }
        }
    }

    public synchronized void addTriangle(Point start, Point end) {
        Shape triangle = CreateShape("Triangle", start, end);
        shapes.add(new ShapeCustom(triangle, currColor, mode, thickness));
        DrawingInfo info = new DrawingInfo(start, end, this.currColor, "Triangle", mode, thickness);
        if (identiy) {
            try {
                remoteService.BroadcastDrawing(info);
                System.out.println("broadcasting " + info);
            } catch (RemoteException ex) {
                ex.printStackTrace();
            }
        }

    }

    public synchronized void addShape(Shape s) {
        shapes.add(new ShapeCustom(s, currColor, mode, thickness));
        repaint();
    }

    public synchronized void ReceiveRemoteShape(DrawingInfo info) {
        System.out.println(info.getShape().toString());
        try {
            System.out.println("Received Remote Shape: " + info.getShape());
            if (info.getShape() == null) {
                System.err.println("Warning: shape in DrawingInfo is null!");
            }
            Shape shape = CreateShape(info.getShape(), info.getStart(), info.getEnd());
            shapes.add(new ShapeCustom(shape, info.getColor(), info.getDrawingMode(), info.getThickness()));
            repaint();
        } catch (RuntimeException e) {
            throw new RuntimeException(e);
        }

    }

    // render the drawn shapes.
    @Override
    protected void paintComponent(Graphics g) {
        super.paintComponent(g);
        Graphics2D g2 = (Graphics2D) g;
        for (ShapeCustom shape : shapes) {
            g2.setColor(shape.color);
            g2.setStroke(new BasicStroke(shape.thickness));
            g2.draw(shape.shape);
        }
        if (previewShape != null) {
            g2.setColor(currColor);
            g2.setStroke(new BasicStroke(thickness));
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
    public JPanel createToolbar() {
        JPanel toolbar = new JPanel(new FlowLayout(FlowLayout.LEFT));
        toolbar.setBackground(new Color(243,243,243,255));

        // shapes Panel
        JPanel shapesPanel = new JPanel();
        shapesPanel.setBorder(BorderFactory.createTitledBorder("Shapes"));
        shapesPanel.setBackground(new Color(243,243,243,255));

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
        toolsPanel.setBackground(new Color(243,243,243,255));

        // processing image
        pencil = pencil.getScaledInstance(24, 24, Image.SCALE_SMOOTH);
        rubber = rubber.getScaledInstance(24, 24, Image.SCALE_SMOOTH);
        text = text.getScaledInstance(24, 24, Image.SCALE_SMOOTH);

        // tool buttons
        JButton pencilBtn = new JButton(new ImageIcon(pencil));
        JButton eraserBtn = new JButton(new ImageIcon(rubber));
        JButton textBtn = new JButton(new ImageIcon(text));
        eraserBtn.addActionListener(e -> {
            this.setDrawingMode(DrawingMode.FREE);
            this.setForeColor(backgroundColor);
            SetSlider(1);
        });
        pencilBtn.addActionListener(e -> {
            this.setDrawingMode(DrawingMode.FREE);
            this.setForeColor(Color.BLACK);
        });
        textBtn.addActionListener(e -> {});
        toolsPanel.add(pencilBtn);
        toolsPanel.add(eraserBtn);
        toolsPanel.add(textBtn);

        // color Panel
        JPanel colorPanel = new JPanel();
        colorPanel.setBorder(BorderFactory.createTitledBorder("Colors"));
        colorPanel.setBackground(new Color(243,243,243,255));
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


    public JPanel createThicknessPanel() {
        JPanel sliderPanel = new JPanel();
        sliderPanel.setBackground(new Color(243,243,243,255));
        // sliderPanel.setMaximumSize(new Dimension(50, 200));
        sliderPanel.setLayout(new BorderLayout());
        sliderPanel.setBackground(new Color(243,243,243,255));
        sliderPanel.setPreferredSize(new Dimension(50, 200));
        sliderPanel.setRequestFocusEnabled(false); // prevents requesting focus
        sliderPanel.setFocusable(false);

        // icon at top
        ImageIcon icon = new ImageIcon(pencil.getScaledInstance(15, 15, Image.SCALE_SMOOTH));
        JLabel iconLabel = new JLabel(icon);
        iconLabel.setHorizontalAlignment(SwingConstants.CENTER);
        iconLabel.setFocusable(Boolean.FALSE);
        sliderPanel.add(iconLabel, BorderLayout.NORTH);

        // vertical slider
        slider = new JSlider(JSlider.VERTICAL, 1, 100, (int) thickness);
          // slider.setMajorTickSpacing(5);
          // slider.setMinorTickSpacing(1);
          // slider.setPaintTicks(true);
        slider.setPaintLabels(false);
        slider.setRequestFocusEnabled(false);
        slider.setFocusable(false);
        slider.setBackground(new Color(243,243,243,255));

        slider.addChangeListener(e -> {
            thickness = slider.getValue();
        });

        sliderPanel.add(slider, BorderLayout.CENTER);
        return sliderPanel;
    }

    public void SetSlider(float value) {
        slider.setValue((int) value);
    }

    public Shape CreateShape(String type, Point start, Point end) {
        Shape shape = null;
        if (type.equals("Oval")) {
            Ellipse2D oval = new Ellipse2D.Double(start.x, start.y, end.x, end.y);
            return oval;
        } else if (type.equals("Triangle")) {
            int[] xPoints = {start.x, end.x, (start.x + end.x) / 2};
            int[] yPoints = {end.y, end.y, start.y};
            Polygon triangle = new Polygon(xPoints, yPoints, 3);
            return triangle;
        } else if (type.equals("Line")) {
            Line2D line = new Line2D.Double(start, end);
            return line;
        } else if (type.equals("Rectangle")) {
            Rectangle2D rect = new Rectangle2D.Double(start.x, start.y, end.x, end.y);
            return rect;
        } else if (type.equals("Free")) {
            Line2D line = new Line2D.Double(start, end);
            return line;
        }
        return shape;
    }

}
