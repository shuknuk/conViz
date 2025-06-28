#pragma once

#include <cstdint>

/**
 * TraceView Binary Format Specification
 * 
 * This file defines the binary format for .tview trace files.
 * The format is designed to be compact, fast to write, and easy to parse.
 */

namespace traceview {

// File format version
constexpr uint32_t TRACE_FORMAT_VERSION = 1;

// Magic number for .tview files: "TVIEW" in ASCII
constexpr uint32_t TRACE_MAGIC = 0x54564945;

// Event types
enum class EventType : uint8_t {
    THREAD_START = 1,
    THREAD_END = 2,
    MUTEX_LOCK_ATTEMPT = 3,
    MUTEX_LOCK_ACQUIRED = 4,
    MUTEX_UNLOCK = 5,
    CONDITION_WAIT = 6,
    CONDITION_SIGNAL = 7,
    ANNOTATION = 8,
    SLEEP_START = 9,
    SLEEP_END = 10,
    IO_START = 11,
    IO_END = 12
};

// Thread states (derived from events)
enum class ThreadState : uint8_t {
    EXECUTING = 1,    // Green - thread is running
    BLOCKED_SYNC = 2, // Red - blocked on mutex/condition
    BLOCKED_IO = 3,   // Blue - blocked on I/O
    SLEEPING = 4      // Gray - sleeping or idle
};

// File header (fixed size: 64 bytes)
struct TraceHeader {
    uint32_t magic;           // TRACE_MAGIC
    uint32_t version;         // TRACE_FORMAT_VERSION
    uint64_t start_time_ns;   // Trace start time (nanoseconds since epoch)
    uint64_t end_time_ns;     // Trace end time (nanoseconds since epoch)
    uint32_t num_threads;     // Number of threads in trace
    uint32_t num_events;      // Total number of events
    uint32_t string_table_offset; // Offset to string table
    uint32_t string_table_size;   // Size of string table
    uint8_t  padding[16];     // Reserved for future use
};

// Event record (variable size)
struct EventRecord {
    uint64_t timestamp_ns;    // Event timestamp (nanoseconds since trace start)
    uint32_t thread_id;       // Thread ID
    EventType event_type;     // Type of event
    uint8_t  data_size;       // Size of event-specific data
    uint16_t padding;         // Alignment padding
    // Followed by event-specific data (data_size bytes)
};

// Thread metadata
struct ThreadInfo {
    uint32_t thread_id;       // Thread ID
    uint32_t name_offset;     // Offset into string table for thread name
    uint64_t start_time_ns;   // Thread start time
    uint64_t end_time_ns;     // Thread end time
};

// Mutex/synchronization object metadata
struct SyncObjectInfo {
    uint64_t object_id;       // Memory address of mutex/condition
    uint32_t name_offset;     // Offset into string table for object name
    uint32_t type;            // Type of sync object (mutex, condition, etc.)
};

// Event-specific data structures

struct MutexLockData {
    uint64_t mutex_id;        // Memory address of mutex
};

struct ConditionWaitData {
    uint64_t condition_id;    // Memory address of condition variable
    uint64_t mutex_id;        // Associated mutex
};

struct AnnotationData {
    uint32_t message_offset;  // Offset into string table
};

struct SleepData {
    uint64_t duration_ns;     // Sleep duration in nanoseconds
};

struct IOData {
    uint32_t operation_offset; // Offset into string table (e.g., "read", "write")
    uint32_t target_offset;    // Offset into string table (e.g., filename, socket)
};

} // namespace traceview
