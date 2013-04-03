package com.bennavetta.util.tycho.impl;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Enumeration;
import java.util.List;
import java.util.jar.JarEntry;
import java.util.jar.JarFile;

import org.sonatype.aether.artifact.Artifact;

import com.bennavetta.util.tycho.BundleGenerator;
import com.google.common.base.CharMatcher;
import com.google.common.base.Splitter;
import com.google.common.collect.Iterables;
import com.google.common.collect.Ordering;
import com.google.common.primitives.Ints;

/**
 * Default bundle metadata generator
 * @author ben
 * @see <a href="http://fusesource.com/docs/esb/4.4.1/esb_deploy_osgi/ESBMavenOSGiConfig.html">FuseSource Maven OSGi Config</a>
 */
public class DefaultBundleGenerator implements BundleGenerator
{
	private static final Ordering<String> PATH_COMPONENTS = new Ordering<String>() {
		@Override
		public int compare(String a, String b)
		{
			return Ints.compare(
				Iterables.size(Splitter.on('/').split(a)),
				Iterables.size(Splitter.on('/').split(b)));
		}
	};
	
	private static final CharMatcher PUNCTUATION = CharMatcher.ASCII
			.and(CharMatcher.is('-').or(CharMatcher.is('.'))); // what we are going to get in an artifact id
	
	@Override
	public String getSymbolicName(Artifact artifact)
	{
		if(artifact.getGroupId().indexOf('.') == -1)
		{
			// Find the first package with classes in it
			try(JarFile jar = new JarFile(artifact.getFile())) // commons-logging:commons-logging -> org.apache.commons.logging
			{
				List<String> contents = new ArrayList<>();
				Enumeration<JarEntry> entries = jar.entries();
				while(entries.hasMoreElements())
				{
					JarEntry entry = entries.nextElement();
					contents.add(entry.getName());
				}
				// sort by number of slashes
				Collections.sort(contents, PATH_COMPONENTS);
				for(String path : contents)
				{
					if(path.endsWith(".class"))
					{
						path = path.substring(0, path.lastIndexOf('/')).replace('/', '.');
						if(path.startsWith("/"))
						{
							path = path.substring(1);
						}
						return path;
					}
				}
			}
			catch (IOException e)
			{
				return null;
			}
		}
		else if(Iterables.getLast(Splitter.on('.').split(artifact.getGroupId())).equals(artifact.getArtifactId()))
		{
			return artifact.getGroupId(); // org.apache.maven:maven -> org.apache.maven
		}
		else
		{
			String gidEnd = Iterables.getLast(Splitter.on('.').split(artifact.getGroupId()));
			if(artifact.getArtifactId().startsWith(gidEnd))
			{
				// org.apache.maven:maven-core -> org.apache.maven.core
				return artifact.getGroupId() + "." + PUNCTUATION.trimFrom(artifact.getArtifactId().substring(gidEnd.length()));
			}
			else
			{
				return artifact.getGroupId() + "." + artifact.getArtifactId(); // groupId + "." + artifactId
			}
		}
		return null;
	}

	@Override
	public String getVersion(Artifact artifact)
	{
		return artifact.getVersion().replace('-', '.');
	}

	@Override
	public String getBundleName(Artifact artifact)
	{
		return artifact.getGroupId() + " " +  artifact.getArtifactId(); // don't have access to the name
	}

}
