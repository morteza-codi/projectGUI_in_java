import java.awt.BasicStroke;
import java.awt.Color;
import java.awt.Font;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.LinearGradientPaint;
import java.awt.Point;
import java.awt.RenderingHints;
import java.awt.Stroke;
import java.awt.geom.AffineTransform;
import java.awt.geom.Path2D;
import java.awt.geom.Point2D;
import java.awt.geom.QuadCurve2D;
import java.awt.geom.RoundRectangle2D;
import java.util.HashMap;
import java.util.Map;

/**
 * Handles rendering of UML diagrams with enhanced visual appearance
 */
public class DiagramRenderer {
    
    // Define colors for different class types
    private final Color CLASS_GRADIENT_START = new Color(240, 248, 255); // AliceBlue
    private final Color CLASS_GRADIENT_END = new Color(176, 226, 255);   // LightSkyBlue
    
    private final Color INTERFACE_GRADIENT_START = new Color(240, 255, 240); // HoneyDew
    private final Color INTERFACE_GRADIENT_END = new Color(144, 238, 144);   // LightGreen
    
    private final Color ENUM_GRADIENT_START = new Color(255, 240, 245); // LavenderBlush
    private final Color ENUM_GRADIENT_END = new Color(255, 182, 193);   // LightPink
    
    private final Color BORDER_COLOR = new Color(70, 130, 180); // SteelBlue
    private final Color TEXT_COLOR = new Color(0, 0, 90);       // Dark blue
    private final Color SHADOW_COLOR = new Color(0, 0, 0, 50);  // Semi-transparent black
    
    // Relationship colors
    private final Color INHERITANCE_COLOR = new Color(100, 149, 237);    // CornflowerBlue
    private final Color IMPLEMENTATION_COLOR = new Color(46, 139, 87);   // SeaGreen
    private final Color COMPOSITION_COLOR = new Color(205, 92, 92);      // IndianRed
    private final Color AGGREGATION_COLOR = new Color(218, 165, 32);     // Goldenrod
    private final Color DEPENDENCY_COLOR = new Color(138, 43, 226);      // BlueViolet
    private final Color POLYMORPHISM_COLOR = new Color(255, 105, 180);   // HotPink
    
    // Fonts
    private final Font CLASS_NAME_FONT = new Font("SansSerif", Font.BOLD, 14);
    private final Font ATTRIBUTE_FONT = new Font("SansSerif", Font.PLAIN, 12);
    private final Font METHOD_FONT = new Font("SansSerif", Font.ITALIC, 12);
    private final Font STEREOTYPE_FONT = new Font("SansSerif", Font.ITALIC, 12);
    private final Font RELATIONSHIP_LABEL_FONT = new Font("SansSerif", Font.PLAIN, 10);
    
    // Map to store relationship labels
    private Map<String, String> relationshipLabels = new HashMap<>();
    
    public DiagramRenderer() {
        // Initialize relationship labels
        relationshipLabels.put("INHERITANCE", "extends");
        relationshipLabels.put("IMPLEMENTATION", "implements");
        relationshipLabels.put("COMPOSITION", "contains");
        relationshipLabels.put("AGGREGATION", "has");
        relationshipLabels.put("DEPENDENCY", "creates");
        relationshipLabels.put("POLYMORPHISM", "is-a");
    }
    
