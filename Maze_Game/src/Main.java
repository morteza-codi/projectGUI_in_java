

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.EventQueue;
import java.awt.Font;
import java.awt.Graphics2D;
import java.awt.GridLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ItemEvent;
import java.awt.event.ItemListener;
import java.awt.event.KeyEvent;
import java.io.File;
import java.io.IOException;
import javax.sound.sampled.AudioInputStream;
import javax.sound.sampled.AudioSystem;
import javax.sound.sampled.Clip;
import javax.sound.sampled.LineUnavailableException;
import javax.sound.sampled.UnsupportedAudioFileException;
import javax.swing.AbstractAction;
import javax.swing.BorderFactory;
import javax.swing.Box;
import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JDialog;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JMenu;
import javax.swing.JMenuBar;
import javax.swing.JMenuItem;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JSlider;
import javax.swing.JTabbedPane;
import javax.swing.KeyStroke;
import javax.swing.SwingConstants;
import javax.swing.SwingUtilities;
import javax.swing.Timer;
import javax.swing.UIManager;
import javax.swing.WindowConstants;
import javax.swing.border.EmptyBorder;
import javax.swing.border.TitledBorder;

public class Main extends JFrame {
	private static final long serialVersionUID = 1L;
	private JPanel contentPane;
	private MazePanel mazePane;
	private JMenuBar menuBar;
	private JMenu game;
	private JMenu level;
	private JMenuItem[] levels;
	private JMenu help;
	private JMenu computer;
	
	// Game stats
	private int currentLevel = 1;
	private int score = 0;
	private int moves = 0;
	private int timeElapsed = 0;
	
	// UI components
	private JPanel statsPanel;
	private JLabel levelLabel;
	private JLabel scoreLabel;
	private JLabel movesLabel;
	private JLabel timeLabel;
	private Timer gameTimer;
	
	// Sound effects
	private Clip backgroundMusic;
	private Clip moveSound;
	private Clip winSound;
	private boolean soundEnabled = true;

	/*******************************************************Main*/
	public static void main(String[] args) {
		EventQueue.invokeLater(new Runnable() {
			public void run() {
				try {
					// Set modern look and feel
					try {
						UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
						
						// Customize UI colors
						UIManager.put("OptionPane.background", new Color(50, 60, 80));
						UIManager.put("Panel.background", new Color(50, 60, 80));
						UIManager.put("OptionPane.messageForeground", Color.WHITE);
						
						// Fix dialog text visibility
						UIManager.put("OptionPane.foreground", Color.WHITE);
						UIManager.put("Label.foreground", Color.WHITE);
						UIManager.put("Button.background", new Color(100, 120, 150));
						UIManager.put("Button.foreground", Color.WHITE);
						
						// Menu colors
						UIManager.put("Menu.background", new Color(60, 70, 90));
						UIManager.put("Menu.foreground", Color.WHITE);
						UIManager.put("Menu.selectionBackground", new Color(80, 100, 140));
						UIManager.put("Menu.selectionForeground", Color.WHITE);
						UIManager.put("Menu.borderPainted", false);
						
						// MenuBar colors
						UIManager.put("MenuBar.background", new Color(40, 50, 70));
						UIManager.put("MenuBar.foreground", Color.WHITE);
						
						// MenuItem colors
						UIManager.put("MenuItem.background", new Color(60, 70, 90));
						UIManager.put("MenuItem.foreground", Color.WHITE);
						UIManager.put("MenuItem.selectionBackground", new Color(80, 100, 140));
						UIManager.put("MenuItem.selectionForeground", Color.WHITE);
						UIManager.put("MenuItem.borderPainted", false);
						
						// PopupMenu colors
						UIManager.put("PopupMenu.background", new Color(40, 50, 70));
						UIManager.put("PopupMenu.foreground", Color.WHITE);
						UIManager.put("PopupMenu.border", BorderFactory.createLineBorder(new Color(80, 100, 140)));
					} catch (Exception e) {
						// Fallback to default look and feel
						System.out.println("Could not set system look and feel");
					}
					
					Main frame = new Main();
					frame.setVisible(true);
				} catch (Exception e) {
					e.printStackTrace();
				}
			}
		});
	}
	/************************************************************End Main*/


