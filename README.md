# TraceView - Concurrency Visualizer

A desktop application for developers that transforms complex concurrency trace files into a simple, interactive timeline. Debug deadlocks and optimize performance bottlenecks in minutes, not hours.

## Architecture

TraceView consists of two main components:

1. **Trace Agent** (`agent/`) - Java agent for collecting concurrency events using JVM instrumentation
2. **GUI Visualizer** (`gui/`) - JavaFX desktop application for visualizing trace files

## Quick Start

### 1. Build the Project
```bash
./gradlew build
```

### 2. Instrument Your Java Application
```java
import com.traceview.agent.TraceAgent;

public class MyApp {
    public static void main(String[] args) {
        TraceAgent.init("my_app_trace.tview");
        // Your application code here
        TraceAgent.shutdown();
    }
}
```

### 3. Run with Java Agent (Alternative)
```bash
java -javaagent:agent/build/libs/trace-agent.jar=output=trace.tview MyApp
```

### 4. Run the Visualizer
```bash
./gradlew :gui:run
# Then drag and drop your .tview file
```

## Project Structure

```
conViz/
├── agent/              # Java agent for trace collection
│   ├── src/main/java/ # Agent source code
│   ├── src/test/java/ # Unit tests
│   └── build.gradle   # Build configuration
├── gui/               # JavaFX desktop visualizer
│   ├── src/main/java/ # GUI source code
│   ├── src/main/resources/ # FXML and assets
│   └── build.gradle   # Build configuration
├── shared/            # Common formats and utilities
│   ├── src/main/java/ # Shared code
│   └── build.gradle   # Build configuration
├── examples/          # Sample applications
│   └── src/main/java/ # Example multi-threaded apps
├── docs/              # Documentation
├── build.gradle       # Root build configuration
└── gradle.properties  # Project properties
```

## Development

- **Agent**: Java with bytecode instrumentation and JVM monitoring
- **GUI**: JavaFX with FXML for rich desktop interface
- **Platform**: Cross-platform JVM (macOS, Linux, Windows)
- **Build**: Gradle multi-project build

## MVP Scope

- Java agent with automatic thread and synchronization monitoring
- Drag-and-drop trace file loading
- Interactive timeline with thread lanes
- State visualization (Executing, Blocked, I/O, Idle)
- Synchronization arrows showing lock handoffs
- Contextual information panel with hyperlinks
