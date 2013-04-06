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
