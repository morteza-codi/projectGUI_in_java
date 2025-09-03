import java.awt.Color;

public class GameConfig {
    // Game dimensions
    public static final int WIDTH = 800;
    public static final int HEIGHT = 600;
    
    // Difficulty levels
    public enum Difficulty {
        EASY, MEDIUM, HARD, INSANE
    }
    
    // Current difficulty (can be changed)
    private static Difficulty currentDifficulty = Difficulty.MEDIUM;
    
    // Player settings
    public static final int PLAYER_SIZE = 30;
    private static final int PLAYER_DEFAULT_SPEED_EASY = 6;
    private static final int PLAYER_DEFAULT_SPEED_MEDIUM = 5;
    private static final int PLAYER_DEFAULT_SPEED_HARD = 5;
    private static final int PLAYER_DEFAULT_SPEED_INSANE = 4;
    
    private static final int PLAYER_BOOSTED_SPEED_EASY = 12;
    private static final int PLAYER_BOOSTED_SPEED_MEDIUM = 10;
    private static final int PLAYER_BOOSTED_SPEED_HARD = 9;
    private static final int PLAYER_BOOSTED_SPEED_INSANE = 8;
    
    // Ball settings
    public static final int BALL_MIN_SIZE = 10;
    public static final int BALL_MAX_SIZE = 50;
    
    private static final int BALL_MAX_SPEED_EASY = 3;
    private static final int BALL_MAX_SPEED_MEDIUM = 5;
    private static final int BALL_MAX_SPEED_HARD = 6;
    private static final int BALL_MAX_SPEED_INSANE = 8;
    
    private static final int INITIAL_BALLS_EASY = 5;
    private static final int INITIAL_BALLS_MEDIUM = 10;
    private static final int INITIAL_BALLS_HARD = 15;
    private static final int INITIAL_BALLS_INSANE = 20;
    
    private static final int MAX_BALLS_EASY = 15;
    private static final int MAX_BALLS_MEDIUM = 30;
    private static final int MAX_BALLS_HARD = 40;
    private static final int MAX_BALLS_INSANE = 50;
    
    private static final int BALL_SPAWN_RATE_EASY = 8; // seconds
    private static final int BALL_SPAWN_RATE_MEDIUM = 5;
    private static final int BALL_SPAWN_RATE_HARD = 3;
    private static final int BALL_SPAWN_RATE_INSANE = 2;
    
    // Power-up settings
    public static final int POWER_UP_SIZE = 20;
    public static final int POWER_UP_LIFETIME = 5000; // milliseconds
    
    private static final int POWER_UP_EFFECT_DURATION_EASY = 15000; // milliseconds
    private static final int POWER_UP_EFFECT_DURATION_MEDIUM = 10000;
    private static final int POWER_UP_EFFECT_DURATION_HARD = 7000;
    private static final int POWER_UP_EFFECT_DURATION_INSANE = 5000;
    
    private static final int NUM_POWER_UPS_EASY = 4;
    private static final int NUM_POWER_UPS_MEDIUM = 3;
    private static final int NUM_POWER_UPS_HARD = 2;
    private static final int NUM_POWER_UPS_INSANE = 1;
    
    // Enemy settings
    private static final int MAX_TRACKER_ENEMIES_EASY = 3;
    private static final int MAX_TRACKER_ENEMIES_MEDIUM = 5;
    private static final int MAX_TRACKER_ENEMIES_HARD = 7;
    private static final int MAX_TRACKER_ENEMIES_INSANE = 10;
    
    private static final int MAX_BOMBER_ENEMIES_EASY = 1;
    private static final int MAX_BOMBER_ENEMIES_MEDIUM = 3;
    private static final int MAX_BOMBER_ENEMIES_HARD = 5;
    private static final int MAX_BOMBER_ENEMIES_INSANE = 7;
    
    private static final int MAX_TOTAL_ENEMIES_EASY = 4;
    private static final int MAX_TOTAL_ENEMIES_MEDIUM = 8;
    private static final int MAX_TOTAL_ENEMIES_HARD = 12;
    private static final int MAX_TOTAL_ENEMIES_INSANE = 15;
    
