import java.awt.AlphaComposite;
import java.awt.Color;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Point;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Random;
import java.util.concurrent.CopyOnWriteArrayList;

/**
 * مدیریت جلوه‌های ویژه بصری بازی
 */
public class EffectManager {
    private static final Random random = new Random();
    private static final CopyOnWriteArrayList<Effect> effects = new CopyOnWriteArrayList<>();
    
    /**
     * به‌روزرسانی همه جلوه‌های ویژه فعال
     */
    public static void update() {
        Iterator<Effect> iterator = effects.iterator();
        while (iterator.hasNext()) {
            Effect effect = iterator.next();
            effect.update();
            
            if (effect.isFinished()) {
                effects.remove(effect);
            }
        }
    }
    
    /**
     * رسم همه جلوه‌های ویژه فعال
     */
    public static void render(Graphics g) {
        for (Effect effect : effects) {
            effect.render(g);
        }
    }
    
    /**
     * اضافه کردن جلوه انفجار
     */
    public static void addExplosion(int x, int y, int size, Color color) {
        effects.add(new ExplosionEffect(x, y, size, color));
        
        // اضافه کردن ذرات پراکنده
        for (int i = 0; i < size / 5; i++) {
            effects.add(new ParticleEffect(
                x + random.nextInt(size) - size / 2, 
                y + random.nextInt(size) - size / 2,
                color,
                random.nextInt(20) + 10));
        }
    }
    
    /**
     * اضافه کردن جلوه جمع‌آوری توپ
     */
    public static void addBallCollectEffect(int x, int y, int size) {
        effects.add(new ScorePopupEffect(x, y, "+" + GameConfig.getScoreBall(), Color.WHITE));
        
        // افکت دایره‌ای اطراف نقطه جمع‌آوری
        effects.add(new RippleEffect(x, y, size * 2, Color.WHITE));
        
        // اضافه کردن ذرات پراکنده
        for (int i = 0; i < 10; i++) {
            effects.add(new ParticleEffect(
                x + random.nextInt(size) - size / 2, 
                y + random.nextInt(size) - size / 2,
                Color.RED,
                random.nextInt(15) + 5));
        }
    }
    
    /**
     * اضافه کردن جلوه جمع‌آوری قدرت
     */
    public static void addPowerUpEffect(int x, int y, Color color) {
        effects.add(new RippleEffect(x, y, 60, color));
        effects.add(new ScorePopupEffect(x, y, "POWER UP!", color));
        
        // ایجاد ذرات متناسب با نوع قدرت
        for (int i = 0; i < 15; i++) {
            effects.add(new ParticleEffect(
                x + random.nextInt(30) - 15, 
                y + random.nextInt(30) - 15,
                color,
                random.nextInt(20) + 15));
        }
    }
    
    /**
     * اضافه کردن جلوه برخورد با دشمن
     */
    public static void addEnemyHitEffect(int x, int y, int score) {
        effects.add(new ScorePopupEffect(x, y, "+" + score, Color.ORANGE));
        
        // ایجاد ذرات متناسب با برخورد
        for (int i = 0; i < 8; i++) {
            effects.add(new ParticleEffect(
                x + random.nextInt(20) - 10, 
                y + random.nextInt(20) - 10,
                Color.ORANGE,
                random.nextInt(10) + 5));
        }
    }
    
    /**
     * اضافه کردن جلوه نابودی دشمن
     */
    public static void addEnemyDestroyEffect(int x, int y, int size, Color color) {
        effects.add(new ExplosionEffect(x, y, size * 2, color));
        
        // اضافه کردن ذرات پراکنده
        for (int i = 0; i < size; i++) {
            effects.add(new ParticleEffect(
                x + random.nextInt(size) - size / 2, 
                y + random.nextInt(size) - size / 2,
                color,
                random.nextInt(30) + 10));
        }
    }
    
    /**
     * پاک کردن همه جلوه‌های ویژه فعال
     */
    public static void clearEffects() {
        effects.clear();
    }
    
    // کلاس پایه برای همه جلوه‌های ویژه
    private static abstract class Effect {
        protected int x, y;
        protected int lifetime;
        protected int maxLifetime;
        protected Color color;
        
        public Effect(int x, int y, Color color, int maxLifetime) {
            this.x = x;
            this.y = y;
            this.color = color;
            this.lifetime = 0;
            this.maxLifetime = maxLifetime;
        }
        
        public void update() {
            lifetime++;
        }
        
        public boolean isFinished() {
            return lifetime >= maxLifetime;
        }
        
        public abstract void render(Graphics g);
        
        protected float getAlpha() {
            return 1.0f - (float)lifetime / maxLifetime;
        }
    }
    
