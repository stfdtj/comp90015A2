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
    private final boolean identity;

    private JSlider slider;

    List<ShapeCustom> shapes = new ArrayList<>();
    // a preview, not finalized
    private Shape previewShape = null;


    private Point currentPoint = null;

    private DrawingMode mode = DrawingMode.FREE;

    private Color currColor = Color.BLACK;
    private Color backgroundColor = Color.WHITE;
    private float thickness = 3;



    private WhiteboardFunctions remoteService;

    public Canvas(WhiteboardFunctions service, boolean identity) {
        this.remoteService = service;
        this.identity = identity;
        this.setLayout(new BorderLayout());
        setBackground(backgroundColor);
        addMouseListener(this);
        addMouseMotionListener(this);


    }



    // record start point
    public void setCurrentPoint(Point p) {
        this.currentPoint = p;
    }
    // get current point
    public Point getCurrentPoint() {
        return currentPoint;
    }

    public synchronized void AddShapeLocalRemote(Point start, Point end, DrawingMode mode) {
        DrawingInfo info;
        if (mode.equals(DrawingMode.FREE)) {
            Shape line = CreateShape(mode, start, end);
            shapes.add(new ShapeCustom(line, currColor, mode, thickness));
        } else if (mode.equals(DrawingMode.LINE)) {
            Shape line = CreateShape(mode, start, end);
            shapes.add(new ShapeCustom(line, currColor, mode, thickness));
        } else if (mode.equals(DrawingMode.RECTANGLE)) {
            Shape rect = CreateShape(mode, start, end);
            shapes.add(new ShapeCustom(rect, currColor, mode, thickness));
        } else if (mode.equals(DrawingMode.OVAL)) {
            Shape oval = CreateShape(mode, start, end);
            shapes.add(new ShapeCustom(oval, currColor, mode, thickness));
        } else if (mode.equals(DrawingMode.TRIANGLE)) {
            Shape triangle = CreateShape(mode, start, end);
            shapes.add(new ShapeCustom(triangle, currColor, mode, thickness));
        }
        repaint();
        info = new DrawingInfo(start, end, currColor, mode, thickness);

        try {
            if (identity) {
                remoteService.BroadcastDrawing(info);
            } else {
                remoteService.SendDrawings(info);
            }

        } catch (RemoteException ex) {
            ex.printStackTrace();
        }
    }



    // client receive drawing from server
    public synchronized void ReceiveRemoteShape(DrawingInfo info) {
        try {
            Shape shape = CreateShape(info.getDrawingMode(), info.getStart(), info.getEnd());
            shapes.add(new ShapeCustom(shape, info.getColor(), info.getDrawingMode(), info.getThickness()));
            repaint();
        } catch (RuntimeException e) {
            throw new RuntimeException(e);
        }

    }

    public synchronized void SendRemoteShape(DrawingInfo info) {
        try {
            Shape shape = CreateShape(info.getDrawingMode(), info.getStart(), info.getEnd());
            shapes.add(new ShapeCustom(shape, info.getColor(), info.getDrawingMode(), info.getThickness()));
            repaint();
        } catch (RuntimeException e) {
            throw new RuntimeException(e);
        }
    }



    // render the drawn shapes
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


    // create a preview
    public void setPreviewShape(Shape s) {
        this.previewShape = s;
        repaint();
    }

    // make preview disappear
    public void clearPreviewShape() {
        this.previewShape = null;
        repaint();
    }



    // mouse events
    @Override
    public void mousePressed(MouseEvent e) {
        if(SwingUtilities.isLeftMouseButton(e)) {
            mode.mousePressed(e, this);
        }

    }

    @Override
    public void mouseDragged(MouseEvent e) {
        if(SwingUtilities.isLeftMouseButton(e)) {
            mode.mouseDragged(e, this);
        }
    }

    @Override
    public void mouseReleased(MouseEvent e) {
        if (SwingUtilities.isLeftMouseButton(e)) {
            mode.mouseReleased(e, this);
        }
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
            this.SetCurrColors(Color.BLACK);
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
        textBtn.addActionListener(e -> {

        });

        toolsPanel.add(pencilBtn);
        toolsPanel.add(eraserBtn);
        toolsPanel.add(textBtn);

        // color Panel
        JPanel colorPanel = new JPanel();
        colorPanel.setBorder(BorderFactory.createTitledBorder("Colors"));
        colorPanel.setBackground(new Color(243,243,243,255));
        Color[] palette = {Color.BLACK, Color.RED, Color.GREEN, Color.BLUE, Color.MAGENTA, Color.YELLOW, Color.CYAN};

        JColorChooser colorChooser = new JColorChooser();
        JButton curr = new JButton();
        curr.setBackground(currColor);
        curr.setPreferredSize(new Dimension(40, 40));
        curr.addActionListener(e -> {
            Color selected = JColorChooser.showDialog(null, "Select Color", colorChooser.getColor());
            if (selected != null) {
                this.SetCurrColors(selected);
                curr.setBackground(selected);
                colorChooser.setColor(selected);
            }
        });
        colorPanel.add(curr);
        for (Color c : palette) {
            JButton colorBtn = new JButton();
            colorBtn.setBackground(c);
            colorBtn.setPreferredSize(new Dimension(20, 20));
            colorBtn.setBorder(BorderFactory.createLineBorder(Color.GRAY));
            colorPanel.add(colorBtn);
            colorBtn.addActionListener(e -> {
                SetCurrColors(c);
                curr.setBackground(currColor);
            });
        }

        // add all panels to toolbar
        toolbar.add(shapesPanel);
        toolbar.add(Box.createRigidArea(new Dimension(15, 0)));
        toolbar.add(toolsPanel);
        toolbar.add(Box.createRigidArea(new Dimension(15, 0)));
        toolbar.add(colorPanel);
        return toolbar;
    }

    // thickness slider
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

    // map thickness with slider
    public void SetSlider(float value) {
        slider.setValue((int) value);
    }

    // return a shape
    public Shape CreateShape(DrawingMode mode, Point start, Point end) {
        try {
            if (mode.equals(DrawingMode.OVAL)) {
                double height = Math.abs(start.getY() - end.getY());
                double width = Math.abs(start.getX() - end.getX());
                return new Ellipse2D.Double(start.x, start.y, width, height);
            } else if (mode.equals(DrawingMode.TRIANGLE)) {
                int[] xPoints = {start.x, end.x, (start.x + end.x) / 2};
                int[] yPoints = {end.y, end.y, start.y};
                return new Polygon(xPoints, yPoints, 3);
            } else if (mode.equals(DrawingMode.LINE)) {
                return new Line2D.Double(start, end);
            } else if (mode.equals(DrawingMode.RECTANGLE)) {
                double height = Math.abs(start.getY() - end.getY());
                double width = Math.abs(start.getX() - end.getX());
                return new Rectangle2D.Double(start.x, start.y, width, height);
            } else if (mode.equals(DrawingMode.FREE)) {
                return new Line2D.Double(start, end);
            } else return null;
        } catch (NullPointerException e) {
            e.printStackTrace();
            return null;
        }

    }

    public void SetCurrColors(Color color) {
        this.currColor = color;
    }

}
