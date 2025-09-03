

import java.awt.Color;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.RenderingHints;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.awt.geom.Rectangle2D;
import java.awt.geom.RoundRectangle2D;
import java.awt.BasicStroke;
import java.awt.GradientPaint;
import java.util.Random;
import java.util.Stack;

import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.SwingUtilities;
import javax.swing.border.EmptyBorder;



class MazePanel extends JPanel implements KeyListener {
	private static final long serialVersionUID = 1L;
	private Rectangle2D[][] cells;
	private RoundRectangle2D[][] cellsRounded;
	private static final int dimension = 600;
	private int[][] array;
	private int cellsMaze;
	private float cell_size;
	private Position start;
	private Position end;
	private Position current;
	private Position initialPosition;
	private int delay = 100;
	public Maze maze;
	
	// Reference to the main frame for callbacks
	private Main mainFrame;
	
	// Theme colors
	private static final Color WALL_COLOR = new Color(70, 80, 100);
	private static final Color PATH_COLOR = new Color(240, 240, 255);
	private static final Color START_COLOR = new Color(100, 200, 150);
	private static final Color END_COLOR = new Color(220, 100, 120);
	private static final Color PLAYER_COLOR = new Color(50, 150, 250);
	private static final Color HINT_COLOR = new Color(255, 215, 0);
	private static final Color SOLUTION_COLOR = new Color(100, 200, 150, 150);
	private static final Color BACKGROUND_COLOR = new Color(50, 60, 80);
	
	// Collectible items
	private boolean[][] collectibles;
	private boolean[][] traps;

	/********************************************constructor*/
	public MazePanel(int cellsMaze) {
		addKeyListener(this);
		setBackground(BACKGROUND_COLOR);
		setBorder(new EmptyBorder(10, 10, 10, 10));
		refresh(cellsMaze);
		
		// Get reference to main frame
		SwingUtilities.invokeLater(() -> {
			mainFrame = (Main) SwingUtilities.getWindowAncestor(this);
		});
	}
	
	/**
	 * Set the main frame reference
	 */
	public void setMainFrame(Main frame) {
		this.mainFrame = frame;
	}
	
	/**
	 * Get the animation delay
	 */
	public int getDelay() {
		return delay;
	}
	
	/**
	 * Set the animation delay
	 */
	public void setDelay(int delay) {
		this.delay = delay;
	}
	
	/**
	 * Reset player position to start
	 */
	public void resetPlayerPosition() {
		current = new Position(start.getX(), start.getY());
		repaint();
	}
	/************************************************constructor*/


	public int getCellsMaze() {
		return this.cellsMaze;
	}

	public void refresh(int cellsMaze) {
		this.cellsMaze = cellsMaze;
		setSize(dimension, dimension);
		cell_size = (float) (1.0 * dimension / cellsMaze);
		
		setFocusable(true);

		// Maze Object
		maze = new Maze(cellsMaze - 2);
		array = maze.getArray();

		// Set Position
		start = current = new Position(1, 0);
		initialPosition = new Position(1, 0); // Store initial position
		end = new Position(cellsMaze - 2, cellsMaze - 1);

		// Set maze on panel
		cells = new Rectangle2D[cellsMaze][cellsMaze];
		cellsRounded = new RoundRectangle2D[cellsMaze][cellsMaze];
		float cornerRadius = cell_size * 0.15f; // Rounded corners
		float cellPadding = cell_size * 0.05f; // Small gap between cells
		
		for (int i = 0; i < cellsMaze; i++) {
			for (int j = 0; j < cellsMaze; j++) {
				cells[i][j] = new Rectangle2D.Double(j * cell_size, i * cell_size, cell_size, cell_size);
				cellsRounded[i][j] = new RoundRectangle2D.Double(
					j * cell_size + cellPadding, 
					i * cell_size + cellPadding, 
					cell_size - 2 * cellPadding, 
					cell_size - 2 * cellPadding, 
					cornerRadius, cornerRadius);
			}
		}
		
		// Initialize collectibles and traps
		collectibles = new boolean[cellsMaze][cellsMaze];
		traps = new boolean[cellsMaze][cellsMaze];
		Random random = new Random();
		
		// Add collectibles and traps randomly in path cells
		for (int i = 1; i < cellsMaze - 1; i++) {
			for (int j = 1; j < cellsMaze - 1; j++) {
				// Only place items in path cells
				if (array[i][j] == 0) {
					// Skip start and end positions
					if ((i == start.getX() && j == start.getY()) || 
						(i == end.getX() && j == end.getY())) {
						continue;
					}
					
					// 10% chance for collectible
					if (random.nextInt(100) < 10) {
						collectibles[i][j] = true;
					}
					
					// 5% chance for trap
					if (random.nextInt(100) < 5) {
						traps[i][j] = true;
					}
				}
			}
		}
		
		// Request focus to capture key events
		requestFocusInWindow();
	}


