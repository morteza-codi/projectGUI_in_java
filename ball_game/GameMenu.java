import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
import java.util.ArrayList;
import java.util.List;

/**
 * کلاس مدیریت منوهای بازی
 */
public class GameMenu {
    private enum MenuState {
        MAIN_MENU, OPTIONS, HIGH_SCORES, DIFFICULTY, CONTROLS, GAME_PAUSED, GAME_OVER, NAME_INPUT
    }
    
    private MenuState currentState = MenuState.MAIN_MENU;
    private MenuState previousState = MenuState.MAIN_MENU;
    private Game gameInstance;
    private int selectedIndex = 0;
    private boolean returnToGame = false;
    
    // منوی اصلی
    private final String[] mainMenuOptions = {
        "Start Game", "Options", "High Scores", "Controls", "Exit"
    };
    
    // منوی تنظیمات
    private final String[] optionsMenuOptions = {
        "Difficulty: " + GameConfig.getCurrentDifficulty().toString(),
        "Sound Effects: " + (GameConfig.soundEnabled ? "ON" : "OFF"),
        "Music: " + (GameConfig.musicEnabled ? "ON" : "OFF"),
        "Sound Volume: " + (int)(GameConfig.soundVolume * 100) + "%",
        "Music Volume: " + (int)(GameConfig.musicVolume * 100) + "%",
        "Show FPS: " + (GameConfig.showFPS ? "ON" : "OFF"),
        "Back"
    };
    
    // منوی انتخاب سختی
    private final String[] difficultyOptions = {
        "Easy", "Medium", "Hard", "Insane", "Back"
    };
    
    // منوی راهنمای کنترل‌ها
    private final String[] controlsOptions = {
        "Arrow Keys / WASD: Move",
        "P / ESC: Pause Game",
        "R: Restart (when game over)",
        "Back"
    };
    
    // منوی توقف بازی
    private final String[] pauseMenuOptions = {
        "Resume", "Options", "Restart", "Main Menu", "Exit"
    };
    
    // منوی پایان بازی
    private final String[] gameOverOptions = {
        "Play Again", "Save Score", "Main Menu", "Exit"
    };
    
    // عرض و ارتفاع دکمه‌های منو
    private final int BUTTON_WIDTH = 200;
    private final int BUTTON_HEIGHT = 40;
    private final int BUTTON_SPACING = 20;
    private final int MENU_Y_OFFSET = 150;
    
    // عرض و ارتفاع پنجره
    private int screenWidth;
    private int screenHeight;
    
    // نام بازیکن برای ذخیره امتیاز
    private String playerName = "";
    private int finalScore = 0;
    
    // آیا کلیدی فشرده شده و هنوز پردازش نشده
    private boolean keyProcessed = true;
    
    /**
     * ایجاد یک نمونه از منوی بازی
     * @param gameInstance نمونه بازی اصلی
     */
    public GameMenu(Game gameInstance, int width, int height) {
        this.gameInstance = gameInstance;
        this.screenWidth = width;
        this.screenHeight = height;
    }
    
    /**
     * سازنده عمومی برای کلاس‌های مختلف بازی
     * @param gameInstance نمونه بازی (می‌تواند Game یا GameRefactored باشد)
     */
    public GameMenu(Object gameInstance, int width, int height) {
        // اگر از نوع Game باشد، مستقیم استفاده می‌کنیم
        if (gameInstance instanceof Game) {
            this.gameInstance = (Game) gameInstance;
        } else {
            // برای سایر انواع، gameInstance را null قرار می‌دهیم
            // منوی عمومی بدون وابستگی به کلاس خاص
            this.gameInstance = null;
        }
        this.screenWidth = width;
        this.screenHeight = height;
    }
    
