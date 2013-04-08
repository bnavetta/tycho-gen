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
package com.bennavetta.util.tycho.impl;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.util.Date;
import java.util.Enumeration;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Properties;
import java.util.Set;
import java.util.jar.Attributes;
import java.util.jar.JarEntry;
import java.util.jar.JarFile;
import java.util.jar.Manifest;

import org.apache.maven.model.Model;
import org.apache.maven.model.Parent;
import org.apache.maven.model.Repository;
import org.apache.maven.model.building.ModelBuildingException;
import org.apache.maven.model.io.xpp3.MavenXpp3Writer;
import org.codehaus.plexus.component.repository.exception.ComponentLookupException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.sonatype.aether.RepositorySystem;
import org.sonatype.aether.RepositorySystemSession;
import org.sonatype.aether.artifact.Artifact;
import org.sonatype.aether.collection.CollectRequest;
import org.sonatype.aether.graph.Dependency;
import org.sonatype.aether.graph.DependencyFilter;
import org.sonatype.aether.graph.DependencyNode;
import org.sonatype.aether.repository.RemoteRepository;
import org.sonatype.aether.resolution.DependencyRequest;
import org.sonatype.aether.resolution.DependencyResolutionException;
import org.sonatype.aether.util.artifact.JavaScopes;
import org.sonatype.aether.util.filter.DependencyFilterUtils;
import org.sonatype.aether.util.graph.PreorderNodeListGenerator;

import aQute.lib.osgi.Analyzer;

import com.bennavetta.util.tycho.BundleGenerator;
import com.bennavetta.util.tycho.WrapException;
import com.bennavetta.util.tycho.WrapRequest;
import com.bennavetta.util.tycho.WrapperGenerator;
import com.bennavetta.util.tycho.maven.Maven;
import com.google.common.base.Charsets;
import com.google.common.base.Joiner;
import com.google.common.base.Splitter;
import com.google.common.collect.Iterables;
import com.google.common.io.ByteStreams;
import com.google.common.io.Files;

public class DefaultWrapperGenerator implements WrapperGenerator
{
	private BundleGenerator metadata = new DefaultBundleGenerator();
	private RepositorySystem repoSystem = Maven.repositorySystem();
	private Logger log = LoggerFactory.getLogger(getClass());
	
	@Override
	public void generate(WrapRequest request) throws WrapException
	{	
		RepositorySystemSession session = Maven.repositorySystemSession(repoSystem);
		
		CollectRequest collect = new CollectRequest();
		collect.addRepository(Maven.central());
		for(Artifact artifact : request.getArtifacts())
		{
			collect.addDependency(new Dependency(artifact, JavaScopes.COMPILE));
		}
		
		// Add explicitly provided repositories
		if(request.getRepositories() != null)
		{
			for(Repository repo : request.getRepositories())
			{
				collect.addRepository(remote(repo));
			}
		}
		try
		{
			DependencyFilter classpathFilter = DependencyFilterUtils.classpathFilter( JavaScopes.COMPILE );
			DependencyRequest dependencyRequest = new DependencyRequest(collect, classpathFilter);
			DependencyNode root = repoSystem.resolveDependencies(session, dependencyRequest).getRoot(); // download the dependencies so they can be processed
			
			// Figure out which version of each dependency to use
			VersionResolver resolver = new VersionResolver();
			resolver.resolve(root);
			
			// Flatten the node graph
			PreorderNodeListGenerator listGenerator = new PreorderNodeListGenerator();
			root.accept(listGenerator);
			for(DependencyNode node : listGenerator.getNodes())
			{
				if(node.getVersion().equals(resolver.getVersion(node)))
				{
					createWrapper(request, session, node);
				}
			}
			
			//createWrapper(request, session, node);
		}
		catch (DependencyResolutionException e)
		{
			throw new WrapException("Error collecting dependencies", e, request);
		}
	}
	