    /**
     * Draws the UML diagram
     */
    public void drawUML(Graphics g, Map<String, ClassInfo> classes, Map<String, ClassInfo> visibleClasses, java.util.List<Relationship> relationships) {
        if (classes.isEmpty()) return;
        
        Graphics2D g2d = (Graphics2D) g;
        g2d.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
        g2d.setRenderingHint(RenderingHints.KEY_TEXT_ANTIALIASING, RenderingHints.VALUE_TEXT_ANTIALIAS_ON);
        g2d.setRenderingHint(RenderingHints.KEY_RENDERING, RenderingHints.VALUE_RENDER_QUALITY);
        
        // First, draw all relationships
        for (Relationship relationship : relationships) {
            ClassInfo source = visibleClasses.get(relationship.getSource());
            ClassInfo target = visibleClasses.get(relationship.getTarget());
            
            if (source != null && target != null) {
                drawRelationship(g2d, source, target, relationship.getType());
            }
        }
        
        // Then, draw all class boxes (so they appear on top of relationships)
        for (ClassInfo classInfo : visibleClasses.values()) {
            drawClassBox(g2d, classInfo);
        }
        
        // Draw legend in the bottom right corner
        // Find the maximum x and y coordinates to place the legend
        int maxX = 20; // Default position
        int maxY = 20;
        
        if (!visibleClasses.isEmpty()) {
            maxX = visibleClasses.values().stream()
                .mapToInt(c -> c.getX() + c.getWidth())
                .max()
                .orElse(20);
            
            maxY = visibleClasses.values().stream()
                .mapToInt(c -> c.getY() + c.getHeight())
                .max()
                .orElse(20);
        }
        
        // Draw legend in the bottom right
        int legendWidth = 200;
        int legendHeight = 230; // Increased height for new relationships
        int legendX = Math.max(maxX + 20, g2d.getClipBounds().width - legendWidth - 20);
        int legendY = Math.max(maxY + 20, g2d.getClipBounds().height - legendHeight - 20);
        
        drawLegend(g2d, legendX, legendY);
    }
    
    /**
     * Draws a legend explaining the UML notation
     */
    private void drawLegend(Graphics2D g, int x, int y) {
        int width = 200;
        int height = 230; // Increased height for new relationships
        int itemHeight = 25;
        int textOffset = 35;
        
        // Draw legend box with semi-transparent background
        RoundRectangle2D rect = new RoundRectangle2D.Double(x, y, width, height, 10, 10);
        g.setColor(new Color(255, 255, 255, 240)); // More opaque background
        g.fill(rect);
        g.setColor(BORDER_COLOR);
        g.setStroke(new BasicStroke(1.0f));
        g.draw(rect);
        
        // Draw title
        g.setFont(new Font("SansSerif", Font.BOLD, 12));
        g.setColor(TEXT_COLOR);
        g.drawString("UML Notation", x + 10, y + 20);
        
        // Draw relationship types
        int currentY = y + 40;
        
        // Inheritance
        g.setColor(INHERITANCE_COLOR);
        drawArrow(g, x + 10, currentY + itemHeight/2, x + 30, currentY + itemHeight/2, RelationshipType.INHERITANCE);
        g.setFont(new Font("SansSerif", Font.PLAIN, 11));
        g.drawString("Inheritance (extends)", x + textOffset, currentY + itemHeight/2 + 5);
        currentY += itemHeight;
        
        // Implementation
        g.setColor(IMPLEMENTATION_COLOR);
        drawArrow(g, x + 10, currentY + itemHeight/2, x + 30, currentY + itemHeight/2, RelationshipType.IMPLEMENTATION);
        g.drawString("Implementation (implements)", x + textOffset, currentY + itemHeight/2 + 5);
        currentY += itemHeight;
        
        // Composition
        g.setColor(COMPOSITION_COLOR);
        drawArrow(g, x + 10, currentY + itemHeight/2, x + 30, currentY + itemHeight/2, RelationshipType.COMPOSITION);
        g.drawString("Composition (contains)", x + textOffset, currentY + itemHeight/2 + 5);
        currentY += itemHeight;
        
        // Aggregation
        g.setColor(AGGREGATION_COLOR);
        drawArrow(g, x + 10, currentY + itemHeight/2, x + 30, currentY + itemHeight/2, RelationshipType.AGGREGATION);
        g.drawString("Aggregation (has)", x + textOffset, currentY + itemHeight/2 + 5);
        currentY += itemHeight;
        
        // Dependency
        g.setColor(DEPENDENCY_COLOR);
        drawArrow(g, x + 10, currentY + itemHeight/2, x + 30, currentY + itemHeight/2, RelationshipType.DEPENDENCY);
        g.drawString("Dependency (creates)", x + textOffset, currentY + itemHeight/2 + 5);
        currentY += itemHeight;
        
        // Polymorphism
        g.setColor(POLYMORPHISM_COLOR);
        drawArrow(g, x + 10, currentY + itemHeight/2, x + 30, currentY + itemHeight/2, RelationshipType.POLYMORPHISM);
        g.drawString("Polymorphism (is-a)", x + textOffset, currentY + itemHeight/2 + 5);
        currentY += itemHeight;
        
        // Visibility symbols
        g.setColor(TEXT_COLOR);
        g.drawString("+ : public    - : private", x + 10, currentY + itemHeight/2);
        currentY += itemHeight - 10;
        g.drawString("# : protected    ~ : package", x + 10, currentY + itemHeight/2);
    }
    