    /**
     * رسم منوی فعلی
     * @param g شیء گرافیکی برای رسم
     */
    public void render(Graphics g) {
        // رسم پس‌زمینه
        drawMenuBackground(g);
        
        // رسم منوی فعلی بر اساس وضعیت
        switch (currentState) {
            case MAIN_MENU:
                drawMainMenu(g);
                break;
            case OPTIONS:
                drawOptionsMenu(g);
                break;
            case HIGH_SCORES:
                drawHighScoresMenu(g);
                break;
            case DIFFICULTY:
                drawDifficultyMenu(g);
                break;
            case CONTROLS:
                drawControlsMenu(g);
                break;
            case GAME_PAUSED:
                drawPauseMenu(g);
                break;
            case GAME_OVER:
                drawGameOverMenu(g);
                break;
            case NAME_INPUT:
                drawNameInputMenu(g);
                break;
        }
    }
    
    /**
     * رسم پس‌زمینه منو
     */
    private void drawMenuBackground(Graphics g) {
        // پس‌زمینه نیمه‌شفاف
        g.setColor(new Color(0, 0, 0, 200));
        g.fillRect(0, 0, screenWidth, screenHeight);
        
        // عنوان بازی
        g.setFont(new Font("Arial", Font.BOLD, 48));
        g.setColor(Color.WHITE);
        String title = "Multi-Threaded Ball Game";
        FontMetrics metrics = g.getFontMetrics();
        int titleX = (screenWidth - metrics.stringWidth(title)) / 2;
        g.drawString(title, titleX, 100);
    }
    
    /**
     * رسم منوی اصلی
     */
    private void drawMainMenu(Graphics g) {
        drawMenu(g, mainMenuOptions, "Main Menu");
    }
    
    /**
     * رسم منوی تنظیمات
     */
    private void drawOptionsMenu(Graphics g) {
        // به‌روزرسانی گزینه‌های منوی تنظیمات
        updateOptionsMenu();
        
        drawMenu(g, optionsMenuOptions, "Options");
    }
    
    /**
     * به‌روزرسانی متن گزینه‌های منوی تنظیمات
     */
    private void updateOptionsMenu() {
        optionsMenuOptions[0] = "Difficulty: " + GameConfig.getCurrentDifficulty().toString();
        optionsMenuOptions[1] = "Sound Effects: " + (GameConfig.soundEnabled ? "ON" : "OFF");
        optionsMenuOptions[2] = "Music: " + (GameConfig.musicEnabled ? "ON" : "OFF");
        optionsMenuOptions[3] = "Sound Volume: " + (int)(GameConfig.soundVolume * 100) + "%";
        optionsMenuOptions[4] = "Music Volume: " + (int)(GameConfig.musicVolume * 100) + "%";
        optionsMenuOptions[5] = "Show FPS: " + (GameConfig.showFPS ? "ON" : "OFF");
    }
    
    /**
     * رسم منوی امتیازات برتر
     */
    private void drawHighScoresMenu(Graphics g) {
        g.setFont(new Font("Arial", Font.BOLD, 28));
        g.setColor(Color.WHITE);
        
        String title = "High Scores - " + GameConfig.getCurrentDifficulty().toString();
        FontMetrics metrics = g.getFontMetrics();
        int titleX = (screenWidth - metrics.stringWidth(title)) / 2;
        g.drawString(title, titleX, MENU_Y_OFFSET);
        
        // نمایش امتیازات برتر
        List<ScoreManager.ScoreEntry> scores = 
            ScoreManager.getHighScoresByDifficulty(GameConfig.getCurrentDifficulty());
        
        int yPos = MENU_Y_OFFSET + 60;
        g.setFont(new Font("Arial", Font.PLAIN, 18));
        
        if (scores.isEmpty()) {
            String noScores = "No high scores yet!";
            g.drawString(noScores, (screenWidth - metrics.stringWidth(noScores)) / 2, yPos);
        } else {
            int rank = 1;
            for (ScoreManager.ScoreEntry score : scores) {
                String scoreText = String.format("%d. %s - %d points - %s", 
                    rank++, score.getPlayerName(), score.getScore(), score.getFormattedDate());
                g.drawString(scoreText, (screenWidth - 350) / 2, yPos);
                yPos += 30;
                
                // نمایش حداکثر 10 امتیاز
                if (rank > 10) break;
            }
        }
        
        // دکمه بازگشت
        g.setFont(new Font("Arial", Font.BOLD, 20));
        String backOption = "Back";
        int optionWidth = g.getFontMetrics().stringWidth(backOption);
        int backX = (screenWidth - optionWidth) / 2;
        int backY = screenHeight - 100;
        
        if (selectedIndex == 0) {
            g.setColor(Color.YELLOW);
        } else {
            g.setColor(Color.WHITE);
        }
        g.drawString(backOption, backX, backY);
    }
    
