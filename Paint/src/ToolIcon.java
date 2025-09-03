import java.awt.BasicStroke;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.RenderingHints;
import java.awt.geom.Ellipse2D;
import java.awt.geom.Line2D;

import java.awt.geom.RoundRectangle2D;
import javax.swing.JPanel;

/**
 * Custom panel that draws tool icons for the paint application
 */
public class ToolIcon extends JPanel {
    
    public static final int PENCIL = 0;
    public static final int ERASER = 1;
    public static final int LINE = 2;
    public static final int BOX = 3;
    public static final int ELLIPSE = 4;
    public static final int TRIANGLE = 5;
    public static final int RIGHT_TRIANGLE = 6;
    public static final int DIAMOND = 7;
    public static final int PENTAGON = 8;
    
    private int iconType;
    private Color iconColor;
    private boolean isSelected;
    
    /**
     * Creates a new tool icon
     * 
     * @param iconType The type of icon to draw
     */
    public ToolIcon(int iconType) {
        this.iconType = iconType;
        this.iconColor = ModernLookAndFeel.TEXT_COLOR;
        setBackground(ModernLookAndFeel.PANEL_COLOR);
        this.isSelected = false;
        setPreferredSize(new Dimension(24, 24));
        setOpaque(false);
    }
    
    /**
     * Sets whether this icon is selected
     * 
     * @param selected true if selected, false otherwise
     */
    public void setSelected(boolean selected) {
        isSelected = selected;
        iconColor = selected ? ModernLookAndFeel.PRIMARY_COLOR : ModernLookAndFeel.TEXT_COLOR;
        repaint();
    }
    
    @Override
    protected void paintComponent(Graphics g) {
        super.paintComponent(g);
        
        Graphics2D g2d = (Graphics2D) g.create();
        g2d.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
        
        int w = getWidth();
        int h = getHeight();
        int padding = 3;
        
        // Draw background
        if (isSelected) {
            g2d.setColor(new Color(ModernLookAndFeel.PRIMARY_COLOR.getRed(), 
                                  ModernLookAndFeel.PRIMARY_COLOR.getGreen(), 
                                  ModernLookAndFeel.PRIMARY_COLOR.getBlue(), 30));
            g2d.fillRoundRect(0, 0, w, h, 8, 8);
        }
        
        g2d.setColor(iconColor);
        g2d.setStroke(new BasicStroke(1.5f, BasicStroke.CAP_ROUND, BasicStroke.JOIN_ROUND));
        
        // Draw the appropriate icon
        switch (iconType) {
            case PENCIL:
                drawPencilIcon(g2d, padding, w, h);
                break;
            case ERASER:
                drawEraserIcon(g2d, padding, w, h);
                break;
            case LINE:
                drawLineIcon(g2d, padding, w, h);
                break;
            case BOX:
                drawBoxIcon(g2d, padding, w, h);
                break;
            case ELLIPSE:
                drawEllipseIcon(g2d, padding, w, h);
                break;
            case TRIANGLE:
                drawTriangleIcon(g2d, padding, w, h);
                break;
            case RIGHT_TRIANGLE:
                drawRightTriangleIcon(g2d, padding, w, h);
                break;
            case DIAMOND:
                drawDiamondIcon(g2d, padding, w, h);
                break;
            case PENTAGON:
                drawPentagonIcon(g2d, padding, w, h);
                break;
        }
        
        g2d.dispose();
    }
    
    private void drawPencilIcon(Graphics2D g2d, int padding, int w, int h) {
        int[] xPoints = {padding, w - padding, w - padding - 6, padding + 6};
        int[] yPoints = {h - padding - 6, padding + 6, padding, h - padding};
        g2d.drawPolygon(xPoints, yPoints, 4);
        
        // Draw pencil tip
        g2d.drawLine(padding + 3, h - padding - 3, padding + 6, h - padding - 6);
    }
    
    private void drawEraserIcon(Graphics2D g2d, int padding, int w, int h) {
        g2d.drawRoundRect(padding + 2, padding + 2, w - 2*padding - 4, h - 2*padding - 4, 4, 4);
        g2d.drawLine(padding + 2, h - padding - 2, w - padding - 2, padding + 2);
    }
    
    private void drawLineIcon(Graphics2D g2d, int padding, int w, int h) {
        g2d.draw(new Line2D.Double(padding, h - padding, w - padding, padding));
    }
    
    private void drawBoxIcon(Graphics2D g2d, int padding, int w, int h) {
        g2d.draw(new RoundRectangle2D.Double(padding, padding, w - 2*padding, h - 2*padding, 4, 4));
    }
    
    private void drawEllipseIcon(Graphics2D g2d, int padding, int w, int h) {
        g2d.draw(new Ellipse2D.Double(padding, padding, w - 2*padding, h - 2*padding));
    }
    
    private void drawTriangleIcon(Graphics2D g2d, int padding, int w, int h) {
        int[] xPoints = {w/2, w - padding, padding};
        int[] yPoints = {padding, h - padding, h - padding};
        g2d.drawPolygon(xPoints, yPoints, 3);
    }
    
    private void drawRightTriangleIcon(Graphics2D g2d, int padding, int w, int h) {
        int[] xPoints = {padding, padding, w - padding};
        int[] yPoints = {padding, h - padding, h - padding};
        g2d.drawPolygon(xPoints, yPoints, 3);
    }
    
    private void drawDiamondIcon(Graphics2D g2d, int padding, int w, int h) {
        int[] xPoints = {w/2, w - padding, w/2, padding};
        int[] yPoints = {padding, h/2, h - padding, h/2};
        g2d.drawPolygon(xPoints, yPoints, 4);
    }
    
    private void drawPentagonIcon(Graphics2D g2d, int padding, int w, int h) {
        int[] xPoints = {w/2, w - padding - 2, w - padding - 6, padding + 6, padding + 2};
        int[] yPoints = {padding, padding + 8, h - padding, h - padding, padding + 8};
        g2d.drawPolygon(xPoints, yPoints, 5);
    }
}
