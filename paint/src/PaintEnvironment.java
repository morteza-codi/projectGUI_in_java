
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
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;

public class PaintEnvironment {

    //ابعاد کنترل‌های PaintEnvironment
    private static final Dimension FRAME_DIMENSION = new Dimension(800, 600);
    private static final Dimension BUTTON_DIMENSION = new Dimension(22, 22);
    private static final Dimension DRAWING_DIMENSION = new Dimension(622, 545);
    private static final Dimension TOOL_PANEL_DIMENSION = new Dimension(144, 200);
    private static final Dimension PREVIEW_BUTTON_DIMENSION
            = new Dimension(33, 33);
    private static final Dimension PAINT_PANEL_DIMENSION = new Dimension(148, 50);
    private static final Dimension PREVIEW_PANEL_DIMENSION
            = new Dimension(150, 200);
    private static final Dimension SLIDER_DIMENSION = new Dimension(140, 20);
    private static final Dimension TOOL_BUTTON_DIMENSION = new Dimension(70, 25);
    
    //ضخامت حاشیه اطراف دکمه های پیش نمایش رنگی (رنگ 1، 2)
    private static final int BORDER_THICKNESS = 3;
    private static final Color BORDER_COLOR = Color.BLACK;
    
    //رنگ خاکستری روشن ابزارهای انتخاب نشده
    private static final Color UNSELECTED_COLOR = new Color(150, 150, 150);
    
    //محدوده ضخامت برای خطوط و اشکال ترسیم شده
    private static final int THICKNESS_MIN = 1;
    private static final int THICKNESS_MAX = 20;
    
    //رنگ های پیش فرض
    private Color primaryColor = Color.BLACK;
    private Color secondaryColor = Color.YELLOW;
    private JLabel primLabel = new JLabel("    Color 1: ");
    private JLabel secondLabel = new JLabel("    Color 2: ");
    private JLabel thicknessLabel
            = new JLabel("Thickness: " + THICKNESS_MIN);
    
    //The frame
    private JFrame frame = new JFrame("Paint");
    //The painting component
    private PaintingComponent pc = new PaintingComponent();
    //The menu bar items
    private JMenuBar menuBar = new JMenuBar();
    private JMenu fileMenu = new JMenu("File");
    private JMenuItem newMenuItem = new JMenuItem("New");
    private JMenuItem openMenuItem = new JMenuItem("Open");
    private JMenuItem saveMenuItem = new JMenuItem("Save");
    private JMenu optionsMenu = new JMenu("Options");
    private JCheckBoxMenuItem antialiasMenuItem
            = new JCheckBoxMenuItem("Anti-Aliasing", true);
    private JCheckBoxMenuItem fillMenuItem
            = new JCheckBoxMenuItem("Fill", true);
    private boolean primary = true;   //Is the primary color selected?
    private boolean fill = true;      //Should we fill in shapes with 2nd color?
    private boolean antialias = true; //True if antialiasing is turned on


    private JPanel sidePanel = new JPanel();
    private JPanel drawPanel = new JPanel();
    private JPanel colorPanel = new JPanel();
    private JPanel toolPanel = new JPanel();
    private JPanel previewPanel = new JPanel();
    //دکمه های کنترل و پیش نمایش
    private JButton chooseColorButton = new JButton("Choose Color");
    private JButton clearButton = new JButton("Clear");
    private JButton primaryPreviewButton = new JButton();
    private JButton secondaryPreviewButton = new JButton();
    private JColorChooser chooser = new JColorChooser();
    private JSlider thicknessSlider = new JSlider();
    //دکمه های رنگ
    private JButton whiteButton = new JButton();
    private JButton blackButton = new JButton();
    private JButton grayButton = new JButton();
    private JButton blueButton = new JButton();
    private JButton redButton = new JButton();
    private JButton pinkButton = new JButton();
    private JButton yellowButton = new JButton();
    private JButton greenButton = new JButton();
    private JButton lightGrayButton = new JButton();
    private JButton cyanButton = new JButton();
    private JButton magentaButton = new JButton();
    private JButton orangeButton = new JButton();
    //دکمه های ابزار
    private JButton eraserButton = new JButton("Eraser");
    private JButton pencilButton = new JButton("Pencil");
    private JButton lineButton = new JButton("Line");
    private JButton boxButton = new JButton("Box");
    private JButton ellipseButton = new JButton("Ellipse");
    private JButton iTriangleButton = new JButton("Isosceles Triangle");
    private JButton rTriangleButton = new JButton("Right Triangle");
    private JButton diamondButton = new JButton("Diamond");
    private JButton pentagonButton = new JButton("Pentagon");
    //private JButton lineRepeaterButton = new JButton("Line Repeater");
    private ArrayList<JButton> colorButtons = new ArrayList<JButton>();
    private ArrayList<JButton> toolButtons = new ArrayList<JButton>();

    /**
     * Constructor
     */
    public PaintEnvironment() {
        initColorButtons();
        initControlButtons();
        initTools();
        initMenuBar();
        initPanelsAndLayout();
        initFrame();
    }


     // هر رنگ را در پنل رنگی مقداردهی اولیه می کند

