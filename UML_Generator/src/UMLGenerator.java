import javax.swing.*;
import javax.swing.filechooser.FileNameExtensionFilter;
import javax.swing.border.*;
import java.awt.*;
import java.awt.event.*;
import java.awt.image.BufferedImage;
import java.awt.geom.RoundRectangle2D;
import java.io.*;
import java.util.*;
import java.util.List;
import java.util.stream.Collectors;

/**
 * Main UML Generator application class
 */
public class UMLGenerator extends JFrame {
    private JTextArea logArea;
    private JPanel diagramPanel;
    private JButton loadButton, generateButton, saveButton, filterButton;
    private File currentDirectory;
    
    // Model
    private CodeAnalyzer codeAnalyzer;
    private DiagramRenderer diagramRenderer;
    private Map<String, ClassInfo> visibleClasses;
    
    // Modern UI Colors
    private static final Color PRIMARY_COLOR = new Color(41, 128, 185);      // Professional blue
    private static final Color SECONDARY_COLOR = new Color(52, 73, 94);      // Dark blue-gray
    private static final Color ACCENT_COLOR = new Color(46, 204, 113);       // Green accent
    private static final Color BACKGROUND_COLOR = new Color(236, 240, 241);   // Light gray
    private static final Color SURFACE_COLOR = Color.WHITE;                   // White surfaces
    private static final Color TEXT_PRIMARY = new Color(33, 37, 41);         // Dark text
    private static final Color TEXT_SECONDARY = new Color(108, 117, 125);    // Gray text
    
    public UMLGenerator() {
        super("🎨 UML Diagram Generator - Professional Edition");
        
        // Initialize components
        codeAnalyzer = new CodeAnalyzer();
        diagramRenderer = new DiagramRenderer();
        visibleClasses = new HashMap<>();
        currentDirectory = new File(System.getProperty("user.dir"));
        
        // Set modern look and feel
        try {
            UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
            setupModernUIDefaults();
        } catch (Exception e) {
            e.printStackTrace();
        }
        
        setupUI();
        setupListeners();
        
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setExtendedState(JFrame.MAXIMIZED_BOTH); // Start maximized
        setMinimumSize(new Dimension(1200, 800));
        setLocationRelativeTo(null);
        
        // Add modern window icon
        setIconImage(createAppIcon());
        
        setVisible(true);
    }
    
    private void setupUI() {
        // Main layout with modern background
        setLayout(new BorderLayout());
        getContentPane().setBackground(BACKGROUND_COLOR);
        
        // Modern header panel
        JPanel headerPanel = createHeaderPanel();
        add(headerPanel, BorderLayout.NORTH);
        
        // Split pane for log and diagram
        JSplitPane splitPane = new JSplitPane(JSplitPane.VERTICAL_SPLIT);
        splitPane.setResizeWeight(0.3);
        
        // Log area (top part of split)
        logArea = new JTextArea();
        logArea.setEditable(false);
        JScrollPane logScrollPane = createEnhancedScrollPane(logArea, "Log");
        splitPane.setTopComponent(logScrollPane);
        
        // Diagram panel (bottom part of split)
        diagramPanel = new JPanel() {
            @Override
            protected void paintComponent(Graphics g) {
                super.paintComponent(g);
                if (!visibleClasses.isEmpty()) {
                    diagramRenderer.drawUML(g, codeAnalyzer.getClasses(), visibleClasses, 
                        codeAnalyzer.getRelationships().stream()
                            .filter(r -> visibleClasses.containsKey(r.getSource()) && 
                                         visibleClasses.containsKey(r.getTarget()))
                            .collect(Collectors.toList()));
                }
            }
        };
        diagramPanel.setBackground(Color.WHITE);
        JScrollPane diagramScrollPane = createEnhancedScrollPane(diagramPanel, "UML Diagram");
        splitPane.setBottomComponent(diagramScrollPane);
        
        add(splitPane, BorderLayout.CENTER);
        
        // Style the components
        styleComponents();
    }
    
    private void setupListeners() {
        loadButton.addActionListener(e -> loadJavaFiles());
        generateButton.addActionListener(e -> generateUML());
        saveButton.addActionListener(e -> saveUMLImage());
        filterButton.addActionListener(e -> filterClasses());
    }
    
    private void loadJavaFiles() {
        JFileChooser fileChooser = new JFileChooser(currentDirectory);
        fileChooser.setMultiSelectionEnabled(true);
        fileChooser.setFileFilter(new FileNameExtensionFilter("Java Files", "java"));
        fileChooser.setFileSelectionMode(JFileChooser.FILES_AND_DIRECTORIES);
        
        int result = fileChooser.showOpenDialog(this);
        if (result == JFileChooser.APPROVE_OPTION) {
            File[] selectedFiles = fileChooser.getSelectedFiles();
            currentDirectory = fileChooser.getCurrentDirectory();
            
            // Clear previous data
            codeAnalyzer.clear();
            visibleClasses.clear();
            logArea.setText("");
            
            try {
                // Analyze each selected file/directory
                for (File selectedFile : selectedFiles) {
                    if (selectedFile.isDirectory()) {
                        log("Analyzing directory: " + selectedFile.getName());
                        codeAnalyzer.analyzeDirectory(selectedFile);
                    } else if (selectedFile.getName().endsWith(".java")) {
                        log("Analyzing file: " + selectedFile.getName());
                        codeAnalyzer.analyzeJavaFile(selectedFile);
                    }
                }
                
                log("Analysis complete. Found " + codeAnalyzer.getClasses().size() + " classes and " + 
                    codeAnalyzer.getRelationships().size() + " relationships.");
                
                // Enable buttons
                generateButton.setEnabled(true);
                
                if (codeAnalyzer.getClasses().isEmpty()) {
                    JOptionPane.showMessageDialog(this, 
                        "No Java classes found in the selected files/directories.", 
                        "Warning", JOptionPane.WARNING_MESSAGE);
                }
                
            } catch (Exception e) {
                log("Error during analysis: " + e.getMessage());
                JOptionPane.showMessageDialog(this, 
                    "Error analyzing files: " + e.getMessage(), 
                    "Error", JOptionPane.ERROR_MESSAGE);
            }
        }
    }
    