    /**
     * Draws a class box with rounded corners, gradient background, and shadow
     */
    private void drawClassBox(Graphics2D g, ClassInfo classInfo) {
        int x = classInfo.getX();
        int y = classInfo.getY();
        int width = classInfo.getWidth();
        int height = classInfo.getHeight();
        
        // Draw shadow
        RoundRectangle2D shadow = new RoundRectangle2D.Double(
            x + 4, y + 4, width, height, 15, 15);
        g.setColor(SHADOW_COLOR);
        g.fill(shadow);
        
        // Determine gradient colors based on class type
        Color gradientStart, gradientEnd;
        switch (classInfo.getType()) {
            case "interface":
                gradientStart = INTERFACE_GRADIENT_START;
                gradientEnd = INTERFACE_GRADIENT_END;
                break;
            case "enum":
                gradientStart = ENUM_GRADIENT_START;
                gradientEnd = ENUM_GRADIENT_END;
                break;
            default:
                gradientStart = CLASS_GRADIENT_START;
                gradientEnd = CLASS_GRADIENT_END;
        }
        
        // Create gradient paint
        LinearGradientPaint gradient = new LinearGradientPaint(
            new Point2D.Float(x, y),
            new Point2D.Float(x, y + height),
            new float[] {0.0f, 1.0f},
            new Color[] {gradientStart, gradientEnd}
        );
        
        // Draw rounded rectangle with gradient
        RoundRectangle2D rect = new RoundRectangle2D.Double(
            x, y, width, height, 15, 15);
        g.setPaint(gradient);
        g.fill(rect);
        
        // Draw border
        g.setColor(BORDER_COLOR);
        g.setStroke(new BasicStroke(1.5f));
        g.draw(rect);
        
        // Draw class name section
        int nameHeight = 35;
        g.drawLine(x, y + nameHeight, x + width, y + nameHeight);
        
        // Draw class name with stereotype and abstract notation
        g.setColor(TEXT_COLOR);
        String classType = "";
        switch (classInfo.getType()) {
            case "interface": classType = "<<interface>>"; break;
            case "enum": classType = "<<enum>>"; break;
        }
        
        // Add abstract notation
        if (classInfo.isAbstract() && classInfo.getType().equals("class")) {
            classType = "<<abstract>>";
        }
        
        if (!classType.isEmpty()) {
            g.setFont(STEREOTYPE_FONT);
            int stereotypeWidth = g.getFontMetrics().stringWidth(classType);
            g.drawString(classType, x + (width - stereotypeWidth) / 2, y + 15);
            
            // Use italic font for abstract classes
            Font nameFont = classInfo.isAbstract() ? 
                new Font(CLASS_NAME_FONT.getName(), Font.BOLD | Font.ITALIC, CLASS_NAME_FONT.getSize()) : 
                CLASS_NAME_FONT;
            g.setFont(nameFont);
            int nameWidth = g.getFontMetrics().stringWidth(classInfo.getName());
            g.drawString(classInfo.getName(), x + (width - nameWidth) / 2, y + 30);
        } else {
            // Use italic font for abstract classes
            Font nameFont = classInfo.isAbstract() ? 
                new Font(CLASS_NAME_FONT.getName(), Font.BOLD | Font.ITALIC, CLASS_NAME_FONT.getSize()) : 
                CLASS_NAME_FONT;
            g.setFont(nameFont);
            int nameWidth = g.getFontMetrics().stringWidth(classInfo.getName());
            g.drawString(classInfo.getName(), x + (width - nameWidth) / 2, y + 22);
        }
        
        // Draw attributes section
        g.setFont(ATTRIBUTE_FONT);
        int attributeStartY = y + nameHeight + 15; // افزایش فاصله از خط جداکننده
        for (int i = 0; i < classInfo.getAttributes().size(); i++) {
            AttributeInfo attr = classInfo.getAttributes().get(i);
            String prefix = getVisibilitySymbol(attr.getVisibility());
            String staticStr = attr.isStatic() ? "static " : "";
            String finalStr = attr.isFinal() ? "final " : "";
            
            // محدود کردن طول نوشته‌ها برای جلوگیری از بیرون زدن از کادر
            String attrName = attr.getName();
            String attrType = attr.getType();
            
            // اگر نوشته خیلی طولانی است، آن را کوتاه کن
            int maxTextWidth = width - 30;
            String attributeText = prefix + " " + attrName + ": " + attrType;
            
            if (g.getFontMetrics().stringWidth(attributeText) > maxTextWidth) {
                // کوتاه کردن نوع داده
                while (g.getFontMetrics().stringWidth(prefix + " " + attrName + ": " + attrType) > maxTextWidth && attrType.length() > 3) {
                    attrType = attrType.substring(0, attrType.length() - 1);
                }
                attributeText = prefix + " " + attrName + ": " + attrType + "...";
            }
            
            // Add identifier icon
            drawIdentifierIcon(g, x + 5, attributeStartY + i * 20 - 10);
            
            // Set color based on visibility
            setVisibilityColor(g, attr.getVisibility());
            g.drawString(attributeText, x + 20, attributeStartY + i * 20);
        }
        
        // Draw separator line
        int methodsLineY = attributeStartY + classInfo.getAttributes().size() * 20;
        if (classInfo.getAttributes().isEmpty()) {
            methodsLineY = y + nameHeight + 15;
        } else {
            methodsLineY += 5;
        }
        
        g.setColor(BORDER_COLOR);
        g.drawLine(x, methodsLineY, x + width, methodsLineY);
        
        // Draw methods
        g.setFont(METHOD_FONT);
        int methodStartY = methodsLineY + 20; // افزایش فاصله از خط جداکننده
        for (int i = 0; i < classInfo.getMethods().size(); i++) {
            MethodInfo method = classInfo.getMethods().get(i);
            String prefix = getVisibilitySymbol(method.getVisibility());
            String staticStr = method.isStatic() ? "static " : "";
            
            // محدود کردن طول نوشته‌ها
            String methodName = method.getName();
            String params = method.getParameters();
            String returnType = method.getReturnType();
            
            // اگر نوشته خیلی طولانی است، آن را کوتاه کن
            int maxTextWidth = width - 30;
            String methodText = prefix + " " + methodName + "(" + params + "): " + returnType;
            
            if (g.getFontMetrics().stringWidth(methodText) > maxTextWidth) {
                // کوتاه کردن پارامترها و نوع بازگشتی
                if (params.length() > 3) {
                    params = "...";
                }
                while (g.getFontMetrics().stringWidth(prefix + " " + methodName + "(" + params + "): " + returnType) > maxTextWidth && returnType.length() > 3) {
                    returnType = returnType.substring(0, returnType.length() - 1);
                }
                methodText = prefix + " " + methodName + "(" + params + "): " + returnType + "...";
            }
            
            // Add method icon
            drawMethodIcon(g, x + 5, methodStartY + i * 20 - 10);
            
            // Set color based on visibility
            setVisibilityColor(g, method.getVisibility());
            g.drawString(methodText, x + 20, methodStartY + i * 20);
        }
    }
    
