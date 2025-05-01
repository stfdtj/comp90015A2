package Whiteboard;

import javax.swing.*;
import java.awt.*;
import java.awt.event.MouseEvent;
import java.awt.geom.Ellipse2D;
import java.awt.geom.Line2D;
import java.awt.geom.Rectangle2D;
import java.io.Serializable;

public enum DrawingMode implements Serializable {
    FREE {

        @Override
        public void mousePressed(MouseEvent e, Canvas canvas) {
            if (!SwingUtilities.isLeftMouseButton(e)) return;
            canvas.setCurrentPoint(e.getPoint());
        }

        @Override
        public void mouseDragged(MouseEvent e, Canvas canvas) {
            Point last = canvas.getCurrentPoint();
            Point current = e.getPoint();
            canvas.AddShapeLocalRemote(last, current, DrawingMode.FREE, null);
            canvas.setCurrentPoint(current);
        }

        @Override
        public void mouseReleased(MouseEvent e, Canvas canvas) {
            canvas.setCurrentPoint(null);
        }
    },
    LINE {
        Point start = null;

        @Override
        public void mousePressed(MouseEvent e, Canvas canvas) {
            if (!SwingUtilities.isLeftMouseButton(e)) return;
            start = e.getPoint();
        }

        @Override
        public void mouseDragged(MouseEvent e, Canvas canvas) {
            if (start != null) {
                Point end = e.getPoint();
                Line2D tempLine = new Line2D.Float(start, end);
                canvas.setPreviewShape(tempLine);
            }
        }

        @Override
        public void mouseReleased(MouseEvent e, Canvas canvas) {
            if (start != null) {
                Point end = e.getPoint();
                canvas.clearPreviewShape(); // remove preview
                canvas.AddShapeLocalRemote(start, end, DrawingMode.LINE, null);
                start = null;
            }
        }
    },
    RECTANGLE {
        Point start = null;
        @Override
        public void mousePressed(MouseEvent e, Canvas canvas) {
            if (!SwingUtilities.isLeftMouseButton(e)) return;
            start = e.getPoint();
        }

        @Override
        public void mouseDragged(MouseEvent e, Canvas canvas) {
            if (start != null) {
                Point end = e.getPoint();
                double height = Math.abs(start.getY() - end.getY());
                double width = Math.abs(start.getX() - end.getX());
                Rectangle2D rect = new Rectangle2D.Double(start.x, start.y, width, height);
                canvas.setPreviewShape(rect);
            }
        }

        @Override
        public void mouseReleased(MouseEvent e, Canvas canvas) {
            if (start != null) {
                Point end = e.getPoint();
                canvas.clearPreviewShape();
                canvas.AddShapeLocalRemote(start, end, DrawingMode.RECTANGLE, null);
                start = null;
            }
        }
    },
    OVAL{
        Point start = null;
        @Override
        public void mousePressed(MouseEvent e, Canvas canvas) {
            if (!SwingUtilities.isLeftMouseButton(e)) return;
            start = e.getPoint();
        }

        @Override
        public void mouseDragged(MouseEvent e, Canvas canvas) {
            if (start != null) {
                Point end = e.getPoint();
                double height = Math.abs(start.getY() - end.getY());
                double width = Math.abs(start.getX() - end.getX());
                Ellipse2D oval = new Ellipse2D.Double(start.x, start.y, width, height);
                canvas.setPreviewShape(oval);
            }
        }

        @Override
        public void mouseReleased(MouseEvent e, Canvas canvas) {
            if (start != null) {
                Point end = e.getPoint();
                canvas.clearPreviewShape();
                canvas.AddShapeLocalRemote(start, end, DrawingMode.OVAL, null);
                start = null;
            }
        }
    },
    TRIANGLE {
        Point start = null;
        @Override
        public void mousePressed(MouseEvent e, Canvas canvas) {
            if (!SwingUtilities.isLeftMouseButton(e)) return;
            start = e.getPoint();
        }

        @Override
        public void mouseDragged(MouseEvent e, Canvas canvas) {
            if (start != null) {
                Point end = e.getPoint();
                int[] xPoints = {start.x, end.x, (start.x + end.x) / 2};
                int[] yPoints = {end.y, end.y, start.y};
                Polygon triangle = new Polygon(xPoints, yPoints, 3);
                canvas.setPreviewShape(triangle);
            }
        }

        @Override
        public void mouseReleased(MouseEvent e, Canvas canvas) {
            if (start != null) {
                Point end = e.getPoint();
                canvas.clearPreviewShape();
                canvas.AddShapeLocalRemote(start, end, DrawingMode.TRIANGLE, null);
                start = null;
            }
        }

    },
    TEXT{
        Point start = null;
        @Override
        public void mousePressed(MouseEvent e, Canvas canvas) {
            start = e.getPoint();
        }

        @Override
        public void mouseDragged(MouseEvent e, Canvas canvas) {
            // set preview?
            if (start != null) {
                Point end = e.getPoint();
                double height = Math.abs(start.getY() - end.getY());
                double width = Math.abs(start.getX() - end.getX());
                Rectangle2D rect = new Rectangle2D.Double(start.x, start.y, width, height);
                canvas.setPreviewShape(rect);
            }
        }

        @Override
        public void mouseReleased(MouseEvent e, Canvas canvas) {
            if (start != null) {
                Point end = e.getPoint();
                canvas.clearPreviewShape();
                canvas.AddTextBox(start, end);
            }
        }
    },
    ERASER{
        @Override
        public void mousePressed(MouseEvent e, Canvas canvas) {
            if (!SwingUtilities.isLeftMouseButton(e)) return;
            canvas.setCurrentPoint(e.getPoint());
        }

        @Override
        public void mouseDragged(MouseEvent e, Canvas canvas) {
            Point last = canvas.getCurrentPoint();
            Point current = e.getPoint();
            canvas.AddShapeLocalRemote(last, current, DrawingMode.FREE, null);
            canvas.setCurrentPoint(current);
        }

        @Override
        public void mouseReleased(MouseEvent e, Canvas canvas) {
            canvas.setCurrentPoint(null);
        }
    }
    ;


    public abstract void mousePressed(MouseEvent e, Canvas canvas);
    public abstract void mouseDragged(MouseEvent e, Canvas canvas);
    public abstract void mouseReleased(MouseEvent e, Canvas canvas);
}