    private void generateUML() {
        if (codeAnalyzer.getClasses().isEmpty()) {
            JOptionPane.showMessageDialog(this, 
                "No classes found. Please load Java files first.", 
                "Error", JOptionPane.ERROR_MESSAGE);
            return;
        }
        
        log("Generating UML diagram...");
        
        // Show all classes by default
        visibleClasses = new HashMap<>(codeAnalyzer.getClasses());
        
        // Calculate layout
        calculateLayout();
        
        // Force scroll pane to recognize new preferred size
        SwingUtilities.invokeLater(() -> {
            diagramPanel.revalidate();
            diagramPanel.repaint();
            
            // Get the scroll pane containing the diagram panel
            JScrollPane scrollPane = (JScrollPane) diagramPanel.getParent().getParent();
            scrollPane.revalidate();
            scrollPane.repaint();
        });
        
        // Enable buttons
        saveButton.setEnabled(true);
        filterButton.setEnabled(true);
        
        log("UML diagram generated.");
    }
    
    private void calculateLayout() {
        // Recalculate class sizes and positions
        int spacing = 150; // افزایش فاصله بین کلاس‌ها
        int x = spacing;
        int y = spacing;
        int maxHeight = 0;
        
        int maxClassesPerRow = 2; // کاهش تعداد کلاس در هر ردیف
        int classCounter = 0;
        
        for (ClassInfo classInfo : visibleClasses.values()) {
            // Calculate class box size based on content
            classInfo.calculateSize(getGraphics());
            
            // افزایش اندازه کلاس برای اطمینان از جا شدن محتوا
            classInfo.setWidth(classInfo.getWidth() + 40);
            classInfo.setHeight(classInfo.getHeight() + 20);
            
            classInfo.setX(x);
            classInfo.setY(y);
            
            x += classInfo.getWidth() + spacing;
            maxHeight = Math.max(maxHeight, classInfo.getHeight());
            
            classCounter++;
            if (classCounter % maxClassesPerRow == 0) {
                x = spacing;
                y += maxHeight + spacing;
                maxHeight = 0;
            }
        }
        
        // Set preferred size for scrolling
        int maxX = visibleClasses.values().stream().mapToInt(c -> c.getX() + c.getWidth()).max().orElse(0);
        int maxY = visibleClasses.values().stream().mapToInt(c -> c.getY() + c.getHeight()).max().orElse(0);
        
        diagramPanel.setPreferredSize(new Dimension(
            Math.max(1200, maxX + spacing * 2),
            Math.max(800, maxY + spacing * 2)));
    }
    
    private void filterClasses() {
        if (codeAnalyzer.getClasses().isEmpty()) {
            return;
        }
        
        createModernFilterDialog();
    }
    
