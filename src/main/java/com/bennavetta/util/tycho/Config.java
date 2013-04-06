package com.bennavetta.util.tycho;

import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.Properties;

import com.google.common.base.Charsets;

public class Config
{	
	private static Properties props;
	
	public static final String VERSION = "version";
	public static final String AETHER_VERSION = "aetherVersion";
	public static final String MAVEN_VERSION = "mavenVersion";
	public static final String GRADLE_VERSION = "gradleVersion";
	public static final String BUILD_TIMESTAMP = "buildTimestamp";
	
	public static Properties getProperties()
	{
		if(props == null)
		{
			props = new Properties();
			try(InputStream in = Config.class.getResourceAsStream("/tycho-gen.properties"))
			{
				props.load(new InputStreamReader(in, Charsets.UTF_8));
			}
			catch (IOException e)
			{
				System.err.println("Error loading build information: " + e.getLocalizedMessage());
			}
		}
		return props;
	}
	
	public static String get(String key)
	{
		return getProperties().getProperty(key);
	}
}