    public static final int TRACKER_SIZE = 20;
    private static final int TRACKER_SPEED_EASY = 1;
    private static final int TRACKER_SPEED_MEDIUM = 2;
    private static final int TRACKER_SPEED_HARD = 3;
    private static final int TRACKER_SPEED_INSANE = 4;
    
    private static final int TRACKER_HEALTH_EASY = 5;
    private static final int TRACKER_HEALTH_MEDIUM = 3;
    private static final int TRACKER_HEALTH_HARD = 2;
    private static final int TRACKER_HEALTH_INSANE = 1;
    
    public static final int BOMBER_SIZE = 30;
    private static final int BOMBER_SPEED_EASY = 1;
    private static final int BOMBER_SPEED_MEDIUM = 1;
    private static final int BOMBER_SPEED_HARD = 2;
    private static final int BOMBER_SPEED_INSANE = 2;
    
    private static final int BOMBER_HEALTH_EASY = 8;
    private static final int BOMBER_HEALTH_MEDIUM = 5;
    private static final int BOMBER_HEALTH_HARD = 4;
    private static final int BOMBER_HEALTH_INSANE = 3;
    
    private static final int ENEMY_SPAWN_DELAY_EASY = 10; // seconds
    private static final int ENEMY_SPAWN_DELAY_MEDIUM = 5;
    private static final int ENEMY_SPAWN_DELAY_HARD = 3;
    private static final int ENEMY_SPAWN_DELAY_INSANE = 2;
    
    // Bomb settings
    public static final int BOMB_SIZE = 10;
    public static final int BOMB_EXPLOSION_SIZE = 50;
    
    private static final int BOMB_LIFETIME_EASY = 3000; // milliseconds
    private static final int BOMB_LIFETIME_MEDIUM = 2000;
    private static final int BOMB_LIFETIME_HARD = 1500;
    private static final int BOMB_LIFETIME_INSANE = 1000;
    
    public static final int BOMB_EXPLOSION_DURATION = 500; // milliseconds
    
    // Score settings
    private static final int SCORE_BALL_EASY = 5;
    private static final int SCORE_BALL_MEDIUM = 10;
    private static final int SCORE_BALL_HARD = 15;
    private static final int SCORE_BALL_INSANE = 20;
    
    private static final int SCORE_ENEMY_EASY = 10;
    private static final int SCORE_ENEMY_MEDIUM = 20;
    private static final int SCORE_ENEMY_HARD = 30;
    private static final int SCORE_ENEMY_INSANE = 50;
    
    private static final int SCORE_MULTIPLIER_EASY = 2;
    private static final int SCORE_MULTIPLIER_MEDIUM = 3;
    private static final int SCORE_MULTIPLIER_HARD = 4;
    private static final int SCORE_MULTIPLIER_INSANE = 5;
    
    // Thread settings
    public static final int RENDER_DELAY = 16; // ~60 FPS
    public static final int BALL_UPDATE_DELAY = 20;
    public static final int ENEMY_UPDATE_DELAY = 30;
    public static final int POWER_UP_UPDATE_DELAY = 100;
    
    // Sound settings
    public static boolean soundEnabled = true;
    public static boolean musicEnabled = true;
    public static float soundVolume = 0.7f;
    public static float musicVolume = 0.5f;
    
    // UI Colors
    public static final Color BACKGROUND_COLOR = Color.BLACK;
    public static final Color PLAYER_COLOR = new Color(0, 255, 0);
    public static final Color PLAYER_INVINCIBLE_COLOR = new Color(255, 255, 0);
    public static final Color BALL_COLOR = new Color(255, 0, 0);
    public static final Color TRACKER_ENEMY_COLOR = new Color(255, 165, 0);
    public static final Color BOMBER_ENEMY_COLOR = new Color(0, 255, 255);
    public static final Color BOMB_COLOR = new Color(64, 64, 64);
    public static final Color EXPLOSION_COLOR = new Color(255, 0, 0);
    public static final Color SPEEDBOOST_POWERUP_COLOR = new Color(0, 0, 255);
    public static final Color INVINCIBILITY_POWERUP_COLOR = new Color(255, 255, 0);
    public static final Color SCORE_POWERUP_COLOR = new Color(255, 0, 255);
    public static final Color TEXT_COLOR = new Color(255, 255, 255);
    public static final Color HUD_BACKGROUND_COLOR = new Color(0, 0, 0, 128);
    
