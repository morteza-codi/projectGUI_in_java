import java.awt.Rectangle;
import java.util.Random;

public class TrackerEnemy implements Enemy {
    private static final Random random = new Random();
    
    private volatile int x, y;
    private volatile int size;
    private volatile int speed;
    private volatile boolean active;
    private volatile boolean running;
    private volatile int health;
    
    // مشخصات حرکت و رفتار
    private long lastDirectionChange;
    private int directionChangeDelay;
    
    public TrackerEnemy(int x, int y) {
        this.x = x;
        this.y = y;
        this.size = GameConfig.TRACKER_SIZE;
        this.speed = GameConfig.getTrackerSpeed();
        this.active = true;
        this.running = true;
        this.health = GameConfig.getTrackerHealth();
        this.lastDirectionChange = System.currentTimeMillis();
        this.directionChangeDelay = 500 + random.nextInt(500); // تغییر جهت هر 0.5 تا 1 ثانیه
    }
    
    @Override
    public void run() {
        while (running) {
            try {
                Thread.sleep(GameConfig.ENEMY_UPDATE_DELAY);
                
                // حرکت براساس به‌روزرسانی توسط آخرین موقعیت بازیکن
                // به‌روزرسانی توسط EnemyManager انجام می‌شود
            } catch (InterruptedException e) {
                running = false;
                break;
            }
        }
    }
    
    @Override
    public void update(int playerX, int playerY) {
        if (!active || !running) return;
        
        long currentTime = System.currentTimeMillis();
        int deltaX = 0;
        int deltaY = 0;
        
        // هوش مصنوعی ساده ردگیری: حرکت به سمت بازیکن با کمی تصادفی بودن
        if (currentTime - lastDirectionChange > directionChangeDelay) {
            if (x < playerX) {
                deltaX = speed;
            } else if (x > playerX) {
                deltaX = -speed;
            }
            
            if (y < playerY) {
                deltaY = speed;
            } else if (y > playerY) {
                deltaY = -speed;
            }
            
            // اضافه کردن کمی تصادفی بودن به حرکت
            if (random.nextInt(10) < 3) { // 30% شانس تغییر جهت
                deltaX += (random.nextInt(3) - 1);
                deltaY += (random.nextInt(3) - 1);
            }
            
            // نرمال‌سازی حرکت قطری
            if (deltaX != 0 && deltaY != 0) {
                double factor = 0.7071; // sqrt(2)/2
                deltaX = (int)(deltaX * factor);
                deltaY = (int)(deltaY * factor);
            }
            
            // به‌روزرسانی موقعیت
            x += deltaX;
            y += deltaY;
            
            // نگه داشتن در محدوده بازی
            x = Math.max(0, Math.min(x, GameConfig.WIDTH - size));
            y = Math.max(0, Math.min(y, GameConfig.HEIGHT - size));
            
            lastDirectionChange = currentTime;
            directionChangeDelay = 300 + random.nextInt(400); // 0.3-0.7 ثانیه
        }
    }
    
    @Override
    public boolean checkCollision(int objX, int objY, int objSize) {
        return x < objX + objSize &&
               x + size > objX &&
               y < objY + objSize &&
               y + size > objY;
    }
    
    @Override
    public boolean checkCollision(Rectangle rect) {
        return getBounds().intersects(rect);
    }
    
    @Override
    public Rectangle getBounds() {
        return new Rectangle(x, y, size, size);
    }
    
    @Override
    public void hit() {
        health--;
        if (health <= 0) {
            active = false;
        }
    }
    
    @Override
    public boolean isActive() {
        return active;
    }
    
    public void shutdown() {
        running = false;
    }
    
    @Override
    public int getX() {
        return x;
    }
    
    @Override
    public int getY() {
        return y;
    }
    
    @Override
    public int getSize() {
        return size;
    }
    
    @Override
    public EnemyType getType() {
        return EnemyType.TRACKER;
    }
} 