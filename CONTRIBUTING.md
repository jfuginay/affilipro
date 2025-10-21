# Contributing to OmniCOT

First off, thank you for considering contributing to OmniCOT! It's people like you that make OmniCOT such a great tool for the ATAK community.

## Code of Conduct

This project and everyone participating in it is governed by our commitment to creating a welcoming and respectful environment. Please be kind and courteous.

## How Can I Contribute?

### Reporting Bugs

Before creating bug reports, please check the existing issues to avoid duplicates. When you create a bug report, include as many details as possible:

- **Use a clear and descriptive title**
- **Describe the exact steps to reproduce the problem**
- **Provide specific examples**
- **Include screenshots if possible**
- **Describe the behavior you observed and what you expected**
- **Include ATAK version, Android version, and device information**

### Suggesting Features

Feature suggestions are welcome! When submitting a feature request:

- **Use a clear and descriptive title**
- **Provide a detailed description of the proposed feature**
- **Explain why this feature would be useful**
- **Include examples of how the feature would be used**
- **Consider including mockups or diagrams if applicable**

### Pull Requests

1. **Fork the repository** and create your branch from `main`
2. **Make your changes** following the code style guidelines
3. **Test your changes** thoroughly on actual Android devices
4. **Update documentation** if needed
5. **Write clear commit messages** following conventional commits format
6. **Submit a pull request** with a comprehensive description

## Development Setup

### Prerequisites
- Java 17 (OpenJDK Temurin)
- Android SDK (API 21-35)
- ATAK CIV SDK 5.5.0
- Gradle 8.13+

### Getting Started
```bash
git clone https://github.com/jfuginay/omnicot.git
cd omnicot
cp template.local.properties local.properties
# Edit local.properties with your SDK paths
./gradlew assembleCivDebug
```

## Code Style Guidelines

### Java Code
- Follow standard Java naming conventions
- Use meaningful variable and method names
- Add JavaDoc comments for public methods and classes
- Keep methods focused and reasonably sized
- Handle exceptions appropriately

### XML Layouts
- Use descriptive IDs for UI elements
- Group related elements together
- Add comments for complex layouts
- Follow ATAK SDK constraints (no AndroidX)

### Commit Messages
Follow conventional commits format:
- `feat: Add new feature`
- `fix: Fix bug description`
- `docs: Update documentation`
- `style: Code formatting changes`
- `refactor: Code restructuring`
- `test: Add or update tests`
- `chore: Maintenance tasks`

## Testing Guidelines

### Manual Testing
- Test on physical Android devices when possible
- Verify compatibility with ATAK CIV 5.5.0
- Test with various map items and shapes
- Verify navigation flows work correctly
- Check for memory leaks in long-running sessions

### Test Cases to Cover
- Creating and deleting AOIs
- Updating affiliation of various map items
- Navigation between screens
- Statistics accuracy
- Back button functionality
- Error handling

## Pull Request Process

1. **Update the README.md** with details of changes if applicable
2. **Update version numbers** following semantic versioning
3. **Ensure all tests pass** and the app builds successfully
4. **Request review** from maintainers
5. **Address feedback** promptly and professionally

## Branch Naming

Use descriptive branch names:
- `feature/feature-name` for new features
- `fix/bug-description` for bug fixes
- `docs/documentation-update` for documentation
- `refactor/component-name` for refactoring

## Questions?

Feel free to open a discussion on GitHub or reach out to the maintainers.

## License

By contributing, you agree that your contributions will be licensed under the Apache License 2.0.

---

Thank you for contributing to OmniCOT! 🎯