	private void drawMaze(Graphics2D g2d) {
		// Enable antialiasing for smoother rendering
		g2d.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
		g2d.setRenderingHint(RenderingHints.KEY_RENDERING, RenderingHints.VALUE_RENDER_QUALITY);
		
		// Draw background
		g2d.setColor(BACKGROUND_COLOR);
		g2d.fillRect(0, 0, getWidth(), getHeight());
		
		// Draw maze cells
		for (int i = 0; i < cellsMaze; i++) {
			for (int j = 0; j < cellsMaze; j++) {
				if (array[i][j] == 0) {
					// Path cells
					g2d.setColor(PATH_COLOR);
					g2d.fill(cellsRounded[i][j]);
					
					// Draw collectibles (coins)
					if (collectibles[i][j]) {
						float coinSize = cell_size * 0.4f;
						float coinX = j * cell_size + (cell_size - coinSize) / 2;
						float coinY = i * cell_size + (cell_size - coinSize) / 2;
						
						// Gold coin with gradient
						GradientPaint coinGradient = new GradientPaint(
							coinX, coinY,
							new Color(255, 215, 0), // Gold
							coinX + coinSize, coinY + coinSize,
							new Color(255, 165, 0) // Orange
						);
						g2d.setPaint(coinGradient);
						g2d.fillOval((int)coinX, (int)coinY, (int)coinSize, (int)coinSize);
						
						// Highlight
						g2d.setColor(new Color(255, 255, 200));
						g2d.fillOval((int)(coinX + coinSize * 0.2), (int)(coinY + coinSize * 0.2), 
							(int)(coinSize * 0.3), (int)(coinSize * 0.3));
					}
					
					// Draw traps
					if (traps[i][j]) {
						float trapSize = cell_size * 0.7f;
						float trapX = j * cell_size + (cell_size - trapSize) / 2;
						float trapY = i * cell_size + (cell_size - trapSize) / 2;
						
						// Red trap with gradient
						GradientPaint trapGradient = new GradientPaint(
							trapX, trapY,
							new Color(200, 0, 0, 180), // Semi-transparent red
							trapX + trapSize, trapY + trapSize,
							new Color(150, 0, 0, 180) // Darker red
						);
						g2d.setPaint(trapGradient);
						
						// Draw trap as a danger symbol (X)
						g2d.setStroke(new BasicStroke(cell_size * 0.1f, BasicStroke.CAP_ROUND, BasicStroke.JOIN_ROUND));
						g2d.drawLine((int)trapX, (int)trapY, (int)(trapX + trapSize), (int)(trapY + trapSize));
						g2d.drawLine((int)(trapX + trapSize), (int)trapY, (int)trapX, (int)(trapY + trapSize));
					}
				} else {
					// Wall cells
					g2d.setColor(WALL_COLOR);
					g2d.fill(cellsRounded[i][j]);
					
					// Add subtle 3D effect to walls
					g2d.setColor(new Color(50, 60, 70));
					g2d.setStroke(new BasicStroke(1.0f));
					g2d.draw(cellsRounded[i][j]);
				}
			}
		}
		
		// Draw end position with gradient
		int endX = end.getX();
		int endY = end.getY();
		GradientPaint endGradient = new GradientPaint(
			(float)(endY * cell_size), (float)(endX * cell_size),
			END_COLOR,
			(float)((endY + 1) * cell_size), (float)((endX + 1) * cell_size),
			END_COLOR.brighter());
		g2d.setPaint(endGradient);
		g2d.fill(cellsRounded[endX][endY]);
		
		// Draw exit sign
		float exitSize = cell_size * 0.6f;
		float exitX = endY * cell_size + (cell_size - exitSize) / 2;
		float exitY = endX * cell_size + (cell_size - exitSize) / 2;
		g2d.setColor(Color.WHITE);
		g2d.setStroke(new BasicStroke(cell_size * 0.05f));
		g2d.drawRect((int)exitX, (int)exitY, (int)exitSize, (int)exitSize);
		g2d.setColor(new Color(255, 255, 255, 180));
		g2d.drawString("EXIT", exitX + exitSize * 0.15f, exitY + exitSize * 0.6f);
		
		// Draw start position
		int startX = start.getX();
		int startY = start.getY();
		g2d.setColor(START_COLOR);
		g2d.fill(cellsRounded[startX][startY]);
		
		// Draw current position (player)
		int x = current.getX();
		int y = current.getY();
		GradientPaint playerGradient = new GradientPaint(
			(float)(y * cell_size), (float)(x * cell_size),
			PLAYER_COLOR,
			(float)((y + 1) * cell_size), (float)((x + 1) * cell_size),
			PLAYER_COLOR.brighter());
		g2d.setPaint(playerGradient);
		g2d.fill(cellsRounded[x][y]);
		
		// Draw player character (smiley face)
		float faceSize = cell_size * 0.6f;
		float faceX = y * cell_size + (cell_size - faceSize) / 2;
		float faceY = x * cell_size + (cell_size - faceSize) / 2;
		
		// Draw face
		g2d.setColor(PLAYER_COLOR.brighter());
		g2d.fillOval((int)faceX, (int)faceY, (int)faceSize, (int)faceSize);
		
		// Draw eyes
		g2d.setColor(Color.WHITE);
		float eyeSize = faceSize * 0.2f;
		g2d.fillOval((int)(faceX + faceSize * 0.25 - eyeSize/2), (int)(faceY + faceSize * 0.3), (int)eyeSize, (int)eyeSize);
		g2d.fillOval((int)(faceX + faceSize * 0.75 - eyeSize/2), (int)(faceY + faceSize * 0.3), (int)eyeSize, (int)eyeSize);
		
		// Draw smile
		g2d.setColor(Color.WHITE);
		g2d.setStroke(new BasicStroke(faceSize * 0.08f, BasicStroke.CAP_ROUND, BasicStroke.JOIN_ROUND));
		g2d.drawArc((int)(faceX + faceSize * 0.2), (int)(faceY + faceSize * 0.4), (int)(faceSize * 0.6), (int)(faceSize * 0.4), 0, -180);
	}

