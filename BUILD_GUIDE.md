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

### 1. Clone the Repository

```bash
git clone https://github.com/melxin/open-runelite.git
cd open-runelite
```

### 2. Build with Gradle

```bash
./gradlew build
```

This will:
- Compile all modules
- Run tests
- Build the client JAR

### 3. Build Specific Modules

To build only specific modules:

```bash
# Build cache module (included build)
./gradlew :cache:build

# Build API module (included build)
./gradlew :runelite-api:build

# Build client module
./gradlew :client:build
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

Build and run the shaded JAR:

```bash
./gradlew :client:shadowJar
java -ea -jar runelite-client/build/libs/client-1.12.25-SNAPSHOT-shaded.jar # Check version in launcher
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
java -ea -jar runelite-client/build/libs/client-1.12.25-SNAPSHOT-shaded.jar --developer-mode
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

## Current RS Version

- RS Version: 235
- Cache Version: 165

## Configuration

### Gradle Properties

Edit `gradle.properties` to customize build settings:

```properties
org.gradle.jvmargs=-Xmx2G
```

### Version Management

Version information is managed in `libs.versions.toml`:

```toml
[versions]
rs = "235"
cache = "165"
```

## Testing

Run all tests:

```bash
./gradlew test
```

Run tests for specific modules:

```bash
./gradlew :cache:test
./gradlew :runelite-api:test
./gradlew :runelite-client:test
```

## Publishing

### Publish to Local Maven Repository

```bash
./gradlew publishToMavenLocal
```

This publishes artifacts to your local Maven repository for use in other projects.

## Troubleshooting

### Build Fails with Out of Memory Error

Increase Gradle JVM memory in `gradle.properties`:

```properties
org.gradle.jvmargs=-Xmx4G
```

### RS Version Mismatch

If you experience issues with RS version mismatches, update the version in `libs.versions.toml` to match the current upstream RuneLite version.

### Plugin Loading Issues

If external plugins fail to load, check:
1. Plugin compatibility with current RS version
2. Plugin manifest file configuration
3. External Plugin Manager settings

## Agent Branch

For advanced features like spoofing random.dat/uuid/platforminfo, check out the `runelite-agent-openrl-api-rs2` branch:

```bash
git checkout runelite-agent-openrl-api-rs2
```

## Contributing

1. Fork the repository
2. Create a feature branch
3. Make your changes
4. Ensure tests pass
5. Submit a pull request

## License

RuneLite is licensed under the BSD 2-clause license. See the license header in respective files for details.

## Additional Resources

