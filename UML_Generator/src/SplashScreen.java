import javax.swing.*;
import java.awt.*;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.geom.RoundRectangle2D;
import java.awt.image.BufferedImage;

/**
 * Modern splash screen for UML Generator application
 */
public class SplashScreen extends JWindow {
    private static final Color PRIMARY_COLOR = new Color(41, 128, 185);
    private static final Color SECONDARY_COLOR = new Color(52, 73, 94);
    private static final Color ACCENT_COLOR = new Color(46, 204, 113);
    private static final Color SURFACE_COLOR = Color.WHITE;
    
    public SplashScreen() {
        createSplashScreen();
    }
    
    private void createSplashScreen() {
        setSize(500, 350);
        setLocationRelativeTo(null);
        
        JPanel mainPanel = new JPanel() {
            @Override
            protected void paintComponent(Graphics g) {
                super.paintComponent(g);
                paintSplashBackground(g);
            }
        };
        
        mainPanel.setLayout(new BorderLayout());
        mainPanel.setOpaque(false);
        
        // Add content
        mainPanel.add(createContentPanel(), BorderLayout.CENTER);
        
        setContentPane(mainPanel);
        
        // Make it draggable
        final Point[] lastPoint = new Point[1];
        addMouseListener(new MouseAdapter() {
            @Override
            public void mousePressed(MouseEvent e) {
                lastPoint[0] = e.getPoint();
            }
        });
        
        addMouseMotionListener(new MouseAdapter() {
            @Override
            public void mouseDragged(MouseEvent e) {
                if (lastPoint[0] != null) {
                    Point currentLocation = getLocation();
                    setLocation(currentLocation.x + e.getX() - lastPoint[0].x,
                               currentLocation.y + e.getY() - lastPoint[0].y);
                }
            }
        });
    }
    
    private void paintSplashBackground(Graphics g) {
        Graphics2D g2d = (Graphics2D) g.create();
        g2d.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
        
        // Gradient background
        GradientPaint gradient = new GradientPaint(
            0, 0, PRIMARY_COLOR,
            getWidth(), getHeight(), SECONDARY_COLOR
        );
        g2d.setPaint(gradient);
        g2d.fillRoundRect(0, 0, getWidth(), getHeight(), 20, 20);
        
        // Add some decorative elements
        g2d.setColor(new Color(255, 255, 255, 30));
        for (int i = 0; i < 5; i++) {
            int size = 50 + i * 30;
            g2d.fillOval(getWidth() - size - 20, 20 + i * 15, size, size);
        }
        
        g2d.dispose();
    }
    