	public void autoMove(Graphics2D g2d) {
		Stack<Position> way = maze.getDirectWay(start, end);
		g2d.setColor(SOLUTION_COLOR);
		g2d.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
		
		while (!way.empty()) {
			Position next = way.pop();
			int x = next.getX();
			int y = next.getY();
			
			// Draw solution path with animation
			g2d.fill(cellsRounded[x][y]);
			
			// Draw a small circle to indicate the path
			float circleSize = cell_size * 0.3f;
			float circleX = y * cell_size + (cell_size - circleSize) / 2;
			float circleY = x * cell_size + (cell_size - circleSize) / 2;
			g2d.setColor(SOLUTION_COLOR.brighter());
			g2d.fillOval((int)circleX, (int)circleY, (int)circleSize, (int)circleSize);
			
			try {
				Thread.sleep(delay);
			} catch (InterruptedException e) {
				e.printStackTrace();
			}
		}
		
		// Redraw the player position
		drawMaze(g2d);
	}

	public void algorithm(Graphics2D g2d) {
		Stack<Position> way = maze.getWay(start, end);
		g2d.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
		
		Color explorationColor = new Color(255, 180, 100, 180); // Semi-transparent orange
		
		while (!way.empty()) {
			Position next = way.pop();
			int x = next.getX();
			int y = next.getY();
			
			// Draw exploration path
			g2d.setColor(explorationColor);
			g2d.fill(cellsRounded[x][y]);
			
			// Draw a small dot to show the exploration path
			float dotSize = cell_size * 0.2f;
			float dotX = y * cell_size + (cell_size - dotSize) / 2;
			float dotY = x * cell_size + (cell_size - dotSize) / 2;
			g2d.setColor(explorationColor.brighter());
			g2d.fillOval((int)dotX, (int)dotY, (int)dotSize, (int)dotSize);
			
			try {
				Thread.sleep(delay);
			} catch (InterruptedException e) {
				e.printStackTrace();
			}
		}
		
		// Redraw the player position
		drawMaze(g2d);
	}
	
	private boolean passed() {
		return current.equals(end);
	}

