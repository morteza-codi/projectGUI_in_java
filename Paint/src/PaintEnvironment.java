import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.GridLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.swing.BorderFactory;
import javax.swing.JButton;
import javax.swing.JCheckBoxMenuItem;
import javax.swing.JColorChooser;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JMenu;
import javax.swing.JMenuBar;
import javax.swing.JMenuItem;
import javax.swing.JPanel;
import javax.swing.JSlider;
import javax.swing.UIManager;
import javax.swing.UnsupportedLookAndFeelException;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;

public class PaintEnvironment {

    // Control dimensions for PaintEnvironment
    private static final Dimension FRAME_DIMENSION = new Dimension(900, 700);
    private static final Dimension BUTTON_DIMENSION = new Dimension(25, 25);
    private static final Dimension DRAWING_DIMENSION = new Dimension(700, 600);
    private static final Dimension TOOL_PANEL_DIMENSION = new Dimension(170, 250);
    private static final Dimension PREVIEW_BUTTON_DIMENSION
            = new Dimension(35, 35);
    private static final Dimension PAINT_PANEL_DIMENSION = new Dimension(170, 70);
    private static final Dimension PREVIEW_PANEL_DIMENSION
            = new Dimension(170, 200);
    private static final Dimension SLIDER_DIMENSION = new Dimension(170, 40);
    private static final Dimension TOOL_BUTTON_DIMENSION = new Dimension(80, 30);
    
    // Border thickness for color preview buttons (color 1, 2)
    private static final int BORDER_THICKNESS = 3;
    private static final Color BORDER_COLOR = ModernLookAndFeel.PRIMARY_COLOR;
    
    // Color for unselected tools
    private static final Color UNSELECTED_COLOR = new Color(150, 150, 150);
    
    // Thickness range for lines and shapes
    private static final int THICKNESS_MIN = 1;
    private static final int THICKNESS_MAX = 20;
    
    // Default colors
    private Color primaryColor = ModernLookAndFeel.PRIMARY_COLOR;
    private Color secondaryColor = ModernLookAndFeel.SECONDARY_COLOR;
    private JLabel primLabel = new JLabel("Color 1: ");
    private JLabel secondLabel = new JLabel("Color 2: ");
    private JLabel thicknessLabel
            = new JLabel("Thickness: " + THICKNESS_MIN);
    
    // The frame
    private JFrame frame = new JFrame("Modern Paint");
    // The painting component
    private PaintingComponent pc = new PaintingComponent();
    // The menu bar items
    private JMenuBar menuBar = ModernUIComponents.createModernMenuBar();
    private JMenu fileMenu = ModernUIComponents.createModernMenu("File");
    private JMenuItem newMenuItem = ModernUIComponents.createModernMenuItem("New");
    private JMenuItem openMenuItem = ModernUIComponents.createModernMenuItem("Open");
    private JMenuItem saveMenuItem = ModernUIComponents.createModernMenuItem("Save");
    private JMenu optionsMenu = ModernUIComponents.createModernMenu("Options");
    private JCheckBoxMenuItem antialiasMenuItem
            = new JCheckBoxMenuItem("Anti-Aliasing", true);
    private JCheckBoxMenuItem fillMenuItem
            = new JCheckBoxMenuItem("Fill", true);
    private boolean primary = true;   //Is the primary color selected?
    private boolean fill = true;      //Should we fill in shapes with 2nd color?
    private boolean antialias = true; //True if antialiasing is turned on


