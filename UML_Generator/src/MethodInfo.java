/**
 * Represents a method in a class for the UML diagram
 */
public class MethodInfo {
    private String name;
    private String visibility;
    private String returnType;
    private String parameters;
    private boolean isStatic;
    
    public MethodInfo(String name, String visibility, String returnType, String parameters, boolean isStatic) {
        this.name = name;
        this.visibility = visibility;
        this.returnType = returnType;
        this.parameters = parameters;
        this.isStatic = isStatic;
    }
    
    // Getters
    public String getName() {
        return name;
    }
    
    public String getVisibility() {
        return visibility;
    }
    
    public String getReturnType() {
        return returnType;
    }
    
    public String getParameters() {
        return parameters;
    }
    
    public boolean isStatic() {
        return isStatic;
    }
} 