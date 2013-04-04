package com.bennavetta.util.tycho.impl;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import org.apache.maven.model.Model;
import org.apache.maven.model.Repository;
import org.sonatype.aether.artifact.Artifact;
import org.sonatype.aether.util.artifact.DefaultArtifact;

import com.bennavetta.util.tycho.WrapRequest;

public class DefaultWrapRequest implements WrapRequest
{
	private Model parent;
	private Set<Artifact> artifacts;
	private List<Repository> repositories;
	
	public Model getParent()
	{
		return parent;
	}
	public void setParent(Model parent)
	{
		this.parent = parent;
	}
	
	public List<Repository> getRepositories()
	{
		return repositories;
	}
	public void setRepositories(List<Repository> repositories)
	{
		this.repositories = repositories;
	}
	
	public void addRepository(Repository repository)
	{
		if(repositories == null)
			repositories = new ArrayList<>();
		repositories.add(repository);
	}
	
	@Override
	public Set<Artifact> getArtifacts()
	{
		return artifacts;
	}
	
	public void setArtifacts(Set<Artifact> artifacts)
	{
		this.artifacts = artifacts;
	}
	
	public void addArtifact(Artifact artifact)
	{
		if(artifacts == null)
			artifacts = new HashSet<>();
		artifacts.add(artifact);
	}
	
	public void addArtifact(String groupId, String artifactId, String version)
	{
		if(artifacts == null)
			artifacts = new HashSet<>();
		artifacts.add(new DefaultArtifact(groupId + ":" + artifactId + ":" + version));
	}
}
