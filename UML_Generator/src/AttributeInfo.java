/**
 * Represents an attribute in a class for the UML diagram
 */
public class AttributeInfo {
    private String name;
    private String visibility;
    private String type;
    private boolean isStatic;
    private boolean isFinal;
    
    public AttributeInfo(String name, String visibility, String type, boolean isStatic, boolean isFinal) {
        this.name = name;
        this.visibility = visibility;
        this.type = type;
        this.isStatic = isStatic;
        this.isFinal = isFinal;
    }
    
    // Getters
    public String getName() {
        return name;
    }
    
    public String getVisibility() {
        return visibility;
    }
    
    public String getType() {
        return type;
    }
    
    public boolean isStatic() {
        return isStatic;
    }
    
    public boolean isFinal() {
        return isFinal;
    }
} 