    /**
     * Draws a small icon representing an identifier/attribute
     */
    private void drawIdentifierIcon(Graphics2D g, int x, int y) {
        Color oldColor = g.getColor();
        g.setColor(new Color(0, 0, 128)); // Navy
        g.fillRect(x, y, 10, 10);
        g.setColor(Color.WHITE);
        g.drawLine(x + 2, y + 5, x + 8, y + 5);
        g.setColor(oldColor);
    }
    
    /**
     * Draws a small icon representing a method
     */
    private void drawMethodIcon(Graphics2D g, int x, int y) {
        Color oldColor = g.getColor();
        g.setColor(new Color(139, 0, 0)); // DarkRed
        g.fillOval(x, y, 10, 10);
        g.setColor(Color.WHITE);
        g.drawLine(x + 3, y + 3, x + 7, y + 7);
        g.drawLine(x + 3, y + 7, x + 7, y + 3);
        g.setColor(oldColor);
    }
    
    /**
     * Sets color based on visibility
     */
    private void setVisibilityColor(Graphics2D g, String visibility) {
        switch (visibility) {
            case "public": 
                g.setColor(new Color(0, 100, 0)); // Dark green
                break;
            case "private": 
                g.setColor(new Color(128, 0, 0)); // Maroon
                break;
            case "protected": 
                g.setColor(new Color(184, 134, 11)); // DarkGoldenrod
                break;
            default: 
                g.setColor(new Color(105, 105, 105)); // DimGray
        }
    }
    
