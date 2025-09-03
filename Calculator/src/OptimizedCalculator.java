import javax.swing.*;
import javax.swing.border.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.HashMap;
import java.util.Map;
import java.util.ArrayList;
import java.util.List;

/**
 * A modern, visually attractive calculator with beautiful styling and smooth animations.
 */
public class OptimizedCalculator implements ActionListener {

    // Calculator state
    private String firstNumber = "0";
    private String secondNumber = "0";
    private char currentOperation = ' ';
    private boolean isOperationPending = false;
    private String currentInput = "";
    private List<String> calculationHistory;

    // UI Components
    private final JFrame frame;
    private final JTextArea historyArea;
    private final JScrollPane historyScrollPane;
    private final Map<String, AbstractButton> buttons;

    // Modern Color Scheme
    private static final Color MAIN_BACKGROUND = new Color(25, 25, 35);
    private static final Color DISPLAY_BACKGROUND = new Color(15, 15, 25);
    private static final Color DISPLAY_TEXT = new Color(220, 220, 235);
    private static final Color NUMBER_BUTTON = new Color(55, 55, 70);
    private static final Color NUMBER_BUTTON_HOVER = new Color(70, 70, 85);
    private static final Color OPERATOR_BUTTON = new Color(255, 149, 0);
    private static final Color OPERATOR_BUTTON_HOVER = new Color(255, 169, 30);
    private static final Color FUNCTION_BUTTON = new Color(165, 165, 180);
    private static final Color FUNCTION_BUTTON_HOVER = new Color(185, 185, 200);
    private static final Color DELETE_BUTTON = new Color(255, 59, 59);
    private static final Color DELETE_BUTTON_HOVER = new Color(255, 79, 79);
    private static final Color EQUALS_BUTTON = new Color(255, 149, 0);
    private static final Color EQUALS_BUTTON_HOVER = new Color(255, 169, 30);
    
    // Modern Fonts
    private static final Font BUTTON_FONT = new Font("SF Pro Display", Font.BOLD, 22);
    private static final Font SMALL_BUTTON_FONT = new Font("SF Pro Display", Font.BOLD, 16);
    private static final Font DISPLAY_FONT = new Font("SF Mono", Font.BOLD, 16);

    public OptimizedCalculator() {
        frame = new JFrame("Modern Calculator");
        historyArea = new JTextArea();
        historyScrollPane = new JScrollPane(historyArea);
        buttons = new HashMap<>();
        calculationHistory = new ArrayList<>();
        currentInput = "";
        
        // Enable anti-aliasing for smooth text
        System.setProperty("awt.useSystemAAFontSettings", "on");
        System.setProperty("swing.aatext", "true");
        
        initializeCalculator();
    }

    private void initializeCalculator() {
        setupFrame();
        createButtons();
        layoutComponents();
        addEventListeners();
        frame.setVisible(true);
    }

    private void setupFrame() {
        frame.setSize(400, 650);
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.setLocationRelativeTo(null);
        frame.setLayout(null);
        frame.getContentPane().setBackground(MAIN_BACKGROUND);
        frame.setResizable(false);
        frame.setUndecorated(false);
        
        // Add subtle shadow effect
        frame.getRootPane().setBorder(BorderFactory.createMatteBorder(1, 1, 1, 1, new Color(50, 50, 60)));
    }

    private void createButtons() {
        // Number buttons
        for (int i = 0; i <= 9; i++) {
            JButton button = new JButton(String.valueOf(i));
            button.setFont(BUTTON_FONT);
            button.setFocusable(false);
            buttons.put(String.valueOf(i), button);
        }

        // Operation buttons
        String[] operations = {"+", "-", "*", "/", "=", ".", "C", "Del", "x²", "√", "1/x"};
        for (String op : operations) {
            JButton button = new JButton(op);
            button.setFont(op.equals("Del") ? SMALL_BUTTON_FONT : BUTTON_FONT);
            button.setFocusable(false);
            
            if (op.equals("+") || op.equals("-") || op.equals("*") || op.equals("/") || 
                op.equals("=") || op.equals("x²") || op.equals("√") || op.equals("1/x")) {
                button.setBackground(OPERATOR_BUTTON);
            } else if (op.equals("C") || op.equals("Del")) {
                button.setBackground(DELETE_BUTTON);
                button.setForeground(Color.WHITE);
            }
            
            buttons.put(op, button);
        }

        // Power button
        JRadioButton onButton = new JRadioButton("On", true);
        JRadioButton offButton = new JRadioButton("Off");
        
        ButtonGroup powerGroup = new ButtonGroup();
        powerGroup.add(onButton);
        powerGroup.add(offButton);
        
        configurePowerButton(onButton);
        configurePowerButton(offButton);
        
        buttons.put("On", onButton);
        buttons.put("Off", offButton);
    }

