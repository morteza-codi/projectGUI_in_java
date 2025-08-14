import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Analyzes Java source code files and extracts class information
 */
public class CodeAnalyzer {
    private Map<String, ClassInfo> classes;
    private List<Relationship> relationships;
    
    public CodeAnalyzer() {
        classes = new HashMap<>();
        relationships = new ArrayList<>();
    }
    
    /**
     * Analyzes a Java file and extracts classes, methods, and relationships
     */
    public void analyzeJavaFile(File file) {
        try (BufferedReader reader = new BufferedReader(new FileReader(file))) {
            StringBuilder content = new StringBuilder();
            String line;
            while ((line = reader.readLine()) != null) {
                content.append(line).append("\n");
            }
            
            String fileContent = content.toString();
            
            // Extract class/interface information with improved regex
            Pattern classPattern = Pattern.compile(
                "(?:^|\\s)(public|private|protected)?\\s*(abstract\\s+)?(class|interface|enum)\\s+(\\w+)(?:\\s*<[^>]*>)?\\s*(?:extends\\s+(\\w+)(?:\\s*<[^>]*>)?)?\\s*(?:implements\\s+([\\w,\\s<>]+))?\\s*\\{", 
                Pattern.MULTILINE);
            
            Matcher classMatcher = classPattern.matcher(fileContent);
            
            while (classMatcher.find()) {
                String visibility = classMatcher.group(1);
                String isAbstract = classMatcher.group(2);
                String type = classMatcher.group(3); // class, interface, or enum
                String className = classMatcher.group(4);
                String extendedClass = classMatcher.group(5);
                String implementedInterfaces = classMatcher.group(6);
                
                ClassInfo classInfo = new ClassInfo(className, type, visibility, isAbstract != null);
                classes.put(className, classInfo);
                
                // Add inheritance relationship
                if (extendedClass != null) {
                    relationships.add(new Relationship(className, extendedClass, RelationshipType.INHERITANCE));
                }
                
                // Add implementation relationships
                if (implementedInterfaces != null) {
                    for (String interfaceName : implementedInterfaces.split(",")) {
                        interfaceName = interfaceName.trim();
                        relationships.add(new Relationship(className, interfaceName, RelationshipType.IMPLEMENTATION));
                    }
                }
                
                // Extract methods with improved pattern
                Pattern methodPattern = Pattern.compile(
                    "(?:^|\\s)(public|private|protected)?\\s*(static)?\\s*(final)?\\s*(abstract)?\\s*(?:synchronized)?\\s*(\\w+(?:\\s*<[^>]*>)?)\\s+(\\w+)\\s*\\(([^)]*)\\)(?:\\s*throws\\s+[\\w,\\s<>]+)?\\s*[{;]", 
                    Pattern.MULTILINE);
                Matcher methodMatcher = methodPattern.matcher(fileContent);
                
                while (methodMatcher.find()) {
                    String methodVisibility = methodMatcher.group(1);
                    String isStatic = methodMatcher.group(2);
                    String isFinal = methodMatcher.group(3);
                    String isAbstractMethod = methodMatcher.group(4);
                    String returnType = methodMatcher.group(5);
                    String methodName = methodMatcher.group(6);
                    String parameters = methodMatcher.group(7);
                    
                    if (!methodName.equals(className)) { // Skip constructors
                        classInfo.addMethod(new MethodInfo(
                            methodName, 
                            methodVisibility == null ? "package-private" : methodVisibility,
                            returnType,
                            parameters,
                            isStatic != null
                        ));
                    }
                }
                
                // Extract attributes with improved pattern
                Pattern attributePattern = Pattern.compile(
                    "(?:^|\\s)(private|public|protected)?\\s*(static)?\\s*(final)?\\s*(volatile)?\\s*(transient)?\\s*(\\w+(?:\\s*<[^>]*>)?)\\s+(\\w+)(?:\\s*=\\s*[^;]+)?\\s*;", 
                    Pattern.MULTILINE);
                Matcher attributeMatcher = attributePattern.matcher(fileContent);
                
                while (attributeMatcher.find()) {
                    String attrVisibility = attributeMatcher.group(1);
                    String isStatic = attributeMatcher.group(2);
                    String isFinal = attributeMatcher.group(3);
                    String isVolatile = attributeMatcher.group(4);
                    String isTransient = attributeMatcher.group(5);
                    String attrType = attributeMatcher.group(6);
                    String name = attributeMatcher.group(7);
                    
                    classInfo.addAttribute(new AttributeInfo(
                        name,
                        attrVisibility == null ? "package-private" : attrVisibility,
                        attrType,
                        isStatic != null,
                        isFinal != null
                    ));
                }
                
                // Detect composition/aggregation relationships
                Pattern compositionPattern = Pattern.compile(
                    "private\\s+(final)?\\s*(\\w+)\\s+(\\w+)\\s*;");
                Matcher compositionMatcher = compositionPattern.matcher(fileContent);
                
                while (compositionMatcher.find()) {
                    String isFinal = compositionMatcher.group(1);
                    String relationType = compositionMatcher.group(2);
                    
                    if (classes.containsKey(relationType) || relationType.equals(className)) {
                        RelationshipType relType = (isFinal != null) ? 
                            RelationshipType.COMPOSITION : RelationshipType.AGGREGATION;
                        relationships.add(new Relationship(className, relationType, relType));
                    }
                }
                
                // Detect object creation (dependency)
                Pattern dependencyPattern = Pattern.compile(
                    "new\\s+(\\w+)\\s*\\(");
                Matcher dependencyMatcher = dependencyPattern.matcher(fileContent);
                
                while (dependencyMatcher.find()) {
                    String targetClass = dependencyMatcher.group(1);
                    if (!targetClass.equals(className) && (classes.containsKey(targetClass) || 
                        isJavaStandardClass(targetClass))) {
                        // Check if this dependency already exists
                        boolean exists = false;
                        for (Relationship rel : relationships) {
                            if (rel.getSource().equals(className) && 
                                rel.getTarget().equals(targetClass) && 
                                rel.getType() == RelationshipType.DEPENDENCY) {
                                exists = true;
                                break;
                            }
                        }
                        
                        if (!exists) {
                            relationships.add(new Relationship(className, targetClass, RelationshipType.DEPENDENCY));
                        }
                    }
                }
                
                // Detect polymorphism (variable of parent type assigned to child type)
                Pattern polymorphismPattern = Pattern.compile(
                    "(\\w+)\\s+(\\w+)\\s*=\\s*new\\s+(\\w+)\\s*\\(");
                Matcher polymorphismMatcher = polymorphismPattern.matcher(fileContent);
                
                while (polymorphismMatcher.find()) {
                    String parentType = polymorphismMatcher.group(1);
                    String childType = polymorphismMatcher.group(3);
                    
                    if (!parentType.equals(childType) && classes.containsKey(parentType) && 
                        classes.containsKey(childType)) {
                        // Check if this polymorphism relationship already exists
                        boolean exists = false;
                        for (Relationship rel : relationships) {
                            if (rel.getSource().equals(childType) && 
                                rel.getTarget().equals(parentType) && 
                                rel.getType() == RelationshipType.POLYMORPHISM) {
                                exists = true;
                                break;
                            }
                        }
                        
                        if (!exists) {
                            relationships.add(new Relationship(childType, parentType, RelationshipType.POLYMORPHISM));
                        }
                    }
                }
            }
            
        } catch (IOException e) {
            System.err.println("Error reading file " + file.getName() + ": " + e.getMessage());
        }
    }
    
    /**
     * Check if a class is a standard Java class (simplified)
     */
    private boolean isJavaStandardClass(String className) {
        String[] commonClasses = {"String", "Integer", "Boolean", "List", "Map", "ArrayList", 
                                 "HashMap", "Set", "HashSet", "File", "Exception"};
        for (String cls : commonClasses) {
            if (cls.equals(className)) {
                return true;
            }
        }
        return false;
    }
    
    /**
     * Analyzes all Java files in a directory
     */
    public void analyzeDirectory(File directory) {
        if (directory.isDirectory()) {
            File[] files = directory.listFiles();
            if (files != null) {
                for (File file : files) {
                    if (file.isFile() && file.getName().endsWith(".java")) {
                        analyzeJavaFile(file);
                    } else if (file.isDirectory()) {
                        analyzeDirectory(file);
                    }
                }
            }
        }
    }
    
    /**
     * Clear all analyzed data
     */
    public void clear() {
        classes.clear();
        relationships.clear();
    }
    
    // Getters
    public Map<String, ClassInfo> getClasses() {
        return classes;
    }
    
    public List<Relationship> getRelationships() {
        return relationships;
    }
} 