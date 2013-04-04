package com.bennavetta.util.tycho.impl;

import java.util.HashMap;
import java.util.Map;

import org.sonatype.aether.RepositorySystem;
import org.sonatype.aether.RepositorySystemSession;
import org.sonatype.aether.artifact.Artifact;
import org.sonatype.aether.collection.CollectRequest;
import org.sonatype.aether.collection.DependencyGraphTransformationContext;
import org.sonatype.aether.graph.Dependency;
import org.sonatype.aether.graph.DependencyFilter;
import org.sonatype.aether.graph.DependencyNode;
import org.sonatype.aether.graph.DependencyVisitor;
import org.sonatype.aether.repository.RemoteRepository;
import org.sonatype.aether.resolution.DependencyRequest;
import org.sonatype.aether.resolution.DependencyResult;
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

		Artifact artifact = new DefaultArtifact( "org.springframework:spring-orm:2.5.6" );
		Artifact artifact2 = new DefaultArtifact("org.springframework:spring-webmvc:3.2.2.RELEASE");

		RemoteRepository repo = Maven.central();

		DependencyFilter classpathFlter = DependencyFilterUtils.classpathFilter( JavaScopes.COMPILE );

		CollectRequest collectRequest = new CollectRequest();
		//collectRequest.setRoot( new Dependency( artifact, JavaScopes.COMPILE ) );
		collectRequest.addDependency(new Dependency(artifact, JavaScopes.COMPILE));
		collectRequest.addDependency(new Dependency(artifact2, JavaScopes.COMPILE));
		collectRequest.addRepository( repo );

		DependencyRequest dependencyRequest = new DependencyRequest( collectRequest, classpathFlter );

		DependencyResult result = system.resolveDependencies( session, dependencyRequest );
		//List<ArtifactResult> artifactResults = result.getArtifactResults();
		//System.out.println(result.getRoot().getClass());
		//System.out.println(result.getRoot().getDependency());
		//result.getRoot().accept(new TreePrinter());

		//PreorderNodeListGenerator nlg = new PreorderNodeListGenerator();
		//result.getRoot().accept(nlg);
		//System.out.println(nlg.getArtifacts(false));
		
		//for ( ArtifactResult artifactResult : artifactResults )
		//{
		//	System.out.println( artifactResult.getArtifact() + " resolved to " + artifactResult.getArtifact().getFile() );
		//}
		
		//new NearestVersionConflictResolver().transformGraph(result.getRoot(), new MyDependencyGraphTransformationContext(session)).accept(new TreePrinter());
		
		new VersionResolver().resolve(result.getRoot());
	}
	
	public static class MyDependencyGraphTransformationContext implements DependencyGraphTransformationContext
	{
		private RepositorySystemSession session;
		private Map<Object, Object> ctx = new HashMap<>();
		public MyDependencyGraphTransformationContext(RepositorySystemSession session)
		{
			this.session = session;
		}
		@Override
		public RepositorySystemSession getSession()
		{
			return session;
		}

		@Override
		public Object get(Object key)
		{
			return ctx.get(key);
		}

		@Override
		public Object put(Object key, Object value)
		{
			Object old = get(key);
			ctx.put(key, value);
			return old;
		}
		
	}
}
