# CLAUDE.md

This file provides guidance to Claude Code (claude.ai/code) when working with code in this repository.

## Project Overview

TDR MC Core is a Java library/framework for Minecraft plugin development that supports both Spigot and BungeeCord platforms. It provides a comprehensive command system, caching mechanisms, UI components, and various utilities for plugin development.

**Key Features:**
- Three-level hierarchical command system with annotations
- Built-in caching system with persistent storage
- GUI framework for inventory-based UIs
- Translation management system
- Update checker functionality
- Utility classes for common Minecraft operations

## Build Commands

This is a Maven project. Common build commands:

```bash
# Compile the project
mvn compile

# Run tests
mvn test

# Package the project (creates JAR)
mvn package

# Clean and rebuild
mvn clean package

# Generate JavaDocs
mvn javadoc:javadoc

# Run a single test
mvn test -Dtest=TabCompleteTest
```

## Architecture Overview

### Core Components

**Main Entry Point:**
- `Mccore.java` - Central class that initializes the framework for either Spigot or BungeeCord platforms

**Command System (`spigot.commands` package):**
- Three-level command hierarchy: Command > SubCommandGroup > SubCommand
- Annotation-based command registration: `@Command`, `@SubCommand`, `@SubCommandGroup`
- Built-in help system and tab completion
- Support for `@Default` and `@Fallback` commands
- Example implementation in `examples/TestSystemCommand.java`

**Command Structure:**
```java
@Command(command = "mycommand", permission = "plugin.use")
public class MyCommand {
    @Default
    public void defaultCommand(CommandSender sender, List<String> args) { }
    
    @SubCommandGroup(value = "admin", permission = "plugin.admin")
    public static class AdminCommands {
        @SubCommand(subCommand = "reload", minParams = 0, maxParams = 0)
        public void reload(CommandSender sender, List<String> args) { }
    }
}
```

**Caching System (`global.caching` package):**
- `CachingManager` - Main caching interface
- `CachingObject` - Individual cached items with expiration
- `JsonFileType` - JSON-based persistent storage
- Async loading and automatic persistence for flagged objects

**UI Framework (`spigot.ui` package):**
- `GUI` - Base inventory GUI class
- `PaginatedGUI` - Multi-page inventory support
- `GUIButton` and `GUIItem` - Interactive inventory items
- `GUIManager` - GUI instance management

**Utilities (`utils` package):**
- `ItemBuilder` - Fluent item creation API
- `MessageUtil` - Chat message formatting and URL components
- `ParticleUtil` and `FireworkUtil` - Visual effects
- `Hologram` - Floating text displays
- `FileManager` - Configuration file handling

### Key Patterns

**Command Registration:**
Commands are automatically discovered via reflection by scanning classes with `@Command` annotations in the plugin's package.

**Caching Pattern:**
Objects implement persistence through the `CachingObject` interface, with automatic disk storage for items marked as persistent.

**GUI Pattern:**
GUIs extend base classes and override click handlers, with automatic inventory management.

## Development Guidelines

**Adding New Commands:**
1. Create class with `@Command` annotation
2. Add methods with `@SubCommand` annotations
3. Use nested static classes with `@SubCommandGroup` for organization
4. Commands are auto-registered on plugin initialization

**Tab Completion:**
Built-in tab completions available: `player`, `uuid`, `world`, `material`, `entitytype`, `permission`, `color`, `plugin`. Register custom completions via `CommandRegistry.getTabCompletable()`.

**Testing:**
- Tests use JUnit Jupiter 5.12.0
- Currently minimal test coverage (placeholder test in `TabCompleteTest`)
- Test files should follow naming convention `*Test.java`

**Dependencies:**
- Spigot API 1.20.1+ (provided scope)
- BungeeCord API (provided scope)
- Lombok for code generation
- Gson for JSON operations (via utilities)

## Package Structure

```
nl.thedutchruben.mccore/
├── Mccore.java              # Main framework class
├── bungee/                  # BungeeCord-specific implementations
├── spigot/                  # Spigot-specific implementations
│   ├── commands/            # Three-level command system
│   ├── listeners/           # Event listener registry
│   ├── runnables/           # Task scheduling system
│   └── ui/                  # GUI framework
├── global/                  # Platform-agnostic components
│   ├── caching/             # Caching system
│   └── translations/        # i18n support
├── utils/                   # Utility classes
└── examples/                # Example implementations
```