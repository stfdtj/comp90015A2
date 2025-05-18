package Whiteboard;

import Whiteboard.Utility.*;

import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
import java.awt.geom.Ellipse2D;
import java.awt.geom.Line2D;
import java.awt.geom.Rectangle2D;
import java.io.File;
import java.rmi.RemoteException;
import java.util.*;
import java.util.List;


public class Canvas extends JPanel implements MouseListener, MouseMotionListener {

    private Image pencil;
    private Image rubber;
    private Image text;

    public final TextEditor textEditor = new TextEditor(this);
    private String boardName;
    private JSlider slider;
    private int canvasWidth  = 1600;
    private int canvasHeight = 900;
    public int offsetX = 0;
    public int offsetY = 0;
    private int dragStartX, dragStartY;
    private int panStartX, panStartY;
    private Shape previewShape = null;
    private Point currentPoint = null;
    private DrawingMode mode = DrawingMode.FREE;
    private Color currColor = Color.BLACK;
    private final Color backgroundColor = Color.WHITE;
    private float thickness = 3;
    // to show thickness
    private JLabel iconLabel;
    private static Rectangle textBoxLocation;
    private static ArrayList<RemoteUser> clients;
    private final RemoteUser me;
    private ChatWindow win;
    private Point cursorPt = null;
    // the indicator of current colour
    private JButton curr;
    private JScrollPane scroll;
    private final WhiteboardData data;
    private final WhiteboardFunctions remoteService;
    private ArrayList<Drawings> drawings = new ArrayList<>();
    private final ArrayList<Drawings> localDrawings = new ArrayList<>();
    private final boolean identity;



