import javax.swing.*;

import java.awt.*;
import java.awt.geom.RoundRectangle2D;

/**
 * Utility class that provides modern UI components for the Paint application
 */
public class ModernUIComponents {
    
    /**
     * Creates a rounded panel with shadow effect
     */
    public static class RoundedPanel extends JPanel {
        private int cornerRadius = 15;
        private Color shadowColor = new Color(0, 0, 0, 50);
        private int shadowSize = 5;
        
        public RoundedPanel() {
            setOpaque(false);
            setBorder(BorderFactory.createEmptyBorder(shadowSize, shadowSize, shadowSize, shadowSize));
        }
        
        public RoundedPanel(int cornerRadius) {
            this();
            this.cornerRadius = cornerRadius;
        }
        
        public RoundedPanel(int cornerRadius, Color bgColor) {
            this(cornerRadius);
            setBackground(bgColor);
        }
        
        @Override
        protected void paintComponent(Graphics g) {
            Graphics2D g2 = (Graphics2D) g.create();
            g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
            
            // Paint shadow
            g2.setColor(shadowColor);
            g2.fill(new RoundRectangle2D.Double(shadowSize, shadowSize, 
                    getWidth() - (2 * shadowSize), 
                    getHeight() - (2 * shadowSize), 
                    cornerRadius, cornerRadius));
            
            // Paint panel background
            g2.setColor(getBackground());
            g2.fill(new RoundRectangle2D.Double(0, 0, 
                    getWidth() - shadowSize, 
                    getHeight() - shadowSize, 
                    cornerRadius, cornerRadius));
            
            g2.dispose();
        }
    }
    
    /**
     * Creates a modern slider with custom colors
     */
    public static JSlider createModernSlider(int min, int max, int value) {
        JSlider slider = new JSlider(min, max, value);
        slider.setBackground(ModernLookAndFeel.PANEL_COLOR);
        slider.setForeground(ModernLookAndFeel.PRIMARY_COLOR);
        slider.setPaintTrack(true);
        slider.setPaintTicks(true);
        slider.setPaintLabels(true);
        slider.setMajorTickSpacing((max - min) / 5);
        slider.setMinorTickSpacing(1);
        
        // Increase label font size and add more space for labels
        java.awt.Font labelFont = new java.awt.Font("Sans-Serif", java.awt.Font.PLAIN, 10);
        slider.setFont(labelFont);
        
        // Add more space below the slider for the labels
        javax.swing.border.Border emptyBorder = BorderFactory.createEmptyBorder(0, 0, 10, 0);
        slider.setBorder(emptyBorder);
        
        return slider;
    }
    
    /**
     * Creates a tool button with hover effect
     */
    public static JButton createToolButton(String text) {
        JButton button = new JButton(text);
        button.setBackground(ModernLookAndFeel.PANEL_COLOR);
        button.setForeground(ModernLookAndFeel.TEXT_COLOR);
        button.setBorder(ModernLookAndFeel.createRoundedPaddedBorder(
                ModernLookAndFeel.PRIMARY_COLOR, 1, ModernLookAndFeel.CORNER_RADIUS, 5));
        button.setFocusPainted(false);
        button.setContentAreaFilled(false);
        button.setOpaque(false);
        
        // Add hover effect
        button.addMouseListener(new java.awt.event.MouseAdapter() {
            @Override
            public void mouseEntered(java.awt.event.MouseEvent evt) {
                button.setForeground(ModernLookAndFeel.PRIMARY_COLOR);
                button.setBorder(ModernLookAndFeel.createRoundedPaddedBorder(
                        ModernLookAndFeel.HOVER_COLOR, 2, ModernLookAndFeel.CORNER_RADIUS, 5));
            }
            
            @Override
            public void mouseExited(java.awt.event.MouseEvent evt) {
                button.setForeground(ModernLookAndFeel.TEXT_COLOR);
                button.setBorder(ModernLookAndFeel.createRoundedPaddedBorder(
                        ModernLookAndFeel.PRIMARY_COLOR, 1, ModernLookAndFeel.CORNER_RADIUS, 5));
            }
        });
        
        return button;
    }
    
    /**
     * Creates a color button with rounded corners and selection effect
     */
    public static JButton createColorButton(Color color) {
        JButton button = new JButton();
        button.setBackground(color);
        button.setPreferredSize(new Dimension(25, 25));
        button.setBorder(ModernLookAndFeel.createRoundedBorder(Color.GRAY, 1, 5));
        button.setFocusPainted(false);
        
        return button;
    }
    
    /**
     * Creates a preview button for selected colors
     */
    public static JButton createColorPreviewButton(Color color) {
        JButton button = new JButton();
        button.setBackground(color);
        button.setPreferredSize(new Dimension(35, 35));
        button.setBorder(ModernLookAndFeel.createRoundedBorder(Color.DARK_GRAY, 2, 8));
        button.setFocusPainted(false);
        
        return button;
    }
    
    /**
     * Creates a modern menu bar
     */
    public static JMenuBar createModernMenuBar() {
        JMenuBar menuBar = new JMenuBar();
        menuBar.setBackground(ModernLookAndFeel.PANEL_COLOR);
        menuBar.setBorder(BorderFactory.createMatteBorder(0, 0, 1, 0, ModernLookAndFeel.PRIMARY_COLOR));
        return menuBar;
    }
    
    /**
     * Creates a modern menu
     */
    public static JMenu createModernMenu(String text) {
        JMenu menu = new JMenu(text);
        menu.setBackground(ModernLookAndFeel.PANEL_COLOR);
        menu.setForeground(ModernLookAndFeel.TEXT_COLOR);
        return menu;
    }
    
    /**
     * Creates a modern menu item
     */
    public static JMenuItem createModernMenuItem(String text) {
        JMenuItem menuItem = new JMenuItem(text);
        menuItem.setBackground(ModernLookAndFeel.PANEL_COLOR);
        menuItem.setForeground(ModernLookAndFeel.TEXT_COLOR);
        return menuItem;
    }
}