	private void createWrapper(WrapRequest request, RepositorySystemSession session, DependencyNode node) throws WrapException
	{
		Artifact artifact = node.getDependency().getArtifact();
		Map<String, Artifact> deps = new HashMap<>(node.getChildren().size());
		for(DependencyNode dependency : node.getChildren())
		{
			//deps.add(metadata.getSymbolicName(dependency.getDependency().getArtifact()));
			deps.put(metadata.getSymbolicName(dependency.getDependency().getArtifact()), dependency.getDependency().getArtifact());
		}
		
		if(artifact.getFile() == null)
		{
			log.warn("Skipping {} - no jar file (is it a runtime dependency?)", artifact);
			return;
		}
		
		try
		{
			log.info("Wrapping {}", artifact);
			String symbolicName = metadata.getSymbolicName(artifact);
			String bundleName = metadata.getBundleName(artifact);
			if(!request.getParent().getModules().contains(symbolicName)) // check first in case the wrapper is being regenerated/updated
				request.getParent().addModule(symbolicName);
			//TODO: instead of editing the model, build a regular XML DOM to keep it from having everything added
			
			Model pom = new Model();
			pom.setParent(asParent(request.getParent()));
			pom.setModelVersion("4.0.0");
			pom.setArtifactId(symbolicName);
			pom.setPackaging("eclipse-plugin");
			pom.setName(bundleName);
			pom.addProperty("project.build.sourceEncoding", "UTF-8");
			
			//pom.setVersion(artifact.getBaseVersion());
			
			// Add the wrapper modules of the project's dependencies so Tycho makes them available during compilation
			for(Map.Entry<String, Artifact> dependency : deps.entrySet())
			{
				org.apache.maven.model.Dependency modelDep = new org.apache.maven.model.Dependency();
				modelDep.setGroupId(request.getParent().getGroupId());
				modelDep.setArtifactId(dependency.getKey());
				//modelDep.setVersion(request.getParent().getVersion());
				modelDep.setVersion(dependency.getValue().getBaseVersion());
				pom.addDependency(modelDep);
			}
			log.debug("POM: {}", pom);
			
			// Assume tycho is applied by a parent project. Otherwise, things get really complicated
			// Also assume that pomDependencies is set to "consider"
			
			// Create project directory
			File projectDir = new File(request.getParent().getProjectDirectory(), symbolicName);
			log.debug("Project dir: {}", projectDir);
			pom.setPomFile(new File(projectDir, "pom.xml"));
			if(projectDir.exists())
			{
				Utils.delete(projectDir.toPath());
			}
			if(!projectDir.mkdir()) // parent should exist
				throw new WrapException("Unable to create project directory", request);
			
			pom.getParent().setRelativePath(null); // uses absolute paths
			// Write out the wrapper pom
			try(OutputStream pomOut = new FileOutputStream(pom.getPomFile()))
			{
				MavenXpp3Writer pomWriter = new MavenXpp3Writer();
				pomWriter.write(new OutputStreamWriter(pomOut, Charsets.UTF_8), pom);
			}
			
			// Generate the manifest
			try(Analyzer analyzer = new Analyzer())
			{
				analyzer.setJar(artifact.getFile());
				analyzer.setProperty("Bundle-SymbolicName", symbolicName);
				//analyzer.setProperty("Bundle-Version", metadata.getVersion(artifact));
				//analyzer.setProperty("Bundle-Version", request.getParent().getVersion());
				analyzer.setProperty("Bundle-Version", metadata.getVersion(request.getParent().getVersion()));
				analyzer.setProperty("Bundle-Name", bundleName);
				
				analyzer.setProperty("Export-Package", "*;version=" + metadata.getVersion(artifact.getBaseVersion()));
				analyzer.setProperty("Import-Package", "*");
				
				if(request.getBndDirectory() != null)
				{
					addBndProperties(request.getBndDirectory(), symbolicName, analyzer);
				}
				
				log.debug("Bnd configuration: {}", analyzer.getFlattenedProperties());
				
				Manifest manifest = analyzer.calcManifest();
				log.debug("Manifest: {}", manifestToString(manifest));
				File metaInf = new File(projectDir, "META-INF");
				if(!metaInf.exists() && !metaInf.mkdir())
					throw new WrapException("Unable to create META-INF directory", request);
				try(OutputStream manifestOut = new FileOutputStream(new File(metaInf, "MANIFEST.MF")))
				{
					manifest.write(manifestOut);
				}
			}
			
			// Write build.properties while extracting jar
			Properties buildProps = new Properties();
			buildProps.setProperty("output..", "target/classes");
			buildProps.setProperty("source..", "src");
			Set<String> includes = new HashSet<String>();
			includes.add("META-INF/");
			try(JarFile jar = new JarFile(artifact.getFile()))
			{
				Enumeration<JarEntry> entries = jar.entries();
				while(entries.hasMoreElements())
				{
					JarEntry entry = entries.nextElement();
					
					if(entry.getName().endsWith("MANIFEST.MF"))
						continue;
					
					includes.add(Iterables.get(Splitter.on('/').split(entry.getName()), 0)); // get the first path component (dir or file in root)
					File outFile = new File(projectDir, entry.getName());
					if(entry.isDirectory())
					{
						if(!outFile.isDirectory() && !outFile.mkdirs())
							throw new WrapException("Unable to create directory: " + outFile, request);
					}
					else
					{
						if(!outFile.getParentFile().isDirectory() && !outFile.getParentFile().mkdirs())
							throw new WrapException("Unable to create directory: " + outFile.getParent(), request);
						try(InputStream in = jar.getInputStream(entry))
						{
							ByteStreams.copy(in, 
									Files.newOutputStreamSupplier(outFile));
						}
					}
				}
			}
			// write out build.properties
			buildProps.setProperty("bin.includes", Joiner.on(", ").join(includes));
			try(OutputStream propsOut = new FileOutputStream(new File(projectDir, "build.properties")))
			{
				buildProps.store(propsOut, "Autogenerated by " + getClass().getName() + " on " + new Date());
			}
		}
		catch(Exception e)
		{
			throw new WrapException("Error generating wrapper", e, request);
		}
	}
	
