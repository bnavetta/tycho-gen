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
package com.bennavetta.util.tycho.maven;

import java.io.File;
import java.util.Collections;

import org.apache.maven.model.Model;
import org.apache.maven.model.building.DefaultModelBuildingRequest;
import org.apache.maven.model.building.ModelBuilder;
import org.apache.maven.model.building.ModelBuildingException;
import org.apache.maven.model.building.ModelBuildingRequest;
import org.apache.maven.repository.internal.MavenRepositorySystemSession;
import org.apache.maven.repository.internal.MavenServiceLocator;
import org.codehaus.plexus.DefaultPlexusContainer;
import org.codehaus.plexus.PlexusContainer;
import org.codehaus.plexus.PlexusContainerException;
import org.codehaus.plexus.component.repository.exception.ComponentLookupException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.sonatype.aether.RepositorySystem;
import org.sonatype.aether.RepositorySystemSession;
import org.sonatype.aether.artifact.Artifact;
import org.sonatype.aether.connector.async.AsyncRepositoryConnectorFactory;
import org.sonatype.aether.connector.file.FileRepositoryConnectorFactory;
import org.sonatype.aether.metadata.Metadata;
import org.sonatype.aether.repository.LocalRepository;
import org.sonatype.aether.repository.RemoteRepository;
import org.sonatype.aether.repository.RepositoryPolicy;
import org.sonatype.aether.resolution.MetadataRequest;
import org.sonatype.aether.resolution.MetadataResult;
import org.sonatype.aether.spi.connector.RepositoryConnectorFactory;
import org.sonatype.aether.util.metadata.DefaultMetadata;

/**
 * Utility methods to initialize Aether components.
 * Based on http://git.eclipse.org/c/aether/aether-demo.git/tree/aether-demo-snippets/src/main/java/org/eclipse/aether/examples/util/Booter.java
 * @author ben
 *
 */
public class Maven
{
	private Maven() {}
	
	private static Logger log = LoggerFactory.getLogger(Maven.class);
	
	public static final LocalRepository LOCAL_REPO = new LocalRepository(".local-repository");
	
	private static final PlexusContainer mavenContainer = initContainer();
	
	public static RepositorySystem repositorySystem()
	{
		MavenServiceLocator locator = new MavenServiceLocator();
        locator.addService( RepositoryConnectorFactory.class, AsyncRepositoryConnectorFactory.class);
        locator.addService(RepositoryConnectorFactory.class, FileRepositoryConnectorFactory.class);
 
        return locator.getService( RepositorySystem.class );
	}
	
	private static PlexusContainer initContainer()
	{
		try
		{
			return new DefaultPlexusContainer();
		}
		catch(PlexusContainerException e)
		{
			log.error("Error initializing Maven", e);
			return null;
		}
	}
	
	public static Model createModel(File pom) throws ModelBuildingException, ComponentLookupException
	{
		ModelBuilder builder = mavenContainer.lookup(ModelBuilder.class);
		ModelBuildingRequest req = new DefaultModelBuildingRequest();
		req.setProcessPlugins(false);
		req.setModelResolver(new RepoModelResolver());
		req.setPomFile(pom);
		return builder.build(req).getEffectiveModel();
	}

	public static RepositorySystemSession repositorySystemSession(RepositorySystem system)
	{
		MavenRepositorySystemSession session = new MavenRepositorySystemSession();
		session.setLocalRepositoryManager(system.newLocalRepositoryManager(LOCAL_REPO));
		
		session.setTransferListener(new ConsoleTransferListener());
		session.setRepositoryListener(new ConsoleRepositoryListener());
		
		session.setChecksumPolicy(RepositoryPolicy.CHECKSUM_POLICY_WARN);
		
		return session;
	}
	
	public static RemoteRepository central()
	{
		return new RemoteRepository("central", "default", "http://repo.maven.apache.org/maven2/");
	}

	public static Model getModel(Artifact artifact) throws ModelBuildingException, ComponentLookupException
	{
		RepositorySystem system = repositorySystem();
		RepositorySystemSession session = repositorySystemSession(system);
		Metadata metadata = getMetadata(system, session, artifact);
		return createModel(metadata.getFile());
	}
	
	public static Metadata getMetadata(RepositorySystem system, RepositorySystemSession session, Artifact artifact)
	{
		Metadata metadata = new DefaultMetadata(
				artifact.getGroupId(),
				artifact.getArtifactId(),
				artifact.getVersion(),
				artifact.getArtifactId() + "-" + artifact.getBaseVersion() + ".pom",
				Metadata.Nature.RELEASE_OR_SNAPSHOT);
		MetadataRequest request = new MetadataRequest(metadata, central(), null);
		MetadataResult result = system.resolveMetadata(session, Collections.singleton(request)).get(0);
		return result.getMetadata();
	}
}
