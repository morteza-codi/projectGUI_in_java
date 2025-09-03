import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;

/**
 * کلاس مدیریت ورودی کلیدها
 */
public class InputHandler extends KeyAdapter {
    private GameStateManager gameStateManager;
    private GameMenu gameMenu;
    private Player player;
    private GameController gameController;
    
    public InputHandler(GameStateManager gameStateManager, GameMenu gameMenu, 
                       Player player, GameController gameController) {
        this.gameStateManager = gameStateManager;
        this.gameMenu = gameMenu;
        this.player = player;
        this.gameController = gameController;
    }
    
    @Override
    public void keyPressed(KeyEvent e) {
        int keyCode = e.getKeyCode();
        
        if (gameStateManager.isShowingMenu()) {
            handleMenuInput(keyCode);
        } else {
            handleGameInput(keyCode);
        }
    }
    
    @Override
    public void keyReleased(KeyEvent e) {
        int keyCode = e.getKeyCode();
        
        if (gameStateManager.isShowingMenu()) {
            gameMenu.handleKeyRelease();
        } else {
            player.handleKeyRelease(keyCode);
        }
    }
    
    /**
     * پردازش ورودی در حالت منو
     */
    private void handleMenuInput(int keyCode) {
        gameMenu.handleKeyPress(keyCode);
        if (gameMenu.shouldReturnToGame()) {
            gameStateManager.setShowingMenu(false);
            gameStateManager.setGamePaused(false);
            // پخش موسیقی بازی
            SoundManager.playMusic(SoundManager.Music.GAMEPLAY, true);
        }
    }
    
    /**
     * پردازش ورودی در حالت بازی
     */
    private void handleGameInput(int keyCode) {
        // کلیدهای کنترل بازی
        player.handleKeyPress(keyCode);
        
        // کلیدهای سیستمی
        switch (keyCode) {
            case KeyEvent.VK_ESCAPE:
            case KeyEvent.VK_P:
                gameController.togglePause();
                break;
            case KeyEvent.VK_R:
                if (!gameStateManager.isGameRunning()) {
                    gameController.restartGame();
                }
                break;
            case KeyEvent.VK_F:
                GameConfig.showFPS = !GameConfig.showFPS;
                break;
            case KeyEvent.VK_M:
                gameController.showPauseMenu();
                break;
        }
    }
}
