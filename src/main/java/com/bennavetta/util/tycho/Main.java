/**
 * Copyright 2013 Ben Navetta <ben.navetta@gmail.com>
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 * 	http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package com.bennavetta.util.tycho;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStreamWriter;
import java.io.Writer;
import java.text.SimpleDateFormat;
import java.util.Date;

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
	
	@Argument(metaVar="config-file", usage="The XML configuration describing the dependencies to wrap", multiValued=false)
	private File configFile;
	
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
				String version = Config.get(Config.VERSION);
				String aetherVersion = Config.get(Config.AETHER_VERSION);
				String mavenVersion = Config.get(Config.MAVEN_VERSION);
				String gradleVersion = Config.get(Config.GRADLE_VERSION);
				Date built = new Date(Long.parseLong(Config.get(Config.BUILD_TIMESTAMP)));
				
				System.out.println("Tycho Generator version: " + version);
				System.out.println("Sonatype Aether version: " + aetherVersion);
				System.out.println("Apache Maven version: " + mavenVersion);
				String builtDate = new SimpleDateFormat("dd MMMM yyyy").format(built);
				String builtTime = new SimpleDateFormat("hh:mm:ss.SSS aa").format(built);
				System.out.println("Built by Gradle " + gradleVersion + " on " + builtDate + " at " + builtTime);
			}
			else if(configFile == null)
			{
				throw new CmdLineException(parser, "No config file given");
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
			String invocation = "tycho-gen-" + Config.get(Config.VERSION) + "-dist.jar";
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
