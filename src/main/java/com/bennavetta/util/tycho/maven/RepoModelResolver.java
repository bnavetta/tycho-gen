package com.bennavetta.util.tycho.maven;

import java.util.ArrayList;
import java.util.List;

import org.apache.maven.model.Repository;
import org.apache.maven.model.building.FileModelSource;
import org.apache.maven.model.building.ModelSource;
import org.apache.maven.model.resolution.InvalidRepositoryException;
import org.apache.maven.model.resolution.ModelResolver;
import org.apache.maven.model.resolution.UnresolvableModelException;
import org.sonatype.aether.RepositorySystem;
import org.sonatype.aether.RepositorySystemSession;
import org.sonatype.aether.artifact.Artifact;
import org.sonatype.aether.repository.RemoteRepository;
import org.sonatype.aether.util.artifact.DefaultArtifact;

public class RepoModelResolver implements ModelResolver
{
	private List<RemoteRepository> repos;
	private RepositorySystem system;
	private RepositorySystemSession session;
	
	public RepoModelResolver()
	{
		this.repos = new ArrayList<>();
		this.system = Maven.repositorySystem();
		this.session = Maven.repositorySystemSession(system);
	}
	
	@Override
	public ModelSource resolveModel(String groupId, String artifactId, String version) throws UnresolvableModelException
	{
		Artifact artifact = new DefaultArtifact(groupId + ":" + artifactId + ":" + version);
		return new FileModelSource(Maven.getMetadata(system, session, artifact).getFile());
	}

	@Override
	public void addRepository(Repository repository) throws InvalidRepositoryException
	{
		RemoteRepository remote = new RemoteRepository(repository.getId(), repository.getLayout(), repository.getUrl());
		repos.add(remote);
	}

	@Override
	public ModelResolver newCopy()
	{
		return this;
	}

}
