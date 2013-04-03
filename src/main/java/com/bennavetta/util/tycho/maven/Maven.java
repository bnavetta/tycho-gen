package com.bennavetta.util.tycho.maven;

import java.io.File;

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
import org.sonatype.aether.connector.async.AsyncRepositoryConnectorFactory;
import org.sonatype.aether.connector.file.FileRepositoryConnectorFactory;
import org.sonatype.aether.repository.LocalRepository;
import org.sonatype.aether.repository.RemoteRepository;
import org.sonatype.aether.spi.connector.RepositoryConnectorFactory;

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
		req.setPomFile(pom);
		return builder.build(req).getEffectiveModel();
	}

	public static RepositorySystemSession repositorySystemSession(RepositorySystem system)
	{
		MavenRepositorySystemSession session = new MavenRepositorySystemSession();
		session.setLocalRepositoryManager(system.newLocalRepositoryManager(LOCAL_REPO));
		
		session.setTransferListener(new ConsoleTransferListener());
		session.setRepositoryListener(new ConsoleRepositoryListener());
		
		return session;
	}
	
	public static RemoteRepository central()
	{
		return new RemoteRepository("central", "default", "http://repo.maven.apache.org/maven2/");
	}
}
