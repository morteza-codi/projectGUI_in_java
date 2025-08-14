import java.util.List;
import java.util.concurrent.CopyOnWriteArrayList;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

/**
 * کلاس مدیریت نخ‌های بازی
 */
public class ThreadManager {
    private ExecutorService ballThreadPool;
    private ScheduledExecutorService scheduledExecutor;
    
    public ThreadManager() {
        ballThreadPool = Executors.newFixedThreadPool(GameConfig.getMaxBalls());
        scheduledExecutor = Executors.newScheduledThreadPool(5);
    }
    
    /**
     * راه‌اندازی زمان‌بندی‌های بازی
     */
    public void setupGameSchedulers(CopyOnWriteArrayList<Ball> balls, 
                                   GameStateManager gameStateManager,
                                   BallManager ballManager) {
        
        // تنظیم زمان‌بندی ایجاد توپ‌های جدید
        scheduledExecutor.scheduleAtFixedRate(() -> {
            if (balls.size() < GameConfig.getMaxBalls() && 
                gameStateManager.isGameRunning() && !gameStateManager.isGamePaused()) {
                ballManager.createNewBall();
            }
        }, GameConfig.getBallSpawnRate(), 
           GameConfig.getBallSpawnRate() - gameStateManager.getBallSpawnAcceleration(), 
           TimeUnit.SECONDS);
        
        // تنظیم زمان‌بندی برای افزایش سختی بازی بر اساس زمان
        scheduledExecutor.scheduleAtFixedRate(() -> {
            if (gameStateManager.isGameRunning() && !gameStateManager.isGamePaused()) {
                gameStateManager.increaseDifficulty();
            }
        }, 60, 60, TimeUnit.SECONDS);
        
        // تنظیم زمان‌بندی محاسبه FPS
        scheduledExecutor.scheduleAtFixedRate(() -> {
            gameStateManager.updateSecondStats();
        }, 0, 1, TimeUnit.SECONDS);
    }
    
    /**
     * اجرای توپ در استخر نخ‌ها
     */
    public void executeBall(Ball ball) {
        ballThreadPool.execute(ball);
    }
    
    /**
     * شروع نخ قدرت
     */
    public void startPowerUpThread(PowerUp powerUp) {
        Thread powerUpThread = new Thread(powerUp);
        powerUpThread.setDaemon(true);
        powerUpThread.start();
    }
    
    /**
     * بستن همه نخ‌ها
     */
    public void shutdownAllThreads(CopyOnWriteArrayList<Ball> balls, List<PowerUp> powerUps, 
                                  EnemyManager enemyManager) {
        // غیرفعال کردن نخ‌های توپ‌ها
        for (Ball ball : balls) {
            ball.deactivate();
        }
        
        // بستن نخ‌های قدرت‌ها
        for (PowerUp powerUp : powerUps) {
            powerUp.shutdown();
        }
        
        // بستن مدیریت دشمنان
        enemyManager.shutdown();
        
        // بستن اجراکننده زمان‌بندی‌شده
        if (scheduledExecutor != null) {
            scheduledExecutor.shutdownNow();
            try {
                scheduledExecutor.awaitTermination(2, TimeUnit.SECONDS);
            } catch (InterruptedException e) {
                Thread.currentThread().interrupt();
            }
        }
        
        // بستن استخر نخ‌های توپ‌ها
        if (ballThreadPool != null) {
            ballThreadPool.shutdownNow();
            try {
                ballThreadPool.awaitTermination(2, TimeUnit.SECONDS);
            } catch (InterruptedException e) {
                Thread.currentThread().interrupt();
            }
        }
    }
    
    /**
     * راه‌اندازی مجدد نخ‌ها
     */
    public void restart() {
        ballThreadPool = Executors.newFixedThreadPool(GameConfig.getMaxBalls());
        scheduledExecutor = Executors.newScheduledThreadPool(5);
    }
}
