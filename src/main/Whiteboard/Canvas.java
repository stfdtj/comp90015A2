package Whiteboard;

import Whiteboard.Utility.*;

import javax.imageio.ImageIO;
import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
import java.awt.geom.Ellipse2D;
import java.awt.geom.Line2D;
import java.awt.geom.Rectangle2D;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.rmi.RemoteException;
import java.util.ArrayList;
import java.util.List;



public class Canvas extends JPanel implements MouseListener, MouseMotionListener {

    private Image pencil = new ImageIcon("src/main/Whiteboard/resources/pencil.png").getImage();
    private Image rubber = new ImageIcon("src/main/Whiteboard/resources/rubber.png").getImage();
    private Image text = new ImageIcon("src/main/Whiteboard/resources/text.png").getImage();



    public final TextEditor textEditor = new TextEditor(this);

    private final boolean identity;
    private final String username;
    private JSlider slider;
    // should be saved
    private List<DrawingInfo> shapes = new ArrayList<>();
    private List<TextInfo> texts = new ArrayList<>();
    private int canvasWidth  = 1600;
    private int canvasHeight = 900;
    public int offsetX = 0;
    public int offsetY = 0;
    // should be saved
    private int dragStartX, dragStartY;
    private int panStartX, panStartY;
    private Shape previewShape = null;
    private Point currentPoint = null;
    private DrawingMode mode = DrawingMode.FREE;
    private Color currColor = Color.BLACK;
    private final Color backgroundColor = Color.WHITE;
    private float thickness = 3;




    private final JLabel cursorLabel;
    JButton curr;
    JScrollPane scroll;



    private final WhiteboardFunctions remoteService;

    public Canvas(WhiteboardFunctions service, boolean identity, String username) {
        this.remoteService = service;
        this.identity = identity;
        this.username = username;

        pencil = pencil.getScaledInstance(24, 24, Image.SCALE_SMOOTH);
        rubber = rubber.getScaledInstance(24, 24, Image.SCALE_SMOOTH);
        text = text.getScaledInstance(24, 24, Image.SCALE_SMOOTH);

        setPreferredSize(new Dimension(1600, 900));
        setBackground(backgroundColor);
        addMouseListener(this);
        addMouseMotionListener(this);
        KeyBindingManager.bindKeysToCanvas(this);



        cursorLabel = new JLabel(username);
        cursorLabel.setSize(cursorLabel.getPreferredSize());
        this.setLayout(null);
        this.add(cursorLabel);
        this.add(textEditor.CreateTextFormatBar());
        repaint();
        this.setFocusable(true);
        this.requestFocusInWindow();
        // testP
        // texts.add(new TextInfo("sample", Color.BLACK, DrawingMode.TEXT, 12, false, false,
                //false, new Font("Arial", Font.PLAIN, 12), new Point(100,100)));
    }

    public void setCanvasSize(int w, int h) {
        this.canvasWidth  = w;
        this.canvasHeight = h;
        revalidate();
        repaint();
    }

    @Override
    public Dimension getPreferredSize() {
        return new Dimension(canvasWidth, canvasHeight);
    }


    // record start point
    public void setCurrentPoint(Point p) {
        this.currentPoint = p;
    }
    // get current point
    public Point getCurrentPoint() {
        return currentPoint;
    }

    // paint locally and remotely
    // this is only for shapes excluding text
    public synchronized void AddShapeLocalRemote(Point start, Point end, DrawingMode mode, TextInfo textInfo) {
        if (!(mode == DrawingMode.TEXT)) {
            DrawingInfo info;
            info = new DrawingInfo(start, end, currColor, mode, thickness);

            shapes.add(info);
            try {
                if (identity) {
                    remoteService.BroadcastDrawing(info);
                } else {
                    remoteService.SendDrawings(info);
                }

            } catch (RemoteException ex) {
                ex.printStackTrace();
            }
        } else {
            texts.add(textInfo);
            try {
                if (identity) {
                    remoteService.BroadCastText(textInfo);
                } else {
                    remoteService.SendText(textInfo);
                }

            } catch (RemoteException ex) {
                ex.printStackTrace();
            }
        }

        this.repaint();


    }



