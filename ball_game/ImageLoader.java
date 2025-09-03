import java.awt.Image;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import javax.imageio.ImageIO;

/**
 * کلاس بارگذاری تصاویر
 */
public class ImageLoader {
    
    /**
     * بارگذاری تصویر از مسیر نسبی
     */
    public static Image loadImage(String relativePath) {
        try {
            String normalized = relativePath.startsWith("/") ? relativePath : "/" + relativePath;
            InputStream in = ImageLoader.class.getResourceAsStream(normalized);
            
            if (in == null) {
                ClassLoader cl = Thread.currentThread().getContextClassLoader();
                if (cl != null) {
                    in = cl.getResourceAsStream(relativePath.startsWith("/") ? relativePath.substring(1) : relativePath);
                }
            }
            
            if (in != null) {
                try (InputStream autoClose = in) {
                    BufferedImage img = ImageIO.read(autoClose);
                    return img;
                }
            }
            
            // fallback to filesystem
            File f = new File(relativePath);
            if (f.exists()) {
                return ImageIO.read(f);
            }
            
        } catch (IOException e) {
            System.err.println("Error loading image: " + relativePath + ": " + e.getMessage());
        }
        
        System.out.println("Image not found: " + relativePath);
        return null;
    }
    
    /**
     * بارگذاری همه تصاویر بازی
     */
    public static GameImages loadAllImages() {
        Image imgPlayer = loadImage("images/me.png");
        Image imgFood = loadImage("images/food.png");
        
        // تلاش برای enemy.png سپس enamy.png
        Image imgEnemy = loadImage("images/enemy.png");
        if (imgEnemy == null) {
            imgEnemy = loadImage("images/enamy.png");
        }
        
        Image imgPowerUp = loadImage("images/power_up.png");
        
        return new GameImages(imgPlayer, imgFood, imgEnemy, imgPowerUp);
    }
    
    /**
     * کلاس نگهداری تصاویر بازی
     */
    public static class GameImages {
        public final Image player;
        public final Image food;
        public final Image enemy;
        public final Image powerUp;
        
        public GameImages(Image player, Image food, Image enemy, Image powerUp) {
            this.player = player;
            this.food = food;
            this.enemy = enemy;
            this.powerUp = powerUp;
        }
    }
}
