import java.awt.Rectangle;
import java.awt.event.KeyEvent;
import java.util.ArrayList;
import java.util.List;

public class Player {
    // موقعیت و سرعت
    private volatile int x;
    private volatile int y;
    private volatile int speed;
    private volatile int defaultSpeed;
    private volatile boolean movingUp = false;
    private volatile boolean movingDown = false;
    private volatile boolean movingLeft = false;
    private volatile boolean movingRight = false;
    
    // وضعیت‌ها و قدرت‌ها
    private volatile boolean invincible = false;
    private volatile boolean hasShield = false;
    private volatile int shieldStrength = 0;
    private volatile int dashCooldown = 0;
    
    // زمان باقی‌مانده قدرت‌ها
    private long powerUpEndTime = 0;
    
    // سابقه موقعیت برای رسم دنباله
    private List<Point> trail;
    private static final int TRAIL_LENGTH = 10;
    
    public Player(int x, int y) {
        this.x = x;
        this.y = y;
        this.speed = GameConfig.getPlayerDefaultSpeed();
        this.defaultSpeed = speed;
        this.trail = new ArrayList<>();
        
        // پر کردن لیست دنباله با موقعیت اولیه
        for (int i = 0; i < TRAIL_LENGTH; i++) {
            trail.add(new Point(x, y));
        }
    }

    public void update() {
        // به‌روزرسانی موقعیت قبلی برای رسم دنباله
        updateTrail();
        
        // محاسبه حرکت براساس کلیدهای فشرده شده
        int deltaX = 0;
        int deltaY = 0;
        
        if (movingUp) {
            deltaY -= speed;
        }
        if (movingDown) {
            deltaY += speed;
        }
        if (movingLeft) {
            deltaX -= speed;
        }
        if (movingRight) {
            deltaX += speed;
        }
        
        // اعمال نرمال‌سازی حرکت قطری
        if (deltaX != 0 && deltaY != 0) {
            // نرمال‌سازی حرکت قطری برای جلوگیری از سرعت بیشتر
            double factor = 0.7071; // sqrt(2)/2
            deltaX = (int)(deltaX * factor);
            deltaY = (int)(deltaY * factor);
        }
        
        // به‌روزرسانی موقعیت
        x += deltaX;
        y += deltaY;
        
        // نگه داشتن بازیکن در محدوده بازی
        x = Math.max(0, Math.min(x, GameConfig.WIDTH - GameConfig.PLAYER_SIZE));
        y = Math.max(0, Math.min(y, GameConfig.HEIGHT - GameConfig.PLAYER_SIZE));
        
        // به‌روزرسانی کولدان حرکت سریع
        if (dashCooldown > 0) {
            dashCooldown--;
        }
        
        // بررسی زمان پایان قدرت‌ها
        if (System.currentTimeMillis() > powerUpEndTime) {
            if (invincible) {
                invincible = false;
            }
        }
    }
    
    /**
     * به‌روزرسانی لیست نقاط دنباله
     */
    private void updateTrail() {
        // حذف قدیمی‌ترین نقطه و اضافه کردن موقعیت جدید
        trail.remove(0);
        trail.add(new Point(x, y));
    }

    public void handleKeyPress(int keyCode) {
        switch (keyCode) {
            case KeyEvent.VK_UP:
            case KeyEvent.VK_W:
                movingUp = true;
                break;
            case KeyEvent.VK_DOWN:
            case KeyEvent.VK_S:
                movingDown = true;
                break;
            case KeyEvent.VK_LEFT:
            case KeyEvent.VK_A:
                movingLeft = true;
                break;
            case KeyEvent.VK_RIGHT:
            case KeyEvent.VK_D:
                movingRight = true;
                break;
            case KeyEvent.VK_SPACE:
                tryDash();
                break;
        }
    }

