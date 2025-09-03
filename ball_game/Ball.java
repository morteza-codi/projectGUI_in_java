import java.awt.Color;
import java.awt.Rectangle;
import java.util.Random;

/**
 * کلاس توپ‌های متحرک در بازی
 */
public class Ball implements Runnable {
    private static final Random random = new Random();
    
    // مختصات، سایز و سرعت
    private volatile int x;
    private volatile int y;
    private volatile int size;
    private volatile int xSpeed;
    private volatile int ySpeed;
    private volatile boolean active = true;
    
    // مشخصات ظاهری و رفتاری
    private Color color;
    private BallType type;
    private int scoreValue;
    private int bounceCount;
    private int maxBounces;
    
    // انواع توپ‌ها
    public enum BallType {
        NORMAL,    // توپ معمولی که امتیاز دارد
        BOUNCY,    // توپ جهشی که از دیواره‌ها منعکس می‌شود
        SPLITTER,  // توپ تقسیم‌شونده که به چند توپ کوچکتر تبدیل می‌شود
        GHOST,     // توپ شبح که گاهی ناپدید و ظاهر می‌شود
        HOMING     // توپ هوشمند که به سمت بازیکن حرکت می‌کند
    }
    
    public Ball() {
        reset();
    }
    
    /**
     * بازنشانی توپ با ویژگی‌های جدید تصادفی
     */
    public void reset() {
        // ایجاد نوع توپ به صورت تصادفی
        int typeRoll = random.nextInt(100);
        if (typeRoll < 60) {
            type = BallType.NORMAL;  // 60% احتمال
        } else if (typeRoll < 80) {
            type = BallType.BOUNCY;  // 20% احتمال
        } else if (typeRoll < 90) {
            type = BallType.SPLITTER; // 10% احتمال
        } else if (typeRoll < 95) {
            type = BallType.GHOST;    // 5% احتمال
        } else {
            type = BallType.HOMING;   // 5% احتمال
        }
        
        // تنظیم اندازه
        size = random.nextInt(GameConfig.BALL_MAX_SIZE - GameConfig.BALL_MIN_SIZE + 1) + GameConfig.BALL_MIN_SIZE;
        
        // تنظیم نقطه شروع از لبه صفحه
        if (random.nextBoolean()) {
            // شروع از چپ یا راست
            x = random.nextBoolean() ? -size : GameConfig.WIDTH;
            y = random.nextInt(GameConfig.HEIGHT);
        } else {
            // شروع از بالا یا پایین
            x = random.nextInt(GameConfig.WIDTH);
            y = random.nextBoolean() ? -size : GameConfig.HEIGHT;
        }
        
        // تنظیم سرعت تصادفی (هرگز صفر نباشد)
        int maxSpeed = GameConfig.getBallMaxSpeed();
        xSpeed = random.nextInt(maxSpeed * 2 + 1) - maxSpeed;
        if (xSpeed == 0) xSpeed = 1;
        
        ySpeed = random.nextInt(maxSpeed * 2 + 1) - maxSpeed;
        if (ySpeed == 0) ySpeed = 1;
        
        // تنظیم رنگ براساس نوع توپ
        switch (type) {
            case NORMAL:
                color = GameConfig.BALL_COLOR;
                break;
            case BOUNCY:
                color = new Color(0, 200, 0);
                break;
            case SPLITTER:
                color = new Color(255, 165, 0);
                break;
            case GHOST:
                color = new Color(200, 200, 255);
                break;
            case HOMING:
                color = new Color(255, 0, 255);
                break;
        }
        
        // تعداد جهش‌ها برای توپ‌های جهشی
        if (type == BallType.BOUNCY) {
            maxBounces = random.nextInt(5) + 3; // بین 3 تا 7 جهش
            bounceCount = 0;
        }
        
        // تنظیم ارزش امتیاز براساس اندازه و نوع
        scoreValue = calculateScoreValue();
        
        active = true;
    }
    
    /**
     * محاسبه ارزش امتیاز توپ براساس اندازه و نوع
     */
    private int calculateScoreValue() {
        int baseScore = GameConfig.getScoreBall();
        
        // توپ‌های کوچک‌تر امتیاز بیشتری دارند
        float sizeMultiplier = (float)(GameConfig.BALL_MAX_SIZE - size) / GameConfig.BALL_MAX_SIZE;
        
        // توپ‌های خاص امتیاز بیشتری دارند
        float typeMultiplier = 1.0f;
        switch (type) {
            case BOUNCY:
                typeMultiplier = 1.5f;
                break;
            case SPLITTER:
                typeMultiplier = 2.0f;
                break;
            case GHOST:
                typeMultiplier = 2.5f;
                break;
            case HOMING:
                typeMultiplier = 3.0f;
                break;
        }
        
        return (int)(baseScore * (1 + sizeMultiplier) * typeMultiplier);
    }
    
    @Override
    public void run() {
        while (active) {
            move();
            
            try {
                Thread.sleep(GameConfig.BALL_UPDATE_DELAY);
            } catch (InterruptedException e) {
                active = false;
                break;
            }
        }
    }
    