    // Getter methods to access difficulty-specific settings
    public static int getPlayerDefaultSpeed() {
        switch (currentDifficulty) {
            case EASY: return PLAYER_DEFAULT_SPEED_EASY;
            case HARD: return PLAYER_DEFAULT_SPEED_HARD;
            case INSANE: return PLAYER_DEFAULT_SPEED_INSANE;
            default: return PLAYER_DEFAULT_SPEED_MEDIUM;
        }
    }
    
    public static int getPlayerBoostedSpeed() {
        switch (currentDifficulty) {
            case EASY: return PLAYER_BOOSTED_SPEED_EASY;
            case HARD: return PLAYER_BOOSTED_SPEED_HARD;
            case INSANE: return PLAYER_BOOSTED_SPEED_INSANE;
            default: return PLAYER_BOOSTED_SPEED_MEDIUM;
        }
    }
    
    public static int getBallMaxSpeed() {
        switch (currentDifficulty) {
            case EASY: return BALL_MAX_SPEED_EASY;
            case HARD: return BALL_MAX_SPEED_HARD;
            case INSANE: return BALL_MAX_SPEED_INSANE;
            default: return BALL_MAX_SPEED_MEDIUM;
        }
    }
    
    public static int getInitialBalls() {
        switch (currentDifficulty) {
            case EASY: return INITIAL_BALLS_EASY;
            case HARD: return INITIAL_BALLS_HARD;
            case INSANE: return INITIAL_BALLS_INSANE;
            default: return INITIAL_BALLS_MEDIUM;
        }
    }
    
    public static int getMaxBalls() {
        switch (currentDifficulty) {
            case EASY: return MAX_BALLS_EASY;
            case HARD: return MAX_BALLS_HARD;
            case INSANE: return MAX_BALLS_INSANE;
            default: return MAX_BALLS_MEDIUM;
        }
    }
    
    public static int getBallSpawnRate() {
        switch (currentDifficulty) {
            case EASY: return BALL_SPAWN_RATE_EASY;
            case HARD: return BALL_SPAWN_RATE_HARD;
            case INSANE: return BALL_SPAWN_RATE_INSANE;
            default: return BALL_SPAWN_RATE_MEDIUM;
        }
    }
    
    public static int getPowerUpEffectDuration() {
        switch (currentDifficulty) {
            case EASY: return POWER_UP_EFFECT_DURATION_EASY;
            case HARD: return POWER_UP_EFFECT_DURATION_HARD;
            case INSANE: return POWER_UP_EFFECT_DURATION_INSANE;
            default: return POWER_UP_EFFECT_DURATION_MEDIUM;
        }
    }
    
    public static int getNumPowerUps() {
        switch (currentDifficulty) {
            case EASY: return NUM_POWER_UPS_EASY;
            case HARD: return NUM_POWER_UPS_HARD;
            case INSANE: return NUM_POWER_UPS_INSANE;
            default: return NUM_POWER_UPS_MEDIUM;
        }
    }
    
    public static int getMaxTrackerEnemies() {
        switch (currentDifficulty) {
            case EASY: return MAX_TRACKER_ENEMIES_EASY;
            case HARD: return MAX_TRACKER_ENEMIES_HARD;
            case INSANE: return MAX_TRACKER_ENEMIES_INSANE;
            default: return MAX_TRACKER_ENEMIES_MEDIUM;
        }
    }
    
