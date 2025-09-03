import java.awt.Rectangle;

public interface Enemy extends Runnable {
    
    /**
     * Update the enemy's state based on player's position
     * 
     * @param playerX The player's X position
     * @param playerY The player's Y position
     */
    void update(int playerX, int playerY);
    
    /**
     * Check if the enemy collides with the given coordinates
     * 
     * @param x The X position to check
     * @param y The Y position to check
     * @param size The size of the object to check collision with
     * @return true if collision occurs, false otherwise
     */
    boolean checkCollision(int x, int y, int size);
    
    /**
     * Check if the enemy collides with the given rectangle
     * 
     * @param rect The rectangle to check collision with
     * @return true if collision occurs, false otherwise
     */
    boolean checkCollision(Rectangle rect);
    
    /**
     * Called when the enemy is hit
     */
    void hit();
    
    /**
     * Check if the enemy is still active
     * 
     * @return true if active, false if dead or removed
     */
    boolean isActive();
    
    /**
     * Get the enemy's X position
     * 
     * @return The X position
     */
    int getX();
    
    /**
     * Get the enemy's Y position
     * 
     * @return The Y position
     */
    int getY();
    
    /**
     * Get the enemy's size
     * 
     * @return The size
     */
    int getSize();
    
    /**
     * Get the enemy's type
     * 
     * @return The enemy type
     */
    EnemyType getType();
    
    /**
     * Get the enemy's bounds as a rectangle
     * 
     * @return The rectangle representing the enemy's bounds
     */
    Rectangle getBounds();
    
    /**
     * Shutdown the enemy thread
     */
    void shutdown();
    
    /**
     * Enum representing different enemy types
     */
    enum EnemyType {
        TRACKER,
        BOMBER
    }
} 