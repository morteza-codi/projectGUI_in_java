import javax.sound.sampled.*;
import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;

/**
 * Utility class to generate simple WAV sound files for the game
 */
public class SoundGenerator {
    
    public static void main(String[] args) {
        try {
            // Create sounds directory if it doesn't exist
            Files.createDirectories(Paths.get("sounds"));
            
            // Generate all required sound effects
            generateBallCollectSound();
            generateBallBounceSound();
            generateEnemyHitSound();
            generateEnemyDestroySound();
            generateBombExplodeSound();
            generatePowerUpSound();
            generateGameOverSound();
            generateMenuSelectSound();
            generateMenuClickSound();
            
            // Generate background music
            generateMenuMusic();
            generateGameplayMusic();
            generateGameOverMusic();
            
            System.out.println("All sound files generated successfully!");
            
        } catch (Exception e) {
            System.err.println("Error generating sound files: " + e.getMessage());
            e.printStackTrace();
        }
    }
    
    /**
     * Generate a simple tone-based sound
     */
    private static void generateToneSound(String filename, double frequency, int durationMs, double volume) {
        try {
            int sampleRate = 44100;
            int samples = (int) (sampleRate * durationMs / 1000.0);
            byte[] audioData = new byte[samples * 2]; // 16-bit mono
            
            for (int i = 0; i < samples; i++) {
                double time = (double) i / sampleRate;
                double amplitude = Math.sin(2 * Math.PI * frequency * time) * volume;
                // Apply envelope to avoid clicks
                double envelope = Math.min(1.0, Math.min(time * 10, (durationMs / 1000.0 - time) * 10));
                amplitude *= envelope;
                
                short sample = (short) (amplitude * Short.MAX_VALUE);
                audioData[i * 2] = (byte) (sample & 0xFF);
                audioData[i * 2 + 1] = (byte) ((sample >> 8) & 0xFF);
            }
            
            // Create audio format
            AudioFormat format = new AudioFormat(sampleRate, 16, 1, true, false);
            AudioInputStream audioStream = new AudioInputStream(
                new ByteArrayInputStream(audioData), format, samples);
            
            // Write to file
            AudioSystem.write(audioStream, AudioFileFormat.Type.WAVE, new File("sounds/" + filename));
            System.out.println("Generated: " + filename);
            
        } catch (Exception e) {
            System.err.println("Error generating " + filename + ": " + e.getMessage());
        }
    }
    
    /**
     * Generate a chord-based sound
     */
    private static void generateChordSound(String filename, double[] frequencies, int durationMs, double volume) {
        try {
            int sampleRate = 44100;
            int samples = (int) (sampleRate * durationMs / 1000.0);
            byte[] audioData = new byte[samples * 2]; // 16-bit mono
            
            for (int i = 0; i < samples; i++) {
                double time = (double) i / sampleRate;
                double amplitude = 0;
                
                // Sum all frequencies
                for (double freq : frequencies) {
                    amplitude += Math.sin(2 * Math.PI * freq * time);
                }
                amplitude = amplitude / frequencies.length * volume;
                
                // Apply envelope
                double envelope = Math.min(1.0, Math.min(time * 5, (durationMs / 1000.0 - time) * 5));
                amplitude *= envelope;
                
                short sample = (short) (amplitude * Short.MAX_VALUE);
                audioData[i * 2] = (byte) (sample & 0xFF);
                audioData[i * 2 + 1] = (byte) ((sample >> 8) & 0xFF);
            }
            
            AudioFormat format = new AudioFormat(sampleRate, 16, 1, true, false);
            AudioInputStream audioStream = new AudioInputStream(
                new ByteArrayInputStream(audioData), format, samples);
            
            AudioSystem.write(audioStream, AudioFileFormat.Type.WAVE, new File("sounds/" + filename));
            System.out.println("Generated: " + filename);
            
        } catch (Exception e) {
            System.err.println("Error generating " + filename + ": " + e.getMessage());
        }
    }
    
    /**
     * Generate noise-based sound
     */
    private static void generateNoiseSound(String filename, int durationMs, double volume, boolean filtered) {
        try {
            int sampleRate = 44100;
            int samples = (int) (sampleRate * durationMs / 1000.0);
            byte[] audioData = new byte[samples * 2];
            
            java.util.Random random = new java.util.Random();
            double lastValue = 0;
            
            for (int i = 0; i < samples; i++) {
                double time = (double) i / sampleRate;
                double amplitude;
                
                if (filtered) {
                    // Low-pass filtered noise
                    double noise = (random.nextDouble() - 0.5) * 2;
                    amplitude = lastValue * 0.8 + noise * 0.2;
                    lastValue = amplitude;
                } else {
                    amplitude = (random.nextDouble() - 0.5) * 2;
                }
                
                amplitude *= volume;
                
                // Apply envelope
                double envelope = Math.min(1.0, Math.min(time * 20, (durationMs / 1000.0 - time) * 10));
                amplitude *= envelope;
                
                short sample = (short) (amplitude * Short.MAX_VALUE);
                audioData[i * 2] = (byte) (sample & 0xFF);
                audioData[i * 2 + 1] = (byte) ((sample >> 8) & 0xFF);
            }
            
            AudioFormat format = new AudioFormat(sampleRate, 16, 1, true, false);
            AudioInputStream audioStream = new AudioInputStream(
                new ByteArrayInputStream(audioData), format, samples);
            
            AudioSystem.write(audioStream, AudioFileFormat.Type.WAVE, new File("sounds/" + filename));
            System.out.println("Generated: " + filename);
            
        } catch (Exception e) {
            System.err.println("Error generating " + filename + ": " + e.getMessage());
        }
    }
    
    // Individual sound generation methods
    private static void generateBallCollectSound() {
        generateToneSound("ball_collect.wav", 800, 150, 0.3);
    }
    
    private static void generateBallBounceSound() {
        generateToneSound("ball_bounce.wav", 400, 100, 0.2);
    }
    
    private static void generateEnemyHitSound() {
        generateNoiseSound("enemy_hit.wav", 200, 0.4, true);
    }
    
    private static void generateEnemyDestroySound() {
        generateNoiseSound("enemy_destroy.wav", 500, 0.5, false);
    }
    
    private static void generateBombExplodeSound() {
        generateNoiseSound("bomb_explode.wav", 800, 0.6, false);
    }
    
    private static void generatePowerUpSound() {
        generateChordSound("power_up.wav", new double[]{523.25, 659.25, 783.99}, 300, 0.3);
    }
    
    private static void generateGameOverSound() {
        generateChordSound("game_over.wav", new double[]{220, 196, 174.61}, 1000, 0.4);
    }
    
    private static void generateMenuSelectSound() {
        generateToneSound("menu_select.wav", 600, 100, 0.2);
    }
    
    private static void generateMenuClickSound() {
        generateToneSound("menu_click.wav", 1000, 50, 0.15);
    }
    
    private static void generateMenuMusic() {
        // Simple melody for menu
        generateToneSound("menu_music.wav", 440, 2000, 0.1);
    }
    
    private static void generateGameplayMusic() {
        // Upbeat tone for gameplay
        generateToneSound("gameplay_music.wav", 523.25, 3000, 0.08);
    }
    
    private static void generateGameOverMusic() {
        // Somber tone for game over
        generateToneSound("game_over_music.wav", 220, 2000, 0.1);
    }
}