    /**
     * Gets the visibility symbol for UML notation
     */
    private String getVisibilitySymbol(String visibility) {
        switch (visibility) {
            case "public": return "+";
            case "private": return "-";
            case "protected": return "#";
            default: return "~"; // package-private
        }
    }
    
    /**
     * Draws a relationship between two classes
     */
    private void drawRelationship(Graphics2D g, ClassInfo source, ClassInfo target, RelationshipType type) {
        // Calculate connection points
        Point sourcePoint = getConnectionPoint(source, target);
        Point targetPoint = getConnectionPoint(target, source);
        
        // Set color based on relationship type
        Color relationshipColor;
        switch (type) {
            case INHERITANCE:
                relationshipColor = INHERITANCE_COLOR;
                break;
            case IMPLEMENTATION:
                relationshipColor = IMPLEMENTATION_COLOR;
                break;
            case COMPOSITION:
                relationshipColor = COMPOSITION_COLOR;
                break;
            case AGGREGATION:
                relationshipColor = AGGREGATION_COLOR;
                break;
            case DEPENDENCY:
                relationshipColor = DEPENDENCY_COLOR;
                break;
            case POLYMORPHISM:
                relationshipColor = POLYMORPHISM_COLOR;
                break;
            default:
                relationshipColor = BORDER_COLOR;
        }
        
        g.setColor(relationshipColor);
        
        // Calculate control point for curved line
        // For simplicity, just use a point perpendicular to the midpoint
        Point midPoint = new Point(
            (sourcePoint.x + targetPoint.x) / 2,
            (sourcePoint.y + targetPoint.y) / 2
        );
        
        // Calculate perpendicular offset
        double dx = targetPoint.x - sourcePoint.x;
        double dy = targetPoint.y - sourcePoint.y;
        double distance = Math.sqrt(dx * dx + dy * dy);
        
        // Only curve the line if the classes are far enough apart
        if (distance > 200) {
            // Create a curved path
            double perpX = -dy / distance * 30;  // Perpendicular vector
            double perpY = dx / distance * 30;
            
            Point controlPoint = new Point(
                (int)(midPoint.x + perpX),
                (int)(midPoint.y + perpY)
            );
            
            QuadCurve2D curve = new QuadCurve2D.Float(
                sourcePoint.x, sourcePoint.y,
                controlPoint.x, controlPoint.y,
                targetPoint.x, targetPoint.y
            );
            
            // Draw the line with appropriate style based on relationship type
            Stroke oldStroke = g.getStroke();
            
            if (type == RelationshipType.IMPLEMENTATION) {
                // Dashed line for implementation
                g.setStroke(new BasicStroke(1.5f, BasicStroke.CAP_BUTT, BasicStroke.JOIN_BEVEL, 
                    0, new float[]{8}, 0));
            } else if (type == RelationshipType.DEPENDENCY) {
                // Dotted line for dependency
                g.setStroke(new BasicStroke(1.5f, BasicStroke.CAP_BUTT, BasicStroke.JOIN_BEVEL, 
                    0, new float[]{3}, 0));
            } else if (type == RelationshipType.POLYMORPHISM) {
                // Dash-dot line for polymorphism
                g.setStroke(new BasicStroke(1.5f, BasicStroke.CAP_BUTT, BasicStroke.JOIN_BEVEL, 
                    0, new float[]{8, 4, 2, 4}, 0));
            } else {
                // Solid line for other relationships
                g.setStroke(new BasicStroke(1.5f));
            }
            
            g.draw(curve);
            g.setStroke(oldStroke);
            
            // Draw the arrowhead
            drawArrowhead(g, controlPoint, targetPoint, type);
            
            // Draw relationship label
            String label = relationshipLabels.get(type.toString());
            if (label != null) {
                g.setFont(RELATIONSHIP_LABEL_FONT);
                g.setColor(relationshipColor.darker());
                g.drawString(label, controlPoint.x - 15, controlPoint.y - 5);
            }
        } else {
            // Draw straight line
            Stroke oldStroke = g.getStroke();
            
            if (type == RelationshipType.IMPLEMENTATION) {
                // Dashed line for implementation
                g.setStroke(new BasicStroke(1.5f, BasicStroke.CAP_BUTT, BasicStroke.JOIN_BEVEL, 
                    0, new float[]{8}, 0));
            } else if (type == RelationshipType.DEPENDENCY) {
                // Dotted line for dependency
                g.setStroke(new BasicStroke(1.5f, BasicStroke.CAP_BUTT, BasicStroke.JOIN_BEVEL, 
                    0, new float[]{3}, 0));
            } else if (type == RelationshipType.POLYMORPHISM) {
                // Dash-dot line for polymorphism
                g.setStroke(new BasicStroke(1.5f, BasicStroke.CAP_BUTT, BasicStroke.JOIN_BEVEL, 
                    0, new float[]{8, 4, 2, 4}, 0));
            } else {
                // Solid line for other relationships
                g.setStroke(new BasicStroke(1.5f));
            }
            
            g.drawLine(sourcePoint.x, sourcePoint.y, targetPoint.x, targetPoint.y);
            g.setStroke(oldStroke);
            
            // Draw the arrowhead
            drawArrowhead(g, sourcePoint, targetPoint, type);
            
            // Draw relationship label
            String label = relationshipLabels.get(type.toString());
            if (label != null) {
                g.setFont(RELATIONSHIP_LABEL_FONT);
                g.setColor(relationshipColor.darker());
                g.drawString(label, midPoint.x - 15, midPoint.y - 5);
            }
        }
    }
    