    private void configurePowerButton(JRadioButton button) {
        button.setFont(new Font("SF Pro Display", Font.BOLD, 14));
        button.setBackground(MAIN_BACKGROUND);
        button.setForeground(DISPLAY_TEXT);
        button.setFocusable(false);
        button.setBorder(BorderFactory.createEmptyBorder());
    }

    private void layoutComponents() {
        // Modern scrolling display area with beautiful styling
        historyArea.setFont(DISPLAY_FONT);
        historyArea.setEditable(false);
        historyArea.setBackground(DISPLAY_BACKGROUND);
        historyArea.setForeground(DISPLAY_TEXT);
        historyArea.setBorder(new EmptyBorder(15, 20, 15, 20));
        historyArea.setLineWrap(true);
        historyArea.setWrapStyleWord(false);
        historyArea.setMargin(new Insets(5, 5, 5, 5));
        
        // Create beautiful rounded border for display
        historyScrollPane.setBounds(20, 20, 360, 180);
        historyScrollPane.setBorder(createRoundedBorder(DISPLAY_BACKGROUND, 15));
        historyScrollPane.setBackground(DISPLAY_BACKGROUND);
        historyScrollPane.getViewport().setBackground(DISPLAY_BACKGROUND);
        historyScrollPane.setVerticalScrollBarPolicy(JScrollPane.VERTICAL_SCROLLBAR_AS_NEEDED);
        historyScrollPane.setHorizontalScrollBarPolicy(JScrollPane.HORIZONTAL_SCROLLBAR_NEVER);
        
        // Style the scrollbar
        styleScrollBar(historyScrollPane.getVerticalScrollBar());
        
        frame.add(historyScrollPane);
        
        // Initialize with welcome message
        updateDisplay("");
        addToHistory("✨ Modern Calculator Ready");

        // Layout buttons with modern styling
        layoutModernButtons();
    }
    