	private void move(Graphics2D g2d, Position current) {
		// Check if player stepped on a collectible
		int x = current.getX();
		int y = current.getY();
		
		// Update move counter in main frame if available
		if (mainFrame != null) {
			mainFrame.incrementMoves();
		}
		
		// Check for collectible
		if (collectibles[x][y]) {
			// Collect the item
			collectibles[x][y] = false;
			
			// Award points
			if (mainFrame != null) {
				mainFrame.updateScore(20);
			}
			
			// Play sound effect (would be implemented in a real game)
			System.out.println("Collected item!");
		}
		
		// Check for trap
		if (traps[x][y]) {
			// Trigger the trap
			traps[x][y] = false;
			
			// Deduct points
			if (mainFrame != null) {
				mainFrame.updateScore(-30);
			}
			
			// Play sound effect (would be implemented in a real game)
			System.out.println("Trap activated!");
			
			// Show trap message with custom dialog
			if (mainFrame != null) {
				mainFrame.showCustomMessageDialog(
					"Ouch! You stepped on a trap!",
					"Trap Activated",
					JOptionPane.WARNING_MESSAGE);
			} else {
				// Fallback if mainFrame is not available
				JOptionPane.showMessageDialog(
					getParent(),
					"Ouch! You stepped on a trap!",
					"Trap Activated",
					JOptionPane.WARNING_MESSAGE);
			}
		}
		
		// Redraw the entire maze to update the player position
		repaint();
		
		// Check win
		if(passed()) {
			// Calculate score bonus based on maze size and moves
			int levelBonus = cellsMaze * 10;
			
			// Award bonus points for completing the level
			if (mainFrame != null) {
				mainFrame.updateScore(levelBonus);
			}
			
			// Play win sound effect (would be implemented in a real game)
			System.out.println("Level complete!");
			
			// Create a custom styled dialog
			if (mainFrame != null) {
				mainFrame.showCustomMessageDialog(
					"Congratulations! You've completed the maze!\nBonus points: " + levelBonus, 
					"Level Complete", 
					JOptionPane.INFORMATION_MESSAGE);
			} else {
				// Fallback if mainFrame is not available
				JOptionPane.showMessageDialog(
					getParent(),
					"Congratulations! You've completed the maze!\nBonus points: " + levelBonus,
					"Level Complete",
					JOptionPane.INFORMATION_MESSAGE);
			}
				
			// Increase the maze size for the next level
			int newSize = cellsMaze + 2;
			if (newSize > 51) newSize = 51; // Cap at maximum size
			
			refresh(newSize);
			repaint();
			
			// Update level in main frame (would update UI in a real implementation)
			System.out.println("Advancing to next level");
		}
	}
	
	// Show a hint for the next move
	public void hint() {
		Stack<Position> way = maze.getDirectWay(current, end);
		// Skip current position
		way.pop();
		
		if (!way.empty()) {
			Position next = way.pop();
			int x = next.getX();
			int y = next.getY();
			
			Graphics2D g2d = (Graphics2D) getGraphics();
			g2d.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
			
			// Draw hint with pulsating effect
			g2d.setColor(HINT_COLOR);
			g2d.fill(cellsRounded[x][y]);
			
			// Draw arrow indicating direction
			float arrowSize = cell_size * 0.4f;
			float arrowX = y * cell_size + (cell_size - arrowSize) / 2;
			float arrowY = x * cell_size + (cell_size - arrowSize) / 2;
			
			g2d.setColor(HINT_COLOR.darker());
			g2d.fillOval((int)arrowX, (int)arrowY, (int)arrowSize, (int)arrowSize);
		}
	}

	@Override
	protected void paintComponent(Graphics g) {
		super.paintComponent(g);
		drawMaze((Graphics2D) g);
	}
	
	public void keyTyped(KeyEvent e) {
	}

	@Override
	public void keyPressed(KeyEvent e) {
		if (e.getKeyCode() == KeyEvent.VK_W) {
			if (maze.canMoveUp(current)) {
				current.setX(current.getX() - 1);
				move((Graphics2D) getGraphics(), current);
			}
		}
		if (e.getKeyCode() == KeyEvent.VK_D) {
			if (maze.canMoveRight(current)) {
				current.setY(current.getY() + 1);
				move((Graphics2D) getGraphics(), current);
			}
		}
		if (e.getKeyCode() == KeyEvent.VK_S) {
			if (maze.canMoveDown(current)) {
				current.setX(current.getX() + 1);
				move((Graphics2D) getGraphics(), current);
			}
		}
		if (e.getKeyCode() == KeyEvent.VK_A) {
			if (maze.canMoveLeft(current)) {
				current.setY(current.getY() - 1);
				move((Graphics2D) getGraphics(), current);
			}
		}
		if (e.getKeyCode() == KeyEvent.VK_UP) {
			if (maze.canMoveUp(current)) {
				current.setX(current.getX() - 1);
				move((Graphics2D) getGraphics(), current);
			}		
		}
		if (e.getKeyCode() == KeyEvent.VK_RIGHT) {
			if (maze.canMoveRight(current)) {
				current.setY(current.getY() + 1);
				move((Graphics2D) getGraphics(), current);
			}
		}
		if (e.getKeyCode() == KeyEvent.VK_DOWN) {
			if (maze.canMoveDown(current)) {
				current.setX(current.getX() + 1);
				move((Graphics2D) getGraphics(), current);
			}
		}
		if (e.getKeyCode() == KeyEvent.VK_LEFT) {
			if (maze.canMoveLeft(current)) {
				current.setY(current.getY() - 1);
				move((Graphics2D) getGraphics(), current);
			}
		}
		if (e.getKeyCode() == KeyEvent.VK_H) {
			hint();
		}
	}

	@Override
	public void keyReleased(KeyEvent e) {
	}
}