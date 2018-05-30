# Profiler

The IOpipe profiler generates snapshots which can be opened and parsed with
[VisualVM](https://visualvm.github.io). VisualVM has downloadable executables
for Windows and Mac, and may also be installed on Debian and Ubuntu based
systems by running `apt-get install visualvm`.

There is currently one mode of operation, which is a sampling-only profiler
which inspects the state of all threads to determine how long they have been
running for. This mode does not require any code to be modified to support profiling
so all code run by your Lambda functions will be profiled automatically, as
long as the profiler is enabled.

## Enabling Profiling

Set the environment variable IOPIPE_PROFILER_ENABLED to `true`

### For Serverless Framework:

In the `serverless.yml` file, under `functions:`, `(your_function_name_here):`, `environment:`, set the environment variable `IOPIPE_PROFILER_ENABLED` to `true`

## Customizing Profiling

The profiler may be customized by setting the specified properties and/or
environment variables:

* Sample Rate: The duration of time between each sample, in microseconds
  * The default is 1,000 microseconds (1ms).
  * `IOPIPE_PROFILER_SAMPLERATE` as an environment variable

## Getting Profiling Data

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

# Statistics Data Meanings

This section documents the meanings of the statistics data.

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
Since there are various garbage collectors there are different groups of them.

 * _Copy_ ^
   * **STOP THE WORLD**
   * Single threaded garbage collector which copies objects from the Eden to
     the survivor spaces.
 * _MarkSweepCompact_ ^
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

 * _GC.?.Count (collections)_
   * **LOWER IS BETTER**
   * The number of times this garbage collector has been ran.
 * _GC.PS MarkSweep.Time (ms)_
   * **LOWER IS BETTER**
   * The amount of time the garbage collector spent cleaning up garbage

^ As of this writing Amazon uses the serial garbage collectors (_Copy_ and
_MarkSweepCompact_).

Regardless of which garbage collector is used, circular references between
objects should be avoided where possible. If a circular reference is to be
used then `Reference` should be used such as `WeakReference` (garbage collected
as soon as nothing points to it) or `SoftReference` (kept as a cache but is
garbage collected when not enough memory is available).

