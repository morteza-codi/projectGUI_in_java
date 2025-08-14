import java.util.concurrent.CopyOnWriteArrayList;

/**
 * کلاس مدیریت توپ‌ها
 */
public class BallManager {
    private CopyOnWriteArrayList<Ball> balls;
    private ThreadManager threadManager;
    
    public BallManager(CopyOnWriteArrayList<Ball> balls, ThreadManager threadManager) {
        this.balls = balls;
        this.threadManager = threadManager;
    }
    
    /**
     * ایجاد توپ جدید
     */
    public void createNewBall() {
        if (balls.size() < GameConfig.getMaxBalls()) {
            Ball ball = new Ball();
            balls.add(ball);
            threadManager.executeBall(ball);
        }
    }
    
    /**
     * ایجاد توپ‌های اولیه
     */
    public void createInitialBalls() {
        for (int i = 0; i < GameConfig.getInitialBalls(); i++) {
            createNewBall();
        }
    }
    
    /**
     * پاکسازی همه توپ‌ها
     */
    public void clearAllBalls() {
        for (Ball ball : balls) {
            ball.deactivate();
        }
        balls.clear();
    }
    
    /**
     * اضافه کردن توپ‌های تقسیم‌شده
     */
    public void addSplitBalls(Ball[] newBalls) {
        for (Ball newBall : newBalls) {
            if (balls.size() < GameConfig.getMaxBalls()) {
                balls.add(newBall);
                threadManager.executeBall(newBall);
            }
        }
    }
    
    /**
     * دریافت تعداد توپ‌های فعال
     */
    public int getActiveBallCount() {
        int count = 0;
        for (Ball ball : balls) {
            if (ball.isActive()) {
                count++;
            }
        }
        return count;
    }
    
    /**
     * دریافت لیست توپ‌ها
     */
    public CopyOnWriteArrayList<Ball> getBalls() {
        return balls;
    }
}
