package com.bennavetta.util.tycho;

import org.apache.commons.cli.BasicParser;
import org.apache.commons.cli.CommandLine;
import org.apache.commons.cli.CommandLineParser;
import org.apache.commons.cli.HelpFormatter;
import org.apache.commons.cli.OptionBuilder;
import org.apache.commons.cli.Options;
import org.apache.commons.cli.ParseException;
import org.apache.logging.log4j.Level;
import org.apache.logging.log4j.core.config.plugins.PluginManager;

public class Main
{
	static {
		PluginManager.addPackage("com.bennavetta.util.tycho.cli.logging"); // a bit of a hack, but none of the other methods seen to be working
	}
	
	public static final String LOGGING_PROP = "tycho.gen.logging.level";
	
	public static void main(String[] args)
	{
		Options options = createOptions();
		CommandLineParser parser = new BasicParser();
		try
		{
			CommandLine cmd = parser.parse(options, args);
			
			// Configure logging
			if(cmd.hasOption("quiet"))
			{
				System.setProperty(LOGGING_PROP, Level.WARN.toString());
			}
			else if(cmd.hasOption("verbose"))
			{
				System.setProperty(LOGGING_PROP, Level.DEBUG.toString());
			}
			else
			{
				System.setProperty(LOGGING_PROP, Level.INFO.toString());
			}
			
			if(cmd.hasOption("version"))
			{
				System.out.println("Tycho Generator version: 1.0");
				System.out.println("Java version: " + System.getProperty("java.version"));
				System.exit(0);
			}
			
		}
		catch (ParseException e)
		{
			printHelp(options);
		}
		
	}

	private static void printHelp(Options options)
	{
		HelpFormatter formatter = new HelpFormatter();
		formatter.printHelp(Main.class.getName(), options);
	}

	private static Options createOptions()
	{
		Options opts = new Options();
		opts.addOption("f", "file", true, "wrapper configuration file");
		opts.addOption(OptionBuilder.withLongOpt("file")
			.withDescription("wrapper configuration file")
			.hasArg()
			.withArgName("file")
			.isRequired()
			.create('f'));
		opts.addOption("v", "version", false, "print version information");
		opts.addOption("V", "verbose", false, "enable verbose output");
		opts.addOption("q", "quiet", false, "limit output");
		return opts;
	}
}
