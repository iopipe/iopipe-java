# Profiler

The profiler generates snapshots which can be opened and parsed with
VisualVM <https://visualvm.github.io/>. VisualVM may also be installed on
Debian and Ubuntu based systems by running `apt-get install visualvm`.

There is currently one mode of operation which is a sampling profiler which
inspects the state of all threads to determine how long they have been running
for. This does not require any code to be modified to support profiling so that
any code which runs will be profiled automatically.

The profiler can be enabled by setting the following:

 * `com.iopipe.plugin.profiler` to `true`
 * `IOPIPE_PROFILER_ENABLE` to `true`

The profiler may be customed by setting the specified properties and/or
environment variables:

 * The duration of time between each sample, in microseconds:
   * The default is 50,000 microseconds.
   * `com.iopipe.plugin.profiler.samplerate`
   * `IOPIPE_PROFILER_SAMPLERATE`

