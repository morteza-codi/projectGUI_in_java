import java.io.*;
import java.nio.file.*;
import java.util.*;
import java.text.SimpleDateFormat;

/**
 * مدیریت امتیازات بازی و ذخیره جدول امتیازات برتر
 */
public class ScoreManager {
    private static final String HIGH_SCORES_FILE = "high_scores.dat";
    private static final int MAX_HIGH_SCORES = 10;
    
    private static List<ScoreEntry> highScores = new ArrayList<>();
    private static boolean scoresLoaded = false;
    
    /**
     * امتیاز جدید را اضافه می‌کند و بررسی می‌کند آیا در جدول امتیازات برتر قرار می‌گیرد
     * @param score امتیاز کسب شده
     * @param playerName نام بازیکن
     * @param difficulty سطح سختی بازی
     * @return true اگر امتیاز جزو امتیازات برتر باشد
     */
    public static boolean addScore(int score, String playerName, GameConfig.Difficulty difficulty) {
        // اطمینان از بارگذاری امتیازات
        if (!scoresLoaded) {
            loadHighScores();
        }
        
        // ایجاد رکورد جدید
        ScoreEntry newEntry = new ScoreEntry(score, playerName, difficulty, new Date());
        
        // اگر لیست خالی است یا امتیاز بزرگتر از کمترین امتیاز در لیست است
        boolean isHighScore = false;
        if (highScores.size() < MAX_HIGH_SCORES) {
            highScores.add(newEntry);
            isHighScore = true;
        } else if (score > highScores.get(highScores.size() - 1).getScore()) {
            highScores.remove(highScores.size() - 1);
            highScores.add(newEntry);
            isHighScore = true;
        }
        
        // مرتب‌سازی لیست
        if (isHighScore) {
            Collections.sort(highScores, (a, b) -> Integer.compare(b.getScore(), a.getScore()));
            saveHighScores();
        }
        
        return isHighScore;
    }
    
    /**
     * بررسی می‌کند آیا امتیاز داده شده جزو امتیازات برتر است
     */
    public static boolean isHighScore(int score) {
        if (!scoresLoaded) {
            loadHighScores();
        }
        
        return highScores.size() < MAX_HIGH_SCORES || score > highScores.get(highScores.size() - 1).getScore();
    }
    
    /**
     * لیست امتیازات برتر را برمی‌گرداند
     */
    public static List<ScoreEntry> getHighScores() {
        if (!scoresLoaded) {
            loadHighScores();
        }
        return new ArrayList<>(highScores);
    }
    
    /**
     * فیلتر کردن امتیازات برتر براساس سطح سختی
     */
    public static List<ScoreEntry> getHighScoresByDifficulty(GameConfig.Difficulty difficulty) {
        if (!scoresLoaded) {
            loadHighScores();
        }
        
        List<ScoreEntry> filteredScores = new ArrayList<>();
        for (ScoreEntry entry : highScores) {
            if (entry.getDifficulty() == difficulty) {
                filteredScores.add(entry);
            }
        }
        
        return filteredScores;
    }
    
    /**
     * بارگذاری امتیازات برتر از فایل
     */
    private static void loadHighScores() {
        highScores.clear();
        
        try {
            if (Files.exists(Paths.get(HIGH_SCORES_FILE))) {
                try (ObjectInputStream ois = new ObjectInputStream(new FileInputStream(HIGH_SCORES_FILE))) {
                    @SuppressWarnings("unchecked")
                    List<ScoreEntry> loadedScores = (List<ScoreEntry>) ois.readObject();
                    highScores.addAll(loadedScores);
                }
            }
        } catch (Exception e) {
            System.err.println("Error loading high scores: " + e.getMessage());
            // ایجاد فایل جدید در صورت مشکل
            saveHighScores();
        }
        
        scoresLoaded = true;
    }
    
    /**
     * ذخیره امتیازات برتر در فایل
     */
    private static void saveHighScores() {
        try (ObjectOutputStream oos = new ObjectOutputStream(new FileOutputStream(HIGH_SCORES_FILE))) {
            oos.writeObject(highScores);
        } catch (IOException e) {
            System.err.println("Error saving high scores: " + e.getMessage());
        }
    }
    
    /**
     * کلاس داخلی برای نگهداری اطلاعات یک رکورد امتیاز
     */
    public static class ScoreEntry implements Serializable {
        private static final long serialVersionUID = 1L;
        
        private final int score;
        private final String playerName;
        private final GameConfig.Difficulty difficulty;
        private final Date date;
        
        public ScoreEntry(int score, String playerName, GameConfig.Difficulty difficulty, Date date) {
            this.score = score;
            this.playerName = playerName;
            this.difficulty = difficulty;
            this.date = date;
        }
        
        public int getScore() {
            return score;
        }
        
        public String getPlayerName() {
            return playerName;
        }
        
        public GameConfig.Difficulty getDifficulty() {
            return difficulty;
        }
        
        public Date getDate() {
            return date;
        }
        
        public String getFormattedDate() {
            SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm");
            return sdf.format(date);
        }
        
        @Override
        public String toString() {
            return String.format("%s - %d points (%s) - %s", 
                playerName, score, difficulty.toString(), getFormattedDate());
        }
    }
} 