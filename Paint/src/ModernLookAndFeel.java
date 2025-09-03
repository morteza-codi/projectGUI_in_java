import javax.swing.*;
import javax.swing.border.Border;
import javax.swing.border.CompoundBorder;
import javax.swing.border.EmptyBorder;

import javax.swing.plaf.basic.BasicLookAndFeel;
import java.awt.*;

/**
 * A custom Look and Feel for the Paint application that provides a modern UI
 */
public class ModernLookAndFeel extends BasicLookAndFeel {
    // Modern color palette
    public static final Color PRIMARY_COLOR = new Color(41, 128, 185);    // Blue
    public static final Color SECONDARY_COLOR = new Color(46, 204, 113);  // Green
    public static final Color BACKGROUND_COLOR = new Color(245, 245, 245); // Light gray
    public static final Color PANEL_COLOR = new Color(255, 255, 255);     // White
    public static final Color TEXT_COLOR = new Color(44, 62, 80);         // Dark blue/gray
    public static final Color ACCENT_COLOR = new Color(231, 76, 60);      // Red
    public static final Color HOVER_COLOR = new Color(52, 152, 219);      // Light blue
    
    // Rounded corner radius
    public static final int CORNER_RADIUS = 8;
    
    @Override
    public String getName() {
        return "ModernLookAndFeel";
    }

    @Override
    public String getID() {
        return "ModernLookAndFeel";
    }

    @Override
    public String getDescription() {
        return "A modern look and feel for the Paint application";
    }

    @Override
    public boolean isNativeLookAndFeel() {
        return false;
    }

    @Override
    public boolean isSupportedLookAndFeel() {
        return true;
    }
    
    @Override
    protected void initClassDefaults(UIDefaults table) {
        super.initClassDefaults(table);
    }
    
    @Override
    protected void initSystemColorDefaults(UIDefaults table) {
        super.initSystemColorDefaults(table);
        
        table.put("control", BACKGROUND_COLOR);
        table.put("info", PANEL_COLOR);
        table.put("nimbusBase", PRIMARY_COLOR);
        table.put("nimbusBlueGrey", BACKGROUND_COLOR);
        table.put("nimbusLightBackground", PANEL_COLOR);
        table.put("text", TEXT_COLOR);
    }
    
    @Override
    protected void initComponentDefaults(UIDefaults table) {
        super.initComponentDefaults(table);
        
        // Button defaults
        table.put("Button.background", PANEL_COLOR);
        table.put("Button.foreground", TEXT_COLOR);
        table.put("Button.border", BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(PRIMARY_COLOR, 1),
                BorderFactory.createEmptyBorder(4, 10, 4, 10)
        ));
        
        // Panel defaults
        table.put("Panel.background", PANEL_COLOR);
        
        // Menu defaults
        table.put("Menu.background", PANEL_COLOR);
        table.put("Menu.foreground", TEXT_COLOR);
        table.put("MenuBar.background", PANEL_COLOR);
        table.put("MenuItem.background", PANEL_COLOR);
        table.put("MenuItem.foreground", TEXT_COLOR);
        
        // Slider defaults
        table.put("Slider.background", PANEL_COLOR);
        table.put("Slider.foreground", PRIMARY_COLOR);
        table.put("Slider.thumbColor", PRIMARY_COLOR);
        table.put("Slider.trackColor", BACKGROUND_COLOR);
    }
    
    /**
     * Creates a rounded border with the specified color
     */
    public static Border createRoundedBorder(Color color, int thickness, int radius) {
        return new RoundedBorder(color, thickness, radius);
    }
    
    /**
     * Creates a rounded border with padding
     */
    public static Border createRoundedPaddedBorder(Color color, int thickness, int radius, int padding) {
        return new CompoundBorder(
                new RoundedBorder(color, thickness, radius),
                new EmptyBorder(padding, padding, padding, padding)
        );
    }
    
    /**
     * Custom border class that draws rounded rectangles
     */
    public static class RoundedBorder implements Border {
        private Color color;
        private int thickness;
        private int radius;
        
        public RoundedBorder(Color color, int thickness, int radius) {
            this.color = color;
            this.thickness = thickness;
            this.radius = radius;
        }
        
        @Override
        public void paintBorder(Component c, Graphics g, int x, int y, int width, int height) {
            Graphics2D g2d = (Graphics2D) g.create();
            g2d.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
            g2d.setColor(color);
            g2d.setStroke(new BasicStroke(thickness));
            g2d.drawRoundRect(x + thickness/2, y + thickness/2, 
                            width - thickness, height - thickness, 
                            radius, radius);
            g2d.dispose();
        }
        
        @Override
        public Insets getBorderInsets(Component c) {
            return new Insets(thickness + 2, thickness + 2, thickness + 2, thickness + 2);
        }
        
        @Override
        public boolean isBorderOpaque() {
            return false;
        }
    }
    
    /**
     * Creates a panel with a gradient background
     */
    public static JPanel createGradientPanel() {
        return new JPanel() {
            @Override
            protected void paintComponent(Graphics g) {
                Graphics2D g2d = (Graphics2D) g.create();
                g2d.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                
                int w = getWidth();
                int h = getHeight();
                
                GradientPaint gradient = new GradientPaint(
                    0, 0, new Color(245, 245, 245),
                    0, h, new Color(225, 225, 225)
                );
                
                g2d.setPaint(gradient);
                g2d.fillRect(0, 0, w, h);
                g2d.dispose();
            }
        };
    }
    
    /**
     * Creates a button with a modern style
     */
    public static JButton createModernButton(String text) {
        JButton button = new JButton(text);
        button.setBackground(PANEL_COLOR);
        button.setForeground(TEXT_COLOR);
        button.setBorder(createRoundedPaddedBorder(PRIMARY_COLOR, 1, CORNER_RADIUS, 4));
        button.setFocusPainted(false);
        button.setContentAreaFilled(false);
        button.setOpaque(false);
        
        return button;
    }
    
    /**
     * Creates a color button with a modern style
     */
    public static JButton createColorButton(Color color) {
        JButton button = new JButton();
        button.setBackground(color);
        button.setBorder(createRoundedBorder(new Color(200, 200, 200), 1, CORNER_RADIUS));
        button.setFocusPainted(false);
        
        return button;
    }
}
