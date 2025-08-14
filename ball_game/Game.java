import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
import java.awt.image.*;
import java.io.*;
import java.util.ArrayList;
import java.util.Random;
import java.util.List;
import java.util.concurrent.CopyOnWriteArrayList;
import java.util.concurrent.*;
import javax.imageio.ImageIO;

/**
 * کلاس اصلی بازی - رفکتور شده با تقسیم به کلاس‌های جداگانه
 */
public class Game extends JFrame {
    // منابع و مدیریت نخ‌ها
    private Canvas gameCanvas;
    private BufferStrategy bufferStrategy;
    private final Random random = new Random();
    private ExecutorService ballThreadPool;
    private ScheduledExecutorService scheduledExecutor;
    
    // مدیریت منوها
    private GameMenu gameMenu;
    private boolean showingMenu = true;
    
    // عناصر بازی
    private Player player;
    private CopyOnWriteArrayList<Ball> balls;
    private List<PowerUp> powerUps;
    private EnemyManager enemyManager;
    
    // وضعیت بازی
    private volatile boolean gameRunning = true;
    private volatile boolean gamePaused = false;
    private volatile int score = 0;
    private volatile int level = 1;
    private volatile int lives = 3;
    private String playerName = "Player";
    
    // پیشرفت بازی
    private int levelThreshold = 100; // آستانه امتیاز برای افزایش سطح
    private int enemySpawnAcceleration = 0; // افزایش سرعت ایجاد دشمنان
    private int ballSpawnAcceleration = 0; // افزایش سرعت ایجاد توپ‌ها
    
    // اثرات قدرت‌ها
    private volatile boolean playerInvincible = false;
    private volatile boolean timeSlowEffect = false;
    private volatile int scoreMultiplier = 1;
    private volatile long powerUpEndTime = 0;
    private PowerUp.PowerUpType activePowerUpType = null;
    
    // آمار بازی
    private volatile int framesPerSecond = 0;
    private volatile int updatesPerSecond = 0;
    private volatile int frameCount = 0;
    private volatile int updateCount = 0;
    private long gameStartTime;
    private long gameTime = 0; // زمان بازی به ثانیه
    private int selectedMenuIndex = 0; // انتخاب فعال در منو

    // تصاویر
    private Image imgPlayer;
    private Image imgFood;
    private Image imgEnemy;
    private Image imgPowerUp;
    
    /**
     * سازنده اصلی بازی
     */
    public Game() {
        // تنظیم پنجره بازی
        setTitle("Multi-Threaded Ball Game");
        setSize(GameConfig.WIDTH, GameConfig.HEIGHT);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setResizable(false);
        setLocationRelativeTo(null);
        
        // ایجاد کنواس برای رسم بازی
        gameCanvas = new Canvas();
        gameCanvas.setPreferredSize(new Dimension(GameConfig.WIDTH, GameConfig.HEIGHT));
        gameCanvas.setFocusable(false);
        add(gameCanvas);
        
        // ایجاد منوی بازی
        gameMenu = new GameMenu(this, GameConfig.WIDTH, GameConfig.HEIGHT);
        
        // آماده‌سازی بازی
        initializeGame();
        
        // اضافه کردن گیرنده کلیدها
        addKeyListener(new GameKeyListener());
        
        // نمایش پنجره
        setFocusable(true);
        setVisible(true);
        
        // ایجاد استراتژی بافر دوتایی برای رسم روان‌تر
        gameCanvas.createBufferStrategy(2);
        bufferStrategy = gameCanvas.getBufferStrategy();
        
        // شروع حلقه اصلی بازی
        startGameLoop();
    }
    