    /**
     * Creates a modern, attractive filter dialog
     */
    private void createModernFilterDialog() {
        JDialog dialog = new JDialog(this, "🔍 Class Filter - Choose Classes to Display", true);
        dialog.setLayout(new BorderLayout());
        dialog.setBackground(SURFACE_COLOR);
        
        // Header panel
        JPanel headerPanel = new JPanel(new BorderLayout());
        headerPanel.setBackground(PRIMARY_COLOR);
        headerPanel.setBorder(BorderFactory.createEmptyBorder(15, 20, 15, 20));
        
        JLabel titleLabel = new JLabel("Select Classes for UML Diagram");
        titleLabel.setFont(new Font("Segoe UI", Font.BOLD, 16));
        titleLabel.setForeground(SURFACE_COLOR);
        headerPanel.add(titleLabel, BorderLayout.WEST);
        
        JLabel countLabel = new JLabel(String.format("%d classes found", codeAnalyzer.getClasses().size()));
        countLabel.setFont(new Font("Segoe UI", Font.PLAIN, 12));
        countLabel.setForeground(new Color(189, 195, 199));
        headerPanel.add(countLabel, BorderLayout.EAST);
        
        dialog.add(headerPanel, BorderLayout.NORTH);
        
        // Create checkboxes panel with modern styling
        JPanel mainPanel = new JPanel(new BorderLayout());
        mainPanel.setBackground(SURFACE_COLOR);
        mainPanel.setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20));
        
        JPanel checkboxPanel = new JPanel();
        checkboxPanel.setLayout(new BoxLayout(checkboxPanel, BoxLayout.Y_AXIS));
        checkboxPanel.setBackground(SURFACE_COLOR);
        
        Map<String, JCheckBox> checkboxes = new HashMap<>();
        
        // Sort classes by type and name for better organization
        Map<String, ClassInfo> sortedClasses = new TreeMap<>((a, b) -> {
            ClassInfo classA = codeAnalyzer.getClasses().get(a);
            ClassInfo classB = codeAnalyzer.getClasses().get(b);
            
            // Sort by type first, then by name
            int typeCompare = classA.getType().compareTo(classB.getType());
            return typeCompare != 0 ? typeCompare : a.compareTo(b);
        });
        sortedClasses.putAll(codeAnalyzer.getClasses());
        
        String currentType = "";
        for (Map.Entry<String, ClassInfo> entry : sortedClasses.entrySet()) {
            String className = entry.getKey();
            ClassInfo classInfo = entry.getValue();
            
            // Add type separator
            if (!classInfo.getType().equals(currentType)) {
                currentType = classInfo.getType();
                if (!checkboxes.isEmpty()) {
                    checkboxPanel.add(Box.createVerticalStrut(10));
                }
                
                JLabel typeLabel = new JLabel("📁 " + currentType.toUpperCase() + "S");
                typeLabel.setFont(new Font("Segoe UI", Font.BOLD, 11));
                typeLabel.setForeground(TEXT_SECONDARY);
                typeLabel.setBorder(BorderFactory.createEmptyBorder(5, 5, 5, 0));
                checkboxPanel.add(typeLabel);
            }
            
            // Create styled checkbox
            JCheckBox checkbox = new JCheckBox(className);
            checkbox.setSelected(visibleClasses.containsKey(className));
            checkbox.setFont(new Font("Segoe UI", Font.PLAIN, 12));
            checkbox.setBackground(SURFACE_COLOR);
            checkbox.setForeground(TEXT_PRIMARY);
            checkbox.setBorder(BorderFactory.createEmptyBorder(3, 20, 3, 5));
            
            // Add type icon
            String icon = getClassTypeIcon(classInfo.getType());
            checkbox.setText(icon + " " + className);
            
            checkboxes.put(className, checkbox);
            checkboxPanel.add(checkbox);
        }
        
        JScrollPane scrollPane = new JScrollPane(checkboxPanel);
        scrollPane.setBorder(BorderFactory.createLineBorder(new Color(206, 212, 218), 1));
        scrollPane.getVerticalScrollBar().setUnitIncrement(16);
        mainPanel.add(scrollPane, BorderLayout.CENTER);
        
        dialog.add(mainPanel, BorderLayout.CENTER);
        
        // Button panel with modern styling
        JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT, 10, 15));
        buttonPanel.setBackground(new Color(248, 249, 250));
        buttonPanel.setBorder(BorderFactory.createMatteBorder(1, 0, 0, 0, new Color(206, 212, 218)));
        
        JButton selectAllButton = createModernButton("✓ Select All", ACCENT_COLOR, "Select all classes");
        selectAllButton.setPreferredSize(new Dimension(100, 35));
        
        JButton deselectAllButton = createModernButton("✗ Clear All", new Color(231, 76, 60), "Deselect all classes");
        deselectAllButton.setPreferredSize(new Dimension(100, 35));
        
        JButton okButton = createModernButton("Apply Filter", PRIMARY_COLOR, "Apply selected filter");
        okButton.setPreferredSize(new Dimension(120, 35));
        
        JButton cancelButton = createModernButton("Cancel", TEXT_SECONDARY, "Cancel without changes");
        cancelButton.setPreferredSize(new Dimension(80, 35));
        
        // Button actions
        selectAllButton.addActionListener(e -> {
            for (JCheckBox checkbox : checkboxes.values()) {
                checkbox.setSelected(true);
            }
        });
        
        deselectAllButton.addActionListener(e -> {
            for (JCheckBox checkbox : checkboxes.values()) {
                checkbox.setSelected(false);
            }
        });
        
        okButton.addActionListener(e -> {
            // Update visible classes
            visibleClasses.clear();
            int selectedCount = 0;
            for (Map.Entry<String, JCheckBox> entry : checkboxes.entrySet()) {
                if (entry.getValue().isSelected()) {
                    visibleClasses.put(entry.getKey(), codeAnalyzer.getClasses().get(entry.getKey()));
                    selectedCount++;
                }
            }
            
            // Recalculate layout
            calculateLayout();
            
            // Force scroll pane to recognize new preferred size
            SwingUtilities.invokeLater(() -> {
                diagramPanel.revalidate();
                diagramPanel.repaint();
                
                // Get the scroll pane containing the diagram panel
                JScrollPane diagramScrollPane = (JScrollPane) diagramPanel.getParent().getParent();
                diagramScrollPane.revalidate();
                diagramScrollPane.repaint();
            });
            
            log(String.format("Filter applied: showing %d of %d classes", selectedCount, codeAnalyzer.getClasses().size()));
            dialog.dispose();
        });
        
        cancelButton.addActionListener(e -> dialog.dispose());
        
        buttonPanel.add(selectAllButton);
        buttonPanel.add(deselectAllButton);
        buttonPanel.add(cancelButton);
        buttonPanel.add(okButton);
        
        dialog.add(buttonPanel, BorderLayout.SOUTH);
        
        // Dialog settings
        dialog.setSize(450, 600);
        dialog.setLocationRelativeTo(this);
        dialog.setResizable(true);
        dialog.setVisible(true);
    }
    
    /**
     * Returns appropriate icon for class type
     */
    private String getClassTypeIcon(String type) {
        switch (type.toLowerCase()) {
            case "interface": return "🔗";
            case "enum": return "📋";
            case "class": return "🏗️";
            default: return "📄";
        }
    }
    
    private void saveUMLImage() {
        if (visibleClasses.isEmpty()) {
            JOptionPane.showMessageDialog(this, 
                "No diagram to save. Please generate a UML diagram first.", 
                "Warning", JOptionPane.WARNING_MESSAGE);
            return;
        }
        
        JFileChooser fileChooser = new JFileChooser(currentDirectory);
        fileChooser.setFileFilter(new FileNameExtensionFilter("PNG Images", "png"));
        fileChooser.setSelectedFile(new File("uml_diagram.png"));
        
        int result = fileChooser.showSaveDialog(this);
        if (result == JFileChooser.APPROVE_OPTION) {
            File file = fileChooser.getSelectedFile();
            currentDirectory = fileChooser.getCurrentDirectory();
            
            // Ensure file has .png extension
            if (!file.getName().toLowerCase().endsWith(".png")) {
                file = new File(file.getAbsolutePath() + ".png");
            }
            
            try {
                // Use preferred size for better quality
                Dimension preferredSize = diagramPanel.getPreferredSize();
                int width = Math.max(diagramPanel.getWidth(), preferredSize.width);
                int height = Math.max(diagramPanel.getHeight(), preferredSize.height);
                
                // Create buffered image with higher quality
                BufferedImage image = new BufferedImage(width, height, BufferedImage.TYPE_INT_RGB);
                Graphics2D g = image.createGraphics();
                
                // Enable anti-aliasing for better quality
                g.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                g.setRenderingHint(RenderingHints.KEY_TEXT_ANTIALIASING, RenderingHints.VALUE_TEXT_ANTIALIAS_ON);
                
                g.setColor(Color.WHITE);
                g.fillRect(0, 0, width, height);
                
                // Draw the diagram
                if (!visibleClasses.isEmpty()) {
                    diagramRenderer.drawUML(g, codeAnalyzer.getClasses(), visibleClasses, 
                        codeAnalyzer.getRelationships().stream()
                            .filter(r -> visibleClasses.containsKey(r.getSource()) && 
                                         visibleClasses.containsKey(r.getTarget()))
                            .collect(Collectors.toList()));
                }
                
                g.dispose();
                
                javax.imageio.ImageIO.write(image, "png", file);
                log("UML diagram saved to " + file.getAbsolutePath());
                JOptionPane.showMessageDialog(this, 
                    "UML diagram saved successfully!", 
                    "Success", JOptionPane.INFORMATION_MESSAGE);
                    
            } catch (IOException e) {
                log("Error saving image: " + e.getMessage());
                JOptionPane.showMessageDialog(this, 
                    "Error saving image: " + e.getMessage(), 
                    "Error", JOptionPane.ERROR_MESSAGE);
            }
        }
    }
    
    /**
     * Sets up modern UI defaults for a professional look
     */
    private void setupModernUIDefaults() {
        // Button styling
        UIManager.put("Button.font", new Font("Segoe UI", Font.BOLD, 12));
        UIManager.put("Button.foreground", SURFACE_COLOR);
        UIManager.put("Button.background", PRIMARY_COLOR);
        UIManager.put("Button.border", BorderFactory.createEmptyBorder(10, 20, 10, 20));
        
        // Panel styling
        UIManager.put("Panel.background", SURFACE_COLOR);
        
        // Text area styling
        UIManager.put("TextArea.font", new Font("Consolas", Font.PLAIN, 12));
        UIManager.put("TextArea.background", SURFACE_COLOR);
        UIManager.put("TextArea.foreground", TEXT_PRIMARY);
    }
    
    /**
     * Creates an attractive header panel with modern buttons
     */
    private JPanel createHeaderPanel() {
        JPanel headerPanel = new JPanel();
        headerPanel.setLayout(new BorderLayout());
        headerPanel.setBackground(SECONDARY_COLOR);
        headerPanel.setBorder(BorderFactory.createEmptyBorder(15, 20, 15, 20));
        
        // Title section
        JPanel titlePanel = new JPanel(new FlowLayout(FlowLayout.LEFT));
        titlePanel.setBackground(SECONDARY_COLOR);
        
        JLabel titleLabel = new JLabel("🎨 UML Generator Pro");
        titleLabel.setFont(new Font("Segoe UI", Font.BOLD, 24));
        titleLabel.setForeground(SURFACE_COLOR);
        titlePanel.add(titleLabel);
        
        JLabel subtitleLabel = new JLabel("Professional Java Code Visualization");
        subtitleLabel.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        subtitleLabel.setForeground(new Color(189, 195, 199));
        subtitleLabel.setBorder(BorderFactory.createEmptyBorder(0, 10, 0, 0));
        titlePanel.add(subtitleLabel);
        
        // Button panel
        JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT, 10, 0));
        buttonPanel.setBackground(SECONDARY_COLOR);
        
        // Create modern styled buttons
        loadButton = createModernButton("📁 Load Files", PRIMARY_COLOR, "Load Java files or directories");
        generateButton = createModernButton("🔄 Generate UML", ACCENT_COLOR, "Create UML diagram from loaded files");
        filterButton = createModernButton("🔍 Filter Classes", new Color(155, 89, 182), "Choose which classes to display");
        saveButton = createModernButton("💾 Save Image", new Color(231, 76, 60), "Export diagram as PNG image");
        
        // Initially disable some buttons
        generateButton.setEnabled(false);
        saveButton.setEnabled(false);
        filterButton.setEnabled(false);
        
        buttonPanel.add(loadButton);
        buttonPanel.add(generateButton);
        buttonPanel.add(filterButton);
        buttonPanel.add(saveButton);
        
        headerPanel.add(titlePanel, BorderLayout.WEST);
        headerPanel.add(buttonPanel, BorderLayout.EAST);
        
        return headerPanel;
    }
    
    /**
     * Creates a modern styled button with hover effects
     */
    private JButton createModernButton(String text, Color backgroundColor, String tooltip) {
        JButton button = new JButton(text) {
            @Override
            protected void paintComponent(Graphics g) {
                Graphics2D g2d = (Graphics2D) g.create();
                g2d.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                
                // Button background
                if (getModel().isPressed()) {
                    g2d.setColor(backgroundColor.darker());
                } else if (getModel().isRollover() && isEnabled()) {
                    g2d.setColor(backgroundColor.brighter());
                } else {
                    g2d.setColor(isEnabled() ? backgroundColor : backgroundColor.darker().darker());
                }
                
                g2d.fillRoundRect(0, 0, getWidth(), getHeight(), 12, 12);
                
                // Button text
                g2d.setColor(isEnabled() ? SURFACE_COLOR : Color.GRAY);
                FontMetrics fm = g2d.getFontMetrics();
                int textX = (getWidth() - fm.stringWidth(getText())) / 2;
                int textY = (getHeight() + fm.getAscent()) / 2 - 2;
                g2d.drawString(getText(), textX, textY);
                
                g2d.dispose();
            }
        };
        
        button.setFont(new Font("Segoe UI", Font.BOLD, 12));
        button.setPreferredSize(new Dimension(140, 40));
        button.setFocusPainted(false);
        button.setBorderPainted(false);
        button.setContentAreaFilled(false);
        button.setCursor(new Cursor(Cursor.HAND_CURSOR));
        button.setToolTipText(tooltip);
        
        // Add subtle animation effect
        button.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseEntered(MouseEvent e) {
                button.repaint();
            }
            
            @Override
            public void mouseExited(MouseEvent e) {
                button.repaint();
            }
        });
        
        return button;
    }
    
    /**
     * Applies modern styling to components
     */
    private void styleComponents() {
        // Style log area
        logArea.setFont(new Font("Consolas", Font.PLAIN, 12));
        logArea.setBackground(new Color(248, 249, 250));
        logArea.setForeground(TEXT_PRIMARY);
        logArea.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));
        
        // Style diagram panel
        diagramPanel.setBackground(SURFACE_COLOR);
        diagramPanel.setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20));
        
        // Custom titled borders
        Border logBorder = BorderFactory.createCompoundBorder(
            BorderFactory.createLineBorder(new Color(206, 212, 218), 1),
            BorderFactory.createTitledBorder(
                BorderFactory.createEmptyBorder(5, 10, 5, 10),
                "📋 Analysis Log",
                TitledBorder.LEFT,
                TitledBorder.TOP,
                new Font("Segoe UI", Font.BOLD, 12),
                TEXT_SECONDARY
            )
        );
        
        Border diagramBorder = BorderFactory.createCompoundBorder(
            BorderFactory.createLineBorder(new Color(206, 212, 218), 1),
            BorderFactory.createTitledBorder(
                BorderFactory.createEmptyBorder(5, 10, 5, 10),
                "🎯 UML Diagram Visualization",
                TitledBorder.LEFT,
                TitledBorder.TOP,
                new Font("Segoe UI", Font.BOLD, 12),
                TEXT_SECONDARY
            )
        );
        
        ((JScrollPane)logArea.getParent().getParent()).setBorder(logBorder);
        ((JScrollPane)diagramPanel.getParent().getParent()).setBorder(diagramBorder);
    }
    
    /**
     * Creates an enhanced scroll pane with smooth scrolling and modern styling
     */
    private JScrollPane createEnhancedScrollPane(Component component, String title) {
        JScrollPane scrollPane = new JScrollPane(component) {
            @Override
            protected void paintComponent(Graphics g) {
                super.paintComponent(g);
                // Custom painting if needed
            }
        };
        
        // Enhanced scrolling properties
        scrollPane.setVerticalScrollBarPolicy(JScrollPane.VERTICAL_SCROLLBAR_AS_NEEDED);
        scrollPane.setHorizontalScrollBarPolicy(JScrollPane.HORIZONTAL_SCROLLBAR_AS_NEEDED);
        
        // Smooth scrolling settings
        scrollPane.getVerticalScrollBar().setUnitIncrement(20);
        scrollPane.getVerticalScrollBar().setBlockIncrement(100);
        scrollPane.getHorizontalScrollBar().setUnitIncrement(20);
        scrollPane.getHorizontalScrollBar().setBlockIncrement(100);
        
        // Modern scroll bar styling
        customizeScrollBar(scrollPane.getVerticalScrollBar());
        customizeScrollBar(scrollPane.getHorizontalScrollBar());
        
        // Enable wheel scrolling with better sensitivity
        scrollPane.setWheelScrollingEnabled(true);
        
        // Add mouse wheel listener for enhanced scrolling
        addEnhancedMouseWheelListener(scrollPane);
        
        // Add keyboard navigation enhancement
        addKeyboardScrollingSupport(scrollPane);
        
        // Add scroll position indicator
        addScrollPositionIndicator(scrollPane);
        
        // Add smooth scrolling on component changes
        addComponentScrollSync(scrollPane);
        
        return scrollPane;
    }
    
    /**
     * Customizes scroll bar appearance for modern look
     */
    private void customizeScrollBar(JScrollBar scrollBar) {
        scrollBar.setUI(new ModernScrollBarUI());
        scrollBar.setPreferredSize(new Dimension(12, 12));
        scrollBar.setBackground(BACKGROUND_COLOR);
        scrollBar.setOpaque(true);
    }
    
    /**
     * Adds enhanced mouse wheel scrolling with smooth animation
     */
    private void addEnhancedMouseWheelListener(JScrollPane scrollPane) {
        scrollPane.addMouseWheelListener(new MouseWheelListener() {
            private javax.swing.Timer scrollTimer;
            private int targetValue;
            private int currentValue;
            private final int SCROLL_SPEED = 25; // pixels per scroll notch
            private final int ANIMATION_DELAY = 10; // milliseconds
            
            @Override
            public void mouseWheelMoved(MouseWheelEvent e) {
                if (e.getComponent() instanceof JScrollPane) {
                    JScrollPane sp = (JScrollPane) e.getComponent();
                    JScrollBar scrollBar;
                    
                    // Determine which scroll bar to use based on modifier keys
                    if (e.isShiftDown()) {
                        scrollBar = sp.getHorizontalScrollBar();
                    } else {
                        scrollBar = sp.getVerticalScrollBar();
                    }
                    
                    // Calculate smooth scroll target
                    int scrollAmount = e.getWheelRotation() * SCROLL_SPEED;
                    currentValue = scrollBar.getValue();
                    targetValue = Math.max(scrollBar.getMinimum(), 
                        Math.min(scrollBar.getMaximum() - scrollBar.getVisibleAmount(), 
                        currentValue + scrollAmount));
                    
                    // Stop any existing animation
                    if (scrollTimer != null && scrollTimer.isRunning()) {
                        scrollTimer.stop();
                    }
                    
                    // Start smooth scroll animation
                    scrollTimer = new javax.swing.Timer(ANIMATION_DELAY, new ActionListener() {
                        private int step = 0;
                        private final int totalSteps = 8;
                        private final int stepSize = (targetValue - currentValue) / totalSteps;
                        
                        @Override
                        public void actionPerformed(ActionEvent e) {
                            if (step >= totalSteps) {
                                scrollBar.setValue(targetValue);
                                scrollTimer.stop();
                                return;
                            }
                            
                            // Ease-out animation
                            double progress = (double) step / totalSteps;
                            double easeOut = 1 - Math.pow(1 - progress, 3);
                            int newValue = currentValue + (int) (stepSize * totalSteps * easeOut);
                            
                            scrollBar.setValue(newValue);
                            step++;
                        }
                    });
                    scrollTimer.start();
                    
                    // Consume the event to prevent default scrolling
                    e.consume();
                }
            }
        });
    }
    
    /**
     * Adds enhanced keyboard scrolling support
     */
    private void addKeyboardScrollingSupport(JScrollPane scrollPane) {
        // Add key bindings for smooth keyboard scrolling
        InputMap inputMap = scrollPane.getInputMap(JComponent.WHEN_ANCESTOR_OF_FOCUSED_COMPONENT);
        ActionMap actionMap = scrollPane.getActionMap();
        
        // Page Up/Down with smooth scrolling
        inputMap.put(KeyStroke.getKeyStroke("PAGE_UP"), "smoothPageUp");
        inputMap.put(KeyStroke.getKeyStroke("PAGE_DOWN"), "smoothPageDown");
        inputMap.put(KeyStroke.getKeyStroke("HOME"), "smoothHome");
        inputMap.put(KeyStroke.getKeyStroke("END"), "smoothEnd");
        
        // Arrow keys for fine scrolling
        inputMap.put(KeyStroke.getKeyStroke("UP"), "smoothUp");
        inputMap.put(KeyStroke.getKeyStroke("DOWN"), "smoothDown");
        inputMap.put(KeyStroke.getKeyStroke("LEFT"), "smoothLeft");
        inputMap.put(KeyStroke.getKeyStroke("RIGHT"), "smoothRight");
        
        // Ctrl+Arrow for faster scrolling
        inputMap.put(KeyStroke.getKeyStroke("ctrl UP"), "fastUp");
        inputMap.put(KeyStroke.getKeyStroke("ctrl DOWN"), "fastDown");
        inputMap.put(KeyStroke.getKeyStroke("ctrl LEFT"), "fastLeft");
        inputMap.put(KeyStroke.getKeyStroke("ctrl RIGHT"), "fastRight");
        
        // Create smooth scrolling actions
        actionMap.put("smoothPageUp", createSmoothScrollAction(scrollPane, 0, -scrollPane.getHeight() + 50));
        actionMap.put("smoothPageDown", createSmoothScrollAction(scrollPane, 0, scrollPane.getHeight() - 50));
        actionMap.put("smoothHome", createSmoothScrollAction(scrollPane, 0, Integer.MIN_VALUE));
        actionMap.put("smoothEnd", createSmoothScrollAction(scrollPane, 0, Integer.MAX_VALUE));
        
        actionMap.put("smoothUp", createSmoothScrollAction(scrollPane, 0, -30));
        actionMap.put("smoothDown", createSmoothScrollAction(scrollPane, 0, 30));
        actionMap.put("smoothLeft", createSmoothScrollAction(scrollPane, -30, 0));
        actionMap.put("smoothRight", createSmoothScrollAction(scrollPane, 30, 0));
        
        actionMap.put("fastUp", createSmoothScrollAction(scrollPane, 0, -120));
        actionMap.put("fastDown", createSmoothScrollAction(scrollPane, 0, 120));
        actionMap.put("fastLeft", createSmoothScrollAction(scrollPane, -120, 0));
        actionMap.put("fastRight", createSmoothScrollAction(scrollPane, 120, 0));
    }
    
    /**
     * Creates a smooth scrolling action for keyboard navigation
     */
    private AbstractAction createSmoothScrollAction(JScrollPane scrollPane, int deltaX, int deltaY) {
        return new AbstractAction() {
            @Override
            public void actionPerformed(ActionEvent e) {
                JScrollBar hScrollBar = scrollPane.getHorizontalScrollBar();
                JScrollBar vScrollBar = scrollPane.getVerticalScrollBar();
                
                if (deltaX != 0) {
                    int newValue;
                    if (deltaX == Integer.MIN_VALUE) {
                        newValue = hScrollBar.getMinimum();
                    } else if (deltaX == Integer.MAX_VALUE) {
                        newValue = hScrollBar.getMaximum() - hScrollBar.getVisibleAmount();
                    } else {
                        newValue = Math.max(hScrollBar.getMinimum(),
                            Math.min(hScrollBar.getMaximum() - hScrollBar.getVisibleAmount(),
                            hScrollBar.getValue() + deltaX));
                    }
                    animateScrollTo(hScrollBar, newValue);
                }
                
                if (deltaY != 0) {
                    int newValue;
                    if (deltaY == Integer.MIN_VALUE) {
                        newValue = vScrollBar.getMinimum();
                    } else if (deltaY == Integer.MAX_VALUE) {
                        newValue = vScrollBar.getMaximum() - vScrollBar.getVisibleAmount();
                    } else {
                        newValue = Math.max(vScrollBar.getMinimum(),
                            Math.min(vScrollBar.getMaximum() - vScrollBar.getVisibleAmount(),
                            vScrollBar.getValue() + deltaY));
                    }
                    animateScrollTo(vScrollBar, newValue);
                }
            }
        };
    }
    
    /**
     * Animates scroll bar to target value with smooth transition
     */
    private void animateScrollTo(JScrollBar scrollBar, int targetValue) {
        int currentValue = scrollBar.getValue();
        if (currentValue == targetValue) return;
        
        javax.swing.Timer timer = new javax.swing.Timer(15, null);
        timer.addActionListener(new ActionListener() {
            private int step = 0;
            private final int totalSteps = 10;
            private final int startValue = currentValue;
            
            @Override
            public void actionPerformed(ActionEvent e) {
                if (step >= totalSteps) {
                    scrollBar.setValue(targetValue);
                    timer.stop();
                    return;
                }
                
                // Smooth ease-out animation
                double progress = (double) step / totalSteps;
                double easeOut = 1 - Math.pow(1 - progress, 2);
                int newValue = startValue + (int) ((targetValue - startValue) * easeOut);
                
                scrollBar.setValue(newValue);
                step++;
            }
        });
        timer.start();
    }
    
    /**
     * Adds scroll position indicator for better navigation
     */
    private void addScrollPositionIndicator(JScrollPane scrollPane) {
        JPanel indicatorPanel = new JPanel() {
            @Override
            protected void paintComponent(Graphics g) {
                super.paintComponent(g);
                
                if (!scrollPane.getVerticalScrollBar().isVisible()) return;
                
                Graphics2D g2d = (Graphics2D) g.create();
                g2d.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                
                JScrollBar vScrollBar = scrollPane.getVerticalScrollBar();
                int total = vScrollBar.getMaximum() - vScrollBar.getMinimum();
                int visible = vScrollBar.getVisibleAmount();
                int current = vScrollBar.getValue();
                
                if (total > visible) {
                    // Draw position indicator
                    g2d.setColor(new Color(0, 0, 0, 60));
                    g2d.fillRoundRect(5, 5, 100, 20, 10, 10);
                    
                    g2d.setColor(Color.WHITE);
                    g2d.setFont(new Font("Segoe UI", Font.PLAIN, 10));
                    
                    int percentage = (int) (((double) current / (total - visible)) * 100);
                    String text = percentage + "% (" + (current / 20 + 1) + "/" + ((total - visible) / 20 + 1) + ")";
                    
                    FontMetrics fm = g2d.getFontMetrics();
                    int textX = (110 - fm.stringWidth(text)) / 2;
                    g2d.drawString(text, textX, 18);
                }
                
                g2d.dispose();
            }
        };
        
        indicatorPanel.setOpaque(false);
        indicatorPanel.setPreferredSize(new Dimension(110, 30));
        
        // Add to scroll pane corner
        scrollPane.setCorner(JScrollPane.UPPER_RIGHT_CORNER, indicatorPanel);
        
        // Update indicator on scroll
        scrollPane.getVerticalScrollBar().addAdjustmentListener(e -> {
            if (!e.getValueIsAdjusting()) {
                SwingUtilities.invokeLater(indicatorPanel::repaint);
            }
        });
        
        // Auto-hide indicator after inactivity
        javax.swing.Timer hideTimer = new javax.swing.Timer(2000, e -> {
            indicatorPanel.setVisible(false);
            scrollPane.revalidate();
        });
        hideTimer.setRepeats(false);
        
        scrollPane.getVerticalScrollBar().addAdjustmentListener(e -> {
            indicatorPanel.setVisible(true);
            hideTimer.restart();
        });
    }
    
    /**
     * Adds smooth scrolling synchronization when component content changes
     */
    private void addComponentScrollSync(JScrollPane scrollPane) {
        Component view = scrollPane.getViewport().getView();
        
        if (view instanceof JTextArea) {
            JTextArea textArea = (JTextArea) view;
            textArea.getDocument().addDocumentListener(new javax.swing.event.DocumentListener() {
                @Override
                public void insertUpdate(javax.swing.event.DocumentEvent e) {
                    SwingUtilities.invokeLater(() -> {
                        // Auto-scroll to bottom when new content is added
                        JScrollBar vScrollBar = scrollPane.getVerticalScrollBar();
                        animateScrollTo(vScrollBar, vScrollBar.getMaximum());
                    });
                }
                
                @Override
                public void removeUpdate(javax.swing.event.DocumentEvent e) {
                    // No action needed
                }
                
                @Override
                public void changedUpdate(javax.swing.event.DocumentEvent e) {
                    // No action needed
                }
            });
        }
        
        // Add component resize listener for diagram panel
        if (view instanceof JPanel) {
            view.addComponentListener(new ComponentAdapter() {
                @Override
                public void componentResized(ComponentEvent e) {
                    SwingUtilities.invokeLater(() -> {
                        scrollPane.getViewport().revalidate();
                        
                        // Smooth scroll to maintain relative position
                        JScrollBar vScrollBar = scrollPane.getVerticalScrollBar();
                        JScrollBar hScrollBar = scrollPane.getHorizontalScrollBar();
                        
                        // Maintain center position if possible
                        if (vScrollBar.isVisible()) {
                            int centerY = (vScrollBar.getMaximum() - vScrollBar.getVisibleAmount()) / 2;
                            animateScrollTo(vScrollBar, centerY);
                        }
                        
                        if (hScrollBar.isVisible()) {
                            int centerX = (hScrollBar.getMaximum() - hScrollBar.getVisibleAmount()) / 2;
                            animateScrollTo(hScrollBar, centerX);
                        }
                    });
                }
            });
        }
    }
    
    /**
     * Creates a custom application icon
     */
    private Image createAppIcon() {
        BufferedImage icon = new BufferedImage(64, 64, BufferedImage.TYPE_INT_ARGB);
        Graphics2D g2d = icon.createGraphics();
        g2d.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
        
        // Background circle
        g2d.setColor(PRIMARY_COLOR);
        g2d.fillOval(4, 4, 56, 56);
        
        // Inner design - UML-like boxes
        g2d.setColor(SURFACE_COLOR);
        g2d.fillRect(16, 16, 12, 8);
        g2d.fillRect(36, 16, 12, 8);
        g2d.fillRect(16, 36, 12, 8);
        g2d.fillRect(36, 36, 12, 8);
        
        // Connection lines
        g2d.setStroke(new BasicStroke(2));
        g2d.drawLine(28, 20, 36, 20);
        g2d.drawLine(22, 24, 22, 36);
        g2d.drawLine(42, 24, 42, 36);
        
        g2d.dispose();
        return icon;
    }
    
    private void log(String message) {
        SwingUtilities.invokeLater(() -> {
            String timestamp = java.time.LocalTime.now().toString().substring(0, 8);
            logArea.append("[" + timestamp + "] " + message + "\n");
            logArea.setCaretPosition(logArea.getDocument().getLength());
        });
    }
    
    public static void main(String[] args) {
        // Set system look and feel for better native appearance
        try {
            UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
        } catch (Exception e) {
            e.printStackTrace();
        }
        
        // Show splash screen first
        SwingUtilities.invokeLater(() -> {
            SplashScreen splash = new SplashScreen();
            splash.showSplash();
            
            // Create main application after splash delay
            javax.swing.Timer appTimer = new javax.swing.Timer(3500, e -> {
                new UMLGenerator();
                ((javax.swing.Timer)e.getSource()).stop();
            });
            appTimer.setRepeats(false);
            appTimer.start();
        });
    }
}