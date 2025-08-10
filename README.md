![alt text](https://img.shields.io/github/commit-activity/m/TheDutchRuben/tdr-mc-core)
# TDR MC Core

A comprehensive Java framework for Minecraft plugin development, supporting both Spigot and BungeeCord platforms. TDR MC Core provides a powerful three-level command system, advanced caching mechanisms, GUI framework, and various utilities to streamline plugin development.

## Features

- 🎯 **Three-Level Command System** - Hierarchical command structure with automatic registration
- 🔧 **Annotation-Based Configuration** - Simple, declarative command definitions
- 💾 **Advanced Caching System** - Persistent caching with automatic expiration
- 🎨 **GUI Framework** - Easy-to-use inventory-based user interfaces
- 🌍 **Translation Management** - Built-in internationalization support
- 🔄 **Update Checker** - Automatic version checking and notifications
- 🛠️ **Utility Classes** - Common Minecraft operations made simple
- 📊 **Cross-Platform** - Works with both Spigot and BungeeCord

## Quick Start

### Maven Dependency

```xml
<dependency>
    <groupId>nl.thedutchruben</groupId>
    <artifactId>mccore</artifactId>
    <version>1.7.0</version>
</dependency>
```

### Basic Setup

```java
public class MyPlugin extends JavaPlugin {
    
    @Override
    public void onEnable() {
        // Initialize TDR MC Core
        new Mccore(this, "your-tdr-id", "your-project-id", Mccore.PluginType.SPIGOT);
        
        getLogger().info("Plugin enabled with TDR MC Core!");
    }
}
```

### Simple Command Example

```java
@Command(command = "mycommand", description = "My awesome command", permission = "myplugin.use")
public class MyCommand {
    
    @Default
    public void defaultCommand(CommandSender sender, List<String> args) {
        sender.sendMessage("Hello from my command!");
    }
    
    @SubCommand(subCommand = "reload", description = "Reload the plugin", permission = "myplugin.reload")
    public void reloadCommand(CommandSender sender, List<String> args) {
        sender.sendMessage("Plugin reloaded!");
    }
}
```

## Command System Architecture

TDR MC Core features a unique three-level command hierarchy:

1. **Command** (`@Command`) - The main command (e.g., `/myplugin`)
2. **Sub-Command Groups** (`@SubCommandGroup`) - Category groupings (e.g., `/myplugin admin`)
3. **Sub-Commands** (`@SubCommand`) - Individual actions (e.g., `/myplugin admin reload`)

### Example Structure:
```
/myplugin              # Default command
/myplugin help         # Auto-generated help
/myplugin version      # Direct sub-command
/myplugin admin        # Sub-command group (shows help)
/myplugin admin reload # Sub-command within group
/myplugin user create  # Another group with sub-command
```

## Core Components

### Command System
- **Automatic Registration** - Commands are discovered via reflection
- **Built-in Help** - Automatic help generation for all commands
- **Tab Completion** - Smart tab completion with built-in types
- **Permission System** - Granular permission control
- **Parameter Validation** - Automatic parameter count checking

### Caching System
- **Persistent Storage** - JSON-based file storage
- **Automatic Expiration** - Time-based cache invalidation
- **Async Operations** - Non-blocking cache operations
- **Memory Efficient** - Smart memory management

### GUI Framework
- **Inventory GUIs** - Easy-to-create inventory interfaces
- **Pagination Support** - Multi-page GUI support
- **Interactive Items** - Clickable GUI elements
- **Event Handling** - Simple click event management

### Utilities
- **Item Builder** - Fluent API for item creation
- **Message Utilities** - Advanced chat formatting
- **Particle Effects** - Easy particle spawning
- **Hologram System** - Floating text displays
- **File Management** - Configuration file handling

## Documentation

- [Annotation Reference](docs/ANNOTATIONS.md) - Complete guide to all annotations
- [Command System Guide](docs/COMMANDS.md) - In-depth command system documentation
- [Examples](src/main/java/nl/thedutchruben/mccore/examples/) - Working examples
- [API Documentation](https://maven.thedutchservers.com/releases) - Generated JavaDocs

## Requirements

- Java 8+
- Spigot/Paper 1.20.1+ or BungeeCord 1.20+
- Maven (for building)

## Building

```bash
# Clone the repository
git clone https://github.com/thedutchruben/tdr-mc-core.git

# Build the project
mvn clean package

# Run tests
mvn test

# Generate documentation
mvn javadoc:javadoc
```

## Contributing

1. Fork the repository
2. Create a feature branch (`git checkout -b feature/amazing-feature`)
3. Commit your changes (`git commit -m 'Add amazing feature'`)
4. Push to the branch (`git push origin feature/amazing-feature`)
5. Open a Pull Request

## License

This project is licensed under the MIT License - see the [LICENSE](LICENSE) file for details.

## Support

- 📧 Contact: [TheDutchRuben](https://github.com/thedutchruben)
- 🐛 Issues: [GitHub Issues](https://github.com/thedutchruben/tdr-mc-core/issues)
- 💬 Discussions: [GitHub Discussions](https://github.com/thedutchruben/tdr-mc-core/discussions)