    /**
     * Calculates the connection point between two classes
     */
    private Point getConnectionPoint(ClassInfo box, ClassInfo otherBox) {
        // Calculate center points
        int centerX1 = box.getX() + box.getWidth() / 2;
        int centerY1 = box.getY() + box.getHeight() / 2;
        int centerX2 = otherBox.getX() + otherBox.getWidth() / 2;
        int centerY2 = otherBox.getY() + otherBox.getHeight() / 2;
        
        // Calculate the intersection of the line between centers and the rectangle
        double angle = Math.atan2(centerY2 - centerY1, centerX2 - centerX1);
        
        // افزایش فاصله از لبه کادر برای جلوگیری از همپوشانی
        double halfWidth = box.getWidth() / 2.0 + 2;
        double halfHeight = box.getHeight() / 2.0 + 2;
        
        // Find the intersection point
        double t1 = Math.abs(Math.cos(angle)) < 0.001 ? 
                    Double.MAX_VALUE : halfWidth / Math.abs(Math.cos(angle));
        double t2 = Math.abs(Math.sin(angle)) < 0.001 ? 
                    Double.MAX_VALUE : halfHeight / Math.abs(Math.sin(angle));
        double t = Math.min(t1, t2);
        
        int x = centerX1 + (int)(t * Math.cos(angle));
        int y = centerY1 + (int)(t * Math.sin(angle));
        
        // Clamp to the box boundaries
        x = Math.max(box.getX(), Math.min(x, box.getX() + box.getWidth()));
        y = Math.max(box.getY(), Math.min(y, box.getY() + box.getHeight()));
        
        return new Point(x, y);
    }
    