    /**
     * رسم منوی انتخاب سختی
     */
    private void drawDifficultyMenu(Graphics g) {
        drawMenu(g, difficultyOptions, "Select Difficulty");
    }
    
    /**
     * رسم منوی راهنمای کنترل‌ها
     */
    private void drawControlsMenu(Graphics g) {
        drawMenu(g, controlsOptions, "Controls");
    }
    
    /**
     * رسم منوی توقف بازی
     */
    private void drawPauseMenu(Graphics g) {
        drawMenu(g, pauseMenuOptions, "Game Paused");
    }
    
    /**
     * رسم منوی پایان بازی
     */
    private void drawGameOverMenu(Graphics g) {
        g.setFont(new Font("Arial", Font.BOLD, 48));
        g.setColor(Color.RED);
        String gameOver = "GAME OVER";
        FontMetrics metrics = g.getFontMetrics();
        int titleX = (screenWidth - metrics.stringWidth(gameOver)) / 2;
        g.drawString(gameOver, titleX, 130);
        
        // نمایش امتیاز
        g.setFont(new Font("Arial", Font.BOLD, 24));
        g.setColor(Color.WHITE);
        String scoreText = "Final Score: " + finalScore;
        metrics = g.getFontMetrics();
        int scoreX = (screenWidth - metrics.stringWidth(scoreText)) / 2;
        g.drawString(scoreText, scoreX, 180);
        
        // نمایش آیا رکورد جدیدی ثبت شده
        if (ScoreManager.isHighScore(finalScore)) {
            g.setColor(Color.YELLOW);
            g.setFont(new Font("Arial", Font.BOLD, 20));
            String newHighScore = "New High Score!";
            metrics = g.getFontMetrics();
            int highScoreX = (screenWidth - metrics.stringWidth(newHighScore)) / 2;
            g.drawString(newHighScore, highScoreX, 210);
        }
        
        drawMenu(g, gameOverOptions, "");
    }
    
    /**
     * رسم منوی ورود نام
     */
    private void drawNameInputMenu(Graphics g) {
        g.setFont(new Font("Arial", Font.BOLD, 28));
        g.setColor(Color.WHITE);
        
        String title = "New High Score!";
        FontMetrics metrics = g.getFontMetrics();
        int titleX = (screenWidth - metrics.stringWidth(title)) / 2;
        g.drawString(title, titleX, MENU_Y_OFFSET);
        
        String prompt = "Enter Your Name:";
        metrics = g.getFontMetrics();
        int promptX = (screenWidth - metrics.stringWidth(prompt)) / 2;
        g.drawString(prompt, promptX, MENU_Y_OFFSET + 60);
        
        // نمایش فیلد ورود نام
        g.setFont(new Font("Arial", Font.PLAIN, 24));
        metrics = g.getFontMetrics();
        int fieldWidth = 300;
        int fieldHeight = 40;
        int fieldX = (screenWidth - fieldWidth) / 2;
        int fieldY = MENU_Y_OFFSET + 100;
        
        g.setColor(Color.DARK_GRAY);
        g.fillRect(fieldX, fieldY, fieldWidth, fieldHeight);
        
        g.setColor(Color.WHITE);
        g.drawRect(fieldX, fieldY, fieldWidth, fieldHeight);
        
        String nameToShow = playerName + "_";
        int nameX = fieldX + 10;
        int nameY = fieldY + 30;
        g.drawString(nameToShow, nameX, nameY);
        
        // دکمه تایید
        g.setFont(new Font("Arial", Font.BOLD, 24));
        String confirm = "Save";
        metrics = g.getFontMetrics();
        int confirmWidth = metrics.stringWidth(confirm);
        int confirmX = (screenWidth - confirmWidth) / 2;
        int confirmY = fieldY + 80;
        
        if (selectedIndex == 0) {
            g.setColor(Color.YELLOW);
        } else {
            g.setColor(Color.WHITE);
        }
        g.drawString(confirm, confirmX, confirmY);
    }
    
