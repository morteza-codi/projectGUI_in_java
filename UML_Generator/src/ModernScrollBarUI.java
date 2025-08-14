import javax.swing.*;
import javax.swing.plaf.basic.BasicScrollBarUI;
import java.awt.*;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;

/**
 * Modern scroll bar UI with sleek styling and hover effects
 */
public class ModernScrollBarUI extends BasicScrollBarUI {
    
    private static final Color TRACK_COLOR = new Color(240, 240, 240);
    private static final Color THUMB_COLOR = new Color(180, 180, 180);
    private static final Color THUMB_HOVER_COLOR = new Color(150, 150, 150);
    private static final Color THUMB_PRESSED_COLOR = new Color(120, 120, 120);
    
    private boolean isThumbHover = false;
    private boolean isThumbPressed = false;
    
    @Override
    protected void configureScrollBarColors() {
        // Override default colors
        thumbColor = THUMB_COLOR;
        thumbHighlightColor = THUMB_HOVER_COLOR;
        thumbDarkShadowColor = THUMB_PRESSED_COLOR;
        trackColor = TRACK_COLOR;
        trackHighlightColor = TRACK_COLOR;
    }
    
    @Override
    protected void paintTrack(Graphics g, JComponent c, Rectangle trackBounds) {
        Graphics2D g2d = (Graphics2D) g.create();
        g2d.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
        
        // Paint track with rounded corners
        g2d.setColor(TRACK_COLOR);
        g2d.fillRoundRect(trackBounds.x, trackBounds.y, 
            trackBounds.width, trackBounds.height, 6, 6);
        
        g2d.dispose();
    }
    
    @Override
    protected void paintThumb(Graphics g, JComponent c, Rectangle thumbBounds) {
        if (thumbBounds.isEmpty() || !scrollbar.isEnabled()) {
            return;
        }
        
        Graphics2D g2d = (Graphics2D) g.create();
        g2d.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
        
        // Determine thumb color based on state
        Color thumbColor;
        if (isThumbPressed) {
            thumbColor = THUMB_PRESSED_COLOR;
        } else if (isThumbHover) {
            thumbColor = THUMB_HOVER_COLOR;
        } else {
            thumbColor = THUMB_COLOR;
        }
        
        // Paint thumb with rounded corners and gradient
        int arc = 6;
        GradientPaint gradient = new GradientPaint(
            thumbBounds.x, thumbBounds.y, thumbColor.brighter(),
            thumbBounds.x, thumbBounds.y + thumbBounds.height, thumbColor
        );
        
        g2d.setPaint(gradient);
        g2d.fillRoundRect(thumbBounds.x, thumbBounds.y, 
            thumbBounds.width, thumbBounds.height, arc, arc);
        
        // Add subtle border
        g2d.setColor(thumbColor.darker());
        g2d.setStroke(new BasicStroke(1.0f));
        g2d.drawRoundRect(thumbBounds.x, thumbBounds.y, 
            thumbBounds.width - 1, thumbBounds.height - 1, arc, arc);
        
        g2d.dispose();
    }
    
    @Override
    protected JButton createDecreaseButton(int orientation) {
        return createZeroSizeButton();
    }
    
    @Override
    protected JButton createIncreaseButton(int orientation) {
        return createZeroSizeButton();
    }
    
    private JButton createZeroSizeButton() {
        JButton button = new JButton();
        button.setPreferredSize(new Dimension(0, 0));
        button.setMinimumSize(new Dimension(0, 0));
        button.setMaximumSize(new Dimension(0, 0));
        return button;
    }
    
    @Override
    protected void installListeners() {
        super.installListeners();
        
        // Add mouse listener for hover effects
        scrollbar.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseEntered(MouseEvent e) {
                isThumbHover = true;
                scrollbar.repaint();
            }
            
            @Override
            public void mouseExited(MouseEvent e) {
                isThumbHover = false;
                scrollbar.repaint();
            }
            
            @Override
            public void mousePressed(MouseEvent e) {
                isThumbPressed = true;
                scrollbar.repaint();
            }
            
            @Override
            public void mouseReleased(MouseEvent e) {
                isThumbPressed = false;
                scrollbar.repaint();
            }
        });
        
        // Add mouse motion listener for hover tracking
        scrollbar.addMouseMotionListener(new MouseAdapter() {
            @Override
            public void mouseMoved(MouseEvent e) {
                Rectangle thumbBounds = getThumbBounds();
                boolean wasHover = isThumbHover;
                isThumbHover = thumbBounds.contains(e.getPoint());
                
                if (wasHover != isThumbHover) {
                    scrollbar.repaint();
                }
            }
        });
    }
    
    @Override
    public Dimension getPreferredSize(JComponent c) {
        if (scrollbar.getOrientation() == JScrollBar.VERTICAL) {
            return new Dimension(12, 0);
        } else {
            return new Dimension(0, 12);
        }
    }
}