    /**
     * Draws an arrowhead for the relationship
     */
    private void drawArrowhead(Graphics2D g, Point source, Point target, RelationshipType type) {
        final int ARR_SIZE = 12;
        
        // Calculate the angle of the line
        double dx = target.x - source.x;
        double dy = target.y - source.y;
        double angle = Math.atan2(dy, dx);
        
        // Create arrow/diamond shape based on relationship type
        switch (type) {
            case INHERITANCE:
            case POLYMORPHISM:
                // Triangle (empty) for inheritance and polymorphism
                int[] xPoints = {
                    target.x, 
                    (int)(target.x - ARR_SIZE * Math.cos(angle - Math.PI/6)),
                    (int)(target.x - ARR_SIZE * Math.cos(angle + Math.PI/6))
                };
                int[] yPoints = {
                    target.y, 
                    (int)(target.y - ARR_SIZE * Math.sin(angle - Math.PI/6)),
                    (int)(target.y - ARR_SIZE * Math.sin(angle + Math.PI/6))
                };
                g.setColor(Color.WHITE);
                g.fillPolygon(xPoints, yPoints, 3);
                g.setColor(type == RelationshipType.INHERITANCE ? 
                    INHERITANCE_COLOR : POLYMORPHISM_COLOR);
                g.setStroke(new BasicStroke(1.5f));
                g.drawPolygon(xPoints, yPoints, 3);
                break;
                
            case IMPLEMENTATION:
                // Triangle (empty) for implementation
                int[] xPointsImpl = {
                    target.x, 
                    (int)(target.x - ARR_SIZE * Math.cos(angle - Math.PI/6)),
                    (int)(target.x - ARR_SIZE * Math.cos(angle + Math.PI/6))
                };
                int[] yPointsImpl = {
                    target.y, 
                    (int)(target.y - ARR_SIZE * Math.sin(angle - Math.PI/6)),
                    (int)(target.y - ARR_SIZE * Math.sin(angle + Math.PI/6))
                };
                g.setColor(Color.WHITE);
                g.fillPolygon(xPointsImpl, yPointsImpl, 3);
                g.setColor(IMPLEMENTATION_COLOR);
                g.setStroke(new BasicStroke(1.5f));
                g.drawPolygon(xPointsImpl, yPointsImpl, 3);
                break;
                
            case COMPOSITION:
                // Filled diamond for composition
                int[] xPointsComp = {
                    target.x,
                    (int)(target.x - ARR_SIZE * Math.cos(angle - Math.PI/4)),
                    (int)(target.x - ARR_SIZE * 1.5 * Math.cos(angle)),
                    (int)(target.x - ARR_SIZE * Math.cos(angle + Math.PI/4))
                };
                int[] yPointsComp = {
                    target.y,
                    (int)(target.y - ARR_SIZE * Math.sin(angle - Math.PI/4)),
                    (int)(target.y - ARR_SIZE * 1.5 * Math.sin(angle)),
                    (int)(target.y - ARR_SIZE * Math.sin(angle + Math.PI/4))
                };
                g.setColor(COMPOSITION_COLOR);
                g.fillPolygon(xPointsComp, yPointsComp, 4);
                break;
                
            case AGGREGATION:
                // Empty diamond for aggregation
                int[] xPointsAgg = {
                    target.x,
                    (int)(target.x - ARR_SIZE * Math.cos(angle - Math.PI/4)),
                    (int)(target.x - ARR_SIZE * 1.5 * Math.cos(angle)),
                    (int)(target.x - ARR_SIZE * Math.cos(angle + Math.PI/4))
                };
                int[] yPointsAgg = {
                    target.y,
                    (int)(target.y - ARR_SIZE * Math.sin(angle - Math.PI/4)),
                    (int)(target.y - ARR_SIZE * 1.5 * Math.sin(angle)),
                    (int)(target.y - ARR_SIZE * Math.sin(angle + Math.PI/4))
                };
                g.setColor(Color.WHITE);
                g.fillPolygon(xPointsAgg, yPointsAgg, 4);
                g.setColor(AGGREGATION_COLOR);
                g.setStroke(new BasicStroke(1.5f));
                g.drawPolygon(xPointsAgg, yPointsAgg, 4);
                break;
                
            case DEPENDENCY:
                // Open arrow for dependency
                g.setColor(DEPENDENCY_COLOR);
                g.setStroke(new BasicStroke(1.5f));
                int[] xPointsDep = {
                    target.x,
                    (int)(target.x - ARR_SIZE * Math.cos(angle - Math.PI/6)),
                    (int)(target.x - ARR_SIZE * Math.cos(angle + Math.PI/6))
                };
                int[] yPointsDep = {
                    target.y,
                    (int)(target.y - ARR_SIZE * Math.sin(angle - Math.PI/6)),
                    (int)(target.y - ARR_SIZE * Math.sin(angle + Math.PI/6))
                };
                g.drawLine(xPointsDep[0], yPointsDep[0], xPointsDep[1], yPointsDep[1]);
                g.drawLine(xPointsDep[0], yPointsDep[0], xPointsDep[2], yPointsDep[2]);
                break;
        }
    }
    
