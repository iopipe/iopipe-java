package com.iopipe;

import com.amazonaws.services.lambda.runtime.Context;
import com.iopipe.plugin.IOpipePluginExecution;
import com.iopipe.plugin.NoSuchPluginException;
import java.util.function.BiFunction;
import java.util.function.Consumer;
import java.util.function.Function;

/**
 * This class provides access to information and functionality which is
 * specific to a single execution of a method.
 *
 * Each execution will have a unique instance of this object and as such will
 * be initialized when it is first used.
 *
 * The {@link com.amazonaws.services.lambda.runtime.Context} object can be
 * obtained by invoking the {@link #context()} method.
 *
 * @since 2018/01/19
 */
public abstract class IOpipeExecution
{
	/** The measurement. */
	protected final IOpipeMeasurement measurement;
	
	/**
	 * Initializes the base execution.
	 *
	 * @param __m The measurement to use.
	 * @throws NullPointerException On null arguments.
	 * @since 2018/08/27
	 */
	IOpipeExecution(IOpipeMeasurement __m)
		throws NullPointerException
	{
		if (__m == null)
			throw new NullPointerException();
		
		this.measurement = __m;
	}
	
	/**
	 * Returns the configuration used to initialize the service.
	 *
	 * @return The service configuration.
	 * @since 2018/01/19
	 */
	public abstract IOpipeConfiguration config();
	
	/**
	 * Returns the context for the Amazon Web Service Lambda execution that
	 * is currently running. If it is not known or is valid then a placeholder
	 * context will be returned.
	 *
	 * @return The AWS context.
	 * @since 2018/01/19
	 */
	public abstract Context context();
	
	/**
	 * Returns the object which was used as input for the method being
	 * executed, {@code null} will be returned if it was not passed or is not
	 * known.
	 *
	 * @return The extra object which was passed to the run method or
	 * {@code null} if it was not passed or is not known.
	 * @since 2018/04/16
	 */
	public abstract Object input();
	
	/**
	 * This returns an instance of a plugin based on the class type of its
	 * interface.
	 *
	 * @param <C> The class type of the execution state.
	 * @param __cl The class object of the execution state.
	 * @return The instance of the plugin's execution state.
	 * @throws ClassCastException If the class type is not valid.
	 * @throws NoSuchPluginException If the plugin does not exist.
	 * @throws NullPointerException On null arguments.
	 * @since 2018/01/20
	 */
	public abstract <C extends IOpipePluginExecution> C plugin(Class<C> __cl)
		throws ClassCastException, NoSuchPluginException, NullPointerException;
	
	/**
	 * Returns the service which ran this execution.
	 *
	 * @return The service which ran this execution.
	 * @since 2018/01/19
	 */
	public abstract IOpipeService service();
	
	/**
	 * Returns the starting time of the execution on the wall clock.
	 *
	 * @return The starting time in milliseconds.
	 * @since 2018/02/16
	 */
	public abstract long startTimestamp();
	
	/**
	 * Adds the specified custom metric with a string value.
	 *
	 * Custom metric names are limited to the length specified in
	 * {@link IOpipeConstants#NAME_CODEPOINT_LIMIT}.
	 *
	 * @param __name The metric name.
	 * @param __sv The string value.
	 * @throws NullPointerException On null arguments.
	 * @since 2018/01/30
	 */
	public final void customMetric(String __name, String __sv)
		throws NullPointerException
	{
		if (__name == null || __sv == null)
			throw new NullPointerException();
		
		this.measurement.customMetric(__name, __sv);
	}
	
	/**
	 * Adds the specified custom metric with a long value.
	 *
	 * Custom metric names are limited to the length specified in
	 * {@link IOpipeConstants#NAME_CODEPOINT_LIMIT}.
	 *
	 * @param __name The metric name.
	 * @param __lv The long value.
	 * @throws NullPointerException On null arguments.
	 * @since 2018/01/30
	 */
	public final void customMetric(String __name, long __lv)
		throws NullPointerException
	{
		if (__name == null)
			throw new NullPointerException();
		
		this.measurement.customMetric(__name, __lv);
	}
	
	/**
	 * Returns the object which was used as input for the method being
	 * executed.
	 *
	 * @param <T> The type of object to return.
	 * @param __cl The type of object to return.
	 * @return The extra object which was passed to the run method.
	 * @throws ClassCastException If it is not of the passed class type.
	 * @throws NullPointerException On null arguments.
	 * @since 2018/04/16
	 */
	public final <T> T input(Class<T> __cl)
		throws ClassCastException, NullPointerException
	{
		if (__cl == null)
			throw new NullPointerException();
		
		return __cl.cast(this.input());
	}
	
	/*
	 * Adds a single label which will be passed in the report.
	 *
	 * Labels are limited to the length specified in
	 * {@link IOpipeConstants#NAME_CODEPOINT_LIMIT}.
	 *
	 * @param __s The label to add.
	 * @throws NullPointerException On null arguments.
	 * @since 2018/04/11
	 */
	public final void label(String __s)
		throws NullPointerException
	{
		if (__s == null)
			throw new NullPointerException();
		
		this.measurement.addLabel(__s);
	}
	
	/**
	 * Returns the measurement recorder.
	 *
	 * @return The measurement recorder.
	 * @since 2018/01/19
	 */
	public final IOpipeMeasurement measurement()
	{
		return this.measurement;
	}
	
