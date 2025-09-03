/**
 * Simple test program to verify the sound system is working
 */
public class SoundTest {
    public static void main(String[] args) {
        System.out.println("=== Sound System Test ===\n");
        
        // Print initial sound system status
        SoundManager.printSoundSystemStatus();
        
        // Test if sound system is initialized
        if (SoundManager.isSoundSystemInitialized()) {
            System.out.println("✓ Sound system is properly initialized!");
            
            // Test a simple sound effect
            if (SoundManager.getLoadedSoundsCount() > 0) {
                System.out.println("\nTesting ball collect sound...");
                SoundManager.playSound(SoundManager.SoundEffect.BALL_COLLECT);
                
                // Wait a moment
                try {
                    Thread.sleep(1000);
                } catch (InterruptedException e) {
                    Thread.currentThread().interrupt();
                }
                
                System.out.println("Testing enemy hit sound...");
                SoundManager.playSound(SoundManager.SoundEffect.ENEMY_HIT);
                
                // Wait a moment
                try {
                    Thread.sleep(1000);
                } catch (InterruptedException e) {
                    Thread.currentThread().interrupt();
                }
                
                System.out.println("Testing power up sound...");
                SoundManager.playSound(SoundManager.SoundEffect.POWER_UP_COLLECT);
                
                // Wait a moment
                try {
                    Thread.sleep(1000);
                } catch (InterruptedException e) {
                    Thread.currentThread().interrupt();
                }
                
                System.out.println("\nTesting background music...");
                SoundManager.playMusic(SoundManager.Music.MENU, false);
                
                // Wait for music to play for a bit
                try {
                    Thread.sleep(3000);
                } catch (InterruptedException e) {
                    Thread.currentThread().interrupt();
                }
                
                SoundManager.stopMusic();
                System.out.println("Music test completed.");
                
            } else {
                System.out.println("✗ No sound effects loaded. Sound files may be missing.");
            }
            
        } else {
            System.out.println("✗ Sound system failed to initialize!");
        }
        
        // Clean up
        System.out.println("\nCleaning up sound system...");
        SoundManager.cleanup();
        System.out.println("✓ Sound test completed successfully!");
    }
}
