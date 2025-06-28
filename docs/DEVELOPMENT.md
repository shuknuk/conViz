# TraceView Development Guide

## Project Structure

```
conViz/
├── agent/              # C++ trace collection library
│   ├── src/           # Implementation files
│   ├── include/       # Public API headers
│   ├── examples/      # Example applications
│   ├── tests/         # Unit tests
│   ├── CMakeLists.txt # Build configuration
│   └── Makefile       # Convenience wrapper
├── gui/               # Electron desktop application
│   ├── src/           # TypeScript/React source
│   ├── public/        # Static assets
│   ├── package.json   # Node.js dependencies
│   └── webpack.config.js # Build configuration
├── shared/            # Common definitions
│   └── trace-format/  # Binary format specification
└── docs/              # Documentation
```

## Building

### Prerequisites

- **C++ Compiler**: GCC 9+ or Clang 10+ with C++17 support
- **CMake**: 3.16 or later
- **Node.js**: 18+ with npm
- **Git**: For version control

### Agent Library

```bash
cd agent
make                    # Build release version
make debug             # Build with debug symbols
make examples          # Build example applications
make test              # Run unit tests
make clean             # Clean build artifacts
```

### GUI Application

```bash
cd gui
npm install            # Install dependencies
npm run dev            # Start development server
npm run build          # Build for production
npm run electron:pack  # Package for distribution
```

## Development Workflow

### 1. Agent Development

The trace agent is a lightweight C++ library that uses pthread interposition to automatically capture synchronization events. Key components:

- **trace_agent.cpp**: Main API implementation
- **pthread_interceptor.cpp**: pthread function interposition
- **event_collector.cpp**: Thread-safe event collection
- **trace_writer.cpp**: Binary file format writer

### 2. GUI Development

The GUI is an Electron application built with React and TypeScript. Key components:

- **main.ts**: Electron main process
- **renderer.tsx**: React application entry point
- **components/**: React components for UI
- **utils/**: Utility functions and trace file parsing

### 3. File Format

The `.tview` binary format is defined in `shared/trace-format/trace_format.h`. It consists of:

- Fixed-size header with metadata
- Variable-size event records
- String table for names and messages
- Thread and synchronization object metadata

## Testing

### Agent Tests

```bash
cd agent
make test
```

### GUI Tests

```bash
cd gui
npm test
```

### Integration Testing

Create a simple multi-threaded C++ program, instrument it with the agent, and load the resulting trace file in the GUI to verify end-to-end functionality.

## Debugging

### Agent Debugging

- Use `make debug` to build with debug symbols
- Set `TRACE_DEBUG=1` environment variable for verbose logging
- Use `gdb` or `lldb` to debug instrumented applications

### GUI Debugging

- Use Chrome DevTools (Ctrl+Shift+I in Electron)
- Enable source maps for TypeScript debugging
- Use React DevTools extension

## Performance Considerations

### Agent Performance

- Events are collected in thread-local buffers to minimize contention
- Binary format is designed for fast writing during execution
- Minimal overhead when tracing is disabled

### GUI Performance

- Use virtualization for large trace files
- Implement efficient zoom/pan algorithms
- Cache rendered timeline segments

## Contributing

1. Follow the existing code style and patterns
2. Add tests for new functionality
3. Update documentation for API changes
4. Ensure cross-platform compatibility (macOS, Linux, Windows)
5. Keep the MVP scope focused on core visualization features

## Architecture Decisions

### Why Post-Mortem Analysis?

- Simpler than real-time streaming for MVP
- No network protocols or live data handling
- Easier to debug and reproduce issues
- Better performance characteristics

### Why Electron for GUI?

- Cross-platform desktop application
- Rich ecosystem for data visualization
- Familiar web technologies (HTML/CSS/JS)
- Easy to prototype and iterate quickly

### Why C++ for Agent?

- Low-level system integration required
- Performance-critical code path
- Wide compatibility with existing codebases
- Standard pthread interposition techniques