	/****************************************************constructor*/
	public Main() {
		// Apply custom UI for popup menus
		UIManager.put("PopupMenu.background", new Color(40, 50, 70));
		UIManager.put("PopupMenu.border", BorderFactory.createLineBorder(new Color(80, 100, 140), 1));
		setSize(800, 700);
		setResizable(true);
		setMinimumSize(new Dimension(650, 600));

		setDefaultCloseOperation(WindowConstants.DISPOSE_ON_CLOSE);
		setLocationRelativeTo(null);
		setTitle("Maze Runner - 3D Adventure");
		
		// Set up content pane with dark theme
		contentPane = new JPanel();
		contentPane.setBackground(new Color(40, 44, 52));
		contentPane.setBorder(new EmptyBorder(10, 10, 10, 10));
		setContentPane(contentPane);
		contentPane.setLayout(new BorderLayout(10, 10));
		
		// Initialize game timer
		gameTimer = new Timer(1000, e -> {
			timeElapsed++;
			updateTimeLabel();
		});

		// Menu Bar with modern styling
		menuBar = new JMenuBar();
		menuBar.setBackground(new Color(30, 34, 42));
		menuBar.setBorder(BorderFactory.createEmptyBorder(5, 5, 5, 5));
		setJMenuBar(menuBar);

		// Menu with keyboard shortcuts
		game = new JMenu("Game");
		game.setForeground(Color.WHITE);
		game.setBackground(new Color(60, 70, 90));
		game.setOpaque(true);
		game.setMnemonic(KeyEvent.VK_G);
		
		level = new JMenu("Level");
		level.setForeground(Color.WHITE);
		level.setBackground(new Color(60, 70, 90));
		level.setOpaque(true);
		level.setMnemonic(KeyEvent.VK_L);
		
		computer = new JMenu("Computer");
		computer.setForeground(Color.WHITE);
		computer.setBackground(new Color(60, 70, 90));
		computer.setOpaque(true);
		computer.setMnemonic(KeyEvent.VK_C);
		
		help = new JMenu("Help");
		help.setForeground(Color.WHITE);
		help.setBackground(new Color(60, 70, 90));
		help.setOpaque(true);
		help.setMnemonic(KeyEvent.VK_H);
		
		JMenu settings = new JMenu("Settings");
		settings.setForeground(Color.WHITE);
		settings.setBackground(new Color(60, 70, 90));
		settings.setOpaque(true);
		settings.setMnemonic(KeyEvent.VK_S);
		
		menuBar.add(game);
		menuBar.add(level);
		menuBar.add(computer);
		menuBar.add(settings);
		menuBar.add(help);
		
		// Add stats panel at the top
		statsPanel = new JPanel(new GridLayout(1, 4, 10, 0));
		statsPanel.setBackground(new Color(50, 55, 65));
		statsPanel.setBorder(BorderFactory.createCompoundBorder(
			BorderFactory.createLineBorder(new Color(70, 80, 100), 1),
			BorderFactory.createEmptyBorder(8, 10, 8, 10)
		));
		
		// Create labels for game stats
		Font statsFont = new Font("SansSerif", Font.BOLD, 14);
		Color statsForeground = new Color(220, 220, 220);
		
		levelLabel = new JLabel("Level: 1");
		levelLabel.setFont(statsFont);
		levelLabel.setForeground(statsForeground);
		
		scoreLabel = new JLabel("Score: 0");
		scoreLabel.setFont(statsFont);
		scoreLabel.setForeground(statsForeground);
		
		movesLabel = new JLabel("Moves: 0");
		movesLabel.setFont(statsFont);
		movesLabel.setForeground(statsForeground);
		
		timeLabel = new JLabel("Time: 0:00");
		timeLabel.setFont(statsFont);
		timeLabel.setForeground(statsForeground);
		
		statsPanel.add(levelLabel);
		statsPanel.add(scoreLabel);
		statsPanel.add(movesLabel);
		statsPanel.add(timeLabel);
		
		contentPane.add(statsPanel, BorderLayout.NORTH);

		// Game menu items with keyboard shortcuts
		JMenuItem newGame = new JMenuItem(new AbstractAction("New Game") {
			private static final long serialVersionUID = 1L;

			@Override
			public void actionPerformed(ActionEvent e) {
				startNewGame();
			}
		});
		newGame.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_N, KeyEvent.CTRL_DOWN_MASK));
		styleMenuItem(newGame);

		JMenuItem restart = new JMenuItem(new AbstractAction("Restart Level") {
			private static final long serialVersionUID = 1L;

			@Override
			public void actionPerformed(ActionEvent e) {
				restartLevel();
			}
		});
		restart.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_R, KeyEvent.CTRL_DOWN_MASK));
		styleMenuItem(restart);
		
		JMenuItem saveGame = new JMenuItem(new AbstractAction("Save Game") {
			private static final long serialVersionUID = 1L;

			@Override
			public void actionPerformed(ActionEvent e) {
				saveGame();
			}
		});
		saveGame.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_S, KeyEvent.CTRL_DOWN_MASK));
		styleMenuItem(saveGame);
		
		JMenuItem loadGame = new JMenuItem(new AbstractAction("Load Game") {
			private static final long serialVersionUID = 1L;

			@Override
			public void actionPerformed(ActionEvent e) {
				loadGame();
			}
		});
		loadGame.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_L, KeyEvent.CTRL_DOWN_MASK));
		styleMenuItem(loadGame);
		
		JMenuItem exitGame = new JMenuItem(new AbstractAction("Exit") {
			private static final long serialVersionUID = 1L;

			@Override
			public void actionPerformed(ActionEvent e) {
				System.exit(0);
			}
		});
		exitGame.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_Q, KeyEvent.CTRL_DOWN_MASK));
		styleMenuItem(exitGame);

		JMenuItem hint = new JMenuItem(new AbstractAction("Hint") {
			private static final long serialVersionUID = 1L;

			@Override
			public void actionPerformed(ActionEvent e) {
				mazePane.hint();
				if (soundEnabled) {
					playSound("hint");
				}
				// Deduct points for using hint
				updateScore(-5);
			}
		});
		hint.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_H, 0));
		styleMenuItem(hint);

		JMenuItem autoMove = new JMenuItem(new AbstractAction("Auto Solve") {
			private static final long serialVersionUID = 1L;

			@Override
			public void actionPerformed(ActionEvent e) {
				mazePane.autoMove((Graphics2D) mazePane.getGraphics());
				if (soundEnabled) {
					playSound("solve");
				}
				// No points for auto solving
				updateScore(-score);
			}
		});
		autoMove.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_A, KeyEvent.CTRL_DOWN_MASK));
		styleMenuItem(autoMove);

		JMenuItem algorithm = new JMenuItem(new AbstractAction("Show Algorithm") {
			private static final long serialVersionUID = 1L;

			@Override
			public void actionPerformed(ActionEvent e) {
				mazePane.algorithm((Graphics2D) mazePane.getGraphics());
				if (soundEnabled) {
					playSound("algorithm");
				}
			}
		});
		algorithm.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_D, KeyEvent.CTRL_DOWN_MASK));
		styleMenuItem(algorithm);
		
		// Create difficulty levels menu
		JMenuItem easyLevels = new JMenuItem(new AbstractAction("Easy (11x11)") {
			private static final long serialVersionUID = 1L;
			
			@Override
			public void actionPerformed(ActionEvent e) {
				setDifficulty(11);
			}
		});
		styleMenuItem(easyLevels);
		
		JMenuItem mediumLevels = new JMenuItem(new AbstractAction("Medium (21x21)") {
			private static final long serialVersionUID = 1L;
			
			@Override
			public void actionPerformed(ActionEvent e) {
				setDifficulty(21);
			}
		});
		styleMenuItem(mediumLevels);
		
		JMenuItem hardLevels = new JMenuItem(new AbstractAction("Hard (31x31)") {
			private static final long serialVersionUID = 1L;
			
			@Override
			public void actionPerformed(ActionEvent e) {
				setDifficulty(31);
			}
		});
		styleMenuItem(hardLevels);
		
		JMenuItem expertLevels = new JMenuItem(new AbstractAction("Expert (51x51)") {
			private static final long serialVersionUID = 1L;
			
			@Override
			public void actionPerformed(ActionEvent e) {
				setDifficulty(51);
			}
		});
		styleMenuItem(expertLevels);
		
		// Add numbered levels
		levels = new JMenuItem[10]; // Reduce to 10 levels for simplicity
		for(int i = 0; i < levels.length; i++) {
			levels[i] = new JMenuItem("Level " + (i + 1));
			levels[i].setActionCommand((i + 1) + "");
			levels[i].addActionListener(new LevelsAction());
			styleMenuItem(levels[i]);
		}
		
		// Settings menu items
		JMenuItem toggleSound = new JMenuItem(new AbstractAction("Toggle Sound") {
			private static final long serialVersionUID = 1L;

			@Override
			public void actionPerformed(ActionEvent e) {
				soundEnabled = !soundEnabled;
				if (soundEnabled) {
					playBackgroundMusic();
				} else {
					stopBackgroundMusic();
				}
			}
		});
		styleMenuItem(toggleSound);
		
		JMenuItem speedSettings = new JMenuItem(new AbstractAction("Animation Speed") {
			private static final long serialVersionUID = 1L;

			@Override
			public void actionPerformed(ActionEvent e) {
				showSpeedDialog();
			}
		});
		styleMenuItem(speedSettings);
		
		// Help menu items
		JMenuItem controls = new JMenuItem(new AbstractAction("Controls") {
			private static final long serialVersionUID = 1L;

			@Override
			public void actionPerformed(ActionEvent e) {
				showControlsDialog();
			}
		});
		styleMenuItem(controls);
		
		JMenuItem highScores = new JMenuItem(new AbstractAction("High Scores") {
			private static final long serialVersionUID = 1L;

			@Override
			public void actionPerformed(ActionEvent e) {
				showHighScores();
			}
		});
		styleMenuItem(highScores);
		
		JMenuItem about = new JMenuItem(new AbstractAction("About") {
			private static final long serialVersionUID = 1L;

			@Override
			public void actionPerformed(ActionEvent e) {
				showAboutDialog();
			}
		});
		styleMenuItem(about);

		// Add menu items to their respective menus
		game.add(newGame);
		game.add(restart);
		game.addSeparator();
		game.add(saveGame);
		game.add(loadGame);
		game.addSeparator();
		game.add(exitGame);
		
		level.add(easyLevels);
		level.add(mediumLevels);
		level.add(hardLevels);
		level.add(expertLevels);
		level.addSeparator();
		for(int i = 0; i < levels.length; i++) {
			level.add(levels[i]);
		}

		computer.add(hint);
		computer.add(autoMove);
		computer.add(algorithm);
		
		settings.add(toggleSound);
		settings.add(speedSettings);
		
		help.add(controls);
		help.add(highScores);
		help.addSeparator();
		help.add(about);

		mazePane = new MazePanel(11);
		contentPane.add(mazePane, BorderLayout.CENTER);
	}
	/*************************************************************End constructor*/

	/**
	 * Start a new game with current settings
	 */
	private void startNewGame() {
		// Reset game statistics
		moves = 0;
		timeElapsed = 0;
		score = 0;
		
		// Update UI
		updateMovesLabel();
		updateTimeLabel();
		updateScoreLabel();
		
		// Reset and start the maze
		mazePane.refresh(mazePane.getCellsMaze());
		repaint();
		
		// Start the timer
		if (!gameTimer.isRunning()) {
			gameTimer.start();
		}
		
		// Play background music
		if (soundEnabled) {
			playBackgroundMusic();
		}
	}
	
	/**
	 * Restart the current level
	 */
	private void restartLevel() {
		// Reset player position but keep the same maze
		mazePane.resetPlayerPosition();
		
		// Reset move count but keep the timer running
		moves = 0;
		updateMovesLabel();
		
		// Update UI
		repaint();
		
		// Play sound effect
		if (soundEnabled) {
			playSound("restart");
		}
	}
	
	/**
	 * Set the difficulty level
	 */
	private void setDifficulty(int size) {
		// Update the maze size
		mazePane.refresh(size);
		
		// Reset game statistics
		currentLevel = 1;
		moves = 0;
		timeElapsed = 0;
		score = 0;
		
		// Update UI
		updateLevelLabel();
		updateMovesLabel();
		updateTimeLabel();
		updateScoreLabel();
		
		// Restart the timer
		if (gameTimer.isRunning()) {
			gameTimer.stop();
		}
		gameTimer.start();
		
		// Update UI
		repaint();
	}
	
	/**
	 * Update the score
	 */
	public void updateScore(int points) {
		score += points;
		updateScoreLabel();
	}
	
	/**
	 * Update the move counter
	 */
	public void incrementMoves() {
		moves++;
		updateMovesLabel();
		
		// Award points for each move (negative to discourage excessive moves)
		updateScore(-1);
	}
	
	/**
	 * Update the level label
	 */
	private void updateLevelLabel() {
		levelLabel.setText("Level: " + currentLevel);
	}
	
	/**
	 * Update the score label
	 */
	private void updateScoreLabel() {
		scoreLabel.setText("Score: " + score);
	}
	
	/**
	 * Update the moves label
	 */
	private void updateMovesLabel() {
		movesLabel.setText("Moves: " + moves);
	}
	
	/**
	 * Update the time label
	 */
	private void updateTimeLabel() {
		int minutes = timeElapsed / 60;
		int seconds = timeElapsed % 60;
		timeLabel.setText(String.format("Time: %d:%02d", minutes, seconds));
	}
	
	/**
	 * Save the current game state
	 */
	private void saveGame() {
		// In a real implementation, this would save to a file
		showCustomMessageDialog(
			"Game saved successfully!", 
			"Save Game", 
			JOptionPane.INFORMATION_MESSAGE);
		
		if (soundEnabled) {
			playSound("save");
		}
	}
	
	/**
	 * Load a saved game
	 */
	private void loadGame() {
		// In a real implementation, this would load from a file
		showCustomMessageDialog(
			"Game loaded successfully!", 
			"Load Game", 
			JOptionPane.INFORMATION_MESSAGE);
		
		if (soundEnabled) {
			playSound("load");
		}
	}
	
	/**
	 * Show the about dialog
	 */
	private void showAboutDialog() {
		JDialog aboutDialog = new JDialog(this, "About Maze Runner", true);
		aboutDialog.setSize(400, 300);
		aboutDialog.setLocationRelativeTo(this);
		
		JPanel aboutPanel = new JPanel(new BorderLayout(10, 10));
		aboutPanel.setBackground(new Color(50, 60, 80));
		aboutPanel.setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20));
		
		JLabel titleLabel = new JLabel("Maze Runner 3D");
		titleLabel.setFont(new Font("SansSerif", Font.BOLD, 24));
		titleLabel.setForeground(Color.WHITE);
		titleLabel.setHorizontalAlignment(SwingConstants.CENTER);
		
		JLabel versionLabel = new JLabel("Version 2.0");
		versionLabel.setFont(new Font("SansSerif", Font.PLAIN, 14));
		versionLabel.setForeground(Color.WHITE);
		versionLabel.setHorizontalAlignment(SwingConstants.CENTER);
		
		JPanel headerPanel = new JPanel(new BorderLayout());
		headerPanel.setOpaque(false);
		headerPanel.add(titleLabel, BorderLayout.CENTER);
		headerPanel.add(versionLabel, BorderLayout.SOUTH);
		
		JLabel descLabel = new JLabel("<html><div style='text-align: center;'>A challenging maze game with beautiful graphics and multiple difficulty levels.<br><br>" +
			"Navigate through the maze using arrow keys or WASD.<br>" +
			"Collect items, avoid traps, and find the exit!<br><br>" +
			"Created by Maze Runner Team</div></html>");
		descLabel.setFont(new Font("SansSerif", Font.PLAIN, 14));
		descLabel.setForeground(Color.WHITE);
		descLabel.setHorizontalAlignment(SwingConstants.CENTER);
		
		aboutPanel.add(headerPanel, BorderLayout.NORTH);
		aboutPanel.add(descLabel, BorderLayout.CENTER);
		
		aboutDialog.add(aboutPanel);
		aboutDialog.setVisible(true);
	}
	
	/**
	 * Show the controls dialog
	 */
	private void showControlsDialog() {
		JDialog controlsDialog = new JDialog(this, "Game Controls", true);
		controlsDialog.setSize(400, 300);
		controlsDialog.setLocationRelativeTo(this);
		
		JPanel controlsPanel = new JPanel(new BorderLayout(10, 10));
		controlsPanel.setBackground(new Color(50, 60, 80));
		controlsPanel.setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20));
		
		JLabel titleLabel = new JLabel("Game Controls");
		titleLabel.setFont(new Font("SansSerif", Font.BOLD, 20));
		titleLabel.setForeground(Color.WHITE);
		titleLabel.setHorizontalAlignment(SwingConstants.CENTER);
		
		JLabel controlsLabel = new JLabel("<html><div style='text-align: left;'>" +
			"<b>Movement:</b><br>" +
			"• Arrow Keys or WASD - Move the player<br><br>" +
			"<b>Game Controls:</b><br>" +
			"• H - Show hint<br>" +
			"• Ctrl+N - New game<br>" +
			"• Ctrl+R - Restart level<br>" +
			"• Ctrl+S - Save game<br>" +
			"• Ctrl+L - Load game<br>" +
			"• Ctrl+A - Auto solve<br>" +
			"• Ctrl+D - Show algorithm<br>" +
			"• Ctrl+Q - Exit game<br>" +
			"</div></html>");
		controlsLabel.setFont(new Font("SansSerif", Font.PLAIN, 14));
		controlsLabel.setForeground(Color.WHITE);
		
		controlsPanel.add(titleLabel, BorderLayout.NORTH);
		controlsPanel.add(controlsLabel, BorderLayout.CENTER);
		
		controlsDialog.add(controlsPanel);
		controlsDialog.setVisible(true);
	}
	
	/**
	 * Show the high scores dialog
	 */
	private void showHighScores() {
		JDialog scoresDialog = new JDialog(this, "High Scores", true);
		scoresDialog.setSize(400, 300);
		scoresDialog.setLocationRelativeTo(this);
		
		JPanel scoresPanel = new JPanel(new BorderLayout(10, 10));
		scoresPanel.setBackground(new Color(50, 60, 80));
		scoresPanel.setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20));
		
		JLabel titleLabel = new JLabel("High Scores");
		titleLabel.setFont(new Font("SansSerif", Font.BOLD, 20));
		titleLabel.setForeground(Color.WHITE);
		titleLabel.setHorizontalAlignment(SwingConstants.CENTER);
		
		// In a real implementation, this would load from a file
		JLabel scoresLabel = new JLabel("<html><div style='text-align: center;'>" +
			"1. Player1 - 1000 points<br>" +
			"2. Player2 - 850 points<br>" +
			"3. Player3 - 720 points<br>" +
			"4. Player4 - 650 points<br>" +
			"5. Player5 - 500 points<br>" +
			"</div></html>");
		scoresLabel.setFont(new Font("SansSerif", Font.PLAIN, 16));
		scoresLabel.setForeground(Color.WHITE);
		scoresLabel.setHorizontalAlignment(SwingConstants.CENTER);
		
		scoresPanel.add(titleLabel, BorderLayout.NORTH);
		scoresPanel.add(scoresLabel, BorderLayout.CENTER);
		
		scoresDialog.add(scoresPanel);
		scoresDialog.setVisible(true);
	}
	
	/**
	 * Show the animation speed settings dialog
	 */
	private void showSpeedDialog() {
		JDialog speedDialog = new JDialog(this, "Animation Speed", true);
		speedDialog.setSize(300, 150);
		speedDialog.setLocationRelativeTo(this);
		
		JPanel speedPanel = new JPanel(new BorderLayout(10, 10));
		speedPanel.setBackground(new Color(50, 60, 80));
		speedPanel.setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20));
		
		JLabel titleLabel = new JLabel("Adjust Animation Speed");
		titleLabel.setFont(new Font("SansSerif", Font.BOLD, 16));
		titleLabel.setForeground(Color.WHITE);
		titleLabel.setHorizontalAlignment(SwingConstants.CENTER);
		
		JSlider speedSlider = new JSlider(JSlider.HORIZONTAL, 10, 500, mazePane.getDelay());
		speedSlider.setBackground(new Color(50, 60, 80));
		speedSlider.setForeground(Color.WHITE);
		speedSlider.setMajorTickSpacing(100);
		speedSlider.setMinorTickSpacing(25);
		speedSlider.setPaintTicks(true);
		speedSlider.setPaintLabels(true);
		speedSlider.addChangeListener(e -> {
			mazePane.setDelay(speedSlider.getValue());
		});
		
		speedPanel.add(titleLabel, BorderLayout.NORTH);
		speedPanel.add(speedSlider, BorderLayout.CENTER);
		
		speedDialog.add(speedPanel);
		speedDialog.setVisible(true);
	}
	
	/**
	 * Play a sound effect
	 */
	private void playSound(String soundName) {
		// In a real implementation, this would play actual sound files
		System.out.println("Playing sound: " + soundName);
	}
	
	/**
	 * Play background music
	 */
	private void playBackgroundMusic() {
		// In a real implementation, this would play actual music
		System.out.println("Playing background music");
	}
	
	/**
	 * Stop background music
	 */
	private void stopBackgroundMusic() {
		// In a real implementation, this would stop the music
		System.out.println("Stopping background music");
	}
	
	/**
	 * Apply consistent styling to menu items
	 */
	private void styleMenuItem(JMenuItem item) {
		item.setForeground(Color.WHITE);
		item.setBackground(new Color(60, 70, 90));
		item.setOpaque(true);
		
		// Set selection colors
		item.getModel().addChangeListener(e -> {
			if (item.getModel().isArmed() || item.getModel().isPressed() || item.getModel().isSelected()) {
				item.setBackground(new Color(80, 100, 140));
			} else {
				item.setBackground(new Color(60, 70, 90));
			}
		});
	}
	
	/**
	 * Show a custom styled message dialog with visible text
	 * This method is public so it can be called from MazePanel
	 */
	public void showCustomMessageDialog(String message, String title, int messageType) {
		// Create a custom dialog
		JDialog dialog = new JDialog(this, title, true);
		dialog.setSize(400, 200);
		dialog.setLocationRelativeTo(this);
		
		// Create the panel with dark background
		JPanel panel = new JPanel(new BorderLayout(10, 10));
		panel.setBackground(new Color(50, 60, 80));
		panel.setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20));
		
		// Create the message label with white text
		JLabel messageLabel = new JLabel(message);
		messageLabel.setFont(new Font("SansSerif", Font.PLAIN, 14));
		messageLabel.setForeground(Color.WHITE);
		messageLabel.setHorizontalAlignment(SwingConstants.CENTER);
		
		// Create OK button with custom styling
		JButton okButton = new JButton("OK");
		okButton.setBackground(new Color(80, 100, 140));
		okButton.setForeground(Color.WHITE);
		okButton.addActionListener(e -> dialog.dispose());
		
		// Add components to panel
		panel.add(messageLabel, BorderLayout.CENTER);
		
		JPanel buttonPanel = new JPanel();
		buttonPanel.setOpaque(false);
		buttonPanel.add(okButton);
		panel.add(buttonPanel, BorderLayout.SOUTH);
		
		// Add panel to dialog
		dialog.add(panel);
		dialog.setVisible(true);
	}
	
	/**
	 * Handle level selection
	 */
	class LevelsAction extends AbstractAction{
		private static final long serialVersionUID = 1L;

		@Override
		public void actionPerformed(ActionEvent e) {
			int levelNum = Integer.valueOf(e.getActionCommand());
			currentLevel = levelNum;
			
			// Update the maze size based on level (more complex for higher levels)
			int size = 11 + (levelNum - 1) * 2;
			mazePane.refresh(size);
			
			// Reset game statistics
			moves = 0;
			timeElapsed = 0;
			score = 0;
			
			// Update UI
			updateLevelLabel();
			updateMovesLabel();
			updateTimeLabel();
			updateScoreLabel();
			
			// Restart the timer
			if (gameTimer.isRunning()) {
				gameTimer.stop();
			}
			gameTimer.start();
			
			repaint();
		}
	}
}
