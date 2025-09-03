import javax.sound.sampled.*;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.BufferedInputStream;
import java.net.URL;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Map;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.ConcurrentHashMap;

/**
 * مدیریت پخش موسیقی و افکت‌های صوتی بازی
 * Improved to load resources from classpath first, avoid creating dummy silent files,
 * and handle volume edge cases reliably.
 */
public class SoundManager {
    // انواع افکت‌های صوتی
    public enum SoundEffect {
        BALL_COLLECT("sounds/ball_collect.wav"),
        BALL_BOUNCE("sounds/ball_bounce.wav"),
        ENEMY_HIT("sounds/enemy_hit.wav"),
        ENEMY_DESTROY("sounds/enemy_destroy.wav"),
        BOMB_EXPLODE("sounds/bomb_explode.wav"),
        POWER_UP_COLLECT("sounds/power_up.wav"),
        GAME_OVER("sounds/game_over.wav"),
        MENU_SELECT("sounds/menu_select.wav"),
        MENU_CLICK("sounds/menu_click.wav");
        
        private final String path;
        
        SoundEffect(String path) {
            this.path = path;
        }
        
        public String getPath() {
            return path;
        }
    }
    
    // انواع موسیقی پس‌زمینه
    public enum Music {
        MENU("sounds/menu_music.wav"),
        GAMEPLAY("sounds/gameplay_music.wav"),
        GAME_OVER("sounds/game_over_music.wav");
        
        private final String path;
        
        Music(String path) {
            this.path = path;
        }
        
        public String getPath() {
            return path;
        }
    }
    
    // Using ConcurrentHashMap for thread safety
    private static final Map<SoundEffect, SoundClip> soundEffects = new ConcurrentHashMap<>();
    private static Clip currentMusic = null;
    private static final ExecutorService soundThreadPool = Executors.newCachedThreadPool();
    
    private static float musicVolume = 0.5f;
    private static float effectVolume = 0.7f;
    private static boolean musicEnabled = true;
    private static boolean effectsEnabled = true;
    private static boolean soundSystemInitialized = false;
    
    // بارگذاری افکت‌های صوتی
    static {
        initializeSoundSystem();
    }
    
    /**
     * راه‌اندازی سیستم صوتی
     */
    public static void initializeSoundSystem() {
        System.out.println("Initializing sound system...");
        
        try {
            // Check if sound system is available
            if (AudioSystem.getMixerInfo().length == 0) {
                System.err.println("No audio mixers available. Sound will be disabled.");
                soundSystemInitialized = false;
                return;
            }
            
            // بررسی وجود پوشه صوت
            Path soundDir = Paths.get("sounds");
            if (!Files.exists(soundDir)) {
                System.out.println("Warning: sounds directory not found at " + soundDir.toAbsolutePath() + ". Will try to load from classpath.");
            } else {
                System.out.println("Found sounds directory: " + soundDir.toAbsolutePath());
            }
            
            int loadedEffects = 0;
            int totalEffects = SoundEffect.values().length;
            
            // بارگذاری افکت‌های صوتی از کلاس‌پس یا فایل سیستم
            for (SoundEffect effect : SoundEffect.values()) {
                try {
                    AudioInputStream stream = openAudioStream(effect.getPath());
                    if (stream != null) {
                        SoundClip clip = new SoundClip(effect.getPath(), stream);
                        soundEffects.put(effect, clip);
                        loadedEffects++;
                        System.out.println("✓ Loaded sound effect: " + effect.name());
                    } else {
                        System.out.println("✗ Sound effect not found: " + effect.getPath());
                    }
                } catch (Exception e) {
                    System.err.println("✗ Error loading sound effect " + effect + ": " + e.getMessage());
                }
            }
            
            soundSystemInitialized = true;
            System.out.println("Sound system initialized successfully!");
            System.out.println("Loaded " + loadedEffects + "/" + totalEffects + " sound effects");
            System.out.println("Music enabled: " + musicEnabled + ", Effects enabled: " + effectsEnabled);
            System.out.println("Music volume: " + (int)(musicVolume * 100) + "%, Effects volume: " + (int)(effectVolume * 100) + "%");
            
        } catch (Exception e) {
            System.err.println("Error initializing sound system: " + e.getMessage());
            e.printStackTrace();
            soundSystemInitialized = false;
        }
    }
    