	/**
	 * This executes the specified method if the plugin exists, if it does
	 * not exist then it will not be executed.
	 *
	 * @deprecated This method is deprecated and should not be used in code
	 * because if a plugin is not available then the specified code will not
	 * be executed. This is error prone and may lead to code paths being
	 * different depending on the state of plugins.
	 * @param <C> The class type of the execution state.
	 * @param __cl The class object of the execution state.
	 * @param __func The function to excute if the plugin exists and is valid.
	 * @throws ClassCastException If the class type is not valid.
	 * @throws NullPointerException On null arguments.
	 * @since 2018/01/23
	 */
	@Deprecated
	public final <C extends IOpipePluginExecution> void plugin(Class<C> __cl,
		Consumer<C> __func)
		throws ClassCastException, NullPointerException
	{
		if (__cl == null || __func == null)
			throw new NullPointerException();
		
		try
		{
			__func.accept(this.plugin(__cl));
		}
		catch (NoSuchPluginException e)
		{
		}
	}
	
	/**
	 * This searches for the specified plugin if the plugin exists it will
	 * return an instance of {@link AutoCloseable} which may be used with
	 * try-with-resources.
	 *
	 * @deprecated This method is deprecated and should not be used in code
	 * because if a plugin is not available then the specified code will not
	 * be executed. This is error prone and may lead to code paths being
	 * different depending on the state of plugins.
	 * @param <C> The class type of the execution state.
	 * @param <R> The type of object to return.
	 * @param __cl The class object of the execution state.
	 * @param __func The function to obtain the {@link AutoCloseable} for use
	 * with try-with-resources for.
	 * @return The {@code A} object or {@code null} if the plugin is not valid
	 * or no value was returned.
	 * @throws ClassCastException If the class type is not valid.
	 * @throws NullPointerException On null arguments.
	 * @since 2018/01/23
	 */
	@Deprecated
	public final <C extends IOpipePluginExecution, R extends AutoCloseable>
		R plugin(Class<C> __cl, Function<C, R> __func)
		throws ClassCastException, NullPointerException
	{
		if (__cl == null || __func == null)
			throw new NullPointerException();
		
		try
		{
			return __func.apply(this.plugin(__cl));
		}
		catch (NoSuchPluginException e)
		{
			return null;
		}
	}
	
	/**
	 * This searches for the specified plugin if the plugin exists it will
	 * return an instance of {@link AutoCloseable} which may be used with
	 * try-with-resources. An optional secondary argument may be passed to
	 * simplify some operations that take an extra parameter.
	 *
	 * @deprecated This method is deprecated and should not be used in code
	 * because if a plugin is not available then the specified code will not
	 * be executed. This is error prone and may lead to code paths being
	 * different depending on the state of plugins.
	 * @param <C> The class type of the execution state.
	 * @param <R> The type of object to return.
	 * @param <V> The type of extra value to pass to the function.
	 * @param __cl The class object of the execution state.
	 * @param __func The function to obtain the {@link AutoCloseable} for use
	 * with try-with-resources for.
	 * @param __v The extra value to be passed to the function.
	 * @return The {@code A} object or {@code null} if the plugin is not valid
	 * or no value was returned.
	 * @throws ClassCastException If the class type is not valid.
	 * @throws NullPointerException On null arguments except for {@code __v}.
	 * @since 2018/01/23
	 */
	@Deprecated
	public final <C extends IOpipePluginExecution, R extends AutoCloseable,
		V> R plugin(Class<C> __cl, BiFunction<C, V, R> __func, V __v)
		throws ClassCastException, NullPointerException
	{
		if (__cl == null || __func == null)
			throw new NullPointerException();
		
		try
		{
			return __func.apply(this.plugin(__cl), __v);
		}
		catch (NoSuchPluginException e)
		{
			return null;
		}
	}
	
	/**
	 * This returns an instance of a plugin based on the class type of its
	 * interface, if the plugin does not exist then {@code null} is returned.
	 *
	 * @param <C> The class type of the execution state.
	 * @param __cl The class object of the execution state.
	 * @return The instance of the plugin's execution state or {@code null}
	 * if no such plugin exists.
	 * @throws ClassCastException If the class type is not valid.
	 * @throws NullPointerException On null arguments.
	 * @since 2018/01/23
	 */
	public final <C extends IOpipePluginExecution> C optionalPlugin(
		Class<C> __cl)
		throws ClassCastException, NullPointerException
	{
		try
		{
			return this.plugin(__cl);
		}
		catch (NoSuchPluginException e)
		{
			return null;
		}
	}
	
	/**
	 * Returns the thread group which this execution is running under.
	 *
	 * @return The thread group of this execution, may return .
	 * @since 2018/02/09
	 */
	public final ThreadGroup threadGroup()
	{
		return Thread.currentThread().getThreadGroup();
	}
	
	/**
	 * Returns the current execution for the given thread.
	 *
	 * @return The execution context which is associated with this thread, if
	 * there is no valid execution context then one that does nothing will be
	 * created.
	 * @since 2018/07/30
	 */
	public static final IOpipeExecution currentExecution()
	{
		IOpipeExecution rv = IOpipeService.__execution();
		if (rv == null)
			return new __NoOpExecution__();
		return rv;
	}
}