    private void layoutModernButtons() {
        int buttonWidth = 75;
        int buttonHeight = 55;
        int spacing = 10;
        int startX = 20;
        int startY = 220;
        
        // Power buttons with modern style
        styleToggleButton((JRadioButton)buttons.get("On"), startX, startY - 30, 60, 25);
        styleToggleButton((JRadioButton)buttons.get("Off"), startX + 70, startY - 30, 60, 25);
        frame.add(buttons.get("On"));
        frame.add(buttons.get("Off"));

        // Function buttons row (C, Del, /, Clear All)
        styleDeleteButton((JButton)buttons.get("C"), startX, startY, buttonWidth, buttonHeight);
        styleDeleteButton((JButton)buttons.get("Del"), startX + buttonWidth + spacing, startY, buttonWidth, buttonHeight);
        styleOperatorButton((JButton)buttons.get("/"), startX + 2 * (buttonWidth + spacing), startY, buttonWidth, buttonHeight);
        
        // Create stylish Clear All button
        JButton clearAllButton = createStyledButton("AC", FUNCTION_BUTTON, FUNCTION_BUTTON_HOVER, Color.BLACK);
        clearAllButton.setBounds(startX + 3 * (buttonWidth + spacing), startY, buttonWidth, buttonHeight);
        clearAllButton.addActionListener(e -> clearAll());
        buttons.put("ClearAll", clearAllButton);
        frame.add(buttons.get("C"));
        frame.add(buttons.get("Del"));
        frame.add(buttons.get("/"));
        frame.add(clearAllButton);

        // Advanced operations row
        startY += buttonHeight + spacing;
        styleOperatorButton((JButton)buttons.get("√"), startX, startY, buttonWidth, buttonHeight);
        styleOperatorButton((JButton)buttons.get("x²"), startX + buttonWidth + spacing, startY, buttonWidth, buttonHeight);
        styleOperatorButton((JButton)buttons.get("1/x"), startX + 2 * (buttonWidth + spacing), startY, buttonWidth, buttonHeight);
        styleOperatorButton((JButton)buttons.get("-"), startX + 3 * (buttonWidth + spacing), startY, buttonWidth, buttonHeight);
        frame.add(buttons.get("√"));
        frame.add(buttons.get("x²"));
        frame.add(buttons.get("1/x"));
        frame.add(buttons.get("-"));

        // Number pad layout with beautiful styling
        int[][] numberLayout = {
            {7, 8, 9}, {4, 5, 6}, {1, 2, 3}
        };
        
        for (int row = 0; row < numberLayout.length; row++) {
            int yPos = startY + (row + 1) * (buttonHeight + spacing);
            for (int col = 0; col < numberLayout[row].length; col++) {
                String num = String.valueOf(numberLayout[row][col]);
                int xPos = startX + col * (buttonWidth + spacing);
                styleNumberButton((JButton)buttons.get(num), xPos, yPos, buttonWidth, buttonHeight);
                frame.add(buttons.get(num));
            }
            // Operation buttons on the right
            if (row == 0) {
                styleOperatorButton((JButton)buttons.get("*"), startX + 3 * (buttonWidth + spacing), yPos, buttonWidth, buttonHeight);
                frame.add(buttons.get("*"));
            } else if (row == 1) {
                styleOperatorButton((JButton)buttons.get("+"), startX + 3 * (buttonWidth + spacing), yPos, buttonWidth, buttonHeight);
                frame.add(buttons.get("+"));
            } else if (row == 2) {
                // Style equals button (taller)
                styleEqualsButton((JButton)buttons.get("="), startX + 3 * (buttonWidth + spacing), yPos, buttonWidth, buttonHeight * 2 + spacing);
                frame.add(buttons.get("="));
            }
        }

        // Bottom row (0, .)
        int bottomY = startY + 4 * (buttonHeight + spacing);
        styleNumberButton((JButton)buttons.get("0"), startX, bottomY, 2 * buttonWidth + spacing, buttonHeight); // Wide 0 button
        styleNumberButton((JButton)buttons.get("."), startX + 2 * (buttonWidth + spacing), bottomY, buttonWidth, buttonHeight);
        frame.add(buttons.get("0"));
        frame.add(buttons.get("."));
    }

    private void addEventListeners() {
        buttons.values().forEach(button -> button.addActionListener(this));
    }

    @Override
    public void actionPerformed(ActionEvent e) {
        Object source = e.getSource();
        String command = ((AbstractButton) source).getText();

        if (source == buttons.get("On")) {
            enableCalculator(true);
        } else if (source == buttons.get("Off")) {
            enableCalculator(false);
        } else if (!buttons.get("On").isSelected()) {
            return; // Calculator is off
        } else {
            processCommand(command);
        }
    }

    private void processCommand(String command) {
        try {
            switch (command) {
                case "C":
                case "AC":
                    clearAll();
                    break;
                case "Del":
                    deleteLast();
                    break;
                case ".":
                    addDecimalPoint();
                    break;
                case "=":
                    calculateResult();
                    break;
                case "+":
                case "-":
                case "*":
                case "/":
                    setOperation(command.charAt(0));
                    break;
                case "x²":
                    calculateSquare();
                    break;
                case "√":
                    calculateSquareRoot();
                    break;
                case "1/x":
                    calculateReciprocal();
                    break;
                default:
                    if (command.matches("\\d")) {
                        addDigit(command);
                    }
                    break;
            }
        } catch (Exception ex) {
            showError("Error");
            firstNumber = "0";
            secondNumber = "0";
            isOperationPending = false;
            currentInput = "";
        }
    }

    private void clearAll() {
        calculationHistory.clear();
        currentInput = "";
        firstNumber = "0";
        secondNumber = "0";
        currentOperation = ' ';
        isOperationPending = false;
        updateDisplay(currentInput);
        addToHistory("Calculator Reset...");
    }

    private void deleteLast() {
        if (currentInput.length() > 0) {
            currentInput = currentInput.substring(0, currentInput.length() - 1);
            updateDisplay(currentInput);
        }
    }