    public Canvas(WhiteboardFunctions service, String username, String boardName,
                  WhiteboardData saved, ChatWindow win, Properties props, boolean identity) {
        this.remoteService = service;
        this.boardName = boardName;
        this.setLayout(null);
        this.win = win;
        this.identity = identity;
        data = saved;
        allocate();
        repaint();
        pencil = new ImageIcon(props.getProperty("app.icon.pencil")).getImage();
        rubber = new ImageIcon(props.getProperty("app.icon.rubber")).getImage();
        text = new ImageIcon(props.getProperty("app.icon.text")).getImage();

        // scale
        pencil = pencil.getScaledInstance(24, 24, Image.SCALE_SMOOTH);
        rubber = rubber.getScaledInstance(24, 24, Image.SCALE_SMOOTH);
        text = text.getScaledInstance(24, 24, Image.SCALE_SMOOTH);

        setPreferredSize(new Dimension(canvasWidth, canvasHeight));
        setBackground(backgroundColor);
        addMouseListener(this);
        addMouseMotionListener(this);
        KeyBindingManager.bindKeysToCanvas(this);

        this.setLayout(null);

        repaint();
        this.setFocusable(true);
        this.requestFocusInWindow();

        me = new RemoteUser(username, cursorPt);
        TryAddRemoteUser();
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
    public synchronized void AddShapeLocalRemote(Point start, Point end, DrawingMode mode, TextInfo textInfo) {
        if (!(mode == DrawingMode.TEXT)) {
            Drawings info;
            info = new DrawingInfo(start, end, currColor, mode, thickness);
            info.id = UUID.randomUUID().toString();
            drawings.add(info);
            localDrawings.add(info);
            try {
                if (identity) {
                    remoteService.BroadcastDrawing(info);
                } else {
                    remoteService.ClientSendDrawing(info);
                }

            } catch (RemoteException ex) {
                Log.error(ex.toString());
            }
        } else {
            textInfo.id = UUID.randomUUID().toString();
            drawings.add(textInfo);
            localDrawings.add(textInfo);
            try {
                remoteService.BroadcastDrawing(textInfo);
            } catch (RemoteException ex) {
                Log.error(ex.toString());
            }
        }

        this.repaint();
    }


    // client receive drawing from server
    public synchronized void ReceiveRemoteShape(Drawings d) {

        try {
            drawings.add(d);
            repaint();
        } catch (RuntimeException e) {
            Log.error("Remote exception: " + e);
            throw new RuntimeException(e);
        }

    }

    // render the drawn shapes
    // everything drawn before should be translated
    @Override
    protected void paintComponent(Graphics g) {
        super.paintComponent(g);
        Graphics2D g2 = (Graphics2D) g.create();
        // Graphics2D g2 = (Graphics2D) g;

        // shift everything by the current pan offset
        g2.translate(offsetX, offsetY);

        for (Drawings drawing : drawings) {
            if (drawing == null) {
                continue;
            }
            if (drawing instanceof TextInfo textInfo) {
                g2.setColor(textInfo.getColor());
                g2.setFont(textInfo.getFont());
                // preserve new line
                FontMetrics fm = g2.getFontMetrics();
                int lineHeight = fm.getHeight();
                String[] lines = textInfo.getText().split("\n");
                int x = textInfo.getLocation().x;
                int y = textInfo.getLocation().y + fm.getAscent();
                // draw line by line
                for (String line : lines) {
                    g2.drawString(line, x, y);
                    y += lineHeight;
                }
            } else if (drawing instanceof DrawingInfo info){
                g2.setColor(info.getColor());
                if (info.getDrawingMode().equals(DrawingMode.FREE)) {
                    g2.setStroke(new BasicStroke(
                            info.getThickness(),
                            BasicStroke.CAP_ROUND,
                            BasicStroke.JOIN_ROUND
                    ));
                } else {
                    g2.setStroke(new BasicStroke(info.getThickness()));
                }
                Shape shape = CreateShape(info.getDrawingMode(), info.getStart(), info.getEnd());
                g2.draw(shape);
            }
        }
        // then draw cursor
        for (RemoteUser client : clients) {
            Point p = client.cursorPosition;
            if (p == null) continue;

            FontMetrics fm = g2.getFontMetrics();
            int textW = fm.stringWidth(client.username);
            int textH = fm.getHeight();

            int x = p.x + 12;
            int y = p.y + 16;

            g2.setColor(client.color);
            g2.fillRoundRect(x-5, y - textH + 2, textW+8, textH+2, 6,6);
            g2.setColor(Color.WHITE);
            g2.drawString(client.username, x, y);
        }

        if (previewShape != null) {
            g2.setColor(currColor);
            g2.setStroke(new BasicStroke(thickness));
            g2.draw(previewShape);
        }
        g2.dispose();

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

    @Override
    public void mousePressed(MouseEvent e) {
        Component hit = SwingUtilities.getDeepestComponentAt(this, e.getX(), e.getY());
        if (hit != this) {
            return;
        }

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

        Component hit = SwingUtilities.getDeepestComponentAt(this, e.getX(), e.getY());
        if (hit != this) {
            return;
        }
        cursorPt = new Point(e.getX() - offsetX, e.getY() - offsetY);
        try {
            remoteService.UpdateCursor(cursorPt, me.id);
        } catch (RemoteException ex) {
            Log.error("Remote exception: " + ex);
        }
        repaint();
        if(SwingUtilities.isLeftMouseButton(e)) {
            mode.mouseDragged(e, this);
        } else if (SwingUtilities.isRightMouseButton(e)) {
            int dx = e.getX() - dragStartX;
            int dy = e.getY() - dragStartY;

            offsetX = panStartX + dx;
            offsetY = panStartY + dy;

            // compute visible area
            Rectangle view = getVisibleRect();
            int neededW = offsetX < 0
                    ? -offsetX + view.width
                    : offsetX + view.width;
            int neededH = offsetY < 0
                    ? -offsetY + view.height
                    : offsetY + view.height;

            // grow canvas
            int newW = Math.max(canvasWidth, neededW);
            int newH = Math.max(canvasHeight, neededH);
            if (newW != canvasWidth || newH != canvasHeight) {
                canvasWidth = newW;
                canvasHeight = newH;
                setCanvasSize(canvasWidth, canvasHeight);
                revalidate();
            }

        }

        if (scroll != null && textBoxLocation != null) {
            Dimension d = scroll.getPreferredSize();
            Point pt  = new Point(textBoxLocation.x + offsetX,
                    textBoxLocation.y + offsetY);
            scroll.setBounds(new Rectangle(pt, d));

        }

        repaint();
    }

    @Override
    public void mouseReleased(MouseEvent e) {

        Component hit = SwingUtilities.getDeepestComponentAt(this, e.getX(), e.getY());
        if (hit != this) {
            return;
        }

        if (SwingUtilities.isLeftMouseButton(e)) {
            mode.mouseReleased(e, this);
        }
        if (scroll != null && textBoxLocation != null) {

            Dimension d = scroll.getPreferredSize();
            Point pt  = new Point(textBoxLocation.x + offsetX,
                    textBoxLocation.y + offsetY);
            scroll.setBounds(new Rectangle(pt, d));

        }
        repaint();
    }

    @Override public void mouseClicked(MouseEvent e) {
    }
    @Override public void mouseEntered(MouseEvent e) {
    }
    @Override public void mouseExited(MouseEvent e) {
    }
    @Override public void mouseMoved(MouseEvent e) {
        cursorPt = new Point(e.getX() - offsetX, e.getY() - offsetY);
        try {
            remoteService.UpdateCursor(cursorPt, me.id);
        } catch (RemoteException ex) {
            throw new RuntimeException(ex);
        }
        repaint();
    }

    // switch current drawing mode
    public void setDrawingMode(DrawingMode mode) {
        this.mode = mode;
        if (mode.equals(DrawingMode.ERASER)) {
            this.currColor = backgroundColor;
            textEditor.textFormatBar.setVisible(false);
        } else if (mode.equals(DrawingMode.TEXT)) {
            this.setForeColor(Color.BLACK);
            this.setSlider(1);
            textEditor.textFormatBar.setVisible(true);
        } else {
            this.setForeColor(Color.BLACK);
            textEditor.textFormatBar.setVisible(false);
        }

        Log.action("Set drawing mode: " + mode);
    }

    // set fore colour
    public void setForeColor(Color c) {

        if (!mode.equals(DrawingMode.ERASER)) {
            this.currColor = c;
            curr.setBackground(c);
            Log.action("Set color: " + currColor.toString());
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
            assert selected != null;
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
        ButtonGroup toolGroup = new ButtonGroup();


        // tool buttons
        JToggleButton pencilBtn = new JToggleButton(new ImageIcon(pencil));
        JToggleButton eraserBtn = new JToggleButton(new ImageIcon(rubber));
        JToggleButton textBtn   = new JToggleButton(new ImageIcon(text));
        toolGroup.add(pencilBtn);
        toolGroup.add(eraserBtn);
        toolGroup.add(textBtn);
        ActionListener toggleListener = e -> {
            JToggleButton src = (JToggleButton)e.getSource();

            if (src.isSelected()) {
                if (src == pencilBtn) {
                    setDrawingMode(DrawingMode.FREE);
                    setForeColor(Color.BLACK);
                } else if (src == eraserBtn) {
                    setDrawingMode(DrawingMode.ERASER);
                    setForeColor(Color.WHITE);
                } else {
                    setDrawingMode(DrawingMode.TEXT);
                    setForeColor(Color.BLACK);
                }
            } else {
                setDrawingMode(DrawingMode.FREE);
            }
        };

        pencilBtn.addActionListener(toggleListener);
        eraserBtn.addActionListener(toggleListener);
        textBtn.addActionListener(toggleListener);

        toolsPanel.add(pencilBtn);
        toolsPanel.add(eraserBtn);
        toolsPanel.add(textBtn);

        // color Panel
        JPanel colorPanel = new JPanel();
        colorPanel.setBorder(BorderFactory.createTitledBorder("Colors"));
        colorPanel.setBackground(new Color(243,243,243,255));
        Color[] palette = {Color.BLACK, Color.RED, Color.GREEN, Color.BLUE, Color.MAGENTA, Color.YELLOW, Color.CYAN,
                Color.PINK, Color.GRAY, new Color(207, 159, 255), Color.WHITE, Color.ORANGE, new Color(0, 79, 45),
                new Color(145, 174, 193), new Color(93, 63, 211), new Color(114, 47, 55)};

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
        sliderPanel.setLayout(new BorderLayout());
        sliderPanel.setBackground(new Color(243,243,243,255));
        sliderPanel.setPreferredSize(new Dimension(50, 200));
        sliderPanel.setRequestFocusEnabled(false); // prevents requesting focus
        sliderPanel.setFocusable(false);

        // icon at top
        iconLabel = new JLabel(String.valueOf(thickness));
        iconLabel.setHorizontalAlignment(SwingConstants.CENTER);
        iconLabel.setFocusable(Boolean.FALSE);
        sliderPanel.add(iconLabel, BorderLayout.NORTH);

        // vertical slider
        slider = new JSlider(JSlider.VERTICAL, 1, 100, (int) thickness);
        slider.setPaintLabels(false);
        slider.setRequestFocusEnabled(false);
        slider.setFocusable(false);
        slider.setBackground(new Color(243,243,243,255));

        slider.addChangeListener(_ -> {
            thickness = slider.getValue();
            iconLabel.setText(String.valueOf(thickness));
        });

        sliderPanel.add(slider, BorderLayout.CENTER);
        return sliderPanel;
    }

    // map thickness with slider
    public void setSlider(float value) {
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
            Log.error(e.getMessage());
            return null;
        }

    }

    // add a text panel to canvas
    public void AddTextBox(Point start, Point end) {

        scroll = textEditor.CreateTextBox(start, end);
        Dimension d = new Dimension(scroll.getPreferredSize().width, scroll.getPreferredSize().height);
        textBoxLocation = new Rectangle(start, d);

        this.add(scroll);
        scroll.setVisible(true);
        this.revalidate();
        this.repaint();
    }

    public void RemoveTextBox() {
        if (scroll != null) {
            this.remove(scroll);
            scroll = null;
            textBoxLocation = null;
            textEditor.CleanTextBox();

        }

        this.revalidate();
        this.repaint();
        SwingUtilities.invokeLater(this::requestFocusInWindow);

    }

    public List<Drawings> getDrawingInfo() {
        return drawings;
    }

    public void Saving(File path){
        Log.info(offsetX+" "+offsetY+" "+canvasHeight+" "+canvasWidth+" "+boardName+" ");
        data.setOffSetX(offsetX);
        data.setOffSetY(offsetY);
        data.setCanvasHeight(canvasHeight);
        data.setCanvasWidth(canvasWidth);
        data.setBoardName(boardName);
        data.setDrawings(drawings);
        // check: should get file path here
        data.SaveData(path);
    }

    public JScrollPane getScroll() {
        return scroll;
    }

    // assign data to each variable
    public void allocate() {
        try {
            boardName = data.getBoardName();
            canvasHeight = data.getCanvasHeight();
            canvasWidth = data.getCanvasWidth();
            offsetX = data.getOffSetX();
            offsetY = data.getOffSetY();
            drawings = (ArrayList<Drawings>) data.getDrawings();
        } catch (NullPointerException e) {
            Log.info("This data is empty");
        }

    }

    public void setClients(ArrayList<RemoteUser> clients) {
        Canvas.clients = clients;
        repaint();
    }

    public void ReceiveMessage(String m) {
        win.AppendMessage(m);
    }

    public void setChatWindow(ChatWindow win) {
        this.win = win;
    }

    public void TryAddRemoteUser() {
        try {
            remoteService.AddRemoteUser(me);
            clients = remoteService.getUsers();
            me.id = remoteService.GetNumUsers();
        } catch (RemoteException e) {
            Log.error(e.getMessage());
        }
    }

    public void Undo() {
        if (localDrawings == null || localDrawings.isEmpty()) {
            return;
        }
        Drawings item = localDrawings.getLast();
        Log.info(item.toString());
        drawings.remove(item);
        localDrawings.remove(item);
        try {
            remoteService.BroadCastRemoving(item.id);
            if (!identity) {
                remoteService.ClientSendRemoving(item.id);
            }
            Log.info("BroadCastRemoving "+item.id);
        } catch (RemoteException e) {
            Log.error(e.getMessage());
        }
        repaint();
    }

    public void ReceiveRemoving(String id) {
        if(identity){
            Log.info(" Server Receive removing "+id);
        }
        Log.info("RemoteRemoving called with id: " + id);
        if (drawings == null) {
            Log.error("drawings is null");
            return;
        }

        for (Drawings drawing : drawings) {
            if (drawing == null || drawing.id == null) {
                Log.info("Skipping drawing with null id");
                continue;
            }
            if (drawing.id.equals(id)) {
                drawings.remove(drawing);
                return;
            }
        }
        repaint();
    }


}