    // جلوه انفجار
    private static class ExplosionEffect extends Effect {
        private int size;
        private int initialSize;
        
        public ExplosionEffect(int x, int y, int size, Color color) {
            super(x, y, color, 20);
            this.initialSize = size;
            this.size = 0;
        }
        
        @Override
        public void update() {
            super.update();
            size = (int)(initialSize * ((float)lifetime / maxLifetime) * 2);
        }
        
        @Override
        public void render(Graphics g) {
            Graphics2D g2d = (Graphics2D)g;
            
            // تنظیم شفافیت
            AlphaComposite alphaComposite = AlphaComposite.getInstance(
                AlphaComposite.SRC_OVER, getAlpha());
            g2d.setComposite(alphaComposite);
            
            // رسم دایره انفجار
            g2d.setColor(color);
            g2d.fillOval(x - size/2, y - size/2, size, size);
            
            // بازگرداندن شفافیت به حالت عادی
            g2d.setComposite(AlphaComposite.getInstance(AlphaComposite.SRC_OVER, 1.0f));
        }
    }
    
    // جلوه ذرات
    private static class ParticleEffect extends Effect {
        private int size;
        private int initialSize;
        private double xSpeed, ySpeed;
        
        public ParticleEffect(int x, int y, Color color, int maxLifetime) {
            super(x, y, color, maxLifetime);
            this.initialSize = 5;
            this.size = initialSize;
            
            // سرعت تصادفی
            this.xSpeed = (random.nextDouble() - 0.5) * 4.0;
            this.ySpeed = (random.nextDouble() - 0.5) * 4.0;
        }
        
        @Override
        public void update() {
            super.update();
            
            // کوچک‌تر شدن ذره با گذشت زمان
            size = (int)(initialSize * (1 - (float)lifetime / maxLifetime));
            
            // حرکت ذره
            x += xSpeed;
            y += ySpeed;
        }
        
        @Override
        public void render(Graphics g) {
            Graphics2D g2d = (Graphics2D)g;
            
            // تنظیم شفافیت
            AlphaComposite alphaComposite = AlphaComposite.getInstance(
                AlphaComposite.SRC_OVER, getAlpha());
            g2d.setComposite(alphaComposite);
            
            // رسم ذره
            g2d.setColor(color);
            g2d.fillOval(x - size/2, y - size/2, size, size);
            
            // بازگرداندن شفافیت به حالت عادی
            g2d.setComposite(AlphaComposite.getInstance(AlphaComposite.SRC_OVER, 1.0f));
        }
    }
    
    // جلوه نمایش امتیاز
    private static class ScorePopupEffect extends Effect {
        private String text;
        private int yOffset;
        
        public ScorePopupEffect(int x, int y, String text, Color color) {
            super(x, y, color, 60);
            this.text = text;
            this.yOffset = 0;
        }
        
        @Override
        public void update() {
            super.update();
            yOffset -= 1; // حرکت به سمت بالا
        }
        
        @Override
        public void render(Graphics g) {
            Graphics2D g2d = (Graphics2D)g;
            
            // تنظیم شفافیت
            AlphaComposite alphaComposite = AlphaComposite.getInstance(
                AlphaComposite.SRC_OVER, getAlpha());
            g2d.setComposite(alphaComposite);
            
            // رسم متن
            g2d.setColor(color);
            g2d.drawString(text, x, y + yOffset);
            
            // بازگرداندن شفافیت به حالت عادی
            g2d.setComposite(AlphaComposite.getInstance(AlphaComposite.SRC_OVER, 1.0f));
        }
    }
    
    // جلوه موج دایره‌ای
    private static class RippleEffect extends Effect {
        private int size;
        private int maxSize;
        
        public RippleEffect(int x, int y, int maxSize, Color color) {
            super(x, y, color, 20);
            this.size = 5;
            this.maxSize = maxSize;
        }
        
        @Override
        public void update() {
            super.update();
            size = (int)(maxSize * ((float)lifetime / maxLifetime));
        }
        
        @Override
        public void render(Graphics g) {
            Graphics2D g2d = (Graphics2D)g;
            
            // تنظیم شفافیت
            AlphaComposite alphaComposite = AlphaComposite.getInstance(
                AlphaComposite.SRC_OVER, getAlpha());
            g2d.setComposite(alphaComposite);
            
            // رسم دایره توخالی
            g2d.setColor(color);
            g2d.drawOval(x - size/2, y - size/2, size, size);
            
            // بازگرداندن شفافیت به حالت عادی
            g2d.setComposite(AlphaComposite.getInstance(AlphaComposite.SRC_OVER, 1.0f));
        }
    }
} 