    public void handleKeyRelease(int keyCode) {
        switch (keyCode) {
            case KeyEvent.VK_UP:
            case KeyEvent.VK_W:
                movingUp = false;
                break;
            case KeyEvent.VK_DOWN:
            case KeyEvent.VK_S:
                movingDown = false;
                break;
            case KeyEvent.VK_LEFT:
            case KeyEvent.VK_A:
                movingLeft = false;
                break;
            case KeyEvent.VK_RIGHT:
            case KeyEvent.VK_D:
                movingRight = false;
                break;
        }
    }
    
    /**
     * تلاش برای انجام حرکت سریع
     */
    private void tryDash() {
        if (dashCooldown <= 0) {
            // محاسبه جهت حرکت
            int dirX = 0;
            int dirY = 0;
            
            if (movingUp) dirY -= 1;
            if (movingDown) dirY += 1;
            if (movingLeft) dirX -= 1;
            if (movingRight) dirX += 1;
            
            // اگر حرکتی در جریان باشد
            if (dirX != 0 || dirY != 0) {
                // نرمالایز کردن جهت
                double length = Math.sqrt(dirX * dirX + dirY * dirY);
                dirX = (int)(dirX / length * 50); // مقدار حرکت سریع
                dirY = (int)(dirY / length * 50);
                
                // اعمال حرکت سریع
                x += dirX;
                y += dirY;
                
                // نگه داشتن بازیکن در محدوده بازی
                x = Math.max(0, Math.min(x, GameConfig.WIDTH - GameConfig.PLAYER_SIZE));
                y = Math.max(0, Math.min(y, GameConfig.HEIGHT - GameConfig.PLAYER_SIZE));
                
                // اضافه کردن افکت بصری
                EffectManager.addExplosion(x, y, 30, GameConfig.PLAYER_COLOR);
                
                // تنظیم کولدان
                dashCooldown = 60; // حدود 1 ثانیه با نرخ 60 فریم بر ثانیه
                
                // پخش صدای حرکت سریع
                SoundManager.playSound(SoundManager.SoundEffect.BALL_BOUNCE);
            }
        }
    }
    
    /**
     * فعال کردن قدرت سپر
     */
    public void activateShield(int strength) {
        hasShield = true;
        shieldStrength = strength;
    }
    
    /**
     * استفاده از سپر در صورت برخورد
     */
    public boolean useShield() {
        if (hasShield && shieldStrength > 0) {
            shieldStrength--;
            if (shieldStrength <= 0) {
                hasShield = false;
            }
            return true;
        }
        return false;
    }
    
    /**
     * بررسی داشتن سپر
     */
    public boolean hasShield() {
        return hasShield && shieldStrength > 0;
    }
    
    /**
     * دریافت قدرت سپر
     */
    public int getShieldStrength() {
        return shieldStrength;
    }
    
    public Rectangle getBounds() {
        return new Rectangle(x, y, GameConfig.PLAYER_SIZE, GameConfig.PLAYER_SIZE);
    }

    public int getX() {
        return x;
    }

    public int getY() {
        return y;
    }
    
    /**
     * دریافت مختصات نقطه وسط بازیکن
     */
    public Point getCenter() {
        return new Point(x + GameConfig.PLAYER_SIZE / 2, y + GameConfig.PLAYER_SIZE / 2);
    }
    
    /**
     * دریافت لیست نقاط دنباله برای رسم
     */
    public List<Point> getTrail() {
        return trail;
    }
    
    public void setSpeed(int newSpeed) {
        this.speed = newSpeed;
    }
    
    public void resetSpeed() {
        this.speed = defaultSpeed;
    }
    
    public void setInvincible(boolean invincible) {
        this.invincible = invincible;
    }
    
    public boolean isInvincible() {
        return invincible;
    }
    
    public void setPowerUpEndTime(long endTime) {
        this.powerUpEndTime = endTime;
    }
    
    /**
     * کلاس داخلی برای نگهداری یک نقطه دنباله
     */
    public static class Point {
        public int x, y;
        
        public Point(int x, int y) {
            this.x = x;
            this.y = y;
        }
    }
} 