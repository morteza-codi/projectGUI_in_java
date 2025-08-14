# UML Diagram Generator - Complete Documentation

## Overview
This is a professional Java Swing application that automatically generates UML class diagrams from Java source code. The application analyzes Java files to extract class structures, relationships, and generates visually appealing UML diagrams.

## Features

### Core Features
- **Multi-file Analysis**: Load individual Java files or entire directories
- **Comprehensive Relationship Detection**: 
  - Inheritance (extends)
  - Implementation (implements)
  - Composition (has-a with final fields)
  - Aggregation (has-a relationships)
  - Dependency (creates/uses)
  - Polymorphism (is-a relationships)

### Visual Features
- **Professional Rendering**: Gradient backgrounds, shadows, anti-aliasing
- **UML Standard Compliance**: Proper notation for visibility, stereotypes
- **Color-coded Elements**: Different colors for different class types and visibilities
- **Flexible Layout**: Automatic positioning with collision detection
- **Interactive Filtering**: Select which classes to display

### Export Features
- **High-quality PNG Export**: Anti-aliased images with proper sizing
- **Scalable Output**: Exports full diagram regardless of window size

## Installation & Setup

### Requirements
- Java 8 or higher
- Windows/Linux/macOS
- At least 512MB RAM
- Java source files to analyze

### Running the Application

#### Option 1: Using Build Script (Windows)
```batch
build.bat
```

#### Option 2: Manual Compilation
```bash
# Compile
javac -encoding UTF-8 *.java

# Run
java -Dfile.encoding=UTF-8 UMLGenerator
```

## User Interface Guide

### Main Window Components

1. **Control Panel (Top)**
   - `Load Java Files`: Select files or directories to analyze
   - `Generate UML`: Create the diagram from loaded files
   - `Filter Classes`: Choose which classes to display
   - `Save UML as Image`: Export diagram as PNG

2. **Log Area (Top)**
   - Shows analysis progress and results
   - Displays error messages and warnings
   - Auto-scrolls to show latest information

3. **Diagram Panel (Bottom)**
   - Displays the generated UML diagram
   - Scrollable for large diagrams
   - Shows legend with relationship notation

### Step-by-Step Usage

#### 1. Load Java Files
- Click "Load Java Files"
- Select individual `.java` files or entire directories
- Multiple selections supported (Ctrl+Click)
- The system will recursively analyze directories

#### 2. Generate Diagram
- Click "Generate UML" after loading files
- All discovered classes will be shown by default
- Layout is automatically calculated

#### 3. Filter Classes (Optional)
- Click "Filter Classes" to customize display
- Select/deselect classes to show
- Use "Select All" / "Deselect All" for quick changes
- Click "OK" to apply changes

#### 4. Save Diagram
- Click "Save UML as Image"
- Choose location and filename
- High-quality PNG will be generated

## UML Notation Reference

### Class Representation
- **Regular Class**: Standard box with gradient background
- **Abstract Class**: Italicized name with `<<abstract>>` stereotype
- **Interface**: `<<interface>>` stereotype with green gradient
- **Enum**: `<<enum>>` stereotype with pink gradient

### Visibility Symbols
- `+` Public
- `-` Private  
- `#` Protected
- `~` Package-private

### Relationship Types
- **Inheritance**: Solid line with empty triangle arrow
- **Implementation**: Dashed line with empty triangle arrow
- **Composition**: Solid line with filled diamond
- **Aggregation**: Solid line with empty diamond
- **Dependency**: Dotted line with open arrow
- **Polymorphism**: Dash-dot line with empty triangle

## Advanced Features

### Regular Expression Patterns
The application uses sophisticated regex patterns to detect:
- Generic types (`List<String>`, `Map<K,V>`)
- Method modifiers (static, final, abstract, synchronized)
- Throws declarations
- Field initializations
- Abstract classes and methods

### Smart Relationship Detection
- **Inheritance**: Detects `extends` keyword
- **Implementation**: Detects `implements` keyword
- **Composition**: Identifies `final` object fields
- **Aggregation**: Identifies non-final object fields
- **Dependency**: Finds `new` object instantiations
- **Polymorphism**: Detects supertype assignments

### Layout Algorithm
- Automatic positioning to minimize overlaps
- Configurable spacing and class grouping
- Scrollable canvas for large diagrams
- Connection point calculation for clean arrows

## Troubleshooting

### Common Issues

#### "No classes found" Error
**Cause**: No valid Java class definitions in selected files
**Solution**: 
- Ensure files contain `public class`, `interface`, or `enum` definitions
- Check file encoding (should be UTF-8)
- Verify files are not corrupted

#### Compilation Errors
**Cause**: Java environment issues
**Solution**:
- Verify Java is installed: `java -version`
- Check JAVA_HOME environment variable
- Ensure all .java files are in same directory

#### Memory Issues with Large Projects
**Cause**: Insufficient JVM heap space
**Solution**:
```bash
java -Xmx1024m -Dfile.encoding=UTF-8 UMLGenerator
```

#### Garbled Text (Non-English)
**Cause**: Character encoding issues
**Solution**: Always use UTF-8 encoding flag when compiling and running

### Performance Tips

#### For Large Projects
1. Filter classes to show only relevant ones
2. Analyze smaller subdirectories separately
3. Increase JVM memory if needed
4. Close other applications to free memory

#### For Better Diagrams
1. Use descriptive class and method names
2. Follow Java naming conventions
3. Add proper visibility modifiers
4. Document relationships clearly

## Architecture Overview

### Class Structure
```
UMLGenerator (Main GUI)
├── CodeAnalyzer (File parsing)
├── DiagramRenderer (Visual rendering)
├── ClassInfo (Class data model)
├── MethodInfo (Method data model)
├── AttributeInfo (Field data model)
├── Relationship (Relationship data)
└── RelationshipType (Relationship enum)
```

### Design Patterns Used
- **MVC Pattern**: Separation of UI, logic, and data
- **Observer Pattern**: Event-driven UI updates
- **Strategy Pattern**: Different rendering strategies
- **Factory Pattern**: Object creation for parsed elements

## Extending the Application

### Adding New Relationship Types
1. Add new enum value to `RelationshipType`
2. Update regex patterns in `CodeAnalyzer`
3. Add rendering logic in `DiagramRenderer`
4. Update legend and colors

### Custom Output Formats
1. Extend `DiagramRenderer` 
2. Add new export methods
3. Implement format-specific rendering

### Enhanced Parsing
1. Modify regex patterns in `CodeAnalyzer`
2. Add new detection methods
3. Handle additional Java language features

## License and Credits

This UML Generator was designed as a comprehensive tool for Java developers to visualize their code architecture. It demonstrates advanced Java Swing programming, regular expression usage, and graphics rendering techniques.

### Key Technologies
- Java Swing for GUI
- Java 2D Graphics for rendering
- Regular Expressions for parsing
- BufferedImage for export
- Stream API for data processing

## Version History

### Current Version
- Improved regex patterns for better Java parsing
- Enhanced error handling and user feedback
- Professional visual rendering with gradients
- Support for abstract classes and generic types
- High-quality image export
- Comprehensive relationship detection
