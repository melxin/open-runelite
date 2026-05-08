# Open RuneLite - Build and Run Guide

## Overview

Open RuneLite is a modified version of RuneLite designed to have minimal downtime at RS revision updates. It stays streamlined with RuneLite master, avoiding significant gamepack alterations and using mouse/invokes instead of direct packet interaction.

## Key Features

- **Minimal Revision Update Downtime**: Stays synchronized with RuneLite master
- **No GamePack Dependency**: Avoids the issues that caused devious-client to be discontinued
- **Agent Support**: Includes an Agent branch for spoofing random.dat/uuid/platforminfo
- **Walker Support**: Devious/Unethicalite Walker implementation (in development)

## Prerequisites

- Java 11 or higher
- Gradle 8.8 or higher (included with the project)
- Git

## Building the Project

### 1. Build with Gradle

```bash
./gradlew build
```

This will:
- Compile all modules
- Run tests
- Build the client JAR

### 2. Build Specific Modules

To build only specific modules:

```bash
# Build cache module (included build)
./gradlew :cache:build

# Build API module (included build)
./gradlew :runelite-api:build

# Build client module
./gradlew :client:build
```

**Force Complete Rebuild**: If Gradle shows "up-to-date" and doesn't generate all JAR files, force a clean rebuild:

```bash
# Force clean rebuild of client module (generates all JARs including shaded)
./gradlew :client:clean :client:build
```

## Running the Project

### From IntelliJ IDEA Community Edition

#### Building in IntelliJ IDEA

1. Open the project in IntelliJ IDEA as a Gradle project
2. Wait for Gradle sync to complete
3. Open the Gradle tool window (View → Tool Windows → Gradle)
4. Navigate to `Tasks → build → build` and double-click to build the entire project
5. To build specific modules:
   - Navigate to `Tasks → build → build` under `cache`, `runelite-api`, or `client`

#### Running in IntelliJ IDEA

1. Navigate to `client/src/main/java/net/runelite/client/RuneLite.java`
2. Right-click on the `RuneLite` class
3. Select "Run 'RuneLite.main()'"
4. Alternatively, click the green triangle next to the `main` method
5. Or press `Shift+F10` when the `RuneLite.java` file is open

**Enable Assertions**: Add `-ea` to your JVM arguments in Run Configuration → VM Options

### From Eclipse

1. Open the project in Eclipse as a Gradle project
2. Wait for Gradle sync to complete
3. Navigate to `client/src/main/java/net/runelite/client/RuneLite.java`
4. Right-click and run the `RuneLite` class

### From Command Line

#### Production Mode (Recommended)

Build and run the shaded JAR in production mode:

```bash
./gradlew :client:shadowJar
java -ea -jar runelite-client/build/libs/client-*-shaded.jar
```

**Production mode automatically:**
- Loads all built-in core plugins
- Uses your existing RuneLite configuration, settings, and profiles from `~/.runelite/`
- Does NOT require the `--developer-mode` flag

**Note**: Open-runelite cannot load external plugins from the official RuneLite plugin hub due to missing verification infrastructure. To use external plugins, use development mode with sideloaded plugins (see below).

#### Development Mode

To enable developer tools and use external plugins:

```bash
./gradlew :client:shadowJar
java -ea -jar runelite-client/build/libs/client-*-shaded.jar --developer-mode
```

**Development mode additionally:**
- Loads plugins from `~/.runelite/sideloaded-plugins/` (bypasses plugin hub verification)
- Enables developer tools and debugging features
- Requires assertions to be enabled (`-ea`)

**To use your existing external plugins in development mode:**

```bash
# Copy plugins from plugin hub directory to sideloaded directory
cp ~/.runelite/plugins/*.jar ~/.runelite/sideloaded-plugins/

# Then run in developer mode
java -ea -jar runelite-client/build/libs/client-*-shaded.jar --developer-mode
```

## Using Side-Loaded Plugins

Open RuneLite supports side-loading plugins, but it's more limited than devious-client's external plugin system.

### Side-Loading Plugins

1. **Enable Developer Mode**: Run the client with the `--developer-mode` flag
2. **Create Plugins Directory**: Create `~/.runelite/sideloaded-plugins/`
3. **Place Plugin JARs**: Copy plugin JAR files to the sideloaded-plugins directory

```bash
mkdir -p ~/.runelite/sideloaded-plugins
cp /path/to/plugin.jar ~/.runelite/sideloaded-plugins/
```

**Important**: Side-loaded plugins must be built specifically for RuneLite/open-runelite with the correct API dependencies (`net.runelite.api.*`). Plugins built for other forks (unethicalite, devious-client, etc.) will not work due to incompatible API dependencies.

Then run the client with developer mode:

```bash
java -ea -jar runelite-client/build/libs/client-*-shaded.jar --developer-mode
```

**Note**: Unlike devious-client, open-runelite does not support loading plugins from GitHub repositories or custom URLs. It only supports loading JAR files from the local sideloaded-plugins directory.

### External Plugin Hub

Open RuneLite includes the standard RuneLite external plugin system, which can download verified plugins from the RuneLite plugin hub. This is accessible through the RuneLite client's plugin configuration panel.

## Development Workflow

The project follows this workflow to minimize revision update downtime:

```
RuneLite master -> (Single) Open RuneLite API commit -> Verification -> Release
```

This ensures that the Open RuneLite API stays synchronized with upstream RuneLite while adding necessary extensions gradually.

## Project Structure

- **cache/** (included build): Libraries for reading/writing cache files and accessing cache data
- **runelite-api/** (included build): RuneLite API interfaces for accessing the client
- **client/**: Game client with plugins
- **jshell/**: JShell integration for development
- **runelite-gradle-plugin/** (included build): Gradle plugin for RuneLite projects