    /**
     * تلاش برای باز کردن استریم صوتی از کلاس‌پس یا فایل سیستم
     */
    private static AudioInputStream openAudioStream(String resourcePath) {
        try {
            // Try classpath (resources bundled with app)
            String normalized = resourcePath.startsWith("/") ? resourcePath : "/" + resourcePath;
            InputStream in = SoundManager.class.getResourceAsStream(normalized);
            if (in == null) {
                // Try context class loader
                ClassLoader cl = Thread.currentThread().getContextClassLoader();
                if (cl != null) {
                    in = cl.getResourceAsStream(resourcePath.startsWith("/") ? resourcePath.substring(1) : resourcePath);
                }
            }
            if (in != null) {
                return AudioSystem.getAudioInputStream(new BufferedInputStream(in));
            }
            
            // Fallback to filesystem relative path
            File f = new File(resourcePath);
            if (f.exists()) {
                return AudioSystem.getAudioInputStream(f);
            }
        } catch (Exception e) {
            System.err.println("Audio open error for " + resourcePath + ": " + e.getMessage());
        }
        return null;
    }
    
    /**
     * پخش افکت صوتی
     * @param effect افکت صوتی مورد نظر
     */
    public static void playSound(SoundEffect effect) {
        if (!soundSystemInitialized || !effectsEnabled || !soundEffects.containsKey(effect)) {
            return;
        }
        
        soundThreadPool.execute(() -> {
            try {
                SoundClip clip = soundEffects.get(effect);
                if (clip != null) {
                    clip.play(effectVolume);
                }
            } catch (Exception e) {
                System.err.println("Error playing sound effect: " + e.getMessage());
            }
        });
    }
    
    /**
     * پخش موسیقی پس‌زمینه
     * @param music نوع موسیقی مورد نظر
     * @param loop آیا موسیقی به صورت مداوم پخش شود
     */
    public static void playMusic(Music music, boolean loop) {
        if (!soundSystemInitialized || !musicEnabled) {
            return;
        }
        
        // توقف موسیقی قبلی
        stopMusic();
        
        soundThreadPool.execute(() -> {
            try {
                AudioInputStream audioStream = openAudioStream(music.getPath());
                if (audioStream == null) {
                    System.out.println("Music file not found: " + music.getPath());
                    return;
                }
                
                currentMusic = AudioSystem.getClip();
                currentMusic.open(audioStream);
                
                // تنظیم ولوم
                setClipVolume(currentMusic, musicVolume);
                
                // پخش و تکرار در صورت نیاز
                if (loop) {
                    currentMusic.loop(Clip.LOOP_CONTINUOUSLY);
                }
                currentMusic.start();
            } catch (Exception e) {
                System.err.println("Error playing music: " + e.getMessage());
            }
        });
    }
    
    /**
     * توقف موسیقی پس‌زمینه
     */
    public static void stopMusic() {
        try {
            if (currentMusic != null) {
                if (currentMusic.isRunning()) {
                    currentMusic.stop();
                }
                currentMusic.close();
            }
        } catch (Exception e) {
            System.err.println("Error stopping music: " + e.getMessage());
        } finally {
            currentMusic = null;
        }
    }
    
    /**
     * تنظیم ولوم موسیقی
     * @param volume ولوم جدید (بین 0.0 و 1.0)
     */
    public static void setMusicVolume(float volume) {
        musicVolume = Math.max(0.0f, Math.min(1.0f, volume));
        
        if (currentMusic != null) {
            setClipVolume(currentMusic, musicVolume);
        }
        
        GameConfig.musicVolume = musicVolume;
    }
    
    /**
     * تنظیم ولوم افکت‌های صوتی
     * @param volume ولوم جدید (بین 0.0 و 1.0)
     */
    public static void setEffectVolume(float volume) {
        effectVolume = Math.max(0.0f, Math.min(1.0f, volume));
        GameConfig.soundVolume = effectVolume;
    }
    
    /**
     * فعال/غیرفعال کردن موسیقی
     */
    public static void toggleMusic() {
        musicEnabled = !musicEnabled;
        
        if (!musicEnabled) {
            stopMusic();
        }
        
        GameConfig.musicEnabled = musicEnabled;
    }
    
    /**
     * فعال/غیرفعال کردن افکت‌های صوتی
     */
    public static void toggleSoundEffects() {
        effectsEnabled = !effectsEnabled;
        GameConfig.soundEnabled = effectsEnabled;
    }
    
    /**
     * تنظیم ولوم یک کلیپ صوتی
     */
    private static void setClipVolume(Clip clip, float volume) {
        try {
            if (clip.isControlSupported(FloatControl.Type.MASTER_GAIN)) {
                FloatControl gainControl = (FloatControl) clip.getControl(FloatControl.Type.MASTER_GAIN);
                // جلوگیری از log(0) که منجر به -Infinity می‌شود
                if (volume <= 0.0001f) {
                    gainControl.setValue(-80.0f); // حداقل ولوم
                } else {
                    float dB = (float) (Math.log10(volume) * 20.0);
                    gainControl.setValue(Math.max(-80.0f, Math.min(6.0f, dB)));
                }
            }
        } catch (Exception e) {
            System.err.println("Error setting volume: " + e.getMessage());
        }
    }
    