    private JPanel createContentPanel() {
        JPanel panel = new JPanel();
        panel.setLayout(new BoxLayout(panel, BoxLayout.Y_AXIS));
        panel.setOpaque(false);
        panel.setBorder(BorderFactory.createEmptyBorder(40, 40, 40, 40));
        
        // Logo/Icon
        JLabel iconLabel = new JLabel(createAppIcon());
        iconLabel.setAlignmentX(Component.CENTER_ALIGNMENT);
        
        // Title
        JLabel titleLabel = new JLabel("🎨 UML Generator Pro");
        titleLabel.setFont(new Font("Segoe UI", Font.BOLD, 32));
        titleLabel.setForeground(SURFACE_COLOR);
        titleLabel.setAlignmentX(Component.CENTER_ALIGNMENT);
        
        // Subtitle
        JLabel subtitleLabel = new JLabel("Professional Java Code Visualization");
        subtitleLabel.setFont(new Font("Segoe UI", Font.PLAIN, 16));
        subtitleLabel.setForeground(new Color(189, 195, 199));
        subtitleLabel.setAlignmentX(Component.CENTER_ALIGNMENT);
        
        // Features list
        JPanel featuresPanel = new JPanel();
        featuresPanel.setLayout(new BoxLayout(featuresPanel, BoxLayout.Y_AXIS));
        featuresPanel.setOpaque(false);
        featuresPanel.setBorder(BorderFactory.createEmptyBorder(20, 0, 20, 0));
        
        String[] features = {
            "✨ Modern & Intuitive Interface",
            "🔍 Advanced Java Code Analysis", 
            "🎯 Professional UML Diagrams",
            "💾 High-Quality Image Export",
            "🚀 Fast & Efficient Processing"
        };
        
        for (String feature : features) {
            JLabel featureLabel = new JLabel(feature);
            featureLabel.setFont(new Font("Segoe UI", Font.PLAIN, 12));
            featureLabel.setForeground(new Color(236, 240, 241));
            featureLabel.setAlignmentX(Component.CENTER_ALIGNMENT);
            featureLabel.setBorder(BorderFactory.createEmptyBorder(2, 0, 2, 0));
            featuresPanel.add(featureLabel);
        }
        
        // Loading indicator
        JProgressBar progressBar = new JProgressBar();
        progressBar.setIndeterminate(true);
        progressBar.setStringPainted(true);
        progressBar.setString("Loading...");
        progressBar.setFont(new Font("Segoe UI", Font.PLAIN, 10));
        progressBar.setBackground(new Color(255, 255, 255, 100));
        progressBar.setForeground(ACCENT_COLOR);
        progressBar.setMaximumSize(new Dimension(300, 20));
        
        // Version info
        JLabel versionLabel = new JLabel("Version 2.0 - Enhanced Edition");
        versionLabel.setFont(new Font("Segoe UI", Font.ITALIC, 10));
        versionLabel.setForeground(new Color(149, 165, 166));
        versionLabel.setAlignmentX(Component.CENTER_ALIGNMENT);
        
        // Add components
        panel.add(iconLabel);
        panel.add(Box.createVerticalStrut(20));
        panel.add(titleLabel);
        panel.add(Box.createVerticalStrut(5));
        panel.add(subtitleLabel);
        panel.add(featuresPanel);
        panel.add(progressBar);
        panel.add(Box.createVerticalStrut(10));
        panel.add(versionLabel);
        
        return panel;
    }
    
    private ImageIcon createAppIcon() {
        BufferedImage icon = new BufferedImage(80, 80, BufferedImage.TYPE_INT_ARGB);
        Graphics2D g2d = icon.createGraphics();
        g2d.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
        
        // Background circle with gradient
        GradientPaint gradient = new GradientPaint(
            0, 0, ACCENT_COLOR,
            80, 80, PRIMARY_COLOR
        );
        g2d.setPaint(gradient);
        g2d.fillOval(10, 10, 60, 60);
        
        // Inner design - modern UML boxes
        g2d.setColor(SURFACE_COLOR);
        g2d.fillRoundRect(25, 25, 15, 10, 3, 3);
        g2d.fillRoundRect(45, 25, 15, 10, 3, 3);
        g2d.fillRoundRect(25, 45, 15, 10, 3, 3);
        g2d.fillRoundRect(45, 45, 15, 10, 3, 3);
        
        // Connection lines with rounded ends
        g2d.setStroke(new BasicStroke(2, BasicStroke.CAP_ROUND, BasicStroke.JOIN_ROUND));
        g2d.drawLine(40, 30, 45, 30);
        g2d.drawLine(32, 35, 32, 45);
        g2d.drawLine(52, 35, 52, 45);
        
        g2d.dispose();
        return new ImageIcon(icon);
    }
    
    /**
     * Shows the splash screen with animation
     */
    public void showSplash() {
        setVisible(true);
        
        // Fade in animation
        Timer fadeTimer = new Timer(20, null);
        fadeTimer.addActionListener(e -> {
            float opacity = Math.min(1.0f, getOpacity() + 0.05f);
            setOpacity(opacity);
            if (opacity >= 1.0f) {
                fadeTimer.stop();
            }
        });
        
        setOpacity(0.0f);
        fadeTimer.start();
        
        // Auto-hide after delay
        Timer hideTimer = new Timer(3000, e -> hideSplash());
        hideTimer.setRepeats(false);
        hideTimer.start();
    }
    
    /**
     * Hides the splash screen with fade out animation
     */
    public void hideSplash() {
        Timer fadeTimer = new Timer(20, null);
        fadeTimer.addActionListener(e -> {
            float opacity = Math.max(0.0f, getOpacity() - 0.05f);
            setOpacity(opacity);
            if (opacity <= 0.0f) {
                fadeTimer.stop();
                dispose();
            }
        });
        fadeTimer.start();
    }
}
