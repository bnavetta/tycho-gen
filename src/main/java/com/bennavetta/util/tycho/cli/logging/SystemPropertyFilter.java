package com.bennavetta.util.tycho.cli.logging;

import org.apache.logging.log4j.Level;
import org.apache.logging.log4j.Logger;
import org.apache.logging.log4j.Marker;
import org.apache.logging.log4j.core.LogEvent;
import org.apache.logging.log4j.core.config.plugins.Plugin;
import org.apache.logging.log4j.core.config.plugins.PluginAttr;
import org.apache.logging.log4j.core.config.plugins.PluginFactory;
import org.apache.logging.log4j.core.filter.AbstractFilter;
import org.apache.logging.log4j.message.Message;

@Plugin(name="SystemPropertyFilter", type="Core", elementType="filter", printObject=true)
public class SystemPropertyFilter extends AbstractFilter
{
	private String property;

	private SystemPropertyFilter(String property, Result onMatch, Result onMismatch)
	{
		super(onMatch, onMismatch);
		this.property = property;
	}

	public Result filter(Logger logger, Level level, Marker marker, String msg, Object[] params)
	{
		return filter(level);
	}

	public Result filter(Logger logger, Level level, Marker marker, Object msg, Throwable t)
	{
		return filter(level);
	}

	public Result filter(Logger logger, Level level, Marker marker, Message msg, Throwable t)
	{
		return filter(level);
	}

	@Override
	public Result filter(LogEvent event)
	{
		return filter(event.getLevel());
	}

	private Result filter(Level level)
	{
		return level.isAtLeastAsSpecificAs(Level.toLevel(System.getProperty(property), Level.INFO)) ? onMatch : onMismatch;
	}

	@Override
	public String toString()
	{
		return property + " (" + System.getProperty(property) + ")";
	}

	/**
	 * Create a ThresholdFilter.
	 * @param loggerLevel The log Level.
	 * @param match The action to take on a match.
	 * @param mismatch The action to take on a mismatch.
	 * @return The created ThresholdFilter.
	 */
	@PluginFactory
	public static SystemPropertyFilter createFilter(@PluginAttr("property") String levelProperty, @PluginAttr("onMatch") String match, @PluginAttr("onMismatch") String mismatch)
	{
		Result onMatch = match == null ? Result.NEUTRAL : Result.valueOf(match.toUpperCase());
		Result onMismatch = mismatch == null ? Result.DENY : Result.valueOf(mismatch.toUpperCase());

		return new SystemPropertyFilter(levelProperty, onMatch, onMismatch);
	}
}
