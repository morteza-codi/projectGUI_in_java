/**
 * کلاس مدیریت وضعیت بازی
 */
public class GameStateManager {
    // وضعیت بازی
    private volatile boolean gameRunning = true;
    private volatile boolean gamePaused = false;
    private volatile boolean showingMenu = true;
    private volatile int score = 0;
    private volatile int level = 1;
    private volatile int lives = 3;
    private String playerName = "Player";
    
    // پیشرفت بازی
    private int levelThreshold = 100;
    private int enemySpawnAcceleration = 0;
    private int ballSpawnAcceleration = 0;
    
    // آمار بازی
    private volatile int framesPerSecond = 0;
    private volatile int updatesPerSecond = 0;
    private volatile int frameCount = 0;
    private volatile int updateCount = 0;
    private long gameStartTime;
    private long gameTime = 0;
    private int selectedMenuIndex = 0;
    
    public GameStateManager() {
        gameStartTime = System.currentTimeMillis();
    }
    
    /**
     * بررسی شرایط افزایش سطح
     */
    public boolean checkLevelUp() {
        if (score >= level * levelThreshold) {
            level++;
            SoundManager.playSound(SoundManager.SoundEffect.POWER_UP_COLLECT);
            EffectManager.addExplosion(GameConfig.WIDTH / 2, GameConfig.HEIGHT / 2, 100, GameConfig.PLAYER_COLOR);
            return true;
        }
        return false;
    }
    
    /**
     * افزایش سختی بازی
     */
    public void increaseDifficulty() {
        if (ballSpawnAcceleration < GameConfig.getBallSpawnRate() - 1) {
            ballSpawnAcceleration++;
        }
        
        if (enemySpawnAcceleration < GameConfig.getEnemySpawnDelay() - 1) {
            enemySpawnAcceleration++;
        }
    }
    
    /**
     * بازنشانی بازی
     */
    public void resetGame() {
        score = 0;
        level = 1;
        lives = 3;
        gameRunning = true;
        gamePaused = false;
        ballSpawnAcceleration = 0;
        enemySpawnAcceleration = 0;
        gameStartTime = System.currentTimeMillis();
        showingMenu = false;
    }
    
    /**
     * کم کردن جان
     */
    public void loseLife() {
        lives--;
        if (lives <= 0) {
            gameRunning = false;
        }
    }
    
    /**
     * اضافه کردن امتیاز
     */
    public void addScore(int points) {
        score += points;
    }
    
    /**
     * به‌روزرسانی آمار فریم
     */
    public void updateFrameStats() {
        frameCount++;
        updateCount++;
    }
    
    /**
     * به‌روزرسانی آمار ثانیه‌ای
     */
    public void updateSecondStats() {
        framesPerSecond = frameCount;
        updatesPerSecond = updateCount;
        frameCount = 0;
        updateCount = 0;
        if (gameRunning && !gamePaused) {
            gameTime = (System.currentTimeMillis() - gameStartTime) / 1000;
        }
    }
    
    /**
     * تغییر وضعیت توقف
     */
    public void togglePause() {
        gamePaused = !gamePaused;
        showingMenu = gamePaused;
    }
    
    // Getters
    public boolean isGameRunning() { return gameRunning; }
    public boolean isGamePaused() { return gamePaused; }
    public boolean isShowingMenu() { return showingMenu; }
    public int getScore() { return score; }
    public int getLevel() { return level; }
    public int getLives() { return lives; }
    public String getPlayerName() { return playerName; }
    public int getEnemySpawnAcceleration() { return enemySpawnAcceleration; }
    public int getBallSpawnAcceleration() { return ballSpawnAcceleration; }
    public int getFramesPerSecond() { return framesPerSecond; }
    public int getUpdatesPerSecond() { return updatesPerSecond; }
    public long getGameTime() { return gameTime; }
    public int getSelectedMenuIndex() { return selectedMenuIndex; }
    
    // Setters
    public void setGameRunning(boolean running) { this.gameRunning = running; }
    public void setGamePaused(boolean paused) { this.gamePaused = paused; }
    public void setShowingMenu(boolean showing) { this.showingMenu = showing; }
    public void setPlayerName(String name) { this.playerName = name; }
    public void setSelectedMenuIndex(int index) { this.selectedMenuIndex = index; }
}