    public static int getMaxBomberEnemies() {
        switch (currentDifficulty) {
            case EASY: return MAX_BOMBER_ENEMIES_EASY;
            case HARD: return MAX_BOMBER_ENEMIES_HARD;
            case INSANE: return MAX_BOMBER_ENEMIES_INSANE;
            default: return MAX_BOMBER_ENEMIES_MEDIUM;
        }
    }
    
    public static int getMaxTotalEnemies() {
        switch (currentDifficulty) {
            case EASY: return MAX_TOTAL_ENEMIES_EASY;
            case HARD: return MAX_TOTAL_ENEMIES_HARD;
            case INSANE: return MAX_TOTAL_ENEMIES_INSANE;
            default: return MAX_TOTAL_ENEMIES_MEDIUM;
        }
    }
    
    public static int getTrackerSpeed() {
        switch (currentDifficulty) {
            case EASY: return TRACKER_SPEED_EASY;
            case HARD: return TRACKER_SPEED_HARD;
            case INSANE: return TRACKER_SPEED_INSANE;
            default: return TRACKER_SPEED_MEDIUM;
        }
    }
    
    public static int getTrackerHealth() {
        switch (currentDifficulty) {
            case EASY: return TRACKER_HEALTH_EASY;
            case HARD: return TRACKER_HEALTH_HARD;
            case INSANE: return TRACKER_HEALTH_INSANE;
            default: return TRACKER_HEALTH_MEDIUM;
        }
    }
    
    public static int getBomberSpeed() {
        switch (currentDifficulty) {
            case EASY: return BOMBER_SPEED_EASY;
            case HARD: return BOMBER_SPEED_HARD;
            case INSANE: return BOMBER_SPEED_INSANE;
            default: return BOMBER_SPEED_MEDIUM;
        }
    }
    
    public static int getBomberHealth() {
        switch (currentDifficulty) {
            case EASY: return BOMBER_HEALTH_EASY;
            case HARD: return BOMBER_HEALTH_HARD;
            case INSANE: return BOMBER_HEALTH_INSANE;
            default: return BOMBER_HEALTH_MEDIUM;
        }
    }
    
    public static int getEnemySpawnDelay() {
        switch (currentDifficulty) {
            case EASY: return ENEMY_SPAWN_DELAY_EASY;
            case HARD: return ENEMY_SPAWN_DELAY_HARD;
            case INSANE: return ENEMY_SPAWN_DELAY_INSANE;
            default: return ENEMY_SPAWN_DELAY_MEDIUM;
        }
    }
    
    public static int getBombLifetime() {
        switch (currentDifficulty) {
            case EASY: return BOMB_LIFETIME_EASY;
            case HARD: return BOMB_LIFETIME_HARD;
            case INSANE: return BOMB_LIFETIME_INSANE;
            default: return BOMB_LIFETIME_MEDIUM;
        }
    }
    
    public static int getScoreBall() {
        switch (currentDifficulty) {
            case EASY: return SCORE_BALL_EASY;
            case HARD: return SCORE_BALL_HARD;
            case INSANE: return SCORE_BALL_INSANE;
            default: return SCORE_BALL_MEDIUM;
        }
    }
    
    public static int getScoreEnemy() {
        switch (currentDifficulty) {
            case EASY: return SCORE_ENEMY_EASY;
            case HARD: return SCORE_ENEMY_HARD;
            case INSANE: return SCORE_ENEMY_INSANE;
            default: return SCORE_ENEMY_MEDIUM;
        }
    }
    
    public static int getScoreMultiplier() {
        switch (currentDifficulty) {
            case EASY: return SCORE_MULTIPLIER_EASY;
            case HARD: return SCORE_MULTIPLIER_HARD;
            case INSANE: return SCORE_MULTIPLIER_INSANE;
            default: return SCORE_MULTIPLIER_MEDIUM;
        }
    }
    
    // Methods to change difficulty
    public static void setDifficulty(Difficulty difficulty) {
        currentDifficulty = difficulty;
    }
    
    public static Difficulty getCurrentDifficulty() {
        return currentDifficulty;
    }
    
    // Game state flags
    public static boolean debugMode = false;
    public static boolean showFPS = true;
} 