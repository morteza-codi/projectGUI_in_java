/**
 * کلاس حلقه اصلی بازی
 */
public class GameLoop {
    private boolean running = true;
    private GameStateManager gameStateManager;
    private Player player;
    private EnemyManager enemyManager;
    private CollisionManager collisionManager;
    private PowerUpManager powerUpManager;
    
    public GameLoop(GameStateManager gameStateManager, Player player, 
                   EnemyManager enemyManager, CollisionManager collisionManager,
                   PowerUpManager powerUpManager) {
        this.gameStateManager = gameStateManager;
        this.player = player;
        this.enemyManager = enemyManager;
        this.collisionManager = collisionManager;
        this.powerUpManager = powerUpManager;
    }
    
    /**
     * شروع حلقه اصلی بازی
     */
    public void startLoop(Runnable renderCallback) {
        Thread gameThread = new Thread(() -> {
            long lastUpdateTime = System.nanoTime();
            double amountOfTicks = 60.0; // تعداد به‌روزرسانی‌ها در ثانیه
            double ns = 1000000000 / amountOfTicks;
            double delta = 0;
            
            // حلقه اصلی بازی
            while (running) {
                long now = System.nanoTime();
                delta += (now - lastUpdateTime) / ns;
                lastUpdateTime = now;
                
                // به‌روزرسانی منطق بازی
                if (delta >= 1) {
                    if (!gameStateManager.isShowingMenu() && 
                        gameStateManager.isGameRunning() && 
                        !gameStateManager.isGamePaused()) {
                        updateGame();
                    }
                    delta--;
                }
                
                // رسم بازی
                renderCallback.run();
                
                // کمی استراحت برای کاهش مصرف CPU
                try {
                    Thread.sleep(2);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }
        });
        
        gameThread.start();
    }
    
    /**
     * به‌روزرسانی منطق بازی
     */
    private void updateGame() {
        gameStateManager.updateFrameStats();
        
        // به‌روزرسانی بازیکن
        player.update();
        
        // به‌روزرسانی دشمنان با سرعت متناسب با اثر کند کردن زمان
        if (powerUpManager.isTimeSlowEffect()) {
            // در حالت کند شدن زمان، دشمنان را کمتر به‌روزرسانی می‌کنیم
            if (gameStateManager.getUpdatesPerSecond() % 3 == 0) {
                enemyManager.updateEnemies(player.getX(), player.getY());
            }
        } else {
            enemyManager.updateEnemies(player.getX(), player.getY());
        }
        
        // به‌روزرسانی افکت‌ها
        EffectManager.update();
        
        // به‌روزرسانی اثرات قدرت‌ها
        powerUpManager.updateEffects();
        
        // بررسی برخوردها
        collisionManager.checkAllCollisions();
        
        // بررسی شرایط افزایش سطح
        if (gameStateManager.checkLevelUp()) {
            gameStateManager.increaseDifficulty();
        }
    }
    
    /**
     * توقف حلقه بازی
     */
    public void stop() {
        running = false;
    }
    
    /**
     * وضعیت اجرای حلقه
     */
    public boolean isRunning() {
        return running;
    }
}