- [RuneLite Wiki](https://github.com/runelite/runelite/wiki)
- [RuneLite Developer Guide](https://github.com/runelite/runelite/wiki/Developer-Guide)
- [RuneLite Discord](https://runelite.net/discord)
- IRC: irc.rizon.net #runelite

## Known Issues and Design Notes

### Minor Code Comments

The following are design notes and minor enhancements, not critical issues:

- **WorldUtil.java**: Design note about handling duplicate interfaces between http-api and runelite-api world types
- **ImageUtilTest.java**: Test enhancements for image size changes and rotation testing
- **ExternalPluginManager.java**: Note about ensuring plugins get fully removed from scheduler/eventbus/other managers
- **Zone.java**: Rendering fix needed for boats
- **ObjectIndicatorsPlugin.java**: Enhancement to remove points when boats despawn

### Major Feature TODOs (From Melxin)

These are architectural features that require significant development:

- **External Plugin Manager**: Complete implementation for managing external plugins
- **Launcher**: Create a dedicated launcher for the client
- **Walker Integration**: Integrate Devious/Unethicalite Walker (already working in another branch, needs improvements/cleanup/verification)
- **Multi-Client Walker**: Add support for walkers from other clients

## Support

For questions or issues:
- Join the [RuneLite Discord](https://runelite.net/discord)
- Visit IRC: irc.rizon.net #runelite
- Check the [RuneLite Wiki](https://github.com/runelite/runelite/wiki)

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

### 1. Clone the Repository

```bash
git clone https://github.com/melxin/open-runelite.git
cd open-runelite
```

### 2. Build with Gradle

```bash
./gradlew build
```

This will:
- Compile all modules
- Run tests
- Build the client JAR

### 3. Build Specific Modules

To build only specific modules:

```bash
# Build cache module (included build)
./gradlew :cache:build

# Build API module (included build)
./gradlew :runelite-api:build

# Build client module
./gradlew :client:build
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

Build and run the shaded JAR:

```bash
./gradlew :client:shadowJar
java -ea -jar runelite-client/build/libs/client-1.12.25-SNAPSHOT-shaded.jar # Check version in launcher
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
java -ea -jar runelite-client/build/libs/client-1.12.25-SNAPSHOT-shaded.jar --developer-mode
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

## Current RS Version

- RS Version: 235
- Cache Version: 165

## Configuration

### Gradle Properties

Edit `gradle.properties` to customize build settings:

```properties
org.gradle.jvmargs=-Xmx2G
```

### Version Management

Version information is managed in `libs.versions.toml`:

```toml
[versions]
rs = "235"
cache = "165"
```

## Testing

Run all tests:

```bash
./gradlew test
```

Run tests for specific modules:

```bash
./gradlew :cache:test
./gradlew :runelite-api:test
./gradlew :runelite-client:test
```

## Publishing

### Publish to Local Maven Repository

```bash
./gradlew publishToMavenLocal
```

This publishes artifacts to your local Maven repository for use in other projects.

## Troubleshooting

### Build Fails with Out of Memory Error

Increase Gradle JVM memory in `gradle.properties`:

```properties
org.gradle.jvmargs=-Xmx4G
```

### RS Version Mismatch

If you experience issues with RS version mismatches, update the version in `libs.versions.toml` to match the current upstream RuneLite version.

### Plugin Loading Issues

If external plugins fail to load, check:
1. Plugin compatibility with current RS version
2. Plugin manifest file configuration
3. External Plugin Manager settings

## Agent Branch

For advanced features like spoofing random.dat/uuid/platforminfo, check out the `runelite-agent-openrl-api-rs2` branch:

```bash
git checkout runelite-agent-openrl-api-rs2
```

## Contributing

1. Fork the repository
2. Create a feature branch
3. Make your changes
4. Ensure tests pass
5. Submit a pull request

## License

RuneLite is licensed under the BSD 2-clause license. See the license header in respective files for details.

## Additional Resources

- [RuneLite Wiki](https://github.com/runelite/runelite/wiki)
- [RuneLite Developer Guide](https://github.com/runelite/runelite/wiki/Developer-Guide)
- [RuneLite Discord](https://runelite.net/discord)
- IRC: irc.rizon.net #runelite

## Known Issues and Design Notes

### Minor Code Comments

The following are design notes and minor enhancements, not critical issues:

- **WorldUtil.java**: Design note about handling duplicate interfaces between http-api and runelite-api world types
- **ImageUtilTest.java**: Test enhancements for image size changes and rotation testing
- **ExternalPluginManager.java**: Note about ensuring plugins get fully removed from scheduler/eventbus/other managers
- **Zone.java**: Rendering fix needed for boats
- **ObjectIndicatorsPlugin.java**: Enhancement to remove points when boats despawn

### Major Feature TODOs (From Melxin)

These are architectural features that require significant development:

- **External Plugin Manager**: Complete implementation for managing external plugins
- **Launcher**: Create a dedicated launcher for the client
- **Walker Integration**: Integrate Devious/Unethicalite Walker (already working in another branch, needs improvements/cleanup/verification)
- **Multi-Client Walker**: Add support for walkers from other clients

## Support

For questions or issues:
- Join the [RuneLite Discord](https://runelite.net/discord)
- Visit IRC: irc.rizon.net #runelite
- Check the [RuneLite Wiki](https://github.com/runelite/runelite/wiki)
