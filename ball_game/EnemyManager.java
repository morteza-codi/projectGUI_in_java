import java.awt.Rectangle;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;
import java.util.concurrent.CopyOnWriteArrayList;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.locks.ReadWriteLock;
import java.util.concurrent.locks.ReentrantReadWriteLock;

public class EnemyManager {
    private final CopyOnWriteArrayList<Enemy> enemies;
    private final ReadWriteLock lock;
    private final Random random;
    private final int screenWidth;
    private final int screenHeight;
    private ScheduledExecutorService scheduler;
    private volatile boolean running = true;
    
    public EnemyManager(int screenWidth, int screenHeight) {
        this.enemies = new CopyOnWriteArrayList<>();
        this.lock = new ReentrantReadWriteLock();
        this.random = new Random();
        this.screenWidth = screenWidth;
        this.screenHeight = screenHeight;
        
        // ایجاد و زمان‌بندی نخ‌های ایجاد دشمن
        this.scheduler = Executors.newScheduledThreadPool(3);
        
        // ایجادکننده دشمن‌های ردگیر
        scheduler.scheduleAtFixedRate(
            new TrackerSpawner(), 
            3, 
            GameConfig.getEnemySpawnDelay(), 
            TimeUnit.SECONDS
        );
        
        // ایجادکننده دشمن‌های بمب‌انداز
        scheduler.scheduleAtFixedRate(
            new BomberSpawner(), 
            5, 
            GameConfig.getEnemySpawnDelay() * 2, // بمب‌اندازها کمتر ظاهر می‌شوند
            TimeUnit.SECONDS
        );
        
        // پاکسازی دشمنان غیرفعال
        scheduler.scheduleAtFixedRate(
            new CleanupTask(), 
            2, 
            2, 
            TimeUnit.SECONDS
        );
    }
    
    public void updateEnemies(int playerX, int playerY) {
        lock.readLock().lock();
        try {
            for (Enemy enemy : enemies) {
                enemy.update(playerX, playerY);
            }
        } finally {
            lock.readLock().unlock();
        }
    }
    
    public List<Enemy> getEnemies() {
        List<Enemy> enemiesCopy = new ArrayList<>();
        lock.readLock().lock();
        try {
            enemiesCopy.addAll(enemies);
        } finally {
            lock.readLock().unlock();
        }
        return enemiesCopy;
    }
    
    public boolean checkHit(int x, int y, int size) {
        boolean hit = false;
        Rectangle playerRect = new Rectangle(x, y, size, size);
        
        lock.readLock().lock();
        try {
            for (Enemy enemy : enemies) {
                if (enemy.isActive() && enemy.checkCollision(playerRect)) {
                    enemy.hit();
                    hit = true;
                }
            }
        } finally {
            lock.readLock().unlock();
        }
        
        return hit;
    }
    
    public boolean checkCollision(Rectangle rect) {
        lock.readLock().lock();
        try {
            for (Enemy enemy : enemies) {
                if (enemy.isActive() && enemy.checkCollision(rect)) {
                    return true;
                }
            }
        } finally {
            lock.readLock().unlock();
        }
        return false;
    }
    
    private int countEnemiesByType(Enemy.EnemyType type) {
        int count = 0;
        for (Enemy enemy : enemies) {
            if (enemy.isActive() && enemy.getType() == type) {
                count++;
            }
        }
        return count;
    }
    
    public void clearEnemies() {
        lock.writeLock().lock();
        try {
            for (Enemy enemy : enemies) {
                if (enemy instanceof TrackerEnemy) {
                    ((TrackerEnemy) enemy).shutdown();
                } else if (enemy instanceof BomberEnemy) {
                    ((BomberEnemy) enemy).shutdown();
                }
            }
            enemies.clear();
        } finally {
            lock.writeLock().unlock();
        }
    }
    