    /**
     * Draws an arrow for the legend
     */
    private void drawArrow(Graphics2D g, int x1, int y1, int x2, int y2, RelationshipType type) {
        // Save old stroke
        Stroke oldStroke = g.getStroke();
        
        // Draw line
        if (type == RelationshipType.IMPLEMENTATION) {
            g.setStroke(new BasicStroke(1.5f, BasicStroke.CAP_BUTT, BasicStroke.JOIN_BEVEL, 
                0, new float[]{5}, 0));
        } else if (type == RelationshipType.DEPENDENCY) {
            g.setStroke(new BasicStroke(1.5f, BasicStroke.CAP_BUTT, BasicStroke.JOIN_BEVEL, 
                0, new float[]{3}, 0));
        } else if (type == RelationshipType.POLYMORPHISM) {
            g.setStroke(new BasicStroke(1.5f, BasicStroke.CAP_BUTT, BasicStroke.JOIN_BEVEL, 
                0, new float[]{8, 4, 2, 4}, 0));
        } else {
            g.setStroke(new BasicStroke(1.5f));
        }
        
        g.drawLine(x1, y1, x2, y2);
        
        // Draw arrowhead
        final int ARR_SIZE = 8;
        
        // Create arrow/diamond shape based on relationship type
        switch (type) {
            case INHERITANCE:
            case POLYMORPHISM:
                // Triangle (empty)
                int[] xPoints = {
                    x2, 
                    x2 - ARR_SIZE,
                    x2 - ARR_SIZE
                };
                int[] yPoints = {
                    y2, 
                    y2 - ARR_SIZE/2,
                    y2 + ARR_SIZE/2
                };
                g.setColor(Color.WHITE);
                g.fillPolygon(xPoints, yPoints, 3);
                g.setColor(type == RelationshipType.INHERITANCE ? 
                    INHERITANCE_COLOR : POLYMORPHISM_COLOR);
                g.drawPolygon(xPoints, yPoints, 3);
                break;
                
            case IMPLEMENTATION:
                // Triangle (empty)
                int[] xPointsImpl = {
                    x2, 
                    x2 - ARR_SIZE,
                    x2 - ARR_SIZE
                };
                int[] yPointsImpl = {
                    y2, 
                    y2 - ARR_SIZE/2,
                    y2 + ARR_SIZE/2
                };
                g.setColor(Color.WHITE);
                g.fillPolygon(xPointsImpl, yPointsImpl, 3);
                g.setColor(IMPLEMENTATION_COLOR);
                g.drawPolygon(xPointsImpl, yPointsImpl, 3);
                break;
                
            case COMPOSITION:
                // Filled diamond
                int[] xPointsComp = {
                    x2,
                    x2 - ARR_SIZE/2,
                    x2 - ARR_SIZE,
                    x2 - ARR_SIZE/2
                };
                int[] yPointsComp = {
                    y2,
                    y2 - ARR_SIZE/2,
                    y2,
                    y2 + ARR_SIZE/2
                };
                g.setColor(COMPOSITION_COLOR);
                g.fillPolygon(xPointsComp, yPointsComp, 4);
                break;
                
            case AGGREGATION:
                // Empty diamond
                int[] xPointsAgg = {
                    x2,
                    x2 - ARR_SIZE/2,
                    x2 - ARR_SIZE,
                    x2 - ARR_SIZE/2
                };
                int[] yPointsAgg = {
                    y2,
                    y2 - ARR_SIZE/2,
                    y2,
                    y2 + ARR_SIZE/2
                };
                g.setColor(Color.WHITE);
                g.fillPolygon(xPointsAgg, yPointsAgg, 4);
                g.setColor(AGGREGATION_COLOR);
                g.drawPolygon(xPointsAgg, yPointsAgg, 4);
                break;
                
            case DEPENDENCY:
                // Open arrow
                g.setColor(DEPENDENCY_COLOR);
                g.drawLine(x2, y2, x2 - ARR_SIZE, y2 - ARR_SIZE/2);
                g.drawLine(x2, y2, x2 - ARR_SIZE, y2 + ARR_SIZE/2);
                break;
        }
        
        // Restore old stroke
        g.setStroke(oldStroke);
    }
} 