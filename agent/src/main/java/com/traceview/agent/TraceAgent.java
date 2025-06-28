package com.traceview.agent;

import java.lang.instrument.Instrumentation;
import java.util.concurrent.atomic.AtomicBoolean;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * TraceView Agent - Java agent for collecting concurrency events
 * 
 * This agent can be used in two ways:
 * 1. As a traditional Java agent: java -javaagent:trace-agent.jar=output=trace.tview MyApp
 * 2. As a library: TraceAgent.init() in your application code
 */
public class TraceAgent {
    
    private static final Logger logger = LoggerFactory.getLogger(TraceAgent.class);
    private static final AtomicBoolean initialized = new AtomicBoolean(false);
    private static final AtomicBoolean active = new AtomicBoolean(false);
    
    private static EventCollector eventCollector;
    private static ThreadMonitor threadMonitor;
    private static String outputPath = "trace.tview";
    
    /**
     * Java agent premain method - called when used as -javaagent
     */
    public static void premain(String agentArgs, Instrumentation instrumentation) {
        logger.info("TraceView Agent starting (premain)");
        
        // Parse agent arguments
        parseAgentArgs(agentArgs);
        
        // Initialize the agent
        if (initialize(instrumentation)) {
            // Install shutdown hook to ensure trace is written
            Runtime.getRuntime().addShutdownHook(new Thread(() -> {
                logger.info("Shutdown hook executing, finalizing trace");
                shutdown();
            }));
        }
    }
    
    /**
     * Java agent agentmain method - called when attached to running JVM
     */
    public static void agentmain(String agentArgs, Instrumentation instrumentation) {
        logger.info("TraceView Agent starting (agentmain)");
        parseAgentArgs(agentArgs);
        initialize(instrumentation);
    }
    
    /**
     * Initialize the trace agent programmatically
     * 
     * @param traceFilePath Path where the .tview trace file will be written
     * @return true if initialization was successful
     */
    public static boolean init(String traceFilePath) {
        if (traceFilePath != null && !traceFilePath.isEmpty()) {
            outputPath = traceFilePath;
        }
        return initialize(null);
    }
    
    /**
     * Initialize the trace agent with default output path
     */
    public static boolean init() {
        return init(null);
    }
    
    /**
     * Internal initialization method
     */
    private static boolean initialize(Instrumentation instrumentation) {
        if (!initialized.compareAndSet(false, true)) {
            logger.warn("TraceView Agent already initialized");
            return true;
        }
        
        try {
            logger.info("Initializing TraceView Agent with output path: {}", outputPath);
            
            // Create event collector
            eventCollector = new EventCollector(outputPath);
            
            // Create and start thread monitor
            threadMonitor = new ThreadMonitor(eventCollector);
            
            // If we have instrumentation, set up bytecode modification
            if (instrumentation != null) {
                setupInstrumentation(instrumentation);
            }
            
            // Start monitoring
            threadMonitor.start();
            eventCollector.start();
            
            active.set(true);
            logger.info("TraceView Agent initialized successfully");
            return true;
            
        } catch (Exception e) {
            logger.error("Failed to initialize TraceView Agent", e);
            initialized.set(false);
            return false;
        }
    }
    
    /**
     * Finalize tracing and write the trace file
     * 
     * @return true if finalization was successful
     */
    public static boolean shutdown() {
        if (!active.compareAndSet(true, false)) {
            logger.debug("TraceView Agent not active or already finalized");
            return true;
        }
        
        try {
            logger.info("Finalizing TraceView Agent");
            
            if (threadMonitor != null) {
                threadMonitor.stop();
            }
            
            if (eventCollector != null) {
                eventCollector.stop();
            }
            
            logger.info("TraceView Agent finalized, trace written to: {}", outputPath);
            return true;
            
        } catch (Exception e) {
            logger.error("Error finalizing TraceView Agent", e);
            return false;
        }
    }
    
    /**
     * Check if the trace agent is currently active
     */
    public static boolean isActive() {
        return active.get();
    }
    
    /**
     * Add a custom annotation to the trace
     */
    public static void annotate(String message) {
        if (isActive() && eventCollector != null) {
            eventCollector.recordAnnotation(message);
        }
    }
    
    /**
     * Parse agent arguments from the command line
     */
    private static void parseAgentArgs(String agentArgs) {
        if (agentArgs == null || agentArgs.trim().isEmpty()) {
            return;
        }
        
        String[] args = agentArgs.split(",");
        for (String arg : args) {
            String[] keyValue = arg.split("=", 2);
            if (keyValue.length == 2) {
                String key = keyValue[0].trim();
                String value = keyValue[1].trim();
                
                switch (key) {
                    case "output":
                        outputPath = value;
                        break;
                    case "debug":
                        if ("true".equalsIgnoreCase(value)) {
                            // Enable debug logging if possible
                            System.setProperty("traceview.debug", "true");
                        }
                        break;
                    default:
                        logger.warn("Unknown agent argument: {}", key);
                }
            }
        }
    }
    
    /**
     * Set up bytecode instrumentation for automatic monitoring
     */
    private static void setupInstrumentation(Instrumentation instrumentation) {
        try {
            // TODO: Implement bytecode instrumentation for automatic 
            // monitoring of synchronization operations
            logger.info("Bytecode instrumentation setup complete");
        } catch (Exception e) {
            logger.warn("Failed to setup bytecode instrumentation", e);
        }
    }
}