    /**
     * بستن و پاکسازی منابع صوتی
     */
    public static void cleanup() {
        stopMusic();
        
        for (SoundClip clip : soundEffects.values()) {
            clip.close();
        }
        
        soundEffects.clear();
        soundThreadPool.shutdown();
    }
    
    /**
     * بارگذاری مجدد فایل‌های صوتی
     */
    public static void reloadSounds() {
        cleanup();
        initializeSoundSystem();
    }
    
    /**
     * بررسی وضعیت سیستم صوتی
     */
    public static boolean isSoundSystemInitialized() {
        return soundSystemInitialized;
    }
    
    /**
     * دریافت تعداد افکت‌های صوتی بارگذاری شده
     */
    public static int getLoadedSoundsCount() {
        return soundEffects.size();
    }
    
    /**
     * نمایش وضعیت کامل سیستم صوتی
     */
    public static void printSoundSystemStatus() {
        System.out.println("\n=== Sound System Status ===");
        System.out.println("Initialized: " + soundSystemInitialized);
        System.out.println("Music enabled: " + musicEnabled + " (Volume: " + (int)(musicVolume * 100) + "%)");
        System.out.println("Effects enabled: " + effectsEnabled + " (Volume: " + (int)(effectVolume * 100) + "%)");
        System.out.println("Loaded sound effects: " + soundEffects.size() + "/" + SoundEffect.values().length);
        
        System.out.println("\nAvailable sound effects:");
        for (SoundEffect effect : SoundEffect.values()) {
            boolean loaded = soundEffects.containsKey(effect);
            System.out.println("  " + (loaded ? "✓" : "✗") + " " + effect.name() + " (" + effect.getPath() + ")");
        }
        
        System.out.println("\nMusic files:");
        for (Music music : Music.values()) {
            File musicFile = new File(music.getPath());
            boolean exists = musicFile.exists();
            System.out.println("  " + (exists ? "✓" : "✗") + " " + music.name() + " (" + music.getPath() + ")");
        }
        System.out.println("==============================\n");
    }
    
    /**
     * تست پخش تمام افکت‌های صوتی
     */
    public static void testAllSounds() {
        System.out.println("Testing all sound effects...");
        
        for (SoundEffect effect : SoundEffect.values()) {
            if (soundEffects.containsKey(effect)) {
                System.out.println("Playing: " + effect.name());
                playSound(effect);
                
                // Wait a bit between sounds
                try {
                    Thread.sleep(500);
                } catch (InterruptedException e) {
                    Thread.currentThread().interrupt();
                    break;
                }
            }
        }
        
        System.out.println("Sound test completed.");
    }
    
    /**
     * دریافت وضعیت موسیقی فعال
     */
    public static boolean isMusicEnabled() {
        return musicEnabled;
    }
    
    /**
     * دریافت وضعیت افکت‌های صوتی
     */
    public static boolean areEffectsEnabled() {
        return effectsEnabled;
    }
    
    /**
     * دریافت ولوم موسیقی فعلی
     */
    public static float getMusicVolume() {
        return musicVolume;
    }
    
    /**
     * دریافت ولوم افکت‌های صوتی فعلی
     */
    public static float getEffectVolume() {
        return effectVolume;
    }
    
    /**
     * بررسی اینکه آیا موسیقی در حال پخش است
     */
    public static boolean isMusicPlaying() {
        return currentMusic != null && currentMusic.isRunning();
    }
    
    /**
     * Modern sound clip implementation using Java Sound API
     */
    private static class SoundClip {
        private final String path;
        private byte[] audioData;
        private AudioFormat format;
        
        public SoundClip(String path, AudioInputStream stream) throws IOException {
            this.path = path;
            try (AudioInputStream s = stream) {
                format = s.getFormat();
                audioData = s.readAllBytes();
            }
        }
        
        public void play(float volume) {
            soundThreadPool.execute(() -> {
                try {
                    // Create a new clip for each playback to allow concurrent sounds
                    AudioInputStream audioStream = new AudioInputStream(
                        new java.io.ByteArrayInputStream(audioData),
                        format,
                        audioData.length / format.getFrameSize()
                    );
                    
                    Clip clip = AudioSystem.getClip();
                    clip.open(audioStream);
                    
                    // Set volume safely
                    setClipVolume(clip, volume);
                    
                    // Add listener to close the clip when done
                    clip.addLineListener(event -> {
                        if (event.getType() == LineEvent.Type.STOP || event.getType() == LineEvent.Type.CLOSE) {
                            try {
                                event.getLine().close();
                            } catch (Exception ignored) {}
                        }
                    });
                    
                    clip.start();
                } catch (Exception e) {
                    System.err.println("Error playing sound " + path + ": " + e.getMessage());
                }
            });
        }
        
        public void close() {
            // Clips are ephemeral and closed after playback
        }
    }
}
