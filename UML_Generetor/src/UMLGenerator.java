import javax.swing.*;
import javax.swing.filechooser.FileNameExtensionFilter;
import java.awt.*;
import java.awt.event.*;
import java.awt.image.BufferedImage;
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
    
    public UMLGenerator() {
        super("UML Diagram Generator");
        
        // Initialize components
        codeAnalyzer = new CodeAnalyzer();
        diagramRenderer = new DiagramRenderer();
        visibleClasses = new HashMap<>();
        currentDirectory = new File(System.getProperty("user.dir"));
        
        setupUI();
        setupListeners();
        
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setSize(1000, 700);
        setLocationRelativeTo(null);
        setVisible(true);
    }
    
    private void setupUI() {
        // Main layout
        setLayout(new BorderLayout());
        
        // Control panel (top)
        JPanel controlPanel = new JPanel(new FlowLayout(FlowLayout.LEFT));
        loadButton = new JButton("Load Java Files");
        generateButton = new JButton("Generate UML");
        saveButton = new JButton("Save UML as Image");
        filterButton = new JButton("Filter Classes");
        saveButton.setEnabled(false);
        filterButton.setEnabled(false);
        
        controlPanel.add(loadButton);
        controlPanel.add(generateButton);
        controlPanel.add(filterButton);
        controlPanel.add(saveButton);
        add(controlPanel, BorderLayout.NORTH);
        
        // Split pane for log and diagram
        JSplitPane splitPane = new JSplitPane(JSplitPane.VERTICAL_SPLIT);
        splitPane.setResizeWeight(0.3);
        
        // Log area (top part of split)
        logArea = new JTextArea();
        logArea.setEditable(false);
        JScrollPane logScrollPane = new JScrollPane(logArea);
        logScrollPane.setBorder(BorderFactory.createTitledBorder("Log"));
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
        JScrollPane diagramScrollPane = new JScrollPane(diagramPanel);
        diagramScrollPane.setBorder(BorderFactory.createTitledBorder("UML Diagram"));
        splitPane.setBottomComponent(diagramScrollPane);
        
        add(splitPane, BorderLayout.CENTER);
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
        
        int result = fileChooser.showOpenDialog(this);
        if (result == JFileChooser.APPROVE_OPTION) {
            File[] files = fileChooser.getSelectedFiles();
            currentDirectory = fileChooser.getCurrentDirectory();
            
            // Clear previous data
            codeAnalyzer.clear();
            visibleClasses.clear();
            logArea.setText("");
            
            // Analyze each file
            for (File file : files) {
                log("Analyzing: " + file.getName());
                codeAnalyzer.analyzeJavaFile(file);
            }
            
            log("Analysis complete. Found " + codeAnalyzer.getClasses().size() + " classes and " + 
                codeAnalyzer.getRelationships().size() + " relationships.");
            
            // Enable buttons
            generateButton.setEnabled(true);
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
        
        // Repaint and enable buttons
        diagramPanel.repaint();
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
        
        // Create a class selection dialog
        JDialog dialog = new JDialog(this, "Select Classes to Display", true);
        dialog.setLayout(new BorderLayout());
        
        // Create checkboxes for each class
        JPanel checkboxPanel = new JPanel();
        checkboxPanel.setLayout(new BoxLayout(checkboxPanel, BoxLayout.Y_AXIS));
        
        Map<String, JCheckBox> checkboxes = new HashMap<>();
        for (String className : codeAnalyzer.getClasses().keySet()) {
            JCheckBox checkbox = new JCheckBox(className);
            checkbox.setSelected(visibleClasses.containsKey(className));
            checkboxes.put(className, checkbox);
            checkboxPanel.add(checkbox);
        }
        
        JScrollPane scrollPane = new JScrollPane(checkboxPanel);
        dialog.add(scrollPane, BorderLayout.CENTER);
        
        // Add buttons
        JPanel buttonPanel = new JPanel();
        JButton selectAllButton = new JButton("Select All");
        JButton deselectAllButton = new JButton("Deselect All");
        JButton okButton = new JButton("OK");
        
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
            for (Map.Entry<String, JCheckBox> entry : checkboxes.entrySet()) {
                if (entry.getValue().isSelected()) {
                    visibleClasses.put(entry.getKey(), codeAnalyzer.getClasses().get(entry.getKey()));
                }
            }
            
            // Recalculate layout and repaint
            calculateLayout();
            diagramPanel.repaint();
            dialog.dispose();
        });
        
        buttonPanel.add(selectAllButton);
        buttonPanel.add(deselectAllButton);
        buttonPanel.add(okButton);
        dialog.add(buttonPanel, BorderLayout.SOUTH);
        
        // Show dialog
        dialog.setSize(300, 400);
        dialog.setLocationRelativeTo(this);
        dialog.setVisible(true);
    }
    
    private void saveUMLImage() {
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
            
            // Create buffered image
            BufferedImage image = new BufferedImage(
                diagramPanel.getWidth(), diagramPanel.getHeight(), BufferedImage.TYPE_INT_RGB);
            Graphics2D g = image.createGraphics();
            g.setColor(Color.WHITE);
            g.fillRect(0, 0, image.getWidth(), image.getHeight());
            diagramPanel.paint(g);
            g.dispose();
            
            try {
                javax.imageio.ImageIO.write(image, "png", file);
                log("UML diagram saved to " + file.getAbsolutePath());
            } catch (IOException e) {
                log("Error saving image: " + e.getMessage());
                JOptionPane.showMessageDialog(this, 
                    "Error saving image: " + e.getMessage(), 
                    "Error", JOptionPane.ERROR_MESSAGE);
            }
        }
    }
    
    private void log(String message) {
        logArea.append(message + "\n");
        logArea.setCaretPosition(logArea.getDocument().getLength());
    }
    
    public static void main(String[] args) {
        System.out.println("Starting UML Generator...");
        try {
            UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
            System.out.println("Set Look and Feel successfully");
        } catch (Exception e) {
            System.out.println("Error setting Look and Feel: " + e.getMessage());
            e.printStackTrace();
        }
        
        System.out.println("Launching application window...");
        SwingUtilities.invokeLater(() -> {
            System.out.println("Creating UML Generator instance");
            new UMLGenerator();
            System.out.println("UML Generator window should now be visible");
        });
    }
}