    private void addDigit(String digit) {
        if (currentInput.equals("0") && !digit.equals("0")) {
            currentInput = digit;
        } else if (!currentInput.equals("0")) {
            currentInput += digit;
        } else if (digit.equals("0") && currentInput.isEmpty()) {
            currentInput = "0";
        }
        updateDisplay(currentInput);
    }

    private void addDecimalPoint() {
        if (currentInput.isEmpty()) {
            currentInput = "0.";
        } else if (!currentInput.contains(".")) {
            currentInput += ".";
        }
        updateDisplay(currentInput);
    }

    private void setOperation(char operation) {
        if (!currentInput.isEmpty()) {
            if (isOperationPending) {
                calculateResult();
            }
            firstNumber = currentInput.isEmpty() ? firstNumber : currentInput;
            currentOperation = operation;
            isOperationPending = true;
            addToHistory(firstNumber + " " + operation);
            currentInput = "";
            updateDisplay("");
        }
    }

    private void calculateResult() {
        if (!isOperationPending || currentInput.isEmpty()) {
            return;
        }

        secondNumber = currentInput;
        BigDecimal result = performCalculation();
        
        if (result != null) {
            String resultStr = formatResult(result);
            String calculation = secondNumber + " = " + resultStr;
            addToHistory(calculation);
            addToHistory("------------------------");
            currentInput = resultStr;
            firstNumber = resultStr;
            updateDisplay(currentInput);
        }
        
        isOperationPending = false;
    }

    private BigDecimal performCalculation() {
        try {
            BigDecimal num1 = new BigDecimal(firstNumber);
            BigDecimal num2 = new BigDecimal(secondNumber);
            
            switch (currentOperation) {
                case '+':
                    return num1.add(num2);
                case '-':
                    return num1.subtract(num2);
                case '*':
                    return num1.multiply(num2);
                case '/':
                    if (num2.compareTo(BigDecimal.ZERO) == 0) {
                        showError("Cannot divide by zero");
                        return null;
                    }
                    return num1.divide(num2, 10, RoundingMode.HALF_UP);
                default:
                    return null;
            }
        } catch (NumberFormatException e) {
            showError("Invalid input");
            return null;
        }
    }

    private void calculateSquare() {
        if (!currentInput.isEmpty()) {
            try {
                BigDecimal num = new BigDecimal(currentInput);
                BigDecimal result = num.multiply(num);
                String resultStr = formatResult(result);
                String calculation = currentInput + "² = " + resultStr;
                addToHistory(calculation);
                currentInput = resultStr;
                updateDisplay(currentInput);
            } catch (NumberFormatException e) {
                showError("Invalid input");
            }
        }
    }

    private void calculateSquareRoot() {
        if (!currentInput.isEmpty()) {
            try {
                double num = Double.parseDouble(currentInput);
                if (num < 0) {
                    showError("Invalid input");
                    return;
                }
                double result = Math.sqrt(num);
                String resultStr = formatResult(BigDecimal.valueOf(result));
                String calculation = "√" + currentInput + " = " + resultStr;
                addToHistory(calculation);
                currentInput = resultStr;
                updateDisplay(currentInput);
            } catch (NumberFormatException e) {
                showError("Invalid input");
            }
        }
    }

    private void calculateReciprocal() {
        if (!currentInput.isEmpty()) {
            try {
                BigDecimal num = new BigDecimal(currentInput);
                if (num.compareTo(BigDecimal.ZERO) == 0) {
                    showError("Cannot divide by zero");
                    return;
                }
                BigDecimal result = BigDecimal.ONE.divide(num, 10, RoundingMode.HALF_UP);
                String resultStr = formatResult(result);
                String calculation = "1/" + currentInput + " = " + resultStr;
                addToHistory(calculation);
                currentInput = resultStr;
                updateDisplay(currentInput);
            } catch (NumberFormatException e) {
                showError("Invalid input");
            }
        }
    }

    private String formatResult(BigDecimal result) {
        // Remove trailing zeros and unnecessary decimal point
        String formatted = result.stripTrailingZeros().toPlainString();
        return formatted;
    }

