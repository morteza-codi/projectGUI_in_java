/**
 * کلاس مدیریت اثرات قدرت‌ها
 */
public class PowerUpManager {
    // اثرات قدرت‌ها
    private volatile boolean playerInvincible = false;
    private volatile boolean timeSlowEffect = false;
    private volatile int scoreMultiplier = 1;
    private volatile long powerUpEndTime = 0;
    private PowerUp.PowerUpType activePowerUpType = null;
    
    /**
     * فعال کردن قدرت شکست‌ناپذیری
     */
    public void activateInvincibility(long duration) {
        playerInvincible = true;
        powerUpEndTime = System.currentTimeMillis() + duration;
        activePowerUpType = PowerUp.PowerUpType.INVINCIBILITY;
    }
    
    /**
     * فعال کردن اثر کند شدن زمان
     */
    public void activateTimeSlowEffect(long duration) {
        timeSlowEffect = true;
        powerUpEndTime = System.currentTimeMillis() + duration;
        activePowerUpType = PowerUp.PowerUpType.TIME_SLOW;
    }
    
    /**
     * فعال کردن ضریب امتیاز
     */
    public void activateScoreMultiplier(int multiplier, long duration) {
        scoreMultiplier = multiplier;
        powerUpEndTime = System.currentTimeMillis() + duration;
        activePowerUpType = PowerUp.PowerUpType.SCORE_MULTIPLIER;
    }
    
    /**
     * بررسی و بازنشانی اثرات منقضی شده
     */
    public void updateEffects() {
        if (System.currentTimeMillis() > powerUpEndTime) {
            resetAllEffects();
        }
    }
    
    /**
     * بازنشانی همه اثرات قدرت‌ها
     */
    public void resetAllEffects() {
        playerInvincible = false;
        timeSlowEffect = false;
        scoreMultiplier = 1;
        activePowerUpType = null;
        powerUpEndTime = 0;
    }
    
    /**
     * تنظیم زمان پایان اثر قدرت
     */
    public void setPowerUpEndTime(long endTime, PowerUp.PowerUpType type) {
        this.powerUpEndTime = endTime;
        this.activePowerUpType = type;
        
        switch (type) {
            case INVINCIBILITY:
                this.playerInvincible = true;
                break;
            case TIME_SLOW:
                this.timeSlowEffect = true;
                break;
            case SCORE_MULTIPLIER:
                this.scoreMultiplier = GameConfig.getScoreMultiplier();
                break;
        }
    }
    
    /**
     * محاسبه امتیاز با ضریب
     */
    public int calculateScore(int baseScore) {
        return baseScore * scoreMultiplier;
    }
    
    /**
     * دریافت زمان باقی‌مانده اثر
     */
    public long getRemainingTime() {
        return Math.max(0, powerUpEndTime - System.currentTimeMillis());
    }
    
    // Getters
    public boolean isPlayerInvincible() { return playerInvincible; }
    public boolean isTimeSlowEffect() { return timeSlowEffect; }
    public int getScoreMultiplier() { return scoreMultiplier; }
    public long getPowerUpEndTime() { return powerUpEndTime; }
    public PowerUp.PowerUpType getActivePowerUpType() { return activePowerUpType; }
    
    // Setters
    public void setPlayerInvincible(boolean invincible) { this.playerInvincible = invincible; }
    public void setTimeSlowEffect(boolean active) { this.timeSlowEffect = active; }
    public void setScoreMultiplier(int multiplier) { this.scoreMultiplier = multiplier; }
}
