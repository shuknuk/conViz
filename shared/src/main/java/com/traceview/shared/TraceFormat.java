package com.traceview.shared;

/**
 * TraceView Binary Format Specification
 * 
 * This class defines the binary format for .tview trace files.
 * The format is designed to be compact, fast to write, and easy to parse.
 */
public class TraceFormat {
    
    // File format version
    public static final int TRACE_FORMAT_VERSION = 1;
    
    // Magic number for .tview files: "TVIEW" in ASCII
    public static final int TRACE_MAGIC = 0x54564945;
    
    // Fixed header size in bytes
    public static final int HEADER_SIZE = 64;
    
    /**
     * Event types that can be recorded in a trace
     */
    public enum EventType {
        THREAD_START(1),
        THREAD_END(2),
        MUTEX_LOCK_ATTEMPT(3),
        MUTEX_LOCK_ACQUIRED(4),
        MUTEX_UNLOCK(5),
        MONITOR_WAIT(6),
        MONITOR_NOTIFY(7),
        ANNOTATION(8),
        SLEEP_START(9),
        SLEEP_END(10),
        IO_START(11),
        IO_END(12);
        
        private final byte value;
        
        EventType(int value) {
            this.value = (byte) value;
        }
        
        public byte getValue() {
            return value;
        }
        
        public static EventType fromValue(byte value) {
            for (EventType type : values()) {
                if (type.value == value) {
                    return type;
                }
            }
            throw new IllegalArgumentException("Unknown event type: " + value);
        }
    }
    
    /**
     * Thread states derived from events
     */
    public enum ThreadState {
        EXECUTING(1),     // Green - thread is running
        BLOCKED_SYNC(2),  // Red - blocked on synchronization
        BLOCKED_IO(3),    // Blue - blocked on I/O
        SLEEPING(4);      // Gray - sleeping or idle
        
        private final byte value;
        
        ThreadState(int value) {
            this.value = (byte) value;
        }
        
        public byte getValue() {
            return value;
        }
        
        public static ThreadState fromValue(byte value) {
            for (ThreadState state : values()) {
                if (state.value == value) {
                    return state;
                }
            }
            throw new IllegalArgumentException("Unknown thread state: " + value);
        }
    }
    
    /**
     * File header structure (64 bytes)
     */
    public static class TraceHeader {
        public int magic;              // TRACE_MAGIC
        public int version;            // TRACE_FORMAT_VERSION
        public long startTimeNs;       // Trace start time (nanoseconds since epoch)
        public long endTimeNs;         // Trace end time (nanoseconds since epoch)
        public int numThreads;         // Number of threads in trace
        public int numEvents;          // Total number of events
        public int stringTableOffset;  // Offset to string table
        public int stringTableSize;    // Size of string table
        public byte[] padding = new byte[16]; // Reserved for future use
        
        public TraceHeader() {
            this.magic = TRACE_MAGIC;
            this.version = TRACE_FORMAT_VERSION;
        }
    }
    
    /**
     * Event record structure (variable size)
     */
    public static class EventRecord {
        public long timestampNs;       // Event timestamp (nanoseconds since trace start)
        public int threadId;           // Thread ID
        public EventType eventType;    // Type of event
        public byte dataSize;          // Size of event-specific data
        public short padding;          // Alignment padding
        public byte[] data;            // Event-specific data
        
        public EventRecord() {}
        
        public EventRecord(long timestampNs, int threadId, EventType eventType, byte[] data) {
            this.timestampNs = timestampNs;
            this.threadId = threadId;
            this.eventType = eventType;
            this.data = data != null ? data : new byte[0];
            this.dataSize = (byte) this.data.length;
        }
    }
    
    /**
     * Thread metadata
     */
    public static class ThreadInfo {
        public int threadId;           // Thread ID
        public int nameOffset;         // Offset into string table for thread name
        public long startTimeNs;       // Thread start time
        public long endTimeNs;         // Thread end time
        
        public ThreadInfo() {}
        
        public ThreadInfo(int threadId, int nameOffset, long startTimeNs, long endTimeNs) {
            this.threadId = threadId;
            this.nameOffset = nameOffset;
            this.startTimeNs = startTimeNs;
            this.endTimeNs = endTimeNs;
        }
    }
    
    /**
     * Synchronization object metadata
     */
    public static class SyncObjectInfo {
        public long objectId;          // Memory address/hash of sync object
        public int nameOffset;         // Offset into string table for object name
        public int type;               // Type of sync object (0=mutex, 1=monitor, etc.)
        
        public SyncObjectInfo() {}
        
        public SyncObjectInfo(long objectId, int nameOffset, int type) {
            this.objectId = objectId;
            this.nameOffset = nameOffset;
            this.type = type;
        }
    }
    
    /**
     * Event-specific data structures
     */
    
    public static class MutexLockData {
        public long mutexId;           // Memory address/hash of mutex
        
        public MutexLockData() {}
        
        public MutexLockData(long mutexId) {
            this.mutexId = mutexId;
        }
    }
    
    public static class MonitorWaitData {
        public long monitorId;         // Memory address/hash of monitor
        public long timeout;           // Wait timeout in nanoseconds (0 = no timeout)
        
        public MonitorWaitData() {}
        
        public MonitorWaitData(long monitorId, long timeout) {
            this.monitorId = monitorId;
            this.timeout = timeout;
        }
    }
    
    public static class AnnotationData {
        public int messageOffset;      // Offset into string table
        
        public AnnotationData() {}
        
        public AnnotationData(int messageOffset) {
            this.messageOffset = messageOffset;
        }
    }
    
    public static class SleepData {
        public long durationNs;        // Sleep duration in nanoseconds
        
        public SleepData() {}
        
        public SleepData(long durationNs) {
            this.durationNs = durationNs;
        }
    }
    
    public static class IOData {
        public int operationOffset;    // Offset into string table (e.g., "read", "write")
        public int targetOffset;       // Offset into string table (e.g., filename, socket)
        
        public IOData() {}
        
        public IOData(int operationOffset, int targetOffset) {
            this.operationOffset = operationOffset;
            this.targetOffset = targetOffset;
        }
    }
}