    private void enableCalculator(boolean enabled) {
        buttons.get("On").setEnabled(!enabled);
        buttons.get("Off").setEnabled(enabled);
        
        if (!enabled) {
            clearAll();
        }
        
        // Enable/disable all buttons except power buttons
        buttons.entrySet().stream()
            .filter(entry -> !entry.getKey().equals("On") && !entry.getKey().equals("Off"))
            .forEach(entry -> entry.getValue().setEnabled(enabled));
        
        historyArea.setEnabled(enabled);
    }
    
    private void addToHistory(String calculation) {
        calculationHistory.add(calculation);
        updateHistoryDisplay();
    }
    
    private void updateHistoryDisplay() {
        StringBuilder historyText = new StringBuilder();
        for (String calc : calculationHistory) {
            historyText.append(calc).append("\n");
        }
        historyArea.setText(historyText.toString());
        
        // Auto-scroll to bottom
        SwingUtilities.invokeLater(() -> {
            JScrollBar verticalBar = historyScrollPane.getVerticalScrollBar();
            verticalBar.setValue(verticalBar.getMaximum());
        });
    }
    
    private void clearHistory() {
        calculationHistory.clear();
        historyArea.setText("");
    }
    
    private void updateDisplay(String input) {
        StringBuilder displayText = new StringBuilder();
        
        // Show calculation history
        for (String calc : calculationHistory) {
            displayText.append(calc).append("\n");
        }
        
        // Show current input with cursor
        if (!input.isEmpty()) {
            displayText.append("> ").append(input);
        } else {
            displayText.append("> ");
        }
        
        historyArea.setText(displayText.toString());
        
        // Auto-scroll to bottom
        SwingUtilities.invokeLater(() -> {
            JScrollBar verticalBar = historyScrollPane.getVerticalScrollBar();
            verticalBar.setValue(verticalBar.getMaximum());
        });
    }
    
    private void showError(String error) {
        addToHistory("❌ ERROR: " + error);
        currentInput = "";
        updateDisplay(currentInput);
    }
    
    // ===== MODERN STYLING METHODS =====
    
    private Border createRoundedBorder(Color color, int radius) {
        return new AbstractBorder() {
            @Override
            public void paintBorder(Component c, Graphics g, int x, int y, int width, int height) {
                Graphics2D g2d = (Graphics2D) g.create();
                g2d.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                g2d.setColor(color.brighter());
                g2d.drawRoundRect(x, y, width - 1, height - 1, radius, radius);
                g2d.dispose();
            }
            
            @Override
            public Insets getBorderInsets(Component c) {
                return new Insets(2, 2, 2, 2);
            }
        };
    }
    
    private void styleScrollBar(JScrollBar scrollBar) {
        scrollBar.setBackground(DISPLAY_BACKGROUND);
        scrollBar.setForeground(new Color(100, 100, 120));
        scrollBar.setPreferredSize(new Dimension(8, 0));
        scrollBar.setUnitIncrement(16);
    }
    
