# Profiler

The IOpipe profiler generates snapshots which can be opened and parsed with
[VisualVM](https://visualvm.github.io). VisualVM has downloadable executables
for Windows and Mac, and may also be installed on Debian and Ubuntu based
systems by running `apt-get install visualvm`.

 * [Enabling Profiling](#enabling-profiling)
   * [For Serverless Framework](#for-serverless-framework)
 * [Customizing Profiling](#customizing-profiling)
 * [Getting Profiling Data](#getting-profiling-data)
 * [How To Use Sampling-Only Profiler Data](#how-to-use-sampling-only-profiler-data)
 * [Statistics Data](#statistics-data)
   * [Timing](#timing)
   * [Class Loading](#class-loading)
   * [Garbage Collection](#garbage-collection)
   * [Memory](#memory)
   * [Threads](#threads)
   * [Buffer Pools](#buffer-pools)

There is currently one mode of operation, which is a sampling-only profiler
which inspects the state of all threads to determine how long they have been
running for. This mode does not require any code to be modified to support profiling
so all code run by your Lambda functions will be profiled automatically, as
long as the profiler is enabled.

# Enabling Profiling

Set the environment variable IOPIPE_PROFILER_ENABLED to `true`

## For Serverless Framework

In the `serverless.yml` file, under `functions:`, `(your_function_name_here):`, `environment:`, set the environment variable `IOPIPE_PROFILER_ENABLED` to `true`

## Customizing Profiling

The profiler may be customized by setting the specified properties and/or
environment variables:

* Sample Rate: The duration of time between each sample, in microseconds
  * The default is 1,000 microseconds (1ms).
  * `IOPIPE_PROFILER_SAMPLERATE` as an environment variable

Additionally the following are advanced environment variables which the
profiler uses to slightly change its behavior, these are not intended for
normal users to use unless in specific cases. These are intended for use in
debugging the profiler.

 * `IOPIPE_PROFILER_LOCAL_DUMP_PATH`
   * A path which to where the ZIP which is uploaded to IOpipe will be stored
     additionally on the file system, this is for debugging purposes
   * Note that the file will be stored on the file systems which are local
     to the lambda.
   * An absolute path should be used because the working directory may be
     undefined.
   * The default value is not set, setting a value will cause the file to be
     created if it is able to be created.
   * Any file that already exists will be truncated and replaced.
   * The string should be a valid filename.
 * `IOPIPE_PROFILER_ALTERNATIVE_PREFIX`
   * Instead of using a timestamp as the prefix for profiler snapshots, the
     following prefix will be used instead.
   * The default value is not set in which case it will the default prefix
     generated from a timestamp, if it is set then that prefix will be used
     instead.
   * The prefix should consist of characters which make up valid filenames.

# Getting Profiling Data

When the profiler is enabled, .zip files containing the profiling data may be downloaded from the individual
invocation information page, under 'Profiling'. Unzip it, then load the file in VisualVM as a profile snapshot.

# How To Use Sampling-Only Profiler Data

In this mode of the profiler, only methods which are currently executed will
have execution time added when it is at the top of the stack trace, as that
is when the profiler 'sees' it. As such in VisualVM the best
metric to use to determine how long a method takes would be the self-time and
the self-time percentage field. The self-time in seconds will generally be very
close to multiples of the sampling frequency and will reflect the amount of time
the sampler has seen the method for. The percentage indicates how much time
compared to all other methods that the sampler has seen the method executing.

Methods which are very fast and run quicker than the sampling rate may or may
not appear in the sampled data. If these methods are rapidly executed
then they may be seen more often. However, the virtual machine may opt to
inline these methods in which case they may never appear to be executed.

The invocation count in this mode is always `1` because it is unknown how many
times a method has actually executed in a thread. The self time reflects the
time the sampler has seen the method.

# Statistics Data

When the profiler is enabled, snapshots of the statistics of the virtual
machine are obtained and stored in the profiler information. It is stored in
comma separated values and may be read by a spreadsheet or other utilities.

The following sections document the meanings of the statistics data.

## Timing

 * _AbsoluteTime (ns)_
   * The time the snapshot was created.
 * _RelativeTime (ns)_
   * The time since the start of execution the snapshot was created.
 * _StartTime (utc ms)_
   * The number of milliseconds since the UNIX Epoch when the virtual machine
    was created.
 * _UpTime (ms)_
   * The number of milliseconds the virtual machine has been online for.

## Class Loading

This contains class loading statistics, for all counts the lower the number
the better. Higher numbers are indicative of more classes being loaded which
slows down initial execution time. Reducing the number of classes loaded will
reduce the time it takes for coldstarts to execute.

 * _CurrentLoadedClasses (classes)_
   * **LOWER IS BETTER**
   * The current number of loaded classes, the higher this number the more
     classes are currently loaded. Classes may require loading, initializing,
     and compilation which can increase cold start times. It is recommended to
     keep this value lower.
 * _TotalLoadedClasses (classes)_
   * **LOWER IS BETTER**
   * The number number of classes which were loaded in the virtual machine,
     unlike the current count this also includes classes which were unloaded. 
 * _TotalUnloadedClasses (classes)_
   * **LOWER IS BETTER**
   * The number of classes which have been unloaded since they were not
     required to be used at all.

## Garbage Collection

These represent garbage collection counts and may vary across virtual machines.
Generally for garbage collectors, they will require time to cleanup objects
and additionally this means that if the garbage collector is running that there
is not enough memory available, it likely has been exhausted.

Since there are various garbage collectors there are different groups of them:

 * _Copy_ `^`
   * **STOP THE WORLD**
   * Single threaded garbage collector which copies objects from the Eden to
     the survivor spaces.
 * _MarkSweepCompact_ `^`
   * **STOP THE WORLD**
   * Single threaded garbage collector which goes through all objects to find
     objects with no strong references to them.
 * _PS Scavenge_
   * **STOP THE WORLD**
   * Similar to _Copy_ except that multiple threads are used instead.
 * _PS MarkSweep_
   * **STOP THE WORLD**
   * Parallel mark and sweep which goes through all objects to find objects
     with no strong references to them.

`^` As of this writing Amazon uses the serial garbage collectors (_Copy_ and
_MarkSweepCompact_).

The following are statistics measured from garbage collectors:

 * _GC.?.Count (collections)_
   * **LOWER IS BETTER**
   * The number of times this garbage collector has been ran.
 * _GC.PS MarkSweep.Time (ms)_
   * **LOWER IS BETTER**
   * The amount of time the garbage collector spent cleaning up garbage

Regardless of which garbage collector is used, circular references between
objects should be avoided where possible. If a circular reference is to be
used then `Reference` should be used such as `WeakReference` (garbage collected
as soon as nothing points to it) or `SoftReference` (kept as a cache but is
garbage collected when not enough memory is available).

Other statistics which may be affected by garbage collection:

 * _PendingFinalizers (count)_
   * The number of objects which are waiting to have their finalizers called.

## Memory

Most of the memory statistics will use the following memory usage statistics,
which will be indicated by a caret `^`.

 * _?.init (byte)_
   * **GENERALLY HIGHER IS BETTER**
   * The number of bytes which were initially allocated for the given purpose.
   * This generally will start as a lower number.
   * This value might not be defined and may be any arbitrary value.
 * _?.used (byte)_
   * **GENERALLY LOWER IS BETTER**
   * This is the number of bytes which are currently being used.
 * _?.committed (byte)_
   * **GENERALLY HIGHER IS BETTER**
   * The number of bytes that are presently available to the virtual machine,
     this will be memory which has actually be allocated rather than reserved.
   * This usually will indicate the amount of memory the virtual machine has
     claimed from the operating system for its own use.
 * _?.max (byte)_
   * The maximum number of bytes which can be used by the virtual machine.
   * This value is not defined and may be any arbitrary value.
   * This may indicate the amount of memory which has been reserved.

There are two global memory spaces, heap and non-heap. These places are
generally where objects and other structures will be stored.

 * _Memory.Heap_ `^`
   * This is where all of the storage for objects and data exists within the
     virtual machine.
 * _Memory.NonHeap_ `^`
   * This is any other memory which is considered part of the heap, this can
     include space reserved for natively compiled classes and the stack.

Additionally beyond basic memory usage the virtual machine has a number of
memory pools which are used for given purposes. Most of the pools are dedicated
to the garbage collector and are used to track the amount of objects and
memory that is within them. The following information on pools are:

 * _Code Cache_
   * This contains the memory storage for the JIT compiler and native code.
 * _Metaspace_
   * This contains all of the metadata which is used for classes and their
     representation.
   * Generally the more classes that are loaded the larger the metaspace will
     be.
 * _Compressed Class Space_
   * This represents the compressed class space which is used to store
     representations of classes uses compacted structures and pointers.
 * _Eden Space_ or _PS Eden Space_
   * Most objects will be initially allocated with memory within this space.
   * This represents objects which have recently been created.
 * _Survivor Space_ or _PS Survivor Space_
   * Objects which have survived in the Eden space are placed in this space.
 * _Tenured Gen_ or _PS Old Gen_
   * Objects which have survived in the Survivor space are placed in this
     space.
   * This represents the longest lived objects.

Then each pool has individual statistics:

 * _MemPool.?.CollectionUsage_ `^`
   * This contains information on the number of bytes that the virtual machine
     has expended to perform garbage collection.
 * _MemPool.?.CollectionUsageThreshold (byte)_
   * This is optional and represents a limit before the hit count is increased.
 * _MemPool.?.CollectionUsageThresholdHit (count)_
   * This represents the number of times the usage has exceeded the threshold.
 * _MemPool.?.PeakUsage_ `^`
   * This represents the peak amount of memory that has been used by the given
     pool.
 * _MemPool.?.Usage_ `^`
   * The amount of memory that is being used by this given pool.
 * _MemPool.?.UsageThreshold (byte)_
   * This is optional and represents a limit before the hit count is increased.
 * _MemPool.?.UsageThresholdHit (count)_
   * This represents the number of times the usage has exceeded the threshold.

## Threads

This contains the information on the threads which were seen executing at the
time the snapshot was created. Note that most of the fields in the thread
information are optional and may not actually have any defined value.

 * _Thread.?.?.CPUTime (ns)_
   * **LOWER IS BETTER**
   * The amount of time the thread spent executing code on the CPU which may
     or may not additionally include kernel mode.
 * _Thread.?.?.UserTime (ns)_
   * **LOWER IS BETTER**
   * The amount of time the thread spent execution code in user mode.
 * _Thread.?.?.Blocked (count)_
   * **LOWER IS BETTER**
   * The number of times the thread was blocked waiting for a synchronization
     event to occur.
   * This is increased by performing `synchronized` and `Object.wait()`.
 * _Thread.?.?.BlockedTime (ns)_
   * **LOWER IS BETTER**
   * The amount of time the thread was blocked for.
   * This is increased by performing `synchronized` and `Object.wait()`.
 * _Thread.?.?.LockedName (object)_
   * This represents the class name and the hashcode of the object that the
     thread has performed `Object.wait()` on.
   * This will be `null` if the thread is not waiting for a notification.
 * _Thread.?.?.LockedOwner (threadid)_
   * This represents the ID number of the thread which currently has the
     lock on the object monitor this thread is waiting on.
   * This will be `-1` if the thread is not waiting for a notification.
 * _Thread.?.?.State (state)_
   * The current thread of the state, matches `Thread.State`.
 * _Thread.?.?.Waited (count)_
   * **LOWER IS BETTER**
   * The number of times the thread waiting on another object's monitor.
   * This is increased by performing `Object.wait()`.
 * _Thread.?.?.WaitedTime (ns)_
   * **LOWER IS BETTER**
   * The amount of time the thread spent waiting for a notification on the
     object monitor.
   * This is increased by performing `Object.wait()`.

## Buffer Pools

These are pools which are used as buffers related to file input and output.

The following pools exist:

 * _direct_
   * Represents the number of direct buffers that are used.
   * Direct buffers are those created by `ByteBuffer.allocateDirect()` and
     similar related classes.
 * _mapped_
   * Represents memory mapped files.

Each pool has the specified statistics:

 * _BufferPool.?.Count (objects)_
   * The number of objects which are currently in the given pool.
 * _BufferPool.?.Used (byte)_
   * The approximate number of bytes which is used to store data in the pool.
 * _BufferPool.?.Capacity (byte)_
   * The approximate maximum capacity in bytes that the pool may contain.