    /**
     * رسم یک منوی استاندارد
     */
    private void drawMenu(Graphics g, String[] options, String title) {
        if (!title.isEmpty()) {
            g.setFont(new Font("Arial", Font.BOLD, 28));
            g.setColor(Color.WHITE);
            FontMetrics metrics = g.getFontMetrics();
            int titleX = (screenWidth - metrics.stringWidth(title)) / 2;
            g.drawString(title, titleX, MENU_Y_OFFSET);
        }
        
        g.setFont(new Font("Arial", Font.BOLD, 20));
        
        int totalButtonsHeight = options.length * (BUTTON_HEIGHT + BUTTON_SPACING);
        int startY = MENU_Y_OFFSET + 80;
        
        for (int i = 0; i < options.length; i++) {
            int buttonY = startY + i * (BUTTON_HEIGHT + BUTTON_SPACING);
            
            FontMetrics metrics = g.getFontMetrics();
            int optionWidth = metrics.stringWidth(options[i]);
            int buttonX = (screenWidth - optionWidth) / 2;
            
            // رسم دکمه با رنگ مناسب
            if (i == selectedIndex) {
                g.setColor(Color.YELLOW);  // رنگ دکمه انتخاب شده
            } else {
                g.setColor(Color.WHITE);   // رنگ دکمه‌های دیگر
            }
            
            g.drawString(options[i], buttonX, buttonY);
        }
    }
    
    /**
     * پردازش فشردن کلیدهای کنترل منو
     * @param keyCode کد کلید فشرده شده
     */
    public void handleKeyPress(int keyCode) {
        if (!keyProcessed) {
            return; // اگر کلید قبلی هنوز پردازش نشده
        }
        
        switch (keyCode) {
            case KeyEvent.VK_UP:
            case KeyEvent.VK_W:
                navigateUp();
                keyProcessed = false;
                SoundManager.playSound(SoundManager.SoundEffect.MENU_SELECT);
                break;
                
            case KeyEvent.VK_DOWN:
            case KeyEvent.VK_S:
                navigateDown();
                keyProcessed = false;
                SoundManager.playSound(SoundManager.SoundEffect.MENU_SELECT);
                break;
                
            case KeyEvent.VK_ENTER:
            case KeyEvent.VK_SPACE:
                selectOption();
                keyProcessed = false;
                SoundManager.playSound(SoundManager.SoundEffect.MENU_CLICK);
                break;
                
            case KeyEvent.VK_ESCAPE:
                if (currentState != MenuState.MAIN_MENU && currentState != MenuState.GAME_OVER) {
                    goBack();
                    keyProcessed = false;
                    SoundManager.playSound(SoundManager.SoundEffect.MENU_CLICK);
                }
                break;
                
            // برای منوی ورود نام
            default:
                if (currentState == MenuState.NAME_INPUT) {
                    handleNameInput(keyCode);
                }
                break;
        }
    }
    
    /**
     * پردازش رها کردن کلید
     */
    public void handleKeyRelease() {
        keyProcessed = true;
    }
    
    /**
     * پردازش ورود نام
     */
    private void handleNameInput(int keyCode) {
        // اگر کلید حرف، عدد یا نشانه باشد
        if (playerName.length() < 20) {
            if ((keyCode >= KeyEvent.VK_A && keyCode <= KeyEvent.VK_Z) ||
                (keyCode >= KeyEvent.VK_0 && keyCode <= KeyEvent.VK_9) ||
                keyCode == KeyEvent.VK_SPACE || keyCode == KeyEvent.VK_MINUS || 
                keyCode == KeyEvent.VK_UNDERSCORE) {
                    
                char c = (char) keyCode;
                playerName += c;
            }
        }
        
        // برای پاک کردن حروف
        if (keyCode == KeyEvent.VK_BACK_SPACE && playerName.length() > 0) {
            playerName = playerName.substring(0, playerName.length() - 1);
        }
    }
    