    private JButton createStyledButton(String text, Color bgColor, Color hoverColor, Color textColor) {
        JButton button = new JButton(text) {
            @Override
            protected void paintComponent(Graphics g) {
                Graphics2D g2d = (Graphics2D) g.create();
                g2d.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                
                // Create gradient effect
                GradientPaint gradient = new GradientPaint(
                    0, 0, getModel().isPressed() ? bgColor.darker() : 
                           getModel().isRollover() ? hoverColor : bgColor,
                    0, getHeight(), getModel().isPressed() ? bgColor.darker().darker() : 
                                   getModel().isRollover() ? hoverColor.darker() : bgColor.darker()
                );
                
                g2d.setPaint(gradient);
                g2d.fillRoundRect(0, 0, getWidth(), getHeight(), 12, 12);
                
                // Add subtle border
                g2d.setColor(bgColor.brighter());
                g2d.setStroke(new BasicStroke(1.0f));
                g2d.drawRoundRect(0, 0, getWidth() - 1, getHeight() - 1, 12, 12);
                
                g2d.dispose();
                super.paintComponent(g);
            }
        };
        
        button.setFont(BUTTON_FONT);
        button.setForeground(textColor);
        button.setFocusable(false);
        button.setBorder(BorderFactory.createEmptyBorder());
        button.setContentAreaFilled(false);
        button.setCursor(new Cursor(Cursor.HAND_CURSOR));
        
        // Add hover effects
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
    
    private void styleNumberButton(JButton button, int x, int y, int width, int height) {
        button.setBounds(x, y, width, height);
        applyModernButtonStyle(button, NUMBER_BUTTON, NUMBER_BUTTON_HOVER, Color.WHITE);
    }
    
    private void styleOperatorButton(JButton button, int x, int y, int width, int height) {
        button.setBounds(x, y, width, height);
        applyModernButtonStyle(button, OPERATOR_BUTTON, OPERATOR_BUTTON_HOVER, Color.WHITE);
    }
    
    private void styleDeleteButton(JButton button, int x, int y, int width, int height) {
        button.setBounds(x, y, width, height);
        applyModernButtonStyle(button, DELETE_BUTTON, DELETE_BUTTON_HOVER, Color.WHITE);
    }
    
    private void styleEqualsButton(JButton button, int x, int y, int width, int height) {
        button.setBounds(x, y, width, height);
        applyModernButtonStyle(button, EQUALS_BUTTON, EQUALS_BUTTON_HOVER, Color.WHITE);
        button.setFont(new Font("SF Pro Display", Font.BOLD, 28)); // Larger font for equals
    }
    
    private void styleToggleButton(JRadioButton button, int x, int y, int width, int height) {
        button.setBounds(x, y, width, height);
        button.setFont(SMALL_BUTTON_FONT);
        button.setForeground(DISPLAY_TEXT);
        button.setBackground(MAIN_BACKGROUND);
        button.setBorder(createRoundedBorder(FUNCTION_BUTTON, 8));
        button.setFocusable(false);
        button.setCursor(new Cursor(Cursor.HAND_CURSOR));
    }
    
    private void applyModernButtonStyle(JButton button, Color bgColor, Color hoverColor, Color textColor) {
        button.setFont(BUTTON_FONT);
        button.setForeground(textColor);
        button.setFocusable(false);
        button.setBorder(BorderFactory.createEmptyBorder());
        button.setContentAreaFilled(false);
        button.setCursor(new Cursor(Cursor.HAND_CURSOR));
        
        // Override paint method for modern appearance
        button.setUI(new javax.swing.plaf.basic.BasicButtonUI() {
            @Override
            public void paint(Graphics g, JComponent c) {
                JButton b = (JButton) c;
                Graphics2D g2d = (Graphics2D) g.create();
                g2d.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                
                // Create beautiful gradient
                Color topColor, bottomColor;
                if (b.getModel().isPressed()) {
                    topColor = bgColor.darker().darker();
                    bottomColor = bgColor.darker();
                } else if (b.getModel().isRollover()) {
                    topColor = hoverColor;
                    bottomColor = hoverColor.darker();
                } else {
                    topColor = bgColor;
                    bottomColor = bgColor.darker();
                }
                
                GradientPaint gradient = new GradientPaint(0, 0, topColor, 0, c.getHeight(), bottomColor);
                g2d.setPaint(gradient);
                g2d.fillRoundRect(0, 0, c.getWidth(), c.getHeight(), 15, 15);
                
                // Add subtle highlight
                g2d.setColor(topColor.brighter());
                g2d.setStroke(new BasicStroke(1.5f));
                g2d.drawRoundRect(1, 1, c.getWidth() - 3, c.getHeight() - 3, 13, 13);
                
                // Add inner shadow effect
                g2d.setColor(new Color(0, 0, 0, 30));
                g2d.drawRoundRect(0, 0, c.getWidth() - 1, c.getHeight() - 1, 15, 15);
                
                g2d.dispose();
                
                // Paint text
                FontMetrics fm = g.getFontMetrics();
                Rectangle stringBounds = fm.getStringBounds(b.getText(), g).getBounds();
                int textX = (c.getWidth() - stringBounds.width) / 2;
                int textY = (c.getHeight() - stringBounds.height) / 2 + fm.getAscent();
                
                g.setColor(textColor);
                g.setFont(button.getFont());
                g.drawString(b.getText(), textX, textY);
            }
        });
        
        // Add smooth hover animation effect
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
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> new OptimizedCalculator());
    }
}
