package com.bennavetta.util.tycho;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStreamWriter;
import java.io.Writer;
import java.net.JarURLConnection;
import java.net.URL;
import java.net.URLConnection;
import java.util.jar.Manifest;

import org.apache.logging.log4j.core.config.plugins.PluginManager;
import org.apache.maven.model.Model;
import org.apache.maven.model.io.xpp3.MavenXpp3Writer;
import org.jdom2.Document;
import org.jdom2.input.SAXBuilder;
import org.kohsuke.args4j.Argument;
import org.kohsuke.args4j.CmdLineException;
import org.kohsuke.args4j.CmdLineParser;
import org.kohsuke.args4j.ExampleMode;
import org.kohsuke.args4j.Option;

import com.bennavetta.util.tycho.cli.XmlWrapDescriptorParser;
import com.bennavetta.util.tycho.impl.DefaultWrapperGenerator;
import com.google.common.base.Charsets;

public class Main
{	
	static {
		PluginManager.addPackage("com.bennavetta.util.tycho.cli.logging"); // a bit of a hack, but none of the other methods seen to be working
	}
	
	public static final String LOGGING_PROP = "tycho.gen.logging.level";
	
	@Option(name="--log", aliases="-l", required=false, metaVar="<level>", usage="Log level to use. One of: off, fatal, error, warn, info, debug, trace, all (case-insensitive)")
	private String logLevel;
	
	@Option(name="-v", aliases="--version", required=false, usage="Display version information")
	private boolean version;
	
	@Argument(required=true, metaVar="config-file", usage="The XML configuration describing the dependencies to wrap", multiValued=false)
	private File configFile;
	
	private Manifest manifest; // store it so we don't have to parse it each time
	
	private Manifest getManifest() throws IOException
	{
		if(manifest == null)
		{
			//try(InputStream in = getClass().getResourceAsStream("/META-INF/MANIFEST.MF"))
			//{
			//	manifest = new Manifest();
			//	manifest.read(in);
			//}
			URL resource = Main.class.getResource(Main.class.getSimpleName() + ".class");
			URLConnection conn = resource.openConnection();
			if(conn instanceof JarURLConnection)
			{
				manifest = ((JarURLConnection) conn).getManifest();
			}
			else
			{
				try(InputStream in = getClass().getResourceAsStream("/META-INF/MANIFEST.MF"))
				{
					manifest = new Manifest();
					manifest.read(in);
				}
			}
		}
		return manifest;
	}
	
	private int run(String[] args) throws Exception
	{
		CmdLineParser parser = new CmdLineParser(this);
		try
		{
			parser.parseArgument(args);
			
			if(logLevel != null)
			{
				System.setProperty(LOGGING_PROP, logLevel.toUpperCase());
			}
			
			if(version)
			{
				System.out.println("Tycho-Gen version " + getManifest().getMainAttributes().getValue("Implementation-Version"));
			}
			else
			{
				SAXBuilder builder = new SAXBuilder();
				Document doc = builder.build(configFile);
				WrapRequest request = new XmlWrapDescriptorParser().createRequest(doc);
				
				WrapperGenerator generator = new DefaultWrapperGenerator();
				generator.generate(request);
				
				writePom(request.getParent());
			}
			
			return 0;
		}
		catch(CmdLineException e)
		{	
			String invocation = getManifest().getMainAttributes().getValue("Dist-Jar");
			// tell the user what was wrong with the arguments
			System.err.println(e.getMessage());
			
			// show usage information
			System.err.println("Usage: java -jar " + invocation + " [options] config-file");
			parser.printUsage(System.err);
			
			// show an example
			System.err.println();
			System.err.println("    Example: java -jar " + invocation + parser.printExample(ExampleMode.REQUIRED) + " myconfig.xml");
			return 1;
		}
	}
	
	private void writePom(Model pom) throws IOException
	{
		MavenXpp3Writer writer = new MavenXpp3Writer();
		Writer out = null;
		try
		{
			out = new OutputStreamWriter(new FileOutputStream(pom.getPomFile()), Charsets.UTF_8);
			writer.write(out, pom);
		}
		finally
		{
			if(out != null)
				out.close();
		}
	}

	public static void main(String[] args)
	{
		try
		{
			int rc = new Main().run(args);
			System.exit(rc);
		}
		catch(Exception e)
		{
			//System.err.println("Error: " + e.getLocalizedMessage());
			e.printStackTrace();
			System.exit(1);
		}
	}
}
