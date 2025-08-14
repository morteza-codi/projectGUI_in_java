import java.awt.*;
import java.awt.image.BufferedImage;
import java.util.List;
import java.util.concurrent.CopyOnWriteArrayList;

/**
 * کلاس رندرر بازی برای رسم همه عناصر
 */
public class GameRenderer {
    private Image imgPlayer;
    private Image imgFood;
    private Image imgEnemy;
    private Image imgPowerUp;
    
    public GameRenderer(Image imgPlayer, Image imgFood, Image imgEnemy, Image imgPowerUp) {
        this.imgPlayer = imgPlayer;
        this.imgFood = imgFood;
        this.imgEnemy = imgEnemy;
        this.imgPowerUp = imgPowerUp;
    }
    
    /**
     * رسم عناصر بازی
     */
    public void renderGame(Graphics g, Player player, CopyOnWriteArrayList<Ball> balls,
                          List<PowerUp> powerUps, EnemyManager enemyManager,
                          PowerUpManager powerUpManager, GameStateManager gameStateManager) {
        Graphics2D g2d = (Graphics2D) g;
        
        // فعال کردن آنتی‌آلیاسینگ برای رسم بهتر
        g2d.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
        
        // رسم دنباله بازیکن
        drawPlayerTrail(g2d, player, powerUpManager.isPlayerInvincible());
        
        // رسم توپ‌ها
        drawBalls(g, balls);
        
        // رسم قدرت‌ها
        drawPowerUps(g, powerUps);
        
        // رسم دشمنان
        drawEnemies(g, enemyManager);
        
        // رسم بازیکن
        drawPlayer(g, g2d, player, powerUpManager.isPlayerInvincible());
        
        // رسم افکت‌های ویژه
        EffectManager.render(g);
        
        // رسم رابط کاربری و آمار بازی
        drawHUD(g, gameStateManager, powerUpManager);
    }
    
    /**
     * رسم دنباله بازیکن
     */
    private void drawPlayerTrail(Graphics2D g, Player player, boolean isInvincible) {
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
                if (isInvincible) {
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
     * رسم توپ‌ها
     */
    private void drawBalls(Graphics g, CopyOnWriteArrayList<Ball> balls) {
        Graphics2D g2d = (Graphics2D) g;
        
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
    }
    
    /**
     * رسم قدرت‌ها
     */
    private void drawPowerUps(Graphics g, List<PowerUp> powerUps) {
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
                
                // رسم نمادهای قدرت‌ها
                g.setColor(Color.WHITE);
                drawPowerUpIcon(g, powerUp);
            }
        }
    }
    
    /**
     * رسم نمادهای قدرت‌ها
     */
    private void drawPowerUpIcon(Graphics g, PowerUp powerUp) {
        int x = powerUp.getX();
        int y = powerUp.getY();
        int size = powerUp.getSize();
        
        switch (powerUp.getType()) {
            case SPEED_BOOST:
                g.drawString("S", x + size/2 - 5, y + size/2 + 5);
                break;
            case INVINCIBILITY:
                g.drawString("I", x + size/2 - 3, y + size/2 + 5);
                break;
            case SCORE_MULTIPLIER:
                g.drawString("x" + GameConfig.getScoreMultiplier(), x + size/2 - 5, y + size/2 + 5);
                break;
            case SHIELD:
                int[] xPoints = {x + size/2, x + size - 3, x + 3};
                int[] yPoints = {y + 3, y + size - 3, y + size - 3};
                g.drawPolygon(xPoints, yPoints, 3);
                break;
            case TIME_SLOW:
                g.drawOval(x + 5, y + 5, size - 10, size - 10);
                g.drawLine(x + size/2, y + size/2, x + size/2, y + 8);
                break;
            case CLEAR_SCREEN:
                g.drawString("C", x + size/2 - 5, y + size/2 + 5);
                break;
        }
    }
    
    /**
     * رسم دشمنان
     */
    private void drawEnemies(Graphics g, EnemyManager enemyManager) {
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
                    drawBomberBombs(g, (BomberEnemy) enemy);
                }
            }
        }
    }
    
    /**
     * رسم بمب‌های دشمن بمب‌انداز
     */
    private void drawBomberBombs(Graphics g, BomberEnemy bomber) {
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
    
    /**
     * رسم بازیکن
     */
    private void drawPlayer(Graphics g, Graphics2D g2d, Player player, boolean isInvincible) {
        int ps = GameConfig.PLAYER_SIZE;
        
        if (isInvincible) {
            // اگر شکست‌ناپذیر باشد، با افکت ویژه رسم می‌شود
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
            if (imgPlayer != null) {
                g.drawImage(imgPlayer, player.getX(), player.getY(), ps, ps, null);
            } else {
                g.setColor(GameConfig.PLAYER_COLOR);
                g.fillRect(player.getX(), player.getY(), ps, ps);
            }
        }
        
        // رسم سپر اگر فعال باشد
        drawPlayerShield(g, g2d, player);
    }
    
    /**
     * رسم سپر بازیکن
     */
    private void drawPlayerShield(Graphics g, Graphics2D g2d, Player player) {
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
    }
    
    /**
     * رسم رابط کاربری و آمار بازی
     */
    private void drawHUD(Graphics g, GameStateManager gameStateManager, PowerUpManager powerUpManager) {
        // رسم پس‌زمینه شفاف برای قسمت بالای صفحه
        g.setColor(GameConfig.HUD_BACKGROUND_COLOR);
        g.fillRect(0, 0, GameConfig.WIDTH, 50);
        
        // رسم امتیاز
        g.setColor(GameConfig.TEXT_COLOR);
        g.setFont(new Font("Arial", Font.BOLD, 20));
        g.drawString("Score: " + gameStateManager.getScore(), 20, 30);
        
        // رسم سطح بازی
        g.drawString("Level: " + gameStateManager.getLevel(), 150, 30);
        
        // رسم تعداد جان‌ها
        g.drawString("Lives: " + gameStateManager.getLives(), 250, 30);
        
        // رسم زمان بازی
        long gameTime = gameStateManager.getGameTime();
        int minutes = (int)(gameTime / 60);
        int seconds = (int)(gameTime % 60);
        g.drawString(String.format("Time: %02d:%02d", minutes, seconds), 350, 30);
        
        // رسم قدرت فعال
        if (powerUpManager.getRemainingTime() > 0) {
            g.setColor(Color.CYAN);
            long remainingTime = powerUpManager.getRemainingTime() / 1000;
            
            String powerUpName = "";
            if (powerUpManager.getActivePowerUpType() != null) {
                powerUpName = powerUpManager.getActivePowerUpType().toString();
            }
            
            g.drawString(powerUpName + ": " + remainingTime + "s", 20, 60);
        }
        
        // نمایش FPS اگر فعال باشد
        if (GameConfig.showFPS) {
            g.setColor(Color.YELLOW);
            g.setFont(new Font("Arial", Font.PLAIN, 12));
            g.drawString("FPS: " + gameStateManager.getFramesPerSecond() + " | UPS: " + gameStateManager.getUpdatesPerSecond(), 
                        GameConfig.WIDTH - 150, 20);
        }
    }
}
