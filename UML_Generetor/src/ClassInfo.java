import java.awt.FontMetrics;
import java.awt.Graphics;
import java.util.ArrayList;
import java.util.List;

/**
 * Represents a class in the UML diagram
 */
public class ClassInfo {
    private String name;
    private String type; // "class", "interface", or "enum"
    private List<AttributeInfo> attributes;
    private List<MethodInfo> methods;
    private int x, y, width, height;
    
    public ClassInfo(String name, String type) {
        this.name = name;
        this.type = type;
        this.attributes = new ArrayList<>();
        this.methods = new ArrayList<>();
    }
    
    public void addAttribute(AttributeInfo attribute) {
        attributes.add(attribute);
    }
    
    public void addMethod(MethodInfo method) {
        methods.add(method);
    }
    
    public void calculateSize(Graphics g) {
        if (g == null) {
            // Default sizes if graphics context not available
            width = 200;
            height = 100 + attributes.size() * 15 + methods.size() * 15;
            return;
        }
        
        FontMetrics fm = g.getFontMetrics();
        
        // Calculate width based on content
        int maxWidth = fm.stringWidth(name) + 40; // افزایش حاشیه
        
        for (AttributeInfo attr : attributes) {
            int attrWidth = fm.stringWidth("+ " + attr.getName() + ": " + attr.getType()) + 40; // افزایش حاشیه
            maxWidth = Math.max(maxWidth, attrWidth);
        }
        
        for (MethodInfo method : methods) {
            int methodWidth = fm.stringWidth("+ " + method.getName() + "(" + method.getParameters() + "): " + method.getReturnType()) + 40; // افزایش حاشیه
            maxWidth = Math.max(maxWidth, methodWidth);
        }
        
        width = Math.max(200, maxWidth); // افزایش حداقل عرض
        
        // Calculate height
        int nameHeight = 35; // افزایش ارتفاع بخش نام
        int attributeHeight = attributes.isEmpty() ? 15 : attributes.size() * 20 + 10; // افزایش فضا برای هر ویژگی
        int methodHeight = methods.isEmpty() ? 15 : methods.size() * 20 + 10; // افزایش فضا برای هر متد
        
        height = nameHeight + attributeHeight + methodHeight;
    }
    
    // Getters and setters
    public String getName() {
        return name;
    }
    
    public String getType() {
        return type;
    }
    
    public List<AttributeInfo> getAttributes() {
        return attributes;
    }
    
    public List<MethodInfo> getMethods() {
        return methods;
    }
    
    public int getX() {
        return x;
    }
    
    public void setX(int x) {
        this.x = x;
    }
    
    public int getY() {
        return y;
    }
    
    public void setY(int y) {
        this.y = y;
    }
    
    public int getWidth() {
        return width;
    }
    
    public void setWidth(int width) {
        this.width = width;
    }
    
    public int getHeight() {
        return height;
    }
    
    public void setHeight(int height) {
        this.height = height;
    }
} 