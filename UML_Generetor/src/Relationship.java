/**
 * Represents a relationship between two classes in the UML diagram
 */
public class Relationship {
    private String source;
    private String target;
    private RelationshipType type;
    
    public Relationship(String source, String target, RelationshipType type) {
        this.source = source;
        this.target = target;
        this.type = type;
    }
    
    // Getters
    public String getSource() {
        return source;
    }
    
    public String getTarget() {
        return target;
    }
    
    public RelationshipType getType() {
        return type;
    }
}