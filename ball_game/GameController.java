import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.CopyOnWriteArrayList;

/**
 * کلاس کنترلر بازی برای مدیریت عملیات سطح بالا
 */
public class GameController {
    private GameStateManager gameStateManager;
    private Player player;
    private BallManager ballManager;
    private List<PowerUp> powerUps;
    private EnemyManager enemyManager;
    private ThreadManager threadManager;
    private GameMenu gameMenu;
    private PowerUpManager powerUpManager;
    
    public GameController(GameStateManager gameStateManager, Player player,
                         BallManager ballManager, List<PowerUp> powerUps,
                         EnemyManager enemyManager, ThreadManager threadManager,
                         GameMenu gameMenu, PowerUpManager powerUpManager) {
        this.gameStateManager = gameStateManager;
        this.player = player;
        this.ballManager = ballManager;
        this.powerUps = powerUps;
        this.enemyManager = enemyManager;
        this.threadManager = threadManager;
        this.gameMenu = gameMenu;
        this.powerUpManager = powerUpManager;
    }
    
    /**
     * شروع بازی جدید
     */
    public void startNewGame() {
        gameStateManager.setShowingMenu(false);
        gameStateManager.setGamePaused(false);
        SoundManager.playMusic(SoundManager.Music.GAMEPLAY, true);
    }
    
    /**
     * ادامه بازی
     */
    public void resumeGame() {
        gameStateManager.setShowingMenu(false);
        gameStateManager.setGamePaused(false);
        SoundManager.playMusic(SoundManager.Music.GAMEPLAY, true);
    }
    
    /**
     * برگشت به منوی اصلی
     */
    public void goToMainMenu() {
        gameStateManager.setShowingMenu(true);
        gameStateManager.setGamePaused(true);
        gameMenu.showMainMenu();
        SoundManager.playMusic(SoundManager.Music.MENU, true);
    }
    
    /**
     * نمایش منوی توقف
     */
    public void showPauseMenu() {
        gameStateManager.setShowingMenu(true);
        gameStateManager.setGamePaused(true);
        gameMenu.showPauseMenu();
        SoundManager.playMusic(SoundManager.Music.MENU, true);
    }
    
    /**
     * تغییر وضعیت توقف بازی
     */
    public void togglePause() {
        gameStateManager.togglePause();
        if (gameStateManager.isGamePaused()) {
            gameMenu.showPauseMenu();
            SoundManager.playMusic(SoundManager.Music.MENU, true);
        } else {
            SoundManager.playMusic(SoundManager.Music.GAMEPLAY, true);
        }
    }
    
    /**
     * راه‌اندازی مجدد بازی
     */
    public void restartGame() {
        // بازنشانی وضعیت بازی
        gameStateManager.resetGame();
        
        // بازنشانی اثرات قدرت‌ها
        powerUpManager.resetAllEffects();
        
        // پاکسازی عناصر بازی
        ballManager.clearAllBalls();
        
        // بازنشانی دشمنان
        enemyManager.shutdown();
        enemyManager = new EnemyManager(GameConfig.WIDTH, GameConfig.HEIGHT);
        
        // بازنشانی قدرت‌ها
        for (PowerUp powerUp : powerUps) {
            powerUp.shutdown();
        }
        powerUps.clear();
        
        // پاکسازی افکت‌های بصری
        EffectManager.clearEffects();
        
        // بازنشانی و اجرای مجدد نخ‌ها
        threadManager.shutdownAllThreads(ballManager.getBalls(), powerUps, enemyManager);
        threadManager.restart();
        
        // ایجاد مجدد بازیکن
        player = new Player(GameConfig.WIDTH / 2, GameConfig.HEIGHT / 2);
        
        // ایجاد توپ‌های اولیه
        ballManager.createInitialBalls();
        
        // ایجاد قدرت‌های اولیه
        createInitialPowerUps();
        
        // راه‌اندازی زمان‌بندی‌ها
        threadManager.setupGameSchedulers(ballManager.getBalls(), gameStateManager, ballManager);
        
        // پخش موسیقی بازی
        SoundManager.playMusic(SoundManager.Music.GAMEPLAY, true);
    }
    
    /**
     * ایجاد قدرت‌های اولیه
     */
    private void createInitialPowerUps() {
        for (int i = 0; i < GameConfig.getNumPowerUps(); i++) {
            PowerUp powerUp = new PowerUp();
            powerUps.add(powerUp);
            threadManager.startPowerUpThread(powerUp);
        }
    }
    
    /**
     * پاکسازی همه دشمنان (برای قدرت CLEAR_SCREEN)
     */
    public void clearEnemies() {
        enemyManager.clearEnemies();
        EffectManager.addExplosion(GameConfig.WIDTH / 2, GameConfig.HEIGHT / 2, 200, GameConfig.PLAYER_COLOR);
        SoundManager.playSound(SoundManager.SoundEffect.ENEMY_DESTROY);
    }
    
    /**
     * بستن بازی
     */
    public void shutdown() {
        threadManager.shutdownAllThreads(ballManager.getBalls(), powerUps, enemyManager);
        SoundManager.cleanup();
    }
    
    // Getters
    public Player getPlayer() { return player; }
}