    /**
     * حرکت به بالا در منو
     */
    private void navigateUp() {
        String[] currentMenu = getCurrentMenuOptions();
        if (selectedIndex > 0) {
            selectedIndex--;
        } else {
            selectedIndex = currentMenu.length - 1;
        }
    }
    
    /**
     * حرکت به پایین در منو
     */
    private void navigateDown() {
        String[] currentMenu = getCurrentMenuOptions();
        if (selectedIndex < currentMenu.length - 1) {
            selectedIndex++;
        } else {
            selectedIndex = 0;
        }
    }
    
    /**
     * انتخاب گزینه فعلی در منو
     */
    private void selectOption() {
        switch (currentState) {
            case MAIN_MENU:
                handleMainMenuSelection();
                break;
            case OPTIONS:
                handleOptionsMenuSelection();
                break;
            case HIGH_SCORES:
                // در منوی امتیازات برتر، فقط گزینه بازگشت وجود دارد
                goBack();
                break;
            case DIFFICULTY:
                handleDifficultySelection();
                break;
            case CONTROLS:
                // آخرین گزینه در منوی کنترل‌ها، بازگشت است
                if (selectedIndex == controlsOptions.length - 1) {
                    goBack();
                }
                break;
            case GAME_PAUSED:
                handlePauseMenuSelection();
                break;
            case GAME_OVER:
                handleGameOverSelection();
                break;
            case NAME_INPUT:
                if (selectedIndex == 0 && !playerName.isEmpty()) {
                    // ذخیره امتیاز با نام وارد شده
                    ScoreManager.addScore(finalScore, playerName, GameConfig.getCurrentDifficulty());
                    changeMenuState(MenuState.HIGH_SCORES);
                }
                break;
        }
    }
    
    /**
     * پردازش انتخاب در منوی اصلی
     */
    private void handleMainMenuSelection() {
        switch (selectedIndex) {
            case 0: // Start Game
                returnToGame = true;
                break;
            case 1: // Options
                changeMenuState(MenuState.OPTIONS);
                break;
            case 2: // High Scores
                changeMenuState(MenuState.HIGH_SCORES);
                break;
            case 3: // Controls
                changeMenuState(MenuState.CONTROLS);
                break;
            case 4: // Exit
                System.exit(0);
                break;
        }
    }
    
    /**
     * پردازش انتخاب در منوی تنظیمات
     */
    private void handleOptionsMenuSelection() {
        switch (selectedIndex) {
            case 0: // Difficulty
                changeMenuState(MenuState.DIFFICULTY);
                break;
            case 1: // Sound Effects
                GameConfig.soundEnabled = !GameConfig.soundEnabled;
                SoundManager.toggleSoundEffects();
                updateOptionsMenu();
                break;
            case 2: // Music
                GameConfig.musicEnabled = !GameConfig.musicEnabled;
                SoundManager.toggleMusic();
                updateOptionsMenu();
                break;
            case 3: // Sound Volume
                // افزایش حجم صدا در گام‌های 10%
                GameConfig.soundVolume = (GameConfig.soundVolume + 0.1f) % 1.05f;
                SoundManager.setEffectVolume(GameConfig.soundVolume);
                updateOptionsMenu();
                break;
            case 4: // Music Volume
                GameConfig.musicVolume = (GameConfig.musicVolume + 0.1f) % 1.05f;
                SoundManager.setMusicVolume(GameConfig.musicVolume);
                updateOptionsMenu();
                break;
            case 5: // Show FPS
                GameConfig.showFPS = !GameConfig.showFPS;
                updateOptionsMenu();
                break;
            case 6: // Back
                goBack();
                break;
        }
    }
    
