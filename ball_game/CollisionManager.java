import java.awt.Rectangle;
import java.util.List;
import java.util.concurrent.CopyOnWriteArrayList;

/**
 * کلاس مدیریت برخوردها
 */
public class CollisionManager {
    private Player player;
    private CopyOnWriteArrayList<Ball> balls;
    private List<PowerUp> powerUps;
    private EnemyManager enemyManager;
    private PowerUpManager powerUpManager;
    private GameStateManager gameStateManager;
    
    public CollisionManager(Player player, CopyOnWriteArrayList<Ball> balls, 
                           List<PowerUp> powerUps, EnemyManager enemyManager,
                           PowerUpManager powerUpManager, GameStateManager gameStateManager) {
        this.player = player;
        this.balls = balls;
        this.powerUps = powerUps;
        this.enemyManager = enemyManager;
        this.powerUpManager = powerUpManager;
        this.gameStateManager = gameStateManager;
    }
    
    /**
     * بررسی همه برخوردها
     */
    public void checkAllCollisions() {
        Rectangle playerRect = player.getBounds();
        
        checkBallCollisions(playerRect);
        checkPowerUpCollisions(playerRect);
        checkEnemyCollisions(playerRect);
    }
    
    /**
     * بررسی برخورد با توپ‌ها
     */
    private void checkBallCollisions(Rectangle playerRect) {
        for (Ball ball : balls) {
            if (ball.isActive() && ball.checkCollision(playerRect)) {
                handleBallCollision(ball);
            }
        }
    }
    
    /**
     * پردازش برخورد با توپ
     */
    private void handleBallCollision(Ball ball) {
        if (ball.getSize() < GameConfig.PLAYER_SIZE || powerUpManager.isPlayerInvincible()) {
            // بازیکن توپ کوچکتر را می‌خورد یا شکست‌ناپذیر است
            int ballScore = powerUpManager.calculateScore(ball.getScoreValue());
            gameStateManager.addScore(ballScore);
            
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
                    // نیاز به thread pool برای اجرای توپ‌های جدید
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
                handlePlayerDamage();
            }
        }
    }
    
    /**
     * بررسی برخورد با قدرت‌ها
     */
    private void checkPowerUpCollisions(Rectangle playerRect) {
        for (PowerUp powerUp : powerUps) {
            if (powerUp.isActive() && powerUp.checkCollision(playerRect)) {
                handlePowerUpCollection(powerUp);
                powerUp.collect();
            }
        }
    }
    
    /**
     * پردازش جمع‌آوری قدرت
     */
    private void handlePowerUpCollection(PowerUp powerUp) {
        long duration = GameConfig.getPowerUpEffectDuration();
        
        switch (powerUp.getType()) {
            case SPEED_BOOST:
                player.setSpeed(GameConfig.getPlayerBoostedSpeed());
                player.setPowerUpEndTime(System.currentTimeMillis() + duration);
                break;
                
            case INVINCIBILITY:
                powerUpManager.activateInvincibility(duration);
                player.setInvincible(true);
                player.setPowerUpEndTime(System.currentTimeMillis() + duration);
                break;
                
            case SCORE_MULTIPLIER:
                powerUpManager.activateScoreMultiplier(GameConfig.getScoreMultiplier(), duration);
                break;
                
            case SHIELD:
                player.activateShield(3); // سپر با 3 نقطه قدرت
                break;
                
            case TIME_SLOW:
                powerUpManager.activateTimeSlowEffect(duration);
                break;
                
            case CLEAR_SCREEN:
                clearAllEnemies();
                break;
        }
        
        // پخش صدا
        SoundManager.playSound(SoundManager.SoundEffect.POWER_UP_COLLECT);
        
        // افزودن افکت بصری
        EffectManager.addPowerUpEffect(
            powerUp.getX() + powerUp.getSize() / 2, 
            powerUp.getY() + powerUp.getSize() / 2, 
            powerUp.getColor()
        );
    }
    
    /**
     * پاکسازی همه دشمنان (برای قدرت CLEAR_SCREEN)
     */
    private void clearAllEnemies() {
        enemyManager.clearEnemies();
        EffectManager.addExplosion(GameConfig.WIDTH / 2, GameConfig.HEIGHT / 2, 200, java.awt.Color.WHITE);
        SoundManager.playSound(SoundManager.SoundEffect.ENEMY_DESTROY);
    }
    
    /**
     * بررسی برخورد با دشمنان
     */
    private void checkEnemyCollisions(Rectangle playerRect) {
        enemyManager.checkHit(player.getX(), player.getY(), GameConfig.PLAYER_SIZE);
        
        List<Enemy> enemies = enemyManager.getEnemies();
        for (Enemy enemy : enemies) {
            if (enemy.isActive() && enemy.checkCollision(playerRect)) {
                handleEnemyCollision(enemy);
            }
        }
    }
    
    /**
     * پردازش برخورد با دشمن
     */
    private void handleEnemyCollision(Enemy enemy) {
        if (powerUpManager.isPlayerInvincible()) {
            // بازیکن شکست‌ناپذیر است، دشمن آسیب می‌بیند
            enemy.hit();
            int enemyScore = powerUpManager.calculateScore(GameConfig.getScoreEnemy());
            gameStateManager.addScore(enemyScore);
            
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
            handlePlayerDamage();
        }
    }
    
    /**
     * پردازش آسیب به بازیکن
     */
    private void handlePlayerDamage() {
        gameStateManager.loseLife();
        player.setInvincible(true); // شکست‌ناپذیری موقت بعد از ضربه خوردن
        
        // افزودن افکت
        EffectManager.addExplosion(
            player.getX() + GameConfig.PLAYER_SIZE / 2, 
            player.getY() + GameConfig.PLAYER_SIZE / 2, 
            GameConfig.PLAYER_SIZE * 2, 
            GameConfig.TRACKER_ENEMY_COLOR
        );
        
        // پخش صدا
        SoundManager.playSound(SoundManager.SoundEffect.ENEMY_HIT);
        
        // تنظیم زمان شکست‌ناپذیری موقت
        powerUpManager.setPowerUpEndTime(System.currentTimeMillis() + 3000, PowerUp.PowerUpType.INVINCIBILITY);
        
        // بررسی پایان بازی
        if (gameStateManager.getLives() <= 0) {
            gameOver();
        }
    }
    
    /**
     * پایان بازی
     */
    private void gameOver() {
        gameStateManager.setGameRunning(false);
        SoundManager.playSound(SoundManager.SoundEffect.GAME_OVER);
        SoundManager.playMusic(SoundManager.Music.GAME_OVER, false);
    }
}