    /**
     * به‌روزرسانی موقعیت توپ
     */
    private void move() {
        // حرکت برای توپ‌های هوشمند متفاوت است
        if (type == BallType.HOMING && random.nextInt(100) < 30) { // 30% شانس دنبال کردن هر حرکت
            // دریافت موقعیت فعلی بازیکن (از گیم فعلی نمی‌توانیم مستقیم دسترسی داشته باشیم)
            // به جای آن از مرکز صفحه به عنوان تقریب استفاده می‌کنیم
            int centerX = GameConfig.WIDTH / 2;
            int centerY = GameConfig.HEIGHT / 2;
            
            // تغییر جهت به سمت مرکز صفحه
            if (x < centerX) xSpeed = Math.abs(xSpeed);
            else if (x > centerX) xSpeed = -Math.abs(xSpeed);
            
            if (y < centerY) ySpeed = Math.abs(ySpeed);
            else if (y > centerY) ySpeed = -Math.abs(ySpeed);
        }
        
        // حرکت توپ
        x += xSpeed;
        y += ySpeed;
        
        // بررسی برخورد با دیواره‌ها
        if (type == BallType.BOUNCY) {
            // برای توپ‌های جهشی، منعکس شدن از دیواره‌ها
            boolean bounced = false;
            
            if (x < 0) {
                x = 0;
                xSpeed = -xSpeed;
                bounced = true;
            } else if (x > GameConfig.WIDTH - size) {
                x = GameConfig.WIDTH - size;
                xSpeed = -xSpeed;
                bounced = true;
            }
            
            if (y < 0) {
                y = 0;
                ySpeed = -ySpeed;
                bounced = true;
            } else if (y > GameConfig.HEIGHT - size) {
                y = GameConfig.HEIGHT - size;
                ySpeed = -ySpeed;
                bounced = true;
            }
            
            if (bounced) {
                bounceCount++;
                if (bounceCount >= maxBounces) {
                    // بعد از تعداد مشخصی جهش، توپ ناپدید می‌شود
                    active = false;
                }
                
                // پخش صدای برخورد
                if (GameConfig.soundEnabled) {
                    SoundManager.playSound(SoundManager.SoundEffect.BALL_BOUNCE);
                }
            }
        } else {
            // برای سایر توپ‌ها، ناپدید شدن بعد از خروج از صفحه
            if (x < -size * 2 || x > GameConfig.WIDTH + size * 2 || 
                y < -size * 2 || y > GameConfig.HEIGHT + size * 2) {
                reset();
            }
        }
    }
    
    /**
     * بررسی برخورد توپ با مستطیل مشخص شده
     */
    public boolean checkCollision(Rectangle other) {
        // توپ‌های شبح گاهی برخورد نمی‌کنند
        if (type == BallType.GHOST && random.nextInt(100) < 30) { // 30% شانس عبور
            return false;
        }
        
        return getBounds().intersects(other);
    }
    
    /**
     * تقسیم توپ به چندین توپ کوچک‌تر (برای توپ‌های تقسیم شونده)
     */
    public Ball[] split() {
        // فقط برای توپ‌های تقسیم‌شونده و اندازه کافی بزرگ
        if (type != BallType.SPLITTER || size < GameConfig.BALL_MIN_SIZE * 2) {
            return new Ball[0];
        }
        
        // تعداد توپ‌های جدید
        int numSplits = random.nextInt(3) + 2; // 2 تا 4 توپ
        Ball[] newBalls = new Ball[numSplits];
        
        for (int i = 0; i < numSplits; i++) {
            Ball newBall = new Ball();
            
            // تنظیم ویژگی‌های توپ جدید
            newBall.size = size / 2;
            newBall.x = x;
            newBall.y = y;
            newBall.type = BallType.NORMAL; // توپ‌های جدید از نوع عادی هستند
            newBall.color = GameConfig.BALL_COLOR;
            
            // سرعت تصادفی
            newBall.xSpeed = random.nextInt(GameConfig.getBallMaxSpeed() * 2 + 1) - GameConfig.getBallMaxSpeed();
            if (newBall.xSpeed == 0) newBall.xSpeed = 1;
            
            newBall.ySpeed = random.nextInt(GameConfig.getBallMaxSpeed() * 2 + 1) - GameConfig.getBallMaxSpeed();
            if (newBall.ySpeed == 0) newBall.ySpeed = 1;
            
            newBall.scoreValue = newBall.calculateScoreValue();
            
            newBalls[i] = newBall;
        }
        
        // افکت تقسیم شدن
        EffectManager.addExplosion(x + size / 2, y + size / 2, size, color);
        
        return newBalls;
    }
    
    /**
     * ایجاد محدوده برخورد برای توپ
     */
    public Rectangle getBounds() {
        return new Rectangle(x, y, size, size);
    }
    
    /**
     * غیرفعال کردن توپ
     */
    public void deactivate() {
        active = false;
    }
    
    /**
     * بررسی فعال بودن توپ
     */
    public boolean isActive() {
        return active;
    }
    
    /**
     * دریافت نوع توپ
     */
    public BallType getType() {
        return type;
    }
    
    /**
     * دریافت رنگ توپ
     */
    public Color getColor() {
        return color;
    }
    
    /**
     * دریافت ارزش امتیاز توپ
     */
    public int getScoreValue() {
        return scoreValue;
    }

    public int getX() {
        return x;
    }

    public int getY() {
        return y;
    }

    public int getSize() {
        return size;
    }
} 