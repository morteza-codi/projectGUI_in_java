import javax.swing.*;
import java.awt.*;
import java.awt.image.BufferStrategy;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.CopyOnWriteArrayList;

/**
 * کلاس اصلی بازی - کاملاً رفکتور شده با تقسیم به 15+ کلاس جداگانه
 */
public class GameRefactored extends JFrame {
    // اجزای اصلی بازی
    private Canvas gameCanvas;
    private BufferStrategy bufferStrategy;
    
    // مدیریت کننده‌ها
    private GameStateManager gameStateManager;
    private PowerUpManager powerUpManager;
    private CollisionManager collisionManager;
    private GameRenderer gameRenderer;
    private ThreadManager threadManager;
    private BallManager ballManager;
    private GameController gameController;
    private GameLoop gameLoop;
    private InputHandler inputHandler;
    
    // عناصر بازی
    private Player player;
    private CopyOnWriteArrayList<Ball> balls;
    private List<PowerUp> powerUps;
    private EnemyManager enemyManager;
    private GameMenu gameMenu;
    
    /**
     * سازنده اصلی بازی
     */
    public GameRefactored() {
        setupWindow();
        initializeManagers();
        setupComponents();
        startGame();
    }
    
    /**
     * تنظیم پنجره بازی
     */
    private void setupWindow() {
        setTitle("Multi-Threaded Ball Game - Refactored");
        setSize(GameConfig.WIDTH, GameConfig.HEIGHT);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setResizable(false);
        setLocationRelativeTo(null);
        
        // ایجاد کنواس برای رسم بازی
        gameCanvas = new Canvas();
        gameCanvas.setPreferredSize(new Dimension(GameConfig.WIDTH, GameConfig.HEIGHT));
        gameCanvas.setFocusable(false);
        add(gameCanvas);
    }
    
    /**
     * مقداردهی اولیه مدیریت کننده‌ها
     */
    private void initializeManagers() {
        // مدیریت کننده‌های اصلی
        gameStateManager = new GameStateManager();
        powerUpManager = new PowerUpManager();
        threadManager = new ThreadManager();
        
        // عناصر بازی
        player = new Player(GameConfig.WIDTH / 2, GameConfig.HEIGHT / 2);
        balls = new CopyOnWriteArrayList<>();
        powerUps = new ArrayList<>();
        enemyManager = new EnemyManager(GameConfig.WIDTH, GameConfig.HEIGHT);
        // gameMenu = new GameMenu(this, GameConfig.WIDTH, GameConfig.HEIGHT);
        
        // مدیریت کننده‌های پیشرفته
        ballManager = new BallManager(balls, threadManager);
        collisionManager = new CollisionManager(player, balls, powerUps, 
                                              enemyManager, powerUpManager, gameStateManager);
        
        // بارگذاری تصاویر
        ImageLoader.GameImages images = ImageLoader.loadAllImages();
        gameRenderer = new GameRenderer(images.player, images.food, images.enemy, images.powerUp);
        
        // کنترلر بازی
        gameController = new GameController(gameStateManager, player, ballManager, 
                                          powerUps, enemyManager, threadManager, 
                                          gameMenu, powerUpManager);
        
        // حلقه بازی
        gameLoop = new GameLoop(gameStateManager, player, enemyManager, 
                               collisionManager, powerUpManager);
        
        // مدیریت ورودی
        inputHandler = new InputHandler(gameStateManager, gameMenu, player, gameController);
    }
    
    /**
     * راه‌اندازی اجزای بازی
     */
    private void setupComponents() {
        // اضافه کردن گیرنده کلیدها
        addKeyListener(inputHandler);
        
        // نمایش پنجره
        setFocusable(true);
        setVisible(true);
        
        // ایجاد استراتژی بافر دوتایی برای رسم روان‌تر
        gameCanvas.createBufferStrategy(2);
        bufferStrategy = gameCanvas.getBufferStrategy();
        
        // ایجاد عناصر اولیه
        ballManager.createInitialBalls();
        createInitialPowerUps();
        
        // راه‌اندازی زمان‌بندی‌ها
        threadManager.setupGameSchedulers(balls, gameStateManager, ballManager);
        
        // تنظیم صدا و موسیقی
        setupAudio();
    }
    
    /**
     * ایجاد قدرت‌های اولیه
     */
    private void createInitialPowerUps() {
        for (int i = 0; i < GameConfig.getNumPowerUps(); i++) {
            PowerUp powerUp = new PowerUp();
            powerUps.add(powerUp);
            threadManager.startPowerUpThread(powerUp);
        }
    }
    
    /**
     * تنظیم صدا و موسیقی
     */
    private void setupAudio() {
        SoundManager.setEffectVolume(GameConfig.soundVolume);
        SoundManager.setMusicVolume(GameConfig.musicVolume);
        if (!GameConfig.soundEnabled) {
            SoundManager.toggleSoundEffects();
        }
        if (!GameConfig.musicEnabled) {
            SoundManager.toggleMusic();
        }
        SoundManager.playMusic(SoundManager.Music.MENU, true);
    }
    
    /**
     * شروع بازی
     */
    private void startGame() {
        // شروع حلقه اصلی بازی
        gameLoop.startLoop(this::render);
    }
    
    /**
     * رسم بازی
     */
    private void render() {
        if (bufferStrategy == null) return;
        
        gameStateManager.updateFrameStats();
        
        // دریافت گرافیک از بافر
        Graphics g = bufferStrategy.getDrawGraphics();
        
        try {
            // پاکسازی صفحه
            g.setColor(GameConfig.BACKGROUND_COLOR);
            g.fillRect(0, 0, GameConfig.WIDTH, GameConfig.HEIGHT);
            
            // رسم عناصر بازی یا منو
            if (gameStateManager.isShowingMenu()) {
                gameMenu.render(g);
            } else {
                gameRenderer.renderGame(g, player, balls, powerUps, 
                                      enemyManager, powerUpManager, gameStateManager);
            }
            
        } finally {
            // آزادسازی منابع گرافیکی
            g.dispose();
        }
        
        // نمایش بافر
        bufferStrategy.show();
    }
    
    /**
     * متدهای عمومی برای دسترسی از خارج
     */
    
    public void restartGame() {
        gameController.restartGame();
    }
    
    public void startNewGame() {
        gameController.startNewGame();
    }
    
    public void resumeGame() {
        gameController.resumeGame();
    }
    
    public void goToMainMenu() {
        gameController.goToMainMenu();
    }
    
    public void clearEnemies() {
        gameController.clearEnemies();
    }
    
    public void setTimeSlowEffect(boolean active) {
        powerUpManager.setTimeSlowEffect(active);
    }
    
    public void setScoreMultiplier(int multiplier) {
        powerUpManager.setScoreMultiplier(multiplier);
    }
    
    public void setPowerUpEndTime(long endTime, PowerUp.PowerUpType type) {
        powerUpManager.setPowerUpEndTime(endTime, type);
    }
    
    public int getScore() {
        return gameStateManager.getScore();
    }
    
    public int getSelectedMenuIndex() {
        return gameStateManager.getSelectedMenuIndex();
    }
    
    public void setSelectedMenuIndex(int index) {
        gameStateManager.setSelectedMenuIndex(index);
    }
    
    /**
     * بستن منابع بازی
     */
    @Override
    public void dispose() {
        gameLoop.stop();
        gameController.shutdown();
        super.dispose();
    }
    
    /**
     * متد اصلی
     */
    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> {
            GameRefactored game = new GameRefactored();
            game.requestFocus();
        });
    }
}