    /**
     * مقداردهی اولیه عناصر بازی
     */
    private void initializeGame() {
        // ایجاد بازیکن
        player = new Player(GameConfig.WIDTH / 2, GameConfig.HEIGHT / 2);
        
        // ایجاد لیست توپ‌ها
        balls = new CopyOnWriteArrayList<>();
        
        // ایجاد لیست قدرت‌ها
        powerUps = new ArrayList<>();
        
        // ایجاد مدیریت دشمنان
        enemyManager = new EnemyManager(GameConfig.WIDTH, GameConfig.HEIGHT);
        
        // راه‌اندازی مدیریت نخ‌ها
        ballThreadPool = Executors.newFixedThreadPool(GameConfig.getMaxBalls());
        scheduledExecutor = Executors.newScheduledThreadPool(5);
        
        // ایجاد توپ‌های اولیه
        for (int i = 0; i < GameConfig.getInitialBalls(); i++) {
            createNewBall();
        }
        
        // ایجاد قدرت‌های اولیه
        for (int i = 0; i < GameConfig.getNumPowerUps(); i++) {
            PowerUp powerUp = new PowerUp();
            powerUps.add(powerUp);
            Thread powerUpThread = new Thread(powerUp);
            powerUpThread.setDaemon(true);
            powerUpThread.start();
        }
        
        // تنظیم زمان‌بندی ایجاد توپ‌های جدید
        scheduledExecutor.scheduleAtFixedRate(() -> {
            if (balls.size() < GameConfig.getMaxBalls() && gameRunning && !gamePaused) {
                createNewBall();
            }
        }, GameConfig.getBallSpawnRate(), GameConfig.getBallSpawnRate() - ballSpawnAcceleration, TimeUnit.SECONDS);
        
        // تنظیم زمان‌بندی برای افزایش سختی بازی بر اساس زمان
        scheduledExecutor.scheduleAtFixedRate(() -> {
            if (gameRunning && !gamePaused) {
                increaseDifficulty();
            }
        }, 60, 60, TimeUnit.SECONDS);
        
        // تنظیم زمان‌بندی محاسبه FPS
        scheduledExecutor.scheduleAtFixedRate(() -> {
            framesPerSecond = frameCount;
            updatesPerSecond = updateCount;
            frameCount = 0;
            updateCount = 0;
            if (gameRunning && !gamePaused) {
                gameTime = (System.currentTimeMillis() - gameStartTime) / 1000;
            }
        }, 0, 1, TimeUnit.SECONDS);
        
        gameStartTime = System.currentTimeMillis();

        // بارگذاری تصاویر از کلاس‌پس یا فایل سیستم
        loadImages();
        
        // همگام‌سازی تنظیمات صدا با GameConfig قبل از پخش
        SoundManager.setEffectVolume(GameConfig.soundVolume);
        SoundManager.setMusicVolume(GameConfig.musicVolume);
        if (!GameConfig.soundEnabled) {
            SoundManager.toggleSoundEffects();
        }
        if (!GameConfig.musicEnabled) {
            SoundManager.toggleMusic();
        }
        
        // پخش موسیقی پس‌زمینه بازی (اگر فعال باشد)
        SoundManager.playMusic(SoundManager.Music.MENU, true);
    }
    
