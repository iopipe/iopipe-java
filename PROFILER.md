# Profiler

The profiler generates snapshots which can be opened and parsed with
VisualVM <https://visualvm.github.io/>. VisualVM may also be installed on
Debian and Ubuntu based systems by running `apt-get install visualvm`.

There is currently one mode of operation which is a sampling only profiler
which inspects the state of all threads to determine how long they have been
running for. This does not require any code to be modified to support profiling
so that any code which runs will be profiled automatically.

## Enabling Profiling

Set the system property `com.iopipe.plugin.profiler` to `true`

### For Serverless Framework:

In the `serverless.yml` file, under `functions:`, `(your_function_name_here):`, `environment:`, set the environwental variable `IOPIPE_PROFILER_ENABLE` to `true`

The profiler may be customized by setting the specified properties and/or
environment variables:

 * The duration of time between each sample, in microseconds:
   * The default is 1,000 microseconds (1ms).
   * `IOPIPE_PROFILER_SAMPLERATE` in the environmental varibles of the `serverless.yml` file, or the environment variable `com.iopipe.plugin.profiler.samplerate`.

When the profiler is enabled, .zip files containing the profiling data may be downloaded from the individual
invocation information. Unzip it, then load them in VisualVM as a profile snapshot.

# How To Use Sampling Only Profiler Data

In this mode of the profiler only methods which are currently executed will
have execution time added to them when the sampler sees that method
executing when it is at the top of the stack. As such in VisualVM the best
metric to use to determine how long a method takes would be the self time and
the self time percentage field. The self time in seconds will generally be very
close to multiples of the sampling frequency and reflects the amount of time
the sampler has seen the method for. The percentage indicates how much time
compared to all other methods that the sampler has seen the method executing.

Methods which are very fast and run quicker than the sampling rate may or may
not appear in the sampled data. If these methods are rapidly executed
then they may be seen more often. However, the virtual machine may opt to
inline these methods in which case they may never appear to be executed.

The invocation count in this mode is always `1` because it is unknown how many
times a method has actually executed in a thread. The self time reflects the
time the sampler has seen the method.

