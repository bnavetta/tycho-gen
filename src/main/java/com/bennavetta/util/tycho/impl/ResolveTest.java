package com.bennavetta.util.tycho.impl;

import java.util.List;

import org.sonatype.aether.RepositorySystem;
import org.sonatype.aether.RepositorySystemSession;
import org.sonatype.aether.artifact.Artifact;
import org.sonatype.aether.collection.CollectRequest;
import org.sonatype.aether.graph.Dependency;
import org.sonatype.aether.graph.DependencyFilter;
import org.sonatype.aether.graph.DependencyNode;
import org.sonatype.aether.graph.DependencyVisitor;
import org.sonatype.aether.repository.RemoteRepository;
import org.sonatype.aether.resolution.ArtifactResult;
import org.sonatype.aether.resolution.DependencyRequest;
import org.sonatype.aether.util.artifact.DefaultArtifact;
import org.sonatype.aether.util.artifact.JavaScopes;
import org.sonatype.aether.util.filter.DependencyFilterUtils;

import com.bennavetta.util.tycho.maven.Maven;

public class ResolveTest
{
	public static class TreePrinter implements DependencyVisitor
	{
		private int indent = 0;
		
		@Override
		public boolean visitEnter(DependencyNode node)
		{
			for(int i = 0; i < indent; i++)
			{
				System.out.print('\t');
			}
			System.out.println(node);
			indent++;
			return true;
		}

		@Override
		public boolean visitLeave(DependencyNode node)
		{
			indent--;
			return true;
		}

	}

	public static void main( String[] args )
	        throws Exception
	    {
		 System.out.println( "------------------------------------------------------------" );
	        System.out.println( ResolveTest.class.getSimpleName() );

	        RepositorySystem system = Maven.repositorySystem();

	        RepositorySystemSession session = Maven.repositorySystemSession(system);

	        Artifact artifact = new DefaultArtifact( "org.sonatype.aether:aether-impl:1.9" );

	        RemoteRepository repo = Maven.central();

	        DependencyFilter classpathFlter = DependencyFilterUtils.classpathFilter( JavaScopes.COMPILE );

	        CollectRequest collectRequest = new CollectRequest();
	        collectRequest.setRoot( new Dependency( artifact, JavaScopes.COMPILE ) );
	        collectRequest.addRepository( repo );

	        DependencyRequest dependencyRequest = new DependencyRequest( collectRequest, classpathFlter );

	        List<ArtifactResult> artifactResults =
	            system.resolveDependencies( session, dependencyRequest ).getArtifactResults();

	        for ( ArtifactResult artifactResult : artifactResults )
	        {
	            System.out.println( artifactResult.getArtifact() + " resolved to " + artifactResult.getArtifact().getFile() );
	        }
	    }
}
