# Contributing to Modern Paint

Thank you for considering contributing to Modern Paint! This document provides guidelines and instructions for contributing to this project.

## Code of Conduct

By participating in this project, you agree to maintain a respectful and inclusive environment for everyone.

## How Can I Contribute?

### Reporting Bugs

If you find a bug, please create an issue with the following information:
- A clear, descriptive title
- Steps to reproduce the issue
- Expected behavior
- Actual behavior
- Screenshots if applicable
- Your environment details (OS, Java version, etc.)

### Suggesting Enhancements

For feature requests or enhancements:
- Use a clear, descriptive title
- Provide a detailed description of the suggested enhancement
- Explain why this enhancement would be useful
- Include any relevant examples or mock-ups

### Pull Requests

1. Fork the repository
2. Create a new branch (`git checkout -b feature/amazing-feature`)
3. Make your changes
4. Commit your changes (`git commit -m 'Add some amazing feature'`)
5. Push to the branch (`git push origin feature/amazing-feature`)
6. Open a Pull Request

#### Pull Request Guidelines

- Follow the existing code style
- Include comments in your code where necessary
- Update documentation if needed
- Add tests if applicable
- Ensure all tests pass
- Keep pull requests focused on a single feature or bug fix

## Development Setup

1. Clone the repository
2. Set up your Java development environment
3. Compile the project:
```
javac -d out/production/Paint src/*.java
```
4. Run the application:
```
java -cp out/production/Paint Main
```

## Styleguides

### Git Commit Messages

- Use the present tense ("Add feature" not "Added feature")
- Use the imperative mood ("Move cursor to..." not "Moves cursor to...")
- Limit the first line to 72 characters or less
- Reference issues and pull requests liberally after the first line

### Java Styleguide

- Follow standard Java naming conventions
- Use meaningful variable and method names
- Add comments for complex logic
- Keep methods focused on a single task
- Use proper indentation (4 spaces)

## Additional Notes

### Issue and Pull Request Labels

- `bug`: Something isn't working
- `enhancement`: New feature or request
- `documentation`: Improvements or additions to documentation
- `good first issue`: Good for newcomers
- `help wanted`: Extra attention is needed

Thank you for contributing to Modern Paint!