    // client receive drawing from server
    public synchronized void ReceiveRemoteShape(DrawingInfo info, TextInfo textInfo) {
        // Log.action("Received remote shape: " + info.toString());
        try {
            if (info != null) {
                shapes.add(info);
            }
            if (textInfo != null) {
                texts.add(textInfo);
            }

            repaint();
        } catch (RuntimeException e) {
            Log.error("Remote exception: " + e.toString());
            throw new RuntimeException(e);
        }

    }

    // send drawing to server
    public synchronized void SendRemoteShape(DrawingInfo info, TextInfo textInfo) {
        try {
            // Log.action("Sent remote shape: " + info.toString());
            if (info != null) {
                shapes.add(info);
            }
            if (textInfo != null) {
                texts.add(textInfo);
            }
            repaint();
        } catch (RuntimeException e) {
            Log.error("Remote exception: " + e.toString());
            throw new RuntimeException(e);
        }
    }



    // render the drawn shapes
    // everything drawn before should be translated
    // but not affect currently doing one
    // lock everything if right drag?

    @Override
    protected void paintComponent(Graphics g) {
        super.paintComponent(g);
        Graphics2D g2 = (Graphics2D) g;

        // shift everything by the current pan offset
        g2.translate(offsetX, offsetY);


        for (DrawingInfo info : shapes) {
            if (info == null) {
                continue;
            }
            g2.setColor(info.getColor());
            g2.setStroke(new BasicStroke(info.getThickness()));
            Shape shape = CreateShape(info.getDrawingMode(), info.getStart(), info.getEnd());
            g2.draw(shape);
        }

        for (TextInfo info : texts) {
            if (info == null) {
                continue;
            }
            g2.setColor(info.getColor());
            g2.setFont(info.getFont());
            g2.drawString(info.getText(),info.getLocation().x, info.getLocation().y);
        }

        if (previewShape != null) {
            g2.setColor(currColor);
            g2.setStroke(new BasicStroke(thickness));
            g2.draw(previewShape);
        }
        // Toolkit.getDefaultToolkit().sync();
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
    // not left and right at same time to avoid issue
    @Override
    public void mousePressed(MouseEvent e) {
        if(SwingUtilities.isLeftMouseButton(e)) {
            mode.mousePressed(e, this);
        } else if (SwingUtilities.isRightMouseButton(e)) {
            dragStartX = e.getX();
            dragStartY = e.getY();
            panStartX  = offsetX;
            panStartY  = offsetY;
        }
    }

    @Override
    public void mouseDragged(MouseEvent e) {
        if(SwingUtilities.isLeftMouseButton(e)) {
            mode.mouseDragged(e, this);
        } else if (SwingUtilities.isRightMouseButton(e)) {
            int dx = e.getX() - dragStartX;
            int dy = e.getY() - dragStartY;

            offsetX = panStartX + dx;
            offsetY = panStartY + dy;

            // compute how much visible area needs to exist
            Rectangle view = getVisibleRect();
            int neededW = offsetX < 0
                    ? -offsetX + view.width
                    : offsetX + view.width;
            int neededH = offsetY < 0
                    ? -offsetY + view.height
                    : offsetY + view.height;

            // grow canvas if we dragged past its edge
            int newW = Math.max(canvasWidth, neededW);
            int newH = Math.max(canvasHeight, neededH);
            if (newW != canvasWidth || newH != canvasHeight) {
                canvasWidth = newW;
                canvasHeight = newH;
                setCanvasSize(canvasWidth, canvasHeight);
                revalidate();
            }
        }

        Point p = e.getPoint();
        cursorLabel.setLocation(p.x, p.y);
        repaint();
    }

    @Override
    public void mouseReleased(MouseEvent e) {
        if (SwingUtilities.isLeftMouseButton(e)) {
            mode.mouseReleased(e, this);
        } else if (SwingUtilities.isRightMouseButton(e)) {

        }

        Point p = e.getPoint();
        int offsetY = 8;
        cursorLabel.setLocation(p.x, p.y + offsetY);
        repaint();
    }



    @Override public void mouseClicked(MouseEvent e) {
        // Log.info("is canvas focused: "+ this.isFocusOwner());
        mode.mousePressed(e, this);
    }
    @Override public void mouseEntered(MouseEvent e) {
        cursorLabel.setVisible(true);
    }
    @Override public void mouseExited(MouseEvent e) {
        cursorLabel.setVisible(false);
    }
    @Override public void mouseMoved(MouseEvent e) {
        Point p = e.getPoint();

        cursorLabel.setLocation(p.x, p.y + offsetY);
        repaint();
    }

    // switch current mode
    public void setDrawingMode(DrawingMode mode) {
        this.mode = mode;
        if (mode.equals(DrawingMode.ERASER)) {
            this.currColor = backgroundColor;
            textEditor.textFormatBar.setVisible(false);
        } else if (mode.equals(DrawingMode.TEXT)) {
            this.setForeColor(Color.BLACK);
            this.SetSlider(1);
            textEditor.textFormatBar.setVisible(true);
        } else {
            this.setForeColor(Color.BLACK);
            textEditor.textFormatBar.setVisible(false);
        }

        Log.action("Set drawing mode: " + mode.toString());
    }

    // set fore colour
    public void setForeColor(Color c) {

        if (!mode.equals(DrawingMode.ERASER)) {
            this.currColor = c;
            curr.setBackground(c);
            Log.action("Set color: " + currColor.toString());
            // to be determined
            textEditor.SetColour(currColor);
        }
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
        shapeSelector.addActionListener(_ -> {
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
            // this.setForeColor(Color.BLACK);
        });
        shapesPanel.add(shapeSelector);


        // tools Panel
        JPanel toolsPanel = new JPanel();
        toolsPanel.setBorder(BorderFactory.createTitledBorder("Tools"));
        toolsPanel.setBackground(new Color(243,243,243,255));


        // tool buttons
        JButton pencilBtn = new JButton(new ImageIcon(pencil));
        JButton eraserBtn = new JButton(new ImageIcon(rubber));
        JButton textBtn = new JButton(new ImageIcon(text));
        eraserBtn.addActionListener(_ -> {
            this.setDrawingMode(DrawingMode.ERASER);
            this.currColor = Color.WHITE;
        });
        pencilBtn.addActionListener(_ -> {
            this.setDrawingMode(DrawingMode.FREE);
            this.setForeColor(Color.BLACK);
            this.currColor = Color.BLACK;
        });
        textBtn.addActionListener(_ -> {
            this.setDrawingMode(DrawingMode.TEXT);
            this.setForeColor(Color.BLACK);
        });

        toolsPanel.add(pencilBtn);
        toolsPanel.add(eraserBtn);
        toolsPanel.add(textBtn);

        // color Panel
        JPanel colorPanel = new JPanel();
        colorPanel.setBorder(BorderFactory.createTitledBorder("Colors"));
        colorPanel.setBackground(new Color(243,243,243,255));
        Color[] palette = {Color.BLACK, Color.RED, Color.GREEN, Color.BLUE, Color.MAGENTA, Color.YELLOW, Color.CYAN};

        // current color display
        JColorChooser colorChooser = new JColorChooser();
        curr = new JButton();
        curr.setBackground(currColor);
        curr.setPreferredSize(new Dimension(40, 40));
        curr.addActionListener(_ -> {
            Color selected = JColorChooser.showDialog(null, "Select Color", colorChooser.getColor());
            if (selected != null) {
                this.setForeColor(selected);
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
            colorBtn.addActionListener(_ -> {
                this.setForeColor(c);
                curr.setBackground(c);
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

        slider.addChangeListener(_ -> {
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

    // add a text panel to canvas
    public void AddTextBox(Point start, Point end) {
        scroll = textEditor.CreateTextBox(start, end);
        this.add(scroll);
        this.revalidate();
        this.repaint();
    }

    public void RemoveTextBox() {
        if (scroll != null) {
            this.remove(scroll);
            scroll = null;
        }

        this.revalidate();
        this.repaint();
        SwingUtilities.invokeLater(this::requestFocusInWindow);

        // Log.info("is canvas focused: "+ this.isFocusOwner());
        // Log.info("Removed TextBox");
    }


    public void Saving(){

    }

}
