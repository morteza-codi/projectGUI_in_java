import java.awt.Color;
import java.awt.Rectangle;
import java.util.Random;

/**
 * کلاس آیتم‌های قدرت در بازی
 */
public class PowerUp implements Runnable {
    private static final Random random = new Random();
    
    // موقعیت و وضعیت
    private volatile int x;
    private volatile int y;
    private volatile boolean active;
    private volatile boolean running = true;
    private volatile PowerUpType type;
    private volatile Color color;
    private int pulseSize = 0; // اندازه پالس برای افکت پالسی
    private int pulseDirection = 1; // جهت تغییر پالس
    
    /**
     * انواع مختلف قدرت
     */
    public enum PowerUpType {
        SPEED_BOOST,      // افزایش سرعت بازیکن
        INVINCIBILITY,    // شکست‌ناپذیری
        SCORE_MULTIPLIER, // افزایش ضریب امتیاز
        SHIELD,           // سپر محافظ
        TIME_SLOW,        // کاهش سرعت دشمنان
        CLEAR_SCREEN      // حذف همه دشمنان و توپ‌ها
    }
    
    public PowerUp() {
        reset();
    }
    
    /**
     * بازنشانی آیتم قدرت با مشخصات جدید
     */
    public void reset() {
        // موقعیت تصادفی در محدوده بازی
        x = random.nextInt(GameConfig.WIDTH - GameConfig.POWER_UP_SIZE * 2) + GameConfig.POWER_UP_SIZE;
        y = random.nextInt(GameConfig.HEIGHT - GameConfig.POWER_UP_SIZE * 2) + GameConfig.POWER_UP_SIZE;
        
        // نوع قدرت تصادفی
        PowerUpType[] types = PowerUpType.values();
        int randomIndex = random.nextInt(types.length);
        type = types[randomIndex];
        
        // تنظیم رنگ بر اساس نوع
        switch (type) {
            case SPEED_BOOST:
                color = GameConfig.SPEEDBOOST_POWERUP_COLOR;
                break;
            case INVINCIBILITY:
                color = GameConfig.INVINCIBILITY_POWERUP_COLOR;
                break;
            case SCORE_MULTIPLIER:
                color = GameConfig.SCORE_POWERUP_COLOR;
                break;
            case SHIELD:
                color = new Color(0, 200, 200);
                break;
            case TIME_SLOW:
                color = new Color(0, 0, 128);
                break;
            case CLEAR_SCREEN:
                color = new Color(255, 255, 255);
                break;
        }
        
        active = true;
        pulseSize = 0;
        pulseDirection = 1;
    }
    
    @Override
    public void run() {
        while (running) {
            if (active) {
                // به‌روزرسانی افکت پالسی
                updatePulse();
                
                try {
                    // قدرت برای مدت مشخص ظاهر می‌شود
                    Thread.sleep(GameConfig.POWER_UP_LIFETIME);
                    active = false;
                    
                    // کمی صبر قبل از ظاهر شدن دوباره
                    Thread.sleep(random.nextInt(5000) + 3000);
                    if (running) {
                        reset();
                    }
                } catch (InterruptedException e) {
                    running = false;
                    break;
                }
            } else {
                try {
                    Thread.sleep(GameConfig.POWER_UP_UPDATE_DELAY);
                } catch (InterruptedException e) {
                    running = false;
                    break;
                }
            }
        }
    }
    
    /**
     * به‌روزرسانی افکت پالسی قدرت
     */
    private void updatePulse() {
        pulseSize += pulseDirection;
        
        if (pulseSize > 5 || pulseSize < 0) {
            pulseDirection *= -1;
        }
    }
    
    /**
     * محدوده برخورد قدرت
     */
    public Rectangle getBounds() {
        return new Rectangle(x, y, GameConfig.POWER_UP_SIZE, GameConfig.POWER_UP_SIZE);
    }
    
    /**
     * بررسی برخورد با مستطیل مشخص شده
     */
    public boolean checkCollision(Rectangle other) {
        return active && getBounds().intersects(other);
    }
    
    /**
     * اعمال اثر قدرت به بازیکن
     */
    public void applyTo(Player player, Game game) {
        switch (type) {
            case SPEED_BOOST:
                player.setSpeed(GameConfig.getPlayerBoostedSpeed());
                break;
            case INVINCIBILITY:
                player.setInvincible(true);
                break;
            case SCORE_MULTIPLIER:
                game.setScoreMultiplier(GameConfig.getScoreMultiplier());
                break;
            case SHIELD:
                player.activateShield(3); // سپر با 3 نقطه قدرت
                break;
            case TIME_SLOW:
                game.setTimeSlowEffect(true);
                break;
            case CLEAR_SCREEN:
                game.clearEnemies();
                break;
        }
        
        // تنظیم زمان پایان اثر قدرت
        long endTime = System.currentTimeMillis() + GameConfig.getPowerUpEffectDuration();
        player.setPowerUpEndTime(endTime);
        game.setPowerUpEndTime(endTime, type);
        
        // پخش صدا
        SoundManager.playSound(SoundManager.SoundEffect.POWER_UP_COLLECT);
        
        // افزودن افکت بصری
        EffectManager.addPowerUpEffect(x + GameConfig.POWER_UP_SIZE / 2, 
            y + GameConfig.POWER_UP_SIZE / 2, color);
    }
    
    public boolean isActive() {
        return active;
    }
    
    public void collect() {
        active = false;
    }
    
    public void shutdown() {
        running = false;
    }
    
    public int getX() {
        return x;
    }
    
    public int getY() {
        return y;
    }
    
    public int getSize() {
        return GameConfig.POWER_UP_SIZE;
    }
    
    public int getPulseSize() {
        return pulseSize;
    }
    
    public PowerUpType getType() {
        return type;
    }
    
    public Color getColor() {
        return color;
    }
} 