    public void shutdown() {
        running = false;
        
        // بستن نخ‌های همه دشمنان
        lock.writeLock().lock();
        try {
            for (Enemy enemy : enemies) {
                if (enemy instanceof TrackerEnemy) {
                    ((TrackerEnemy) enemy).shutdown();
                } else if (enemy instanceof BomberEnemy) {
                    ((BomberEnemy) enemy).shutdown();
                }
            }
        } finally {
            lock.writeLock().unlock();
        }
        
        // بستن زمان‌بند
        scheduler.shutdownNow();
        try {
            scheduler.awaitTermination(2, TimeUnit.SECONDS);
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
        }
    }
    
    private class TrackerSpawner implements Runnable {
        @Override
        public void run() {
            if (!running) return;
            
            // ایجاد دشمن ردگیر جدید اگر از حد مجاز کمتر است
            int trackerCount = countEnemiesByType(Enemy.EnemyType.TRACKER);
            int totalEnemies = enemies.size();
            
            if (trackerCount < GameConfig.getMaxTrackerEnemies() && 
                totalEnemies < GameConfig.getMaxTotalEnemies()) {
                
                int x, y;
                // ایجاد موقعیت خارج از مرکز صفحه
                if (random.nextBoolean()) {
                    // ایجاد از لبه افقی
                    x = random.nextInt(screenWidth);
                    y = random.nextBoolean() ? -GameConfig.TRACKER_SIZE : screenHeight;
                } else {
                    // ایجاد از لبه عمودی
                    x = random.nextBoolean() ? -GameConfig.TRACKER_SIZE : screenWidth;
                    y = random.nextInt(screenHeight);
                }
                
                TrackerEnemy tracker = new TrackerEnemy(x, y);
                
                lock.writeLock().lock();
                try {
                    enemies.add(tracker);
                } finally {
                    lock.writeLock().unlock();
                }
                
                // اجرای نخ دشمن
                Thread trackerThread = new Thread(tracker);
                trackerThread.setDaemon(true);
                trackerThread.start();
            }
        }
    }
    
    private class BomberSpawner implements Runnable {
        @Override
        public void run() {
            if (!running) return;
            
            // ایجاد بمب‌انداز جدید اگر از حد مجاز کمتر است
            int bomberCount = countEnemiesByType(Enemy.EnemyType.BOMBER);
            int totalEnemies = enemies.size();
            
            if (bomberCount < GameConfig.getMaxBomberEnemies() &&
                totalEnemies < GameConfig.getMaxTotalEnemies()) {
                
                int x, y;
                // ایجاد موقعیت خارج از مرکز صفحه
                if (random.nextBoolean()) {
                    // ایجاد از لبه افقی
                    x = random.nextInt(screenWidth);
                    y = random.nextBoolean() ? -GameConfig.BOMBER_SIZE : screenHeight;
                } else {
                    // ایجاد از لبه عمودی
                    x = random.nextBoolean() ? -GameConfig.BOMBER_SIZE : screenWidth;
                    y = random.nextInt(screenHeight);
                }
                
                BomberEnemy bomber = new BomberEnemy(x, y);
                
                lock.writeLock().lock();
                try {
                    enemies.add(bomber);
                } finally {
                    lock.writeLock().unlock();
                }
                
                // اجرای نخ دشمن
                Thread bomberThread = new Thread(bomber);
                bomberThread.setDaemon(true);
                bomberThread.start();
            }
        }
    }
    
    private class CleanupTask implements Runnable {
        @Override
        public void run() {
            lock.writeLock().lock();
            try {
                List<Enemy> toRemove = new ArrayList<>();
                
                for (Enemy enemy : enemies) {
                    if (!enemy.isActive()) {
                        toRemove.add(enemy);
                    }
                }
                
                // حذف دشمنان غیرفعال
                for (Enemy enemy : toRemove) {
                    if (enemy instanceof TrackerEnemy) {
                        ((TrackerEnemy) enemy).shutdown();
                    } else if (enemy instanceof BomberEnemy) {
                        ((BomberEnemy) enemy).shutdown();
                    }
                    enemies.remove(enemy);
                }
            } finally {
                lock.writeLock().unlock();
            }
        }
    }
} 