    private void initColorButtons() {
        //Set each button's background color
        whiteButton.setBackground(Color.WHITE);
        blackButton.setBackground(Color.BLACK);
        grayButton.setBackground(Color.GRAY);
        blueButton.setBackground(Color.BLUE);
        redButton.setBackground(Color.RED);
        pinkButton.setBackground(Color.PINK);
        yellowButton.setBackground(Color.YELLOW);
        cyanButton.setBackground(Color.CYAN);
        magentaButton.setBackground(Color.MAGENTA);
        orangeButton.setBackground(Color.ORANGE);
        greenButton.setBackground(Color.GREEN);
        lightGrayButton.setBackground(Color.LIGHT_GRAY);
        
        colorButtons.addAll(new ArrayList<JButton>(Arrays.asList(blackButton,
                grayButton, lightGrayButton, blueButton, cyanButton,
                greenButton, whiteButton, yellowButton, orangeButton,
                pinkButton, magentaButton, redButton)));


        //رنگ و اندازه دکمه خا
        for (int i = 0; i < colorButtons.size(); i++) {
            final int buttonIndex = i;
            colorButtons.get(i).setBorder(
                    BorderFactory.createRaisedBevelBorder());
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

    //عملکرد دکمه ها را تنظیم میکند thickness
    public final void initControlButtons() {
        thicknessSlider.setValue(1);
        thicknessSlider.setMinimum(THICKNESS_MIN);
        thicknessSlider.setMaximum(THICKNESS_MAX);
        thicknessSlider.setPreferredSize(SLIDER_DIMENSION);
        primaryPreviewButton.setPreferredSize(PREVIEW_BUTTON_DIMENSION);
        secondaryPreviewButton.setPreferredSize(PREVIEW_BUTTON_DIMENSION);
        primaryPreviewButton.setBackground(primaryColor);
        secondaryPreviewButton.setBackground(secondaryColor);

        //ضخامت را تغییر میدهد
        thicknessSlider.addChangeListener(new ChangeListener() {
            @Override
            public void stateChanged(ChangeEvent e) {
                pc.setLineThickness((float) thicknessSlider.getValue());
                thicknessLabel.setText("Thickness: "
                        + thicknessSlider.getValue());
            }
        });

        //رنگ اصلی را در فوکوس قرار می دهد
        primaryPreviewButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                primaryPreviewButton.setBorder(
                        BorderFactory.createLineBorder(BORDER_COLOR, BORDER_THICKNESS));
                primary = true;
                secondaryPreviewButton.setBorder(
                        BorderFactory.createLineBorder(BORDER_COLOR, 1));
                System.out.println("Color 1 selected.");
            }
        });

        //رنگ ثانویه را در فوکوس قرار می دهد
        secondaryPreviewButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {

                secondaryPreviewButton.setBorder(
                        BorderFactory.createLineBorder(BORDER_COLOR, BORDER_THICKNESS));
                primary = false;
                primaryPreviewButton.setBorder(
                        BorderFactory.createLineBorder(BORDER_COLOR, 1));
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
        
        toolButtons.addAll(new ArrayList<JButton>(Arrays.asList(eraserButton,
                pencilButton, lineButton, boxButton, ellipseButton,
                iTriangleButton/*, rTriangleButton, diamondButton, pentagonButton
                ,lineRepeaterButton*/)));
        


        for (int i = 0; i < toolButtons.size(); i++) {
            toolButtons.get(i).setBorder(BorderFactory.createEmptyBorder());
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
        colorButtonLayout.setHgap(2);
        colorButtonLayout.setVgap(2);
        colorPanel.setLayout(colorButtonLayout);
        colorPanel.setBorder(BorderFactory.createEmptyBorder(3, 3, 3, 3));
        colorPanel.setPreferredSize(PAINT_PANEL_DIMENSION);

        //Preview Panel
        previewPanel.setLayout(new FlowLayout(FlowLayout.CENTER));
        previewPanel.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLoweredBevelBorder(),
                BorderFactory.createEmptyBorder(3, 3, 3, 3)));
        previewPanel.setPreferredSize(PREVIEW_PANEL_DIMENSION);

        //Tool Panel
        toolPanel.setLayout(new GridLayout(toolButtons.size(), 1));
        toolPanel.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLoweredBevelBorder(),
                BorderFactory.createEmptyBorder(3, 3, 3, 3)));
        toolPanel.setPreferredSize(TOOL_PANEL_DIMENSION);

        //Side Panel
        GridBagLayout sideLayout = new GridBagLayout();
        GridBagConstraints c;
        sidePanel.setBorder(BorderFactory.createEtchedBorder());
        sidePanel.setLayout(sideLayout);

        //Draw Panel
        drawPanel.setLayout(new FlowLayout(FlowLayout.LEFT));


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
     * سبک حاشیه هر دکمه را تغییر می دهد. دکمه انتخاب شده تورفتگی دارد
     *       و بقیه نیستند.
     */
    public void toggleColorButtons(ArrayList<JButton> buttons, JButton selected) {
        for (int i = 0; i < buttons.size(); i++) {
            if (buttons.get(i) == selected) {
                buttons.get(i).setBorder(
                        BorderFactory.createLoweredBevelBorder());
            } else {
                buttons.get(i).setBorder(
                        BorderFactory.createRaisedBevelBorder());
            }
        }
    }

    /**
     * ابزار انتخاب شده را روشن می کند
     */
    public void toggleTools(ArrayList<JButton> b, JButton selected) {
        for (int i = 0; i < b.size(); i++) {
            if (b.get(i) == selected) {
                b.get(i).setForeground(Color.BLACK);
            } else {
                b.get(i).setForeground(UNSELECTED_COLOR);
            }
        }
    }

    /**
     * قاب را مقدار دهی اولیه می کند
     */
    private void initFrame() {
        pc.setPreferredSize(DRAWING_DIMENSION);
        frame.add(sidePanel, BorderLayout.WEST);
        frame.add(drawPanel, BorderLayout.EAST);

        //Frame
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.setEnabled(true);
        frame.setResizable(false);
        frame.setJMenuBar(menuBar);
        frame.setSize(FRAME_DIMENSION);
        frame.setVisible(true);
    }

}
