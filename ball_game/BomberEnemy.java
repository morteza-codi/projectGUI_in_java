import java.awt.Rectangle;
import java.util.ArrayList;
import java.util.Random;
import java.util.concurrent.CopyOnWriteArrayList;

public class BomberEnemy implements Enemy {
    private static final Random random = new Random();
    
    // مشخصات موقعیت و ویژگی‌ها
    private volatile int x, y;
    private volatile int size;
    private volatile int speed;
    private volatile boolean active;
    private volatile boolean running;
    private volatile int health;
    
    // مشخصات حرکت
    private int directionX, directionY;
    private long lastDirectionChange;
    private int directionChangeDelay;
    
    // مشخصات بمب‌گذاری
    private long lastBombTime;
    private int bombCooldown;
    private CopyOnWriteArrayList<Bomb> bombs;
    
    public BomberEnemy(int x, int y) {
        this.x = x;
        this.y = y;
        this.size = GameConfig.BOMBER_SIZE;
        this.speed = GameConfig.getBomberSpeed();
        this.active = true;
        this.running = true;
        this.health = GameConfig.getBomberHealth();
        
        // مقداردهی اولیه حرکت
        this.directionX = random.nextBoolean() ? 1 : -1;
        this.directionY = random.nextBoolean() ? 1 : -1;
        this.lastDirectionChange = System.currentTimeMillis();
        this.directionChangeDelay = 1000 + random.nextInt(2000); // 1-3 ثانیه
        
        // مقداردهی اولیه بمب‌ها
        this.lastBombTime = System.currentTimeMillis();
        this.bombCooldown = 2000 + random.nextInt(3000); // 2-5 ثانیه
        this.bombs = new CopyOnWriteArrayList<>();
    }
    
    @Override
    public void run() {
        while (running) {
            try {
                Thread.sleep(GameConfig.ENEMY_UPDATE_DELAY);
                
                // حرکت براساس به‌روزرسانی توسط EnemyManager انجام می‌شود
                
                // بررسی زمان ایجاد بمب جدید
                long currentTime = System.currentTimeMillis();
                if (active && currentTime - lastBombTime > bombCooldown) {
                    dropBomb();
                    lastBombTime = currentTime;
                    bombCooldown = 2000 + random.nextInt(3000);
                }
                
                // به‌روزرسانی بمب‌های فعال
                updateBombs();
                
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
        
        // تغییر جهت تصادفی هر چند ثانیه یکبار
        if (currentTime - lastDirectionChange > directionChangeDelay) {
            // بعضی اوقات به سمت بازیکن حرکت کن
            if (random.nextInt(100) < 30) { // 30% شانس تعقیب بازیکن
                if (x < playerX) directionX = 1;
                else if (x > playerX) directionX = -1;
                
                if (y < playerY) directionY = 1;
                else if (y > playerY) directionY = -1;
            } else {
                // در غیر این صورت حرکت تصادفی
                directionX = random.nextBoolean() ? 1 : -1;
                directionY = random.nextBoolean() ? 1 : -1;
            }
            
            lastDirectionChange = currentTime;
            directionChangeDelay = 1000 + random.nextInt(2000);
        }
        
        // حرکت دشمن
        x += directionX * speed;
        y += directionY * speed;
        
        // برخورد با لبه‌های صفحه
        if (x <= 0) {
            x = 0;
            directionX = 1;
        } else if (x >= GameConfig.WIDTH - size) {
            x = GameConfig.WIDTH - size;
            directionX = -1;
        }
        
        if (y <= 0) {
            y = 0;
            directionY = 1;
        } else if (y >= GameConfig.HEIGHT - size) {
            y = GameConfig.HEIGHT - size;
            directionY = -1;
        }
    }
    
    private void dropBomb() {
        if (!active || !running) return;
        
        Bomb bomb = new Bomb(x + size/2, y + size/2);
        bombs.add(bomb);
        
        Thread bombThread = new Thread(bomb);
        bombThread.setDaemon(true);
        bombThread.start();
    }
    
    private void updateBombs() {
        for (int i = bombs.size() - 1; i >= 0; i--) {
            Bomb bomb = bombs.get(i);
            if (!bomb.isActive()) {
                bombs.remove(i);
            }
        }
    }
    
    public CopyOnWriteArrayList<Bomb> getBombs() {
        return bombs;
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
    
    @Override
    public void shutdown() {
        running = false;
        
        // غیرفعال کردن همه بمب‌ها
        for (Bomb bomb : bombs) {
            bomb.deactivate();
        }
        bombs.clear();
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
        return EnemyType.BOMBER;
    }
    
    public static class Bomb implements Runnable {
        private volatile int x, y;
        private volatile int size;
        private volatile boolean active;
        private volatile boolean exploding;
        private long createTime;
        
        public Bomb(int x, int y) {
            this.x = x;
            this.y = y;
            this.size = GameConfig.BOMB_SIZE;
            this.active = true;
            this.exploding = false;
            this.createTime = System.currentTimeMillis();
        }
        
        @Override
        public void run() {
            try {
                // بمب به مدت 2 ثانیه وجود دارد و سپس منفجر می‌شود
                Thread.sleep(GameConfig.getBombLifetime());
                
                if (active) {
                    explode();
                }
                
                // انفجار به مدت 0.5 ثانیه باقی می‌ماند
                Thread.sleep(GameConfig.BOMB_EXPLOSION_DURATION);
                active = false;
                
            } catch (InterruptedException e) {
                active = false;
            }
        }
        
        public void explode() {
            if (!exploding) {
                exploding = true;
                size = GameConfig.BOMB_EXPLOSION_SIZE; // افزایش اندازه هنگام انفجار
            }
        }
        
        public boolean isActive() {
            return active;
        }
        
        public void deactivate() {
            active = false;
        }
        
        public boolean checkCollision(int objX, int objY, int objSize) {
            Rectangle bombRect = new Rectangle(x - size / 2, y - size / 2, size, size);
            Rectangle otherRect = new Rectangle(objX, objY, objSize, objSize);
            
            return bombRect.intersects(otherRect);
        }
        
        public boolean checkCollision(Rectangle rect) {
            Rectangle bombRect = new Rectangle(x - size / 2, y - size / 2, size, size);
            return bombRect.intersects(rect);
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
        
        public boolean isExploding() {
            return exploding;
        }
    }
} 