	private String manifestToString(Manifest manifest)
	{
		StringBuilder builder = new StringBuilder();
		Attributes main = manifest.getMainAttributes();
		for(Map.Entry<Object, Object> entry : main.entrySet())
		{
			builder.append(entry.getKey())
			.append(": ")
			.append(entry.getValue())
			.append('\n');
		}
		for(Map.Entry<String, Attributes> attrs : manifest.getEntries().entrySet())
		{
			if(!attrs.getValue().equals(main))
			{
				builder.append("Name: ").append(attrs.getKey()).append('\n');
				for(Map.Entry<Object, Object> entry : attrs.getValue().entrySet())
				{
					builder.append(entry.getKey())
						.append(": ")
						.append(entry.getValue())
						.append('\n');
				}
			}
		}
		
		return builder.toString();
	}

	private void addBndProperties(File bndDir, String symbolicName, Analyzer analyzer) throws Exception
	{
		File bndFile = new File(bndDir, symbolicName);
		if(!bndFile.isFile())
			bndFile = new File(bndDir, symbolicName + ".bnd");
		if(!bndFile.isFile())
			bndFile = new File(bndDir, symbolicName + "properties");
		
		if(bndFile.isFile())
		{
			analyzer.addProperties(bndFile);
		}
	}
	
	private Parent asParent(Model model) throws IOException
	{
		Parent parent = new Parent();
		parent.setGroupId(model.getGroupId());
		parent.setArtifactId(model.getArtifactId());
		parent.setVersion(model.getVersion());
		parent.setRelativePath(model.getPomFile().getCanonicalPath());
		return parent;
	}

	private RemoteRepository remote(Repository modelRepo)
	{
		return new RemoteRepository(modelRepo.getId(), modelRepo.getLayout(), modelRepo.getUrl());
	}

	@Override
	public void setBundleGenerator(BundleGenerator generator)
	{
		this.metadata = generator;
	}
	
	public static void main(String[] args) throws ModelBuildingException, ComponentLookupException, WrapException
	{
		DefaultWrapperGenerator generator = new DefaultWrapperGenerator();
		DefaultWrapRequest req = new DefaultWrapRequest();
		req.setParent(Maven.createModel(new File("/Users/ben/workspaces/gae-website/appsite-client/thirdparty/pom.xml")));
		req.addArtifact("org.codehaus.jedi", "jedi-core", "3.0.5");
		req.addArtifact("org.springframework", "spring-webmvc", "3.2.2.RELEASE");
		req.addArtifact("org.springframework", "spring-orm", "2.5.6");
		generator.generate(req);
		// then write modified parent pom
	}
}