    /**
     * پردازش انتخاب سطح سختی
     */
    private void handleDifficultySelection() {
        switch (selectedIndex) {
            case 0: // Easy
                GameConfig.setDifficulty(GameConfig.Difficulty.EASY);
                goBack();
                break;
            case 1: // Medium
                GameConfig.setDifficulty(GameConfig.Difficulty.MEDIUM);
                goBack();
                break;
            case 2: // Hard
                GameConfig.setDifficulty(GameConfig.Difficulty.HARD);
                goBack();
                break;
            case 3: // Insane
                GameConfig.setDifficulty(GameConfig.Difficulty.INSANE);
                goBack();
                break;
            case 4: // Back
                goBack();
                break;
        }
    }
    
    /**
     * پردازش انتخاب در منوی توقف
     */
    private void handlePauseMenuSelection() {
        switch (selectedIndex) {
            case 0: // Resume
                returnToGame = true;
                break;
            case 1: // Options
                previousState = MenuState.GAME_PAUSED;
                changeMenuState(MenuState.OPTIONS);
                break;
            case 2: // Restart
                gameInstance.restartGame();
                returnToGame = true;
                break;
            case 3: // Main Menu
                changeMenuState(MenuState.MAIN_MENU);
                break;
            case 4: // Exit
                System.exit(0);
                break;
        }
    }
    
    /**
     * پردازش انتخاب در منوی پایان بازی
     */
    private void handleGameOverSelection() {
        switch (selectedIndex) {
            case 0: // Play Again
                gameInstance.restartGame();
                returnToGame = true;
                break;
            case 1: // Save Score
                if (ScoreManager.isHighScore(finalScore)) {
                    playerName = "";
                    changeMenuState(MenuState.NAME_INPUT);
                } else {
                    // اگر امتیاز بالا نباشد، به منوی امتیازات برتر برو
                    changeMenuState(MenuState.HIGH_SCORES);
                }
                break;
            case 2: // Main Menu
                changeMenuState(MenuState.MAIN_MENU);
                break;
            case 3: // Exit
                System.exit(0);
                break;
        }
    }
    
    /**
     * بازگشت به منوی قبلی
     */
    private void goBack() {
        // اگر از منوی تنظیمات در حالت توقف بازی آمده باشیم
        if (currentState == MenuState.OPTIONS && previousState == MenuState.GAME_PAUSED) {
            changeMenuState(MenuState.GAME_PAUSED);
            return;
        }
        
        // در غیر این صورت به منوی اصلی برمی‌گردیم
        if (currentState != MenuState.MAIN_MENU) {
            changeMenuState(previousState);
        }
    }
    
    /**
     * تغییر حالت منو
     */
    private void changeMenuState(MenuState newState) {
        previousState = currentState;
        currentState = newState;
        selectedIndex = 0; // بازنشانی انتخاب
    }
    
    /**
     * دریافت گزینه‌های منوی فعلی
     */
    private String[] getCurrentMenuOptions() {
        switch (currentState) {
            case MAIN_MENU:
                return mainMenuOptions;
            case OPTIONS:
                return optionsMenuOptions;
            case HIGH_SCORES:
                return new String[]{"Back"};
            case DIFFICULTY:
                return difficultyOptions;
            case CONTROLS:
                return controlsOptions;
            case GAME_PAUSED:
                return pauseMenuOptions;
            case GAME_OVER:
                return gameOverOptions;
            case NAME_INPUT:
                return new String[]{"Save"};
            default:
                return mainMenuOptions;
        }
    }
    
    /**
     * بررسی آیا باید به بازی بازگردیم
     */
    public boolean shouldReturnToGame() {
        if (returnToGame) {
            returnToGame = false;
            return true;
        }
        return false;
    }
    
    /**
     * نمایش منوی توقف بازی
     */
    public void showPauseMenu() {
        changeMenuState(MenuState.GAME_PAUSED);
    }
    
    /**
     * نمایش منوی پایان بازی
     */
    public void showGameOverMenu(int score) {
        this.finalScore = score;
        changeMenuState(MenuState.GAME_OVER);
    }
    
    /**
     * نمایش منوی اصلی
     */
    public void showMainMenu() {
        changeMenuState(MenuState.MAIN_MENU);
    }
    
    /**
     * دریافت وضعیت فعلی منو
     */
    public MenuState getCurrentState() {
        return currentState;
    }
}
