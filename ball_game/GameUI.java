import javax.swing.*;
import java.awt.*;
import java.awt.event.KeyEvent;

public class GameUI {
    private Game game;
    private Font titleFont = new Font("Arial", Font.BOLD, 48);
    private Font menuFont = new Font("Arial", Font.BOLD, 24);
    private Image background;

    public GameUI(Game game) {
        this.game = game;
        loadResources();
    }

    private void loadResources() {
        try {
            background = new ImageIcon(getClass().getResource("/sounds/background.jpg")).getImage();
        } catch (Exception e) {
            background = null;
        }
    }

    public void drawMainMenu(Graphics g) {
        if (background != null) {
            g.drawImage(background, 0, 0, null);
        } else {
            g.setColor(Color.BLACK);
            g.fillRect(0, 0, GameConfig.WIDTH, GameConfig.HEIGHT);
        }

        g.setFont(titleFont);
        g.setColor(Color.WHITE);
        drawCenteredString(g, "Enhanced Ball Game", GameConfig.WIDTH / 2, 100);

        g.setFont(menuFont);
        String[] menuItems = { "Start Game", "Options", "Exit" };
        for (int i = 0; i < menuItems.length; i++) {
            if (i == game.getSelectedMenuIndex()) {
                g.setColor(Color.YELLOW);
            } else {
                g.setColor(Color.WHITE);
            }
            drawCenteredString(g, menuItems[i], GameConfig.WIDTH / 2, 250 + i * 50);
        }
    }

    public void drawPauseMenu(Graphics g) {
        g.setColor(new Color(0, 0, 0, 150));
        g.fillRect(0, 0, GameConfig.WIDTH, GameConfig.HEIGHT);
        g.setFont(titleFont);
        g.setColor(Color.WHITE);
        drawCenteredString(g, "Paused", GameConfig.WIDTH / 2, 100);

        g.setFont(menuFont);
        String[] menuItems = { "Resume", "Main Menu", "Exit" };
        for (int i = 0; i < menuItems.length; i++) {
            if (i == game.getSelectedMenuIndex()) {
                g.setColor(Color.YELLOW);
            } else {
                g.setColor(Color.WHITE);
            }
            drawCenteredString(g, menuItems[i], GameConfig.WIDTH / 2, 250 + i * 50);
        }
    }

    public void drawGameOverScreen(Graphics g) {
        g.setColor(new Color(0, 0, 0, 150));
        g.fillRect(0, 0, GameConfig.WIDTH, GameConfig.HEIGHT);
        g.setFont(titleFont);
        g.setColor(Color.RED);
        drawCenteredString(g, "Game Over", GameConfig.WIDTH / 2, 100);

        g.setFont(menuFont);
        g.setColor(Color.WHITE);
        drawCenteredString(g, "Final Score: " + game.getScore(), GameConfig.WIDTH / 2, 200);

        String[] menuItems = { "Play Again", "Main Menu", "Exit" };
        for (int i = 0; i < menuItems.length; i++) {
            if (i == game.getSelectedMenuIndex()) {
                g.setColor(Color.YELLOW);
            } else {
                g.setColor(Color.WHITE);
            }
            drawCenteredString(g, menuItems[i], GameConfig.WIDTH / 2, 300 + i * 50);
        }
    }

    private void drawCenteredString(Graphics g, String text, int x, int y) {
        FontMetrics metrics = g.getFontMetrics(g.getFont());
        int newX = x - metrics.stringWidth(text) / 2;
        g.drawString(text, newX, y);
    }

    public void handleMainMenuInput(int keyCode) {
        if (keyCode == KeyEvent.VK_UP) {
            game.setSelectedMenuIndex((game.getSelectedMenuIndex() - 1 + 3) % 3);
        } else if (keyCode == KeyEvent.VK_DOWN) {
            game.setSelectedMenuIndex((game.getSelectedMenuIndex() + 1) % 3);
        } else if (keyCode == KeyEvent.VK_ENTER) {
            if (game.getSelectedMenuIndex() == 0) {
                game.startGame();
            } else if (game.getSelectedMenuIndex() == 1) {
                // Options
            } else if (game.getSelectedMenuIndex() == 2) {
                System.exit(0);
            }
        }
    }

    public void handlePauseMenuInput(int keyCode) {
        if (keyCode == KeyEvent.VK_UP) {
            game.setSelectedMenuIndex((game.getSelectedMenuIndex() - 1 + 3) % 3);
        } else if (keyCode == KeyEvent.VK_DOWN) {
            game.setSelectedMenuIndex((game.getSelectedMenuIndex() + 1) % 3);
        } else if (keyCode == KeyEvent.VK_ENTER) {
            if (game.getSelectedMenuIndex() == 0) {
                game.resumeGame();
            } else if (game.getSelectedMenuIndex() == 1) {
                game.goToMainMenu();
            } else if (game.getSelectedMenuIndex() == 2) {
                System.exit(0);
            }
        }
    }

    public void handleGameOverInput(int keyCode) {
        if (keyCode == KeyEvent.VK_UP) {
            game.setSelectedMenuIndex((game.getSelectedMenuIndex() - 1 + 3) % 3);
        } else if (keyCode == KeyEvent.VK_DOWN) {
            game.setSelectedMenuIndex((game.getSelectedMenuIndex() + 1) % 3);
        } else if (keyCode == KeyEvent.VK_ENTER) {
            if (game.getSelectedMenuIndex() == 0) {
                game.restartGame();
            } else if (game.getSelectedMenuIndex() == 1) {
                game.goToMainMenu();
            } else if (game.getSelectedMenuIndex() == 2) {
                System.exit(0);
            }
        }
    }
}