    private JPanel sidePanel = new ModernUIComponents.RoundedPanel(ModernLookAndFeel.CORNER_RADIUS, ModernLookAndFeel.PANEL_COLOR);
    private JPanel drawPanel = ModernLookAndFeel.createGradientPanel();
    private JPanel colorPanel = new ModernUIComponents.RoundedPanel(ModernLookAndFeel.CORNER_RADIUS, ModernLookAndFeel.PANEL_COLOR);
    private JPanel toolPanel = new ModernUIComponents.RoundedPanel(ModernLookAndFeel.CORNER_RADIUS, ModernLookAndFeel.PANEL_COLOR);
    private JPanel previewPanel = new ModernUIComponents.RoundedPanel(ModernLookAndFeel.CORNER_RADIUS, ModernLookAndFeel.PANEL_COLOR);
    // Control and preview buttons
    private JButton chooseColorButton = ModernUIComponents.createToolButton("Choose Color");
    private JButton clearButton = ModernUIComponents.createToolButton("Clear");
    private JButton primaryPreviewButton = ModernUIComponents.createColorPreviewButton(primaryColor);
    private JButton secondaryPreviewButton = ModernUIComponents.createColorPreviewButton(secondaryColor);
    private JColorChooser chooser = new JColorChooser();
    private JSlider thicknessSlider = ModernUIComponents.createModernSlider(THICKNESS_MIN, THICKNESS_MAX, THICKNESS_MIN);
    // Tool buttons with icons
    private JButton eraserButton;
    private JButton pencilButton;
    private JButton lineButton;
    private JButton boxButton;
    private JButton ellipseButton;
    private JButton iTriangleButton;
    private JButton rTriangleButton;
    private JButton diamondButton;
    private JButton pentagonButton;
    private ArrayList<JButton> colorButtons = new ArrayList<JButton>();
    private ArrayList<JButton> toolButtons = new ArrayList<JButton>();

    /**
     * Constructor
     */
    public PaintEnvironment() {
        try {
            UIManager.setLookAndFeel(new ModernLookAndFeel());
        } catch (UnsupportedLookAndFeelException e) {
            e.printStackTrace();
        }
        
        initColorButtons();
        initControlButtons();
        initTools();
        initMenuBar();
        initPanelsAndLayout();
        initFrame();
    }


     // Initialize each color in the color panel

    private void initColorButtons() {
        // Set each button's background color
        JButton whiteButton = ModernUIComponents.createColorButton(Color.WHITE);
        JButton blackButton = ModernUIComponents.createColorButton(Color.BLACK);
        JButton grayButton = ModernUIComponents.createColorButton(Color.GRAY);
        JButton blueButton = ModernUIComponents.createColorButton(Color.BLUE);
        JButton redButton = ModernUIComponents.createColorButton(Color.RED);
        JButton pinkButton = ModernUIComponents.createColorButton(Color.PINK);
        JButton yellowButton = ModernUIComponents.createColorButton(Color.YELLOW);
        JButton cyanButton = ModernUIComponents.createColorButton(Color.CYAN);
        JButton magentaButton = ModernUIComponents.createColorButton(Color.MAGENTA);
        JButton orangeButton = ModernUIComponents.createColorButton(Color.ORANGE);
        JButton greenButton = ModernUIComponents.createColorButton(Color.GREEN);
        JButton lightGrayButton = ModernUIComponents.createColorButton(Color.LIGHT_GRAY);
        
        colorButtons.addAll(new ArrayList<JButton>(Arrays.asList(blackButton,
                grayButton, lightGrayButton, blueButton, cyanButton,
                greenButton, whiteButton, yellowButton, orangeButton,
                pinkButton, magentaButton, redButton)));


        // Set button size and actions
        for (int i = 0; i < colorButtons.size(); i++) {
            final int buttonIndex = i;
            colorButtons.get(i).setPreferredSize(BUTTON_DIMENSION);
            colorButtons.get(i).addActionListener(new ActionListener() {
                @Override
                public void actionPerformed(ActionEvent e) {
                    Color tmp = colorButtons.get(buttonIndex).getBackground();
                    toggleColorButtons(colorButtons,
                            colorButtons.get(buttonIndex));
                    if (primary) {
                        pc.setPrimaryColor(tmp);
                        primaryPreviewButton.setBackground(tmp);
                    } else {
                        pc.setSecondaryColor(tmp);
                        secondaryPreviewButton.setBackground(tmp);
                    }
                }
            });

            colorPanel.add(colorButtons.get(i));
        }
    }