    /**
     * شروع حلقه اصلی بازی
     */
    private void startGameLoop() {
        // ایجاد و شروع نخ اصلی بازی
        Thread gameThread = new Thread(() -> {
            long lastUpdateTime = System.nanoTime();
            double amountOfTicks = 60.0; // تعداد به‌روزرسانی‌ها در ثانیه
            double ns = 1000000000 / amountOfTicks;
            double delta = 0;
            
            // حلقه اصلی بازی
            while (true) {
                long now = System.nanoTime();
                delta += (now - lastUpdateTime) / ns;
                lastUpdateTime = now;
                
                // به‌روزرسانی منطق بازی
                if (delta >= 1) {
                    if (!showingMenu && gameRunning && !gamePaused) {
                        updateGame();
                    }
                    delta--;
                }
                
                // رسم بازی
                render();
                
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
        updateCount++;
        
        // به‌روزرسانی بازیکن
        player.update();
        
        // به‌روزرسانی دشمنان با سرعت متناسب با اثر کند کردن زمان
        if (timeSlowEffect) {
            // در حالت کند شدن زمان، دشمنان را کمتر به‌روزرسانی می‌کنیم
            if (updateCount % 3 == 0) {
                enemyManager.updateEnemies(player.getX(), player.getY());
            }
        } else {
            enemyManager.updateEnemies(player.getX(), player.getY());
        }
        
        // به‌روزرسانی افکت‌ها
        EffectManager.update();
        
        // بررسی برخوردها
        checkCollisions();
        
        // بررسی اتمام زمان قدرت‌ها
        if (System.currentTimeMillis() > powerUpEndTime) {
            resetPowerUpEffects();
        }
        
        // بررسی شرایط افزایش سطح
        checkLevelUp();
    }
    
    /**
     * بررسی شرایط افزایش سطح
     */
    private void checkLevelUp() {
        if (score >= level * levelThreshold) {
            level++;
            SoundManager.playSound(SoundManager.SoundEffect.POWER_UP_COLLECT);
            EffectManager.addExplosion(GameConfig.WIDTH / 2, GameConfig.HEIGHT / 2, 100, Color.GREEN);
            
            // افزایش سختی بازی با بالا رفتن سطح
            increaseDifficulty();
        }
    }
    
    /**
     * افزایش سختی بازی
     */
    private void increaseDifficulty() {
        // افزایش سرعت ایجاد توپ‌ها و دشمنان
        if (ballSpawnAcceleration < GameConfig.getBallSpawnRate() - 1) {
            ballSpawnAcceleration++;
        }
        
        if (enemySpawnAcceleration < GameConfig.getEnemySpawnDelay() - 1) {
            enemySpawnAcceleration++;
        }
    }
    
    /**
     * رسم بازی
     */
    private void render() {
        frameCount++;
        
        // دریافت گرافیک از بافر
        Graphics g = bufferStrategy.getDrawGraphics();
        
        try {
            // پاکسازی صفحه
            g.setColor(GameConfig.BACKGROUND_COLOR);
            g.fillRect(0, 0, GameConfig.WIDTH, GameConfig.HEIGHT);
            
            // رسم عناصر بازی یا منو
            if (showingMenu) {
                gameMenu.render(g);
            } else {
                renderGame(g);
            }
            
        } finally {
            // آزادسازی منابع گرافیکی
            g.dispose();
        }
        
        // نمایش بافر
        bufferStrategy.show();
    }
    
    /**
     * رسم عناصر بازی
     */
    private void renderGame(Graphics g) {
        Graphics2D g2d = (Graphics2D) g;
        
        // فعال کردن آنتی‌آلیاسینگ برای رسم بهتر
        g2d.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
        
        // رسم دنباله بازیکن (اگر بازیکن حرکت سریع دارد)
        drawPlayerTrail(g2d);
        
        // رسم توپ‌ها
        for (Ball ball : balls) {
            if (ball.isActive()) {
                int bx = ball.getX();
                int by = ball.getY();
                int bs = ball.getSize();
                Image useImg = (bs < GameConfig.PLAYER_SIZE) ? imgFood : imgEnemy;
                if (useImg != null) {
                    g.drawImage(useImg, bx, by, bs, bs, null);
                    // اگر توپ شبح است، نیمه‌شفاف دوباره رسم شود برای افکت
                    if (ball.getType() == Ball.BallType.GHOST) {
                        g2d.setComposite(AlphaComposite.getInstance(AlphaComposite.SRC_OVER, 0.5f));
                        g.drawImage(useImg, bx, by, bs, bs, null);
                        g2d.setComposite(AlphaComposite.getInstance(AlphaComposite.SRC_OVER, 1.0f));
                    }
                } else {
                    // رسم توپ با رنگ مناسب نوع آن (fallback)
                    g.setColor(ball.getColor());
                    g.fillOval(bx, by, bs, bs);
                    if (ball.getType() == Ball.BallType.GHOST) {
                        g2d.setComposite(AlphaComposite.getInstance(AlphaComposite.SRC_OVER, 0.5f));
                        g.fillOval(bx, by, bs, bs);
                        g2d.setComposite(AlphaComposite.getInstance(AlphaComposite.SRC_OVER, 1.0f));
                    }
                }
            }
        }
        
        // رسم قدرت‌ها
        for (PowerUp powerUp : powerUps) {
            if (powerUp.isActive()) {
                int pulse = powerUp.getPulseSize();
                int size = powerUp.getSize() + pulse * 2;
                int drawX = powerUp.getX() - (powerUp.getSize()/2 + pulse);
                int drawY = powerUp.getY() - (powerUp.getSize()/2 + pulse);
                if (imgPowerUp != null) {
                    g.drawImage(imgPowerUp, drawX, drawY, size, size, null);
                } else {
                    // fallback: رسم دایره رنگی
                    g.setColor(powerUp.getColor());
                    g.fillOval(drawX, drawY, size, size);
                }
                
                // نمادها را می‌توان حفظ کرد روی تصویر نیز
                g.setColor(Color.WHITE);
                switch (powerUp.getType()) {
                    case SPEED_BOOST:
                        drawSpeedBoostIcon(g, powerUp.getX(), powerUp.getY(), powerUp.getSize());
                        break;
                    case INVINCIBILITY:
                        drawInvincibilityIcon(g, powerUp.getX(), powerUp.getY(), powerUp.getSize());
                        break;
                    case SCORE_MULTIPLIER:
                        drawMultiplierIcon(g, powerUp.getX(), powerUp.getY(), powerUp.getSize());
                        break;
                    case SHIELD:
                        drawShieldIcon(g, powerUp.getX(), powerUp.getY(), powerUp.getSize());
                        break;
                    case TIME_SLOW:
                        drawTimeSlowIcon(g, powerUp.getX(), powerUp.getY(), powerUp.getSize());
                        break;
                    case CLEAR_SCREEN:
                        drawClearScreenIcon(g, powerUp.getX(), powerUp.getY(), powerUp.getSize());
                        break;
                }
            }
        }
        
        // رسم دشمنان
        List<Enemy> enemies = enemyManager.getEnemies();
        for (Enemy enemy : enemies) {
            if (enemy.isActive()) {
                if (enemy.getType() == Enemy.EnemyType.TRACKER) {
                    g.setColor(GameConfig.TRACKER_ENEMY_COLOR);
                } else {
                    g.setColor(GameConfig.BOMBER_ENEMY_COLOR);
                }
                
                if (imgEnemy != null) {
                    g.drawImage(imgEnemy, enemy.getX(), enemy.getY(), enemy.getSize(), enemy.getSize(), null);
                } else {
                    g.fillRect(enemy.getX(), enemy.getY(), enemy.getSize(), enemy.getSize());
                }
                
                // رسم بمب‌ها برای دشمن بمب‌انداز
                if (enemy.getType() == Enemy.EnemyType.BOMBER) {
                    BomberEnemy bomber = (BomberEnemy) enemy;
                    CopyOnWriteArrayList<BomberEnemy.Bomb> bombs = bomber.getBombs();
                    
                    for (BomberEnemy.Bomb bomb : bombs) {
                        if (bomb.isActive()) {
                            if (bomb.isExploding()) {
                                g.setColor(GameConfig.EXPLOSION_COLOR);
                            } else {
                                g.setColor(GameConfig.BOMB_COLOR);
                            }
                            
                            int bombX = bomb.getX() - bomb.getSize() / 2;
                            int bombY = bomb.getY() - bomb.getSize() / 2;
                            g.fillOval(bombX, bombY, bomb.getSize(), bomb.getSize());
                        }
                    }
                }
            }
        }
        
        // رسم بازیکن
        if (playerInvincible) {
            // اگر شکست‌ناپذیر باشد، با افکت ویژه رسم می‌شود
            int ps = GameConfig.PLAYER_SIZE;
            if (imgPlayer != null) {
                g.drawImage(imgPlayer, player.getX(), player.getY(), ps, ps, null);
            } else {
                g.setColor(GameConfig.PLAYER_INVINCIBLE_COLOR);
                g.fillRect(player.getX(), player.getY(), ps, ps);
            }
            
            // افکت نورانی
            int glowSize = 6;
            g2d.setComposite(AlphaComposite.getInstance(AlphaComposite.SRC_OVER, 0.3f));
            g.fillRect(
                player.getX() - glowSize, 
                player.getY() - glowSize, 
                GameConfig.PLAYER_SIZE + glowSize * 2, 
                GameConfig.PLAYER_SIZE + glowSize * 2
            );
            g2d.setComposite(AlphaComposite.getInstance(AlphaComposite.SRC_OVER, 1.0f));
        } else {
            int ps = GameConfig.PLAYER_SIZE;
            if (imgPlayer != null) {
                g.drawImage(imgPlayer, player.getX(), player.getY(), ps, ps, null);
            } else {
                g.setColor(GameConfig.PLAYER_COLOR);
                g.fillRect(player.getX(), player.getY(), ps, ps);
            }
        }
        
        // رسم سپر اگر فعال باشد
        if (player.hasShield()) {
            int shieldSize = GameConfig.PLAYER_SIZE + 10;
            int shieldX = player.getX() - 5;
            int shieldY = player.getY() - 5;
            
            g2d.setComposite(AlphaComposite.getInstance(AlphaComposite.SRC_OVER, 0.7f));
            g.setColor(new Color(0, 200, 200));
            g.drawOval(shieldX, shieldY, shieldSize, shieldSize);
            
            // رسم خطوط بیشتر برای قدرت سپر
            for (int i = 1; i <= player.getShieldStrength(); i++) {
                g.drawOval(shieldX - i, shieldY - i, shieldSize + i * 2, shieldSize + i * 2);
            }
            
            g2d.setComposite(AlphaComposite.getInstance(AlphaComposite.SRC_OVER, 1.0f));
        }
        
        // رسم افکت‌های ویژه
        EffectManager.render(g);
        
        // رسم رابط کاربری و آمار بازی
        drawHUD(g);
    }
    
    /**
     * رسم دنباله بازیکن
     */
    private void drawPlayerTrail(Graphics2D g) {
        List<Player.Point> trail = player.getTrail();
        
        // رسم دنباله فقط اگر بازیکن در حال حرکت باشد
        int trailSize = trail.size();
        for (int i = 0; i < trailSize - 1; i++) {
            Player.Point current = trail.get(i);
            
            // محاسبه شفافیت براساس فاصله از موقعیت فعلی
            float alpha = (float)i / trailSize * 0.5f;
            g.setComposite(AlphaComposite.getInstance(AlphaComposite.SRC_OVER, alpha));
            
            // محاسبه اندازه براساس فاصله از موقعیت فعلی
            int size = GameConfig.PLAYER_SIZE - (trailSize - i) * 2;
            if (size > 0) {
                if (playerInvincible) {
                    g.setColor(GameConfig.PLAYER_INVINCIBLE_COLOR);
                } else {
                    g.setColor(GameConfig.PLAYER_COLOR);
                }
                g.fillRect(current.x, current.y, size, size);
            }
        }
        
        // بازگرداندن شفافیت به حالت عادی
        g.setComposite(AlphaComposite.getInstance(AlphaComposite.SRC_OVER, 1.0f));
    }
    
    /**
     * رسم نمادهای مربوط به انواع قدرت‌ها
     */
    private void drawSpeedBoostIcon(Graphics g, int x, int y, int size) {
        g.drawString("S", x + size/2 - 5, y + size/2 + 5);
    }
    
    private void drawInvincibilityIcon(Graphics g, int x, int y, int size) {
        g.drawString("I", x + size/2 - 3, y + size/2 + 5);
    }
    
    private void drawMultiplierIcon(Graphics g, int x, int y, int size) {
        g.drawString("x" + GameConfig.getScoreMultiplier(), x + size/2 - 5, y + size/2 + 5);
    }
    
    private void drawShieldIcon(Graphics g, int x, int y, int size) {
        int[] xPoints = {x + size/2, x + size - 3, x + 3};
        int[] yPoints = {y + 3, y + size - 3, y + size - 3};
        g.drawPolygon(xPoints, yPoints, 3);
    }
    
    private void drawTimeSlowIcon(Graphics g, int x, int y, int size) {
        g.drawOval(x + 5, y + 5, size - 10, size - 10);
        g.drawLine(x + size/2, y + size/2, x + size/2, y + 8);
    }
    
    private void drawClearScreenIcon(Graphics g, int x, int y, int size) {
        g.drawString("C", x + size/2 - 5, y + size/2 + 5);
    }
    
    /**
     * رسم رابط کاربری و آمار بازی
     */
    private void drawHUD(Graphics g) {
        // رسم پس‌زمینه شفاف برای قسمت بالای صفحه
        g.setColor(GameConfig.HUD_BACKGROUND_COLOR);
        g.fillRect(0, 0, GameConfig.WIDTH, 50);
        
        // رسم امتیاز
        g.setColor(GameConfig.TEXT_COLOR);
        g.setFont(new Font("Arial", Font.BOLD, 20));
        g.drawString("Score: " + score, 20, 30);
        
        // رسم سطح بازی
        g.drawString("Level: " + level, 150, 30);
        
        // رسم تعداد جان‌ها
        g.drawString("Lives: " + lives, 250, 30);
        
        // رسم زمان بازی
        int minutes = (int)(gameTime / 60);
        int seconds = (int)(gameTime % 60);
        g.drawString(String.format("Time: %02d:%02d", minutes, seconds), 350, 30);
        
        // رسم قدرت فعال
        if (System.currentTimeMillis() < powerUpEndTime) {
            g.setColor(Color.CYAN);
            long remainingTime = (powerUpEndTime - System.currentTimeMillis()) / 1000;
            
            String powerUpName = "";
            if (activePowerUpType != null) {
                powerUpName = activePowerUpType.toString();
            }
            
            g.drawString(powerUpName + ": " + remainingTime + "s", 20, 60);
        }
        
        // نمایش FPS اگر فعال باشد
        if (GameConfig.showFPS) {
            g.setColor(Color.YELLOW);
            g.setFont(new Font("Arial", Font.PLAIN, 12));
            g.drawString("FPS: " + framesPerSecond + " | UPS: " + updatesPerSecond, GameConfig.WIDTH - 150, 20);
        }
    }
    
    /**
     * بررسی برخوردها
     */
    private void checkCollisions() {
        Rectangle playerRect = player.getBounds();
        
        // بررسی برخورد با توپ‌ها
        for (Ball ball : balls) {
            if (ball.isActive() && ball.checkCollision(playerRect)) {
                if (ball.getSize() < GameConfig.PLAYER_SIZE || playerInvincible) {
                    // بازیکن توپ کوچکتر را می‌خورد یا شکست‌ناپذیر است
                    int ballScore = ball.getScoreValue() * scoreMultiplier;
                    score += ballScore;
                    
                    // افزودن افکت برخورد
                    EffectManager.addBallCollectEffect(
                        ball.getX() + ball.getSize() / 2,
                        ball.getY() + ball.getSize() / 2,
                        ball.getSize()
                    );
                    
                    // پخش صدا
                    SoundManager.playSound(SoundManager.SoundEffect.BALL_COLLECT);
                    
                    // اگر توپ از نوع تقسیم‌شونده باشد
                    if (ball.getType() == Ball.BallType.SPLITTER) {
                        Ball[] newBalls = ball.split();
                        for (Ball newBall : newBalls) {
                            balls.add(newBall);
                            ballThreadPool.execute(newBall);
                        }
                    }
                    
                    ball.reset();
                } else {
                    // برخورد با توپ بزرگتر
                    if (player.useShield()) {
                        // اگر سپر داشته باشد، از آن استفاده می‌کند
                        ball.reset();
                        SoundManager.playSound(SoundManager.SoundEffect.ENEMY_HIT);
                    } else {
                        // در غیر این صورت، کاهش جان
                        loseLife();
                    }
                }
            }
        }
        
        // بررسی برخورد با قدرت‌ها
        for (PowerUp powerUp : powerUps) {
            if (powerUp.isActive() && powerUp.checkCollision(playerRect)) {
                powerUp.applyTo(player, this);
                powerUp.collect();
            }
        }
        
        // بررسی برخورد با دشمنان
        enemyManager.checkHit(player.getX(), player.getY(), GameConfig.PLAYER_SIZE);
        
        List<Enemy> enemies = enemyManager.getEnemies();
        for (Enemy enemy : enemies) {
            if (enemy.isActive() && enemy.checkCollision(playerRect)) {
                if (playerInvincible) {
                    // بازیکن شکست‌ناپذیر است، دشمن آسیب می‌بیند
                    enemy.hit();
                    int enemyScore = GameConfig.getScoreEnemy() * scoreMultiplier;
                    score += enemyScore;
                    
                    // افزودن افکت
                    EffectManager.addEnemyHitEffect(enemy.getX(), enemy.getY(), enemyScore);
                    
                    // پخش صدا
                    SoundManager.playSound(SoundManager.SoundEffect.ENEMY_HIT);
                    
                    // اگر دشمن نابود شده
                    if (!enemy.isActive()) {
                        EffectManager.addEnemyDestroyEffect(
                            enemy.getX() + enemy.getSize() / 2,
                            enemy.getY() + enemy.getSize() / 2,
                            enemy.getSize(),
                            enemy.getType() == Enemy.EnemyType.TRACKER ? 
                                GameConfig.TRACKER_ENEMY_COLOR : GameConfig.BOMBER_ENEMY_COLOR
                        );
                        SoundManager.playSound(SoundManager.SoundEffect.ENEMY_DESTROY);
                    }
                } else if (player.useShield()) {
                    // اگر سپر داشته باشد، از آن استفاده می‌کند
                    enemy.hit();
                    SoundManager.playSound(SoundManager.SoundEffect.ENEMY_HIT);
                } else {
                    // در غیر این صورت، کاهش جان
                    loseLife();
                }
            }
        }
    }
    
    /**
     * از دست دادن یک جان
     */
    private void loseLife() {
        lives--;
        player.setInvincible(true); // شکست‌ناپذیری موقت بعد از ضربه خوردن
        
        // افزودن افکت
        EffectManager.addExplosion(
            player.getX() + GameConfig.PLAYER_SIZE / 2, 
            player.getY() + GameConfig.PLAYER_SIZE / 2, 
            GameConfig.PLAYER_SIZE * 2, 
            Color.RED
        );
        
        // پخش صدا
        SoundManager.playSound(SoundManager.SoundEffect.ENEMY_HIT);
        
        // تنظیم زمان شکست‌ناپذیری موقت
        powerUpEndTime = System.currentTimeMillis() + 3000; // 3 ثانیه شکست‌ناپذیری
        
        // بررسی پایان بازی
        if (lives <= 0) {
            gameOver();
        }
    }
    
    /**
     * پایان بازی
     */
    private void gameOver() {
        gameRunning = false;
        SoundManager.playSound(SoundManager.SoundEffect.GAME_OVER);
        SoundManager.playMusic(SoundManager.Music.GAME_OVER, false);
        
        // نمایش منوی پایان بازی
        gameMenu.showGameOverMenu(score);
        showingMenu = true;
    }
    
    /**
     * ایجاد توپ جدید
     */
    private void createNewBall() {
        Ball ball = new Ball();
        balls.add(ball);
        ballThreadPool.execute(ball);
    }
    
    /**
     * راه‌اندازی مجدد بازی
     */
    public void restartGame() {
        // بازنشانی امتیاز و مقادیر بازی
        score = 0;
        level = 1;
        lives = 3;
        gameRunning = true;
        gamePaused = false;
        playerInvincible = false;
        scoreMultiplier = 1;
        ballSpawnAcceleration = 0;
        enemySpawnAcceleration = 0;
        gameStartTime = System.currentTimeMillis();
        
        // پاکسازی عناصر بازی
        for (Ball ball : balls) {
            ball.deactivate();
        }
        balls.clear();
        
        // بازنشانی دشمنان
        enemyManager.shutdown();
        enemyManager = new EnemyManager(GameConfig.WIDTH, GameConfig.HEIGHT);
        
        // بازنشانی قدرت‌ها
        for (PowerUp powerUp : powerUps) {
            powerUp.shutdown();
        }
        powerUps.clear();
        
        // پاکسازی افکت‌های بصری
        EffectManager.clearEffects();
        
        // بازنشانی و اجرای مجدد نخ‌ها
        shutdownThreads();
        
        // راه‌اندازی مجدد نخ‌ها
        ballThreadPool = Executors.newFixedThreadPool(GameConfig.getMaxBalls());
        scheduledExecutor = Executors.newScheduledThreadPool(5);
        
        // ایجاد مجدد بازیکن
        player = new Player(GameConfig.WIDTH / 2, GameConfig.HEIGHT / 2);
        
        // ایجاد توپ‌های اولیه
        for (int i = 0; i < GameConfig.getInitialBalls(); i++) {
            createNewBall();
        }
        
        // ایجاد قدرت‌های اولیه
        for (int i = 0; i < GameConfig.getNumPowerUps(); i++) {
            PowerUp powerUp = new PowerUp();
            powerUps.add(powerUp);
            Thread powerUpThread = new Thread(powerUp);
            powerUpThread.setDaemon(true);
            powerUpThread.start();
        }
        
        // تنظیم زمان‌بندی ایجاد توپ‌های جدید
        scheduledExecutor.scheduleAtFixedRate(() -> {
            if (balls.size() < GameConfig.getMaxBalls() && gameRunning && !gamePaused) {
                createNewBall();
            }
        }, GameConfig.getBallSpawnRate(), GameConfig.getBallSpawnRate() - ballSpawnAcceleration, TimeUnit.SECONDS);
        
        // تنظیم زمان‌بندی برای افزایش سختی بازی بر اساس زمان
        scheduledExecutor.scheduleAtFixedRate(() -> {
            if (gameRunning && !gamePaused) {
                increaseDifficulty();
            }
        }, 60, 60, TimeUnit.SECONDS);
        
        // تنظیم زمان‌بندی محاسبه FPS
        scheduledExecutor.scheduleAtFixedRate(() -> {
            framesPerSecond = frameCount;
            updatesPerSecond = updateCount;
            frameCount = 0;
            updateCount = 0;
            if (gameRunning && !gamePaused) {
                gameTime = (System.currentTimeMillis() - gameStartTime) / 1000;
            }
        }, 0, 1, TimeUnit.SECONDS);
        
        // پخش موسیقی بازی
        SoundManager.playMusic(SoundManager.Music.GAMEPLAY, true);
    }
    
    /**
     * اعمال اثر قدرت
     */
    private void resetPowerUpEffects() {
        if (playerInvincible) {
            playerInvincible = false;
        }
        
        if (scoreMultiplier > 1) {
            scoreMultiplier = 1;
        }
        
        if (timeSlowEffect) {
            timeSlowEffect = false;
        }
        
        player.resetSpeed();
        activePowerUpType = null;
    }
    
    /**
     * پردازش کلیدهای فشرده شده
     */
    private class GameKeyListener extends KeyAdapter {
        @Override
        public void keyPressed(KeyEvent e) {
            int keyCode = e.getKeyCode();
            
            if (showingMenu) {
                gameMenu.handleKeyPress(keyCode);
                if (gameMenu.shouldReturnToGame()) {
                    showingMenu = false;
                    gamePaused = false;
                    // پخش موسیقی بازی
                    SoundManager.playMusic(SoundManager.Music.GAMEPLAY, true);
                }
                return;
            }
            
            // کلیدهای کنترل بازی
            player.handleKeyPress(keyCode);
            
            // کلیدهای سیستمی
            switch (keyCode) {
                case KeyEvent.VK_ESCAPE:
                case KeyEvent.VK_P:
                    togglePause();
                    break;
                case KeyEvent.VK_R:
                    if (!gameRunning) {
                        restartGame();
                    }
                    break;
                case KeyEvent.VK_F:
                    GameConfig.showFPS = !GameConfig.showFPS;
                    break;
                case KeyEvent.VK_M:
                    showingMenu = true;
                    gamePaused = true;
                    gameMenu.showPauseMenu();
                    // پخش موسیقی منو
                    SoundManager.playMusic(SoundManager.Music.MENU, true);
                    break;
            }
        }
        
        @Override
        public void keyReleased(KeyEvent e) {
            int keyCode = e.getKeyCode();
            
            if (showingMenu) {
                gameMenu.handleKeyRelease();
                return;
            }
            
            player.handleKeyRelease(keyCode);
        }
    }
    
    /**
     * تغییر وضعیت توقف بازی
     */
    private void togglePause() {
        gamePaused = !gamePaused;
        if (gamePaused) {
            showingMenu = true;
            gameMenu.showPauseMenu();
            // پخش موسیقی منو
            SoundManager.playMusic(SoundManager.Music.MENU, true);
        } else {
            showingMenu = false;
            // پخش موسیقی بازی
            SoundManager.playMusic(SoundManager.Music.GAMEPLAY, true);
        }
    }
    
    /**
     * بستن منابع بازی
     */
    @Override
    public void dispose() {
        shutdownThreads();
        SoundManager.cleanup();
        super.dispose();
    }
    
    /**
     * بستن نخ‌ها
     */
    private void shutdownThreads() {
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
     * پاکسازی همه دشمنان (برای قدرت CLEAR_SCREEN)
     */
    public void clearEnemies() {
        enemyManager.clearEnemies();
        EffectManager.addExplosion(GameConfig.WIDTH / 2, GameConfig.HEIGHT / 2, 200, Color.WHITE);
        SoundManager.playSound(SoundManager.SoundEffect.ENEMY_DESTROY);
    }
    
    /**
     * تنظیم وضعیت کند شدن زمان
     */
    public void setTimeSlowEffect(boolean active) {
        this.timeSlowEffect = active;
    }
    
    /**
     * تنظیم ضریب امتیازدهی
     */
    public void setScoreMultiplier(int multiplier) {
        this.scoreMultiplier = multiplier;
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
        }
    }

    /**
     * بارگذاری تصاویر از کلاس‌پس یا فایل سیستم
     */
    private void loadImages() {
        imgPlayer = loadImage("images/me.png");
        imgFood = loadImage("images/food.png");
        // تلاش برای enemy.png سپس enamy.png
        imgEnemy = loadImage("images/enemy.png");
        if (imgEnemy == null) {
            imgEnemy = loadImage("images/enamy.png");
        }
        imgPowerUp = loadImage("images/power_up.png");
    }

    private Image loadImage(String relativePath) {
        try {
            String normalized = relativePath.startsWith("/") ? relativePath : "/" + relativePath;
            InputStream in = Game.class.getResourceAsStream(normalized);
            if (in == null) {
                ClassLoader cl = Thread.currentThread().getContextClassLoader();
                if (cl != null) {
                    in = cl.getResourceAsStream(relativePath.startsWith("/") ? relativePath.substring(1) : relativePath);
                }
            }
            if (in != null) {
                try (InputStream autoClose = in) {
                    BufferedImage img = ImageIO.read(autoClose);
                    return img;
                }
            }
            // fallback to filesystem
            File f = new File(relativePath);
            if (f.exists()) {
                return ImageIO.read(f);
            }
        } catch (IOException e) {
            System.err.println("Error loading image: " + relativePath + ": " + e.getMessage());
        }
        System.out.println("Image not found: " + relativePath);
        return null;
    }

    /**
     * دریافت امتیاز بازی
     */
    public int getScore() {
        return score;
    }

    /**
     * دریافت انتخاب فعال منو
     */
    public int getSelectedMenuIndex() {
        return selectedMenuIndex;
    }

    /**
     * تنظیم انتخاب فعال منو
     */
    public void setSelectedMenuIndex(int index) {
        this.selectedMenuIndex = index;
    }

    /**
     * شروع بازی جدید
     */
    public void startGame() {
        showingMenu = false;
        gamePaused = false;
        SoundManager.playMusic(SoundManager.Music.GAMEPLAY, true);
    }

    /**
     * ادامه بازی
     */
    public void resumeGame() {
        showingMenu = false;
        gamePaused = false;
        SoundManager.playMusic(SoundManager.Music.GAMEPLAY, true);
    }

    /**
     * برگشت به منوی اصلی
     */
    public void goToMainMenu() {
        showingMenu = true;
        gamePaused = true;
        gameMenu.showMainMenu();
        SoundManager.playMusic(SoundManager.Music.MENU, true);
    }

    /**
     * متد اصلی
     */
    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> {
            Game game = new Game();
            game.requestFocus();
        });
    }
}