    // Configure thickness slider and control buttons
    public final void initControlButtons() {
        thicknessSlider.setPreferredSize(SLIDER_DIMENSION);
        primaryPreviewButton.setPreferredSize(PREVIEW_BUTTON_DIMENSION);
        secondaryPreviewButton.setPreferredSize(PREVIEW_BUTTON_DIMENSION);
        
        // Style the labels
        primLabel.setForeground(ModernLookAndFeel.TEXT_COLOR);
        secondLabel.setForeground(ModernLookAndFeel.TEXT_COLOR);
        thicknessLabel.setForeground(ModernLookAndFeel.TEXT_COLOR);

        //ضخامت را تغییر میدهد
        thicknessSlider.addChangeListener(new ChangeListener() {
            @Override
            public void stateChanged(ChangeEvent e) {
                pc.setLineThickness((float) thicknessSlider.getValue());
                thicknessLabel.setText("Thickness: "
                        + thicknessSlider.getValue());
            }
        });

        // Focus on primary color
        primaryPreviewButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                primaryPreviewButton.setBorder(
                        ModernLookAndFeel.createRoundedBorder(BORDER_COLOR, BORDER_THICKNESS, 8));
                primary = true;
                secondaryPreviewButton.setBorder(
                        ModernLookAndFeel.createRoundedBorder(BORDER_COLOR, 1, 8));
                System.out.println("Color 1 selected.");
            }
        });

        // Focus on secondary color
        secondaryPreviewButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {

                secondaryPreviewButton.setBorder(
                        ModernLookAndFeel.createRoundedBorder(BORDER_COLOR, BORDER_THICKNESS, 8));
                primary = false;
                primaryPreviewButton.setBorder(
                        ModernLookAndFeel.createRoundedBorder(BORDER_COLOR, 1, 8));
                System.out.println("Color 2 selected.");
            }
        });

        //مولفه نقاشی را پاک می کند
        clearButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                pc.clear();
            }
        });

        //انتخاب رنگ
        chooseColorButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                if (primary) {
                    primaryColor = JColorChooser.showDialog(chooser,
                            "Choose Primary Color", primaryColor);
                    primaryPreviewButton.setBackground(primaryColor);
                    pc.setPrimaryColor(primaryColor);
                } else {
                    secondaryColor = JColorChooser.showDialog(chooser,
                            "Choose Secondary Color", secondaryColor);
                    secondaryPreviewButton.setBackground(secondaryColor);
                    pc.setSecondaryColor(secondaryColor);
                }
            }
        });
    }

    /**
     * تنظیم کردن منو
     */
    private void initMenuBar() {
        //Toggles fill - whether or not shapes are filled in with 2nd color
        fillMenuItem.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                fill = !fill;
                pc.setFill(fill);
            }
        });

        //Toggles antialiasing
        antialiasMenuItem.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                antialias = !antialias;
                pc.setAntiAliasing(antialias);
            }
        });

        //زخیره تصویر
        saveMenuItem.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                try {
                    pc.save();
                } catch (IOException ex) {
                    Logger.getLogger(PaintEnvironment.class.getName())
                            .log(Level.SEVERE, null, ex);
                }
            }
        });

        //باز کردن تصویر
        openMenuItem.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                try {
                    pc.load();
                } catch (IOException ex) {
                    Logger.getLogger(PaintEnvironment.class.getName())
                            .log(Level.SEVERE, null, ex);
                }
            }
        });

        //پاک کردن تصویر
        newMenuItem.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                pc.clear();
            }
        });

        fileMenu.add(newMenuItem);
        fileMenu.add(openMenuItem);
        fileMenu.add(saveMenuItem);
        optionsMenu.add(fillMenuItem);
        //optionsMenu.add(antialiasMenuItem);
        menuBar.add(fileMenu);
        menuBar.add(optionsMenu);
    }

    /**
     * مقدار دهی اولیه ابزار ها
     */
    public final void initTools() {
        // Create modern tool buttons with icons
        pencilButton = createToolButtonWithIcon("Pencil", ToolIcon.PENCIL);
        eraserButton = createToolButtonWithIcon("Eraser", ToolIcon.ERASER);
        lineButton = createToolButtonWithIcon("Line", ToolIcon.LINE);
        boxButton = createToolButtonWithIcon("Box", ToolIcon.BOX);
        ellipseButton = createToolButtonWithIcon("Ellipse", ToolIcon.ELLIPSE);
        iTriangleButton = createToolButtonWithIcon("Triangle", ToolIcon.TRIANGLE);
        rTriangleButton = createToolButtonWithIcon("Right Triangle", ToolIcon.RIGHT_TRIANGLE);
        diamondButton = createToolButtonWithIcon("Diamond", ToolIcon.DIAMOND);
        pentagonButton = createToolButtonWithIcon("Pentagon", ToolIcon.PENTAGON);
        
        toolButtons.addAll(new ArrayList<JButton>(Arrays.asList(eraserButton,
                pencilButton, lineButton, boxButton, ellipseButton,
                iTriangleButton, rTriangleButton, diamondButton, pentagonButton)));
        

        for (int i = 0; i < toolButtons.size(); i++) {
            toolButtons.get(i).setPreferredSize(TOOL_BUTTON_DIMENSION);
            final int toolIndex = i;
            toolButtons.get(i).setForeground(UNSELECTED_COLOR);
            toolButtons.get(i).addActionListener(new ActionListener() {
                @Override
                public void actionPerformed(ActionEvent e) {
                    pc.setDrawMode((byte) toolIndex);
                    toggleTools(toolButtons, toolButtons.get(toolIndex));
                }
            });
            pencilButton.setForeground(Color.BLACK);

            toolPanel.add(toolButtons.get(i));
        }
    }

    private void initPanelsAndLayout() {

        //ColorPanel
        GridLayout colorButtonLayout = new GridLayout(2, 6);
        colorButtonLayout.setHgap(4);
        colorButtonLayout.setVgap(4);
        colorPanel.setLayout(colorButtonLayout);
        colorPanel.setBorder(BorderFactory.createEmptyBorder(8, 8, 8, 8));
        colorPanel.setPreferredSize(PAINT_PANEL_DIMENSION);

        //Preview Panel
        previewPanel.setLayout(new FlowLayout(FlowLayout.CENTER));
        previewPanel.setBorder(BorderFactory.createEmptyBorder(8, 8, 8, 8));
        previewPanel.setPreferredSize(PREVIEW_PANEL_DIMENSION);

        //Tool Panel
        toolPanel.setLayout(new GridLayout(toolButtons.size(), 1, 0, 8));
        toolPanel.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));
        toolPanel.setPreferredSize(TOOL_PANEL_DIMENSION);

        //Side Panel
        GridBagLayout sideLayout = new GridBagLayout();
        GridBagConstraints c;
        sidePanel.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));
        sidePanel.setLayout(sideLayout);

        //Draw Panel
        drawPanel.setLayout(new BorderLayout());


        //Preview panel
        previewPanel.add(chooseColorButton);
        previewPanel.add(primLabel);
        previewPanel.add(primaryPreviewButton);
        previewPanel.add(secondLabel);
        previewPanel.add(secondaryPreviewButton);
        previewPanel.add(clearButton);
        previewPanel.add(thicknessLabel);
        previewPanel.add(thicknessSlider);
        previewPanel.setPreferredSize(PREVIEW_PANEL_DIMENSION);
        previewPanel.setSize(PREVIEW_PANEL_DIMENSION);

        //Draw Panel
        drawPanel.add(pc);

        //Side Panel
        c = new GridBagConstraints();
        c.fill = GridBagConstraints.HORIZONTAL;
        c.anchor = GridBagConstraints.NORTH;
        c.weightx = 1.0;
        c.weighty = 0.0;
        c.gridx = 0;
        c.gridy = 0;
        sidePanel.add(colorPanel, c);

        c.gridx = 0;
        c.gridy = 1;
        c.weightx = 0.0;
        c.weighty = 1.0;
        sidePanel.add(previewPanel, c);

        c.fill = GridBagConstraints.NORTH;
        c.gridx = 0;
        c.gridy = 2;
        c.weightx = 0.0;
        c.weighty = 1.0;
        sidePanel.add(toolPanel, c);

        c.fill = GridBagConstraints.REMAINDER;
        c.gridx = 0;
        c.gridy = 0;
        c.weightx = 1.0;
        c.weighty = 1.0;
        sidePanel.add(new JPanel());
    }

    /**
     * Changes the border style of each button. The selected button has an inset
     * and the others don't.
     */
    public void toggleColorButtons(ArrayList<JButton> buttons, JButton selected) {
        for (int i = 0; i < buttons.size(); i++) {
            if (buttons.get(i) == selected) {
                buttons.get(i).setBorder(
                        ModernLookAndFeel.createRoundedBorder(ModernLookAndFeel.PRIMARY_COLOR, 2, 5));
            } else {
                buttons.get(i).setBorder(
                        ModernLookAndFeel.createRoundedBorder(Color.GRAY, 1, 5));
            }
        }
    }

    /**
     * Highlights the selected tool
     */
    public void toggleTools(ArrayList<JButton> b, JButton selected) {
        for (int i = 0; i < b.size(); i++) {
            if (b.get(i) == selected) {
                b.get(i).setForeground(ModernLookAndFeel.PRIMARY_COLOR);
                b.get(i).setBorder(ModernLookAndFeel.createRoundedPaddedBorder(
                        ModernLookAndFeel.PRIMARY_COLOR, 2, ModernLookAndFeel.CORNER_RADIUS, 5));
                
                // Update icon to selected state
                if (b.get(i).getClientProperty("toolIcon") instanceof ToolIcon) {
                    ((ToolIcon) b.get(i).getClientProperty("toolIcon")).setSelected(true);
                }
            } else {
                b.get(i).setForeground(UNSELECTED_COLOR);
                b.get(i).setBorder(ModernLookAndFeel.createRoundedPaddedBorder(
                        ModernLookAndFeel.PRIMARY_COLOR, 1, ModernLookAndFeel.CORNER_RADIUS, 5));
                
                // Update icon to unselected state
                if (b.get(i).getClientProperty("toolIcon") instanceof ToolIcon) {
                    ((ToolIcon) b.get(i).getClientProperty("toolIcon")).setSelected(false);
                }
            }
        }
    }

    /**
     * Initializes the frame
     */
    private void initFrame() {
        pc.setPreferredSize(DRAWING_DIMENSION);
        pc.setBackground(Color.WHITE);
        
        // Create a rounded panel for the drawing area
        JPanel drawingWrapper = new ModernUIComponents.RoundedPanel(ModernLookAndFeel.CORNER_RADIUS);
        drawingWrapper.setBackground(Color.WHITE);
        drawingWrapper.setLayout(new BorderLayout());
        drawingWrapper.add(pc, BorderLayout.CENTER);
        drawPanel.add(drawingWrapper, BorderLayout.CENTER);
        
        frame.add(sidePanel, BorderLayout.WEST);
        frame.add(drawPanel, BorderLayout.CENTER);

        // Frame settings
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.setEnabled(true);
        frame.setResizable(true);
        frame.setJMenuBar(menuBar);
        frame.setSize(FRAME_DIMENSION);
        frame.setLocationRelativeTo(null); // Center on screen
        frame.getContentPane().setBackground(ModernLookAndFeel.BACKGROUND_COLOR);
        frame.setVisible(true);
    }
    
    /**
     * Creates a tool button with an icon
     * 
     * @param text The text to display on the button
     * @param iconType The type of icon to display
     * @return A JButton with the specified text and icon
     */
    private JButton createToolButtonWithIcon(String text, int iconType) {
        JButton button = ModernUIComponents.createToolButton(text);
        ToolIcon icon = new ToolIcon(iconType);
        button.setLayout(new BorderLayout());
        button.add(icon, BorderLayout.WEST);
        button.putClientProperty("toolIcon", icon);
        return button